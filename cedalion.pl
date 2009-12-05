% Debugging
:- op(700, xfx, '::').
:- op(200, fx, '?').
? Term:-
        write('? '),
        write(Term),
        nl,
        Term,
        write('. '),
        write(Term),
        nl.

? Term:-
        write('! '),
        write(Term),
        nl,
        fail.

% Sets
setMember(X, set(X, Goal), _) :-
	Goal.
setMember(X, listSet([X|_]), _).
setMember(X, listSet([_|List]), T) :-
	setMember(X, listSet(List), T).

% If predicate
if(Cond, Then, _) :-
	Cond,
	!,
	Then.
if(_, _, Else) :-
	Else.

if(Cond, Then) :-
	if(Cond, Then, true).

% Map a list
mapList(Set, Lists) :-
	if(firstElements(Lists, Firsts, Tails),
	(
		copy_term(Set, Set1),
		setMember(Firsts, Set, _),
		mapList(Set1, Tails)
	),
	(
		allListAreEmpty(Lists)
	)).
firstElements([], [], []).
firstElements([[Item | Tail] :: list(T) | Lists], [Item::T | Items], [Tail::list(T) | Tails]) :-
	firstElements(Lists, Items, Tails).

allListAreEmpty([]).
allListAreEmpty([[]::list(_) | Lists]) :-
	allListAreEmpty(Lists).

% Transform a term, based on a set of pairs - source and target
transformTerm(TermFrom, TermTo, Type, Set, ConvType) :-
	if(var(TermFrom), TermTo = TermFrom,
		if(setMember(pair(TermFrom, TermTo), Set, pair(ConvType)),
			true,
			(
				parseTerm(TermFrom::Type, Func, ArgsFrom),
				mapList(set([(ArgFrom::ArgType)::typedTerm, (ArgTo::ArgType)::typedTerm], transformTerm(ArgFrom, ArgTo, ArgType, Set, ConvType)), 
					[ArgsFrom::list(typedTerm), ArgsTo::list(typedTerm)]),
				parseTerm(TermTo::Type, Func, ArgsTo)
			))).


% Parse a typed term to its name and arguments, or vice versa
parseTerm(Term::_, s(Func), TArgs) :-
	if(var(Term), 
		(
			makeTyped(Args, TArgs),
			Term =.. [Func | Args]
		),
		(
			Term =.. [Func | Args],
			makeTyped(Args, TArgs)
		)).

makeTyped([], []).
makeTyped([First | Rest], [First::_ | TRest]) :-
	makeTyped(Rest, TRest).


%defaultSignature(Term::_, TArgs) :-
%	Term =.. [_|Args],
%	mapList(set([Arg, Arg::_], true), [Args, TArgs]).
% Just some signatures to get started...
signature(a(A, B, C)::t, [A::t1, B::t2, C::t1]).
signature(b(B)::t1, [B::number]).
signature(c::t2, []).
signature((H:-B)::statement, [H::pred, B::pred]).
signature((A,B)::pred, [A::pred, B::pred]).
signature(pair(X,Y)::pairType(TX,TY), [X::TX, Y::TY]).
signature(x::xt, []).

% Path into a term
pathExtract(wholeFile(FileName), Content::list(statement), VarNames) :-
	loadedFile(FileName, Content, VarNames).

pathExtract(path(SubPath, Index), TChildTerm, VarNames) :-
	pathExtract(SubPath, TTerm, VarNames),
	parseTerm(TTerm, _, TArgs),
	elementAt(TArgs, Index, TChildTerm).

% Replace the subterm under a certain path.  Provides the commands that need to run in order to make this change.
pathModify(wholeFile(FileName), NewContent::list(statement), VarNames,
	doBoth(
		remove(loadedFile(FileName, _, _)),
		insert(loadedFile(FileName, NewContent, VarNames))
	)).

pathModify(path(SubPath, Index), TNewContent, NewVarNames, Command) :-
	pathExtract(SubPath, TOldContent, OldVarNames),
	mergeVarNames(NewVarNames, OldVarNames, VarNames),
	parseTerm(TOldContent, Func, TArgs),
	replaceAt(TArgs, Index, TNewContent, TNewArgs),
	parseTerm(TNewParentContent, Func, TNewArgs),
	pathModify(SubPath, TNewParentContent, VarNames, Command).


% TODO: Change this...
mergeVarNames(NewVarNames, _, NewVarNames).

% An element at a certain index of a list
elementAt([X|_], 0, X) :- true.
elementAt([_|List], Index, X) :-
	Index > 0,
	IndexMinus1 is Index - 1,
	elementAt(List, IndexMinus1, X).

replaceAt([_|List], 0, NewContent, [NewContent|List]).
replaceAt([First|Olds], Index, NewContent, [First|News]) :-
	Index > 0,
	IndexMinus1 is Index-1,
	replaceAt(Olds, IndexMinus1, NewContent, News).

