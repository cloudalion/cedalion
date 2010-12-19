% Consistency
:- [util].

prooveForAll(Vars1, If1, Then1) :-
	copy_term((Vars1, If1, Then1), (Vars, If, Then)),
	makeConcrete(Vars),
	assert(If),
	once(Then).
 
makeConcrete([]).
makeConcrete(['$var'(Unique) | Vars]) :-
	unique(Unique),
	makeConcrete(Vars).
 
consistent([]).
consistent(Goals) :-
	pickOne(Goals, Goal, Rest),
	consistentGoal(Goal, Rest).

consistentGoal(Goal, _) :-
	ground(Goal),
	!,
	once(Goal).

consistentGoal(Goal, Rest) :-
	allUnbound(Goal, Vars),
	prooveForAll(Vars, Goal, consistent(Rest)).
 
pickOne([First | Rest], First, Rest).
pickOne([First | List], Pick, [First | Rest]) :-
	pickOne(List, Pick, Rest).
 
allUnbound(Var, [Var]) :-
	var(Var),
	!.
allUnbound(Term, Vars) :-
	Term =.. [_ | Args],
	allUnboundInList(Args, Vars).
 
allUnboundInList([], []).
allUnboundInList([First | Rest], Vars) :-
	allUnbound(First, Vars1),
	allUnboundInList(Rest, Vars2),
	append(Vars1, Vars2, Vars).
 
:- dynamic lastUnique/1.
:- lastUnique(_) -> true ; assert(lastUnique(0)).
unique(U) :-
	retract(lastUnique(U)),
	Next is U + 1,
	assert(lastUnique(Next)).
 

:- dynamic cat/1, anim/1, dog/1.
anim(Cat) :- cat(Cat).
anim(Dog) :- dog(Dog).
 
cat(shunra).
dog(kalba).

