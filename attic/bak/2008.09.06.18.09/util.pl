% Debugging tools
:- op(900, fx, '?').

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


% Apply a goal on lists
doList(Lists, ItemSet) :-
	firstOfAll(Lists, Firsts),
	copy_term(ItemSet, CopyOfItemSet),
	member(Firsts, CopyOfItemSet),
	restOfAll(Lists, Rests),
	doList(Rests, ItemSet).
doList(EmptyLists, _) :-
	allEmptyLists(EmptyLists).

% Membership in a set
member(X, {X; Goal}) :-
	\+var(Goal),
	Goal.
member(X, {X}) :-
	\+(X=(_;_)).
member(X, [X | _]).
member(X, [_ | List]) :-
	member(X, List).
member(X, Set) :-
	defineSet(Set, Definition),
	member(X, Definition).

% Define a union of sets
defineSet(union([FirstSet | OtherSets]), {X; (member(X, FirstSet); member(X, union(OtherSets)))}).

% Collect the first elements from a list of lists into a list
firstOfAll([], []).
firstOfAll([[FirstOfFirst | _] | Rest], [FirstOfFirst | FirstOfRest]) :-
	firstOfAll(Rest, FirstOfRest).

% Remove the first elements from each list in the list
restOfAll([], []).
restOfAll([[_ | RestOfFirst] | Rest], [RestOfFirst | RestOfRest]) :-
	restOfAll(Rest, RestOfRest).

% Check that a list contains nothing but empty lists
allEmptyLists([]).
allEmptyLists([[] | Rest]) :-
	allEmptyLists(Rest).

% Transform a term: Takes a set of elements of the shape: A->B, a source term and a target term.
% For every node in the source term tree, if it matches the "A" part of an element in the set, the term is replaced
% with the corresponding "B".
transformTerm(_, Var, Var) :-
	var(Var),
	!.
transformTerm(Set, Source, Target) :-
	Source =.. [Func | Args],
	doList([Args, TransArgs], {[Arg, TransArg]; transformTerm(Set, Arg, TransArg)}),
	TransSource =.. [Func | TransArgs],
	(member((TransSource->Target), Set) -> true; Target = TransSource).
% The 2 lists are of the same size
sameSizeList([], []).
sameSizeList([_|L1], [_|L2]) :-
	sameSizeList(L1, L2).

% For-all
forall(Cond, Action) :-
	\+((Cond, \+Action)).

% Append lists
append([], X, X).
append([F|R], X, [F|RX]) :-
	append(R, X, RX).

