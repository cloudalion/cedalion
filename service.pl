% Service utilities for the Cedalion commands
:- [cedalion].
:- op(1200, xfy, '~>').
:- op(100, xfx, ':').

% Insert a statement to the database
insert(Statement) :-
	translateStatement(Statement, Clause),
	assert(Clause).

% Remove a statement to the database
remove(Statement) :-
	translateStatement(Statement, Clause),
	retract(Clause).


translateStatement(Statement, Clause) :-
	if(Statement = (Head ~> Body),
		translateRewrite(Head, Body, Clause),
		Clause = Statement).

translateRewrite(Head, Body, Clause) :-
	if(Body = (H :- B),
		Clause = (H :- Head, B),
		Clause = (Body :- Head)).

% Load/Reload a file to the database
loadFile(FileName, Namespace) :-
	forall(retract(loadedStatement(FileName, Statement)), remove(Statement)),
	open(FileName, read, Stream),
	read(Stream, Term),
	insertTermsFromSteam(Stream, Term, FileName, [default=Namespace]).

insertTermsFromSteam(Stream, Term, FileName, NsList) :-
	if(Term=end_of_file,
		(
			close(Stream),
			assert(loadedNamespaces(FileName, NsList))
		),
		(
			interpretTerm(Term, FileName, NsList, NewNsList),
			read(Stream, NewTerm),
			insertTermsFromSteam(Stream, NewTerm, FileName, NewNsList)
		)).

interpretTerm(Term, FileName, NsList, NewNsList) :-
	if(Term = import(s(Alias), s(FullNs)),
		NewNsList = [Alias=FullNs | NsList],
		(
			NewNsList = NsList,
			localToGlobal(Term, NsList, GTerm),
			assert(loadedStatement(FileName, GTerm)),
			insert(GTerm)

		)).

% Translating from local terms (specific to this file) to their global representation
localToGlobal(Local, NsList, Global) :-
	if(nonCompoundTerm(Local),
		Global = Local,
		(
			if(Local = NsAlias:Term,
				(
					findNamespaceInList(NsAlias, NsList, Namespace),
					termLocalToGlobal(Term, Namespace, NsList, Global)
				),
				localToGlobal(default:Local, NsList, Global))
		)).

nonCompoundTerm(Term) :-
	var(Term).
nonCompoundTerm(Term) :-
	number(Term).
nonCompoundTerm(s(X)) :-
	atom(X).

termLocalToGlobal(Term, Namespace, NsList, Global) :-
	Term =.. [Func | Args],
	if(dontConvertFunc(Func),
		GFunc = Func,
		concat_atom([Namespace, ':', Func], GFunc)),
	localToGlobalList(Args, NsList, GArgs),
	Global =.. [GFunc | GArgs].

localToGlobalList([], _, []).
localToGlobalList([Local | Locals], NsList, [Global | Globals]) :-
	localToGlobal(Local, NsList, Global),
	localToGlobalList(Locals, NsList, Globals).

findNamespaceInList(Alias, [], Alias).
findNamespaceInList(Alias, [Alias=Namespace | _], Namespace).
findNamespaceInList(Alias, [BadAlias=_ | NsList], Namespace) :-
	\+(BadAlias = Alias),
	findNamespaceInList(Alias, NsList, Namespace).

dontConvertFunc('[]').
dontConvertFunc('.').
dontConvertFunc(Op) :- current_op(_, _, Op).
dontConvertFunc(Func) :- \+atom(Func).


% Read a file into a list of terms and variable names (translates to global terms).
readFile(FileName, Namespace, 'cedalion-services:fileContent'(Terms, NsListOut)) :-
	open(FileName, read, Stream),
	read_term(Stream, Term, [variable_names(VarNames)]),
	readFromSteam(Stream, Term, VarNames, FileName, [default=Namespace], NsListOut, Terms).

readFromSteam(Stream, Term, VarNames, FileName, NsList, NsListOut, Terms) :-
	if(Term = end_of_file,
		(
			Terms = [],
			close(Stream),
			NsListOut = NsList
		),
		(
			if(Term = import(s(Alias), s(Namespace)),
				(
					NewNsList = [Alias=Namespace | NsList],
					GTerm = Term
				),
				(
					NewNsList = NsList,
					localToGlobal(Term, NsList, GTerm)
				)),
			read_term(Stream, NewTerm, [variable_names(NewVarNames)]),
			readFromSteam(Stream, NewTerm, NewVarNames, FileName, NewNsList, NsListOut, OtherTerms),
			convertVarNames(VarNames, ConvVarNames),
			Terms = ['cedalion-services:statement'(GTerm, ConvVarNames) | OtherTerms]
		)).

convertVarNames([], []).
convertVarNames([Name=Var | RestIn], ['cedalion-services:varName'(Var::_, Name) | RestOut]) :-
	convertVarNames(RestIn, RestOut).

% Write a cedalion file
writeFile(FileName, 'cedalion-services:fileContent'(Terms, NsList)) :-
	open(FileName, write, Stream),
	writeImports(Stream, NsList),
	writeToStream(Stream, Terms, NsList).

writeToStream(Stream, [], _) :-
	close(Stream).

writeToStream(Stream, ['cedalion-services:statement'(GTerm, VarNames) | OtherTerms], NsList) :-
	globalToLocal(GTerm, NsList, Term),
	writeTerm(Stream, Term, VarNames).

globalToLocal(GTerm, NsList, Term) :-
	if(nonCompoundTerm(GTerm),
		Term = GTerm,
		(
			GTerm =.. [GFunc | GArgs],
			globalToLocalList(GArgs, Args),
			if(splitNamespace(GFunc, Namespace, Func),
				(
					LTerm =.. [Func | Args],
					findNamespaceAlias(Namespace, NsList, Alias),
					if(Alias = default,
						Term = LTerm,
						Term = Alias:LTerm)
				),
				Term =.. [GFunc | Args])
		)).

globalToLocalList([], []).
globalToLocalList([GArg | GArgs], [Arg | Args]) :-
	globalToLocal(GArg, Arg),
	globalToLocalList(GArgs, Args).

splitNamespace(GlobalName, NsAlias, LocalName) :-
	atom_chars(GlobalName, Chars),
	append(Pre, [':' | Post], Chars),
	atom_chars(NsAlias, Pre),
	atom_chars(LocalName, Post).

findNamespaceAlias(Alias, [], Alias).
findNamespaceAlias(Namespace, [Alias1 = Namespace1 | NsList], Alias) :-
	if(Namespace = Namespace1,
		Alias = Alias1,
		findNamespaceAlias(Namespace, NsList, Alias)).

writeImports(_, []).
writeImports(Stream, [Alias = Namespace | NsList]) :-
	if(Alias = default,
		true,
		writeTerm(Stream, import(s(Alias), s(Namespace), [])),
	writeImports(Stream, NsList).
