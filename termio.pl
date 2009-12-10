% Write a term to a stream from a term(Term, VarNames) tupple
writeTerm(Stream, Term, VarNames) :-
	convertTermToWritable(Term, VarNames, WTerm),
	write_term(Stream, WTerm, [quoted(false)]),
	write(Stream, '.\n').

% Convert a term(Term, VarNames) tupple into a term to be written without quotes:
% Variables are converted to contain their to their names, atoms are quoted if needed
convertTermToWritable(Var, VarNames, Name) :-
	var(Var),
	findVarName(Var, VarNames, Name),
	!.
convertTermToWritable(Var, _, '_') :-
	var(Var),
	!.
convertTermToWritable(Num, _, Num) :-
	number(Num),
	!.
convertTermToWritable(Term, VarNames, WTerm) :-
	Term =.. [Atom | Args],
	quoteAtomIfNeeded(Atom, QAtom),
	convertTermsToWritable(Args, VarNames, WArgs),
	WTerm =.. [QAtom | WArgs].
	
convertTermsToWritable([], _, []).
convertTermsToWritable([Term | Terms], VarNames, [WTerm | WTerms]) :-
	convertTermToWritable(Term, VarNames, WTerm),
	convertTermsToWritable(Terms, VarNames, WTerms).

% Find the name of a varialbe in a list of Name=Var
findVarName(Var, ['cedalion#varName'(Var1::_, Name) | _], Name) :-
	Var == Var1.

findVarName(Var, [_| VarNames], Name) :-
	findVarName(Var, VarNames, Name).

% Check if an atom needs to be quoted
simpleAtom(Atom) :-
	atom_codes(Atom, [First | Rest]),
	First >= 97,
	First =< 122,
	forall(member(Code, Rest), identifierCode(Code)).
	
% Codes that may appear in an unquoted atom
identifierCode(Code) :-
	Code >= 97,
	Code =< 122.
identifierCode(Code) :-
	Code >= 65,
	Code =< 90.
identifierCode(95).

% Quote an atom
quote(Atom, QAtom) :-
	atom_chars(Atom, Chars),
	quoteList(Chars, QChars),
	atom_chars(QAtom, ['\'' | QChars]).

% Quote an atom given as a list of characters
quoteList([], ['\'']).
quoteList(['\'' | Rest], ['\\', '\'' | QRest]) :-
	!,
	quoteList(Rest, QRest).

quoteList([Char | Rest], [Char | QRest]) :-
	quoteList(Rest, QRest).

% Quote if needed
quoteAtomIfNeeded(Atom, Atom) :-
	simpleAtom(Atom),
	!.
quoteAtomIfNeeded(Atom, Atom) :-
	dontConvertFunc(Atom),
	!.
quoteAtomIfNeeded(Atom, QAtom) :-
	quote(Atom, QAtom).
