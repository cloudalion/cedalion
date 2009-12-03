% Implementatino of a Hindley-Milner type system
:- [util].
% Operator :: - is of type..
:- op(200, xfx, '::').
% Operator :< - is of class..
:- op(205, xfx, ':<').
% Operator ::< - is a special case of...
:- op(700, xfx, '::<').

% typeEquations(Environment, Term, Type, Path, Equations):
%	Constructs the Equations for to proove that Term is of Type, given the local Environment.  Path is the location
%	of the sub-term in the bigger term
% Scalar types
typeEquations((Env->Env), Int, Type, Path, [subtype(Path, int, Type)]) :-
	integer(Int),
	!.
typeEquations((Env->Env), Real, Type, Path, [subtype(Path, real, Type)]) :-
	number(Real),
	\+integer(Real),
	!.
% A variable defined at the local environment
typeEquations((Env->Env), Var, Type, Path, [subtype(Path, VarType, Type)]) :-
	var(Var),
	lookupVarType(Env, Var, VarType),
	!.
% A variable NOT defined at the local environment
typeEquations((Env->[varType(Var, VarType) | Env]), Var, Type, Path, [subtype(Path, VarType, Type)]) :-
	var(Var),
	!.
% A term of the form Term::Type.
typeEquations((EnvIn->EnvOut), Term::Type, ExternalType, Path, [subtype(Path, Type, ExternalType) | Eq]) :-
	!,
	typeEquationsForArgs((EnvIn->EnvOut), [Term, Type], [Type, type], Path, 0, Eq).
% A functor defined at the local/global environment
typeEquations((EnvIn->EnvOut), Term, Type, Path, [subtype(Path, DefType, Type) | ArgEquations]) :-
	Term =.. [Func | Args],
	sameSizeList(Args, ArgTypes),
	defined(EnvIn, Func, ArgTypes, DefType),
	!,
	typeEquationsForArgs((EnvIn->EnvOut), Args, ArgTypes, Path, 0, ArgEquations).
% A functor NOT defined at the local/global environment
typeEquations((EnvIn->[defined(Func, ArgTypes, Type) | EnvOut]), Term, Type, Path, ArgEquations) :-
	Term =.. [Func | Args],
	sameSizeList(Args, ArgTypes),
	typeEquationsForArgs((EnvIn->EnvOut), Args, ArgTypes, Path, 0, ArgEquations).


% typeEquationsForArgs((EnvIn->EnvOut), Args, ArgTypes, Path, Index, ArgEquations):
%	Provide equations for a list of arguments with a list of types.
typeEquationsForArgs((EnvIn->EnvOut), [Arg | Args], [ArgType | ArgTypes], Path, Index, ArgEquations) :-
	NewIndex is Index + 1,
	append(Path, [NewIndex], ArgPath),
	typeEquations((EnvIn->EnvMid), Arg, ArgType, ArgPath, Eq1),
	typeEquationsForArgs((EnvMid->EnvOut), Args, ArgTypes, Path, NewIndex, Eq2),
	append(Eq1, Eq2, ArgEquations).
typeEquationsForArgs((Env->Env), [], [], _, _, []).

% lookupVarType(Env, Var, VarType):
%	Lookup a variable type in a given environment
lookupVarType([varType(Var1, VarType) | _], Var, VarType) :-
	Var1 == Var.
lookupVarType([_ | Env], Var, VarType) :-
	lookupVarType(Env, Var, VarType).

% defined(Env, Func, ArgTypes, Type, Restrictions, DefPath):
%	Fetches a definition of Func with ArgTypes, of type Type from either the local Env, or the global environment.
%	Restrictions provides a list of Type::<Class restrictions, and DefPath is the path to the original definition.
defined(Env, Func, ArgTypes, Type) :-
	member(defined(Func, ArgTypes, Type), Env).
defined(_, Func, ArgTypes, Type) :-
	defined(Func, ArgTypes, Type).

% solveInequations(Inequations) :
%	Solve the list of Inequations of the type subtype(X, Y, _).  
%	This predicate succeeds if all inequations can be solved.
solveInequations(Inequations) :-
	solveInequations(Inequations, 0),
	solveInequations(Inequations, 1),
	solveInequations(Inequations, 2).

% solveInequations(Inequations, VarCount) :
%	Solve the list of Inequations of the type subtype(X, Y, _), 
%	where out of [X,Y] at most VarCount elements are unbound variables.
solveInequations([], _).
solveInequations([subtype(X, Y, _) | Rest], MaxVarCount) :-
	varCount([X, Y], VarCount),
	(VarCount =< MaxVarCount ->
		subtype(X, Y);
		true
	),
	solveInequations(Rest, MaxVarCount).

% varCount(List, Count):
%	Count the number of unbound variables in a list
varCount([], 0).
varCount([V | Rest], Count) :-
	varCount(Rest, RestCount),
	var(V),
	!,
	Count is RestCount + 1.
varCount([_ | Rest], Count) :-
	varCount(Rest, Count).

% subtype(T1, T2):
%	Is T1 a subtype of T2?  This is an reflexive and transitive closure over ::<
subtype(T, T).
subtype(T1, T3) :-
	T1 ::< T2,
	subtype(T2, T3).

% findTypeErrors(Inequations, Errors) :
%	Finds the errors preventing a list of inequations from being solvable.
%	This predicate always succeeds, and finds only 1 set of errors (there could be numerous combinations).
findTypeErrors([], []).
findTypeErrors([subtype(X, Y, _) | Rest], Errors) :-
	subtype(X, Y),
	!,
	findTypeErrors(Rest, Errors).
findTypeErrors([subtype(X, T, Path) | Rest], [typeMismatch(X, Y, Path) | Errors]) :-
	findTypeErrors(Rest, Errors).


