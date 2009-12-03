% loader.pl: a collection of predicates for loading and consulting cedalion code
:- [termio].
:- op(200, fx, ':').

% loadFile(FileName, Terms, VarNames) :
%	Load a file into a list of terms and a list of variable names
loadFile(FileName, Terms, VarNames):-
	open(FileName, read, Stream),
	readTerms(Stream, Terms, VarNames),
	!.	% FIXME

% readTerms(Stream, Terms, VarNames):
%	Read the terms in stream Stream as a list of terms.  Variable names are unified with VarNames
readTerms(Stream, [], []) :-
	at_end_of_stream(Stream),
	close(Stream),
	!.
readTerms(Stream, [Term | Terms], VarNames) :-
	readTerm(Stream, term(Term, VarNames1)),
	readTerms(Stream, Terms, VarNames2),
	append(VarNames1, VarNames2, VarNames).

%%%%%%%%%%% Namespace handling %%%%%%%%%%%%%%5

% loadModel(FileName, Namespace):
%	Load a file with the given name into the database.  The file will be associated with Namespace.
%	The result will be asserted as model(Namespace, Content, VarNames, Requires).
loadModel(FileName, Namespace) :-
	loadFile(FileName, LocalContent, VarNames),
	convertNamespaces(LocalContent, Namespace, Content, [], Requires),
	assert(model(Namespace, Content, VarNames, Requires)).

% convertNamespaces(LocalContent, Namespace, Content, NamespaceAliases, Requires):
%	Convert local clauses (terms) to global, given a default namespace.
%	LocalContent is a list of terms, including require(Alias, NS) clauses.  require/2 clauses are interpreted as
%	addition of namespaces to Requires and to the local-to-global conversion.
convertNamespaces([], _, [], _, []).
convertNamespaces([require(Alias, NS) | LocalContent], Namespace, Content, NamespaceAliases, [NS | Requires]) :-
	!,
	convertNamespaces(LocalContent, Namespace, Content, [(Alias->NS) | NamespaceAliases], Requires).
convertNamespaces([LocalClause | LocalContent], Namespace, [GlobalClause | Content], NamespaceAliases, Requires) :-
	localToGlobal(LocalClause, Namespace, NamespaceAliases, GlobalClause),
	convertNamespaces(LocalContent, Namespace, Content, NamespaceAliases, Requires).

% localToGlobal(LocalClause, Namespace, NamespaceAliases, GlobalClause):
%	Transform a term from local mode to global mode.  Namespace is the default namespace, and the rest of the
%	namespace bindings are given in NamespaceAliases.
localToGlobal(Var, _, _, Var) :-
	var(Var),
	!.
localToGlobal(Alias:Term, Namespace, NamespaceAliases, Global) :-
	member((Alias->NS), NamespaceAliases),
	!,
	applyNamespace(Term, NS, Global, Namespace, NamespaceAliases).
localToGlobal(Alias:Term, Namespace, NamespaceAliases, Global) :-
	!,
	applyNamespace(Term, Alias, Global, Namespace, NamespaceAliases).
localToGlobal(:Term, Namespace, NamespaceAliases, GTerm) :-
	!,
	subTermLocalToGlobal(Term, Namespace, NamespaceAliases, GTerm).

localToGlobal(Term, Namespace, NamespaceAliases, Global) :-
	applyNamespace(Term, Namespace, Global, Namespace, NamespaceAliases).

% applyNamespace(Term, Namespace, Global, DefaultNamespace, NamespaceAliases):
%	Change the name of a compound term to include Namespace
applyNamespace(Term, _, GTerm, DefaultNamespace, NamespaceAliases) :-
	noPrefix(Term),
	!,
	subTermLocalToGlobal(Term, DefaultNamespace, NamespaceAliases, GTerm).
applyNamespace(Term, Namespace, Global, DefaultNamespace, NamespaceAliases) :-
	Term =.. [Func | Args],
	atom_concat([Namespace, ':', Func], GlobalFunc),
	doList([Args, GArgs], {[Arg, GArg]; localToGlobal(Arg, DefaultNamespace, NamespaceAliases, GArg)}),
	Global =.. [GlobalFunc | GArgs].

% noPrefix(Term) :
%	Succeeds if Term should not be prefixed
noPrefix(Num) :-
	number(Num),
	!.
noPrefix(Var) :-
	var(Var),
	!.
noPrefix([]).
noPrefix([_|_]).
noPrefix(!).
noPrefix(Term) :-
	Term =.. [Op | _],
	current_op(_, _, Op).

subTermLocalToGlobal(Var, _, _, Var) :-
	var(Var),
	!.
subTermLocalToGlobal(Term, Namespace, NamespaceAliases, GTerm) :-
	Term =.. [Func | Args],
	doList([Args, GArgs], {[Arg, GArg]; localToGlobal(Arg, Namespace, NamespaceAliases, GArg)}),
	GTerm =.. [Func | GArgs].

% requiredNamespace(Namespace):
%	Namespace is unified with the every namespace that is required and not loaded
requiredNamespace(Namespace) :-
	model(_, _, _, Required),
	member(Namespace, Required),
	\+model(Namespace, _, _, _).

% requirementsSatisfied(Namespace):
%	Are all requirements for the given namespace satisfied?
requirementsSatisfied(Namespace) :-
	model(Namespace, _, _, Requied),
	forall(member(Req, Required), requirementsSatisfied(Req)).

% needToConsult(Namespace):
%	Provides all Namespaces were requirements are satisfied but are not yet consulted
needToConsult(Namespace) :-
	requirementsSatisfied(Namespace),
	\+consulted(Namespace, _).

% consultModel(Namespace):
%	Performs type checking and consults the terms in the given Namespace.  Errors and other information is reported
%	via marker/2.  It asserts the consulted/2 predicate to indicate that this namespace has been consulted, and
%	to provide information for un-consulting this namespace.
consultModel(Namespace) :-
	needToConsult(Namespace),
	model(Namespace, Content, _, _),
	checkType(TypedContent, list(goal), [Namespace], ([]->Markers)),
	removeTypeDefinitions(TypedContent, Content),
	forall(member(marker(Path, Marker), Markers), assert(marker(Namespace, Path, Marker))),
	forall(member(Clause, Content), assert(Clause)),
	assert(consulted(Namespace, Content)).

% reconsultModel(Namespace) :
%	un-consult and re-consult this Namespace and all depending namespaces.
reconsultModel(Namespace) :-
	unconsultModel(Namespace),
	forall((model(Depend, _, _, Req), member(Namespace, Req)), reconsultModel(Depend)),
	consultModel(Namespace).

% unconsultModel(Namespace):
%	Remove all traces of consulting a namespace
unconsultModel(Namespace) :-
	retract(consulted(Namespace, Content)),
	forall(member(Clause, Content), retract(Clause)),
	retractall(marker(Namespace, _, _)).

