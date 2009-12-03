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
typeEquations((Env->Env), Int, Type, Path, [eq(Path, Type=int)]) :-
	integer(Int),
	!.
typeEquations((Env->Env), Real, Type, Path, [eq(Path, Type=real)]) :-
	number(Real),
	\+integer(Real),
	!.
% A variable defined at the local environment
typeEquations((Env->Env), Var, Type, Path, [eq(Path, Type=VarType)]) :-
	var(Var),
	lookupVarType(Env, Var, VarType),
	!.
% A variable NOT defined at the local environment
typeEquations((Env->[varType(Var, VarType, Path) | Env]), Var, Type, Path, [eq(Path, Type=VarType)]) :-
	var(Var),
	!.
% A term of the form Term::Type.
typeEquations((EnvIn->EnvOut), Term::Type, Type, Path, Eq) :-
	!,
	typeEquationsForArgs((EnvIn->EnvOut), [Term, Type], [Type, type], Path, 0, Eq).
% A term of the form Term :< Class.
typeEquations((EnvIn->EnvOut), Term:<Class, Type, Path, [eq(Path, Type ::< Class) | Eq]) :-
	!,
	typeEquationsForArgs((EnvIn->EnvOut), [Term, Class], [Type, class], Path, 0, Eq).
% A class definition
typeEquation((EnvIn->EnvOut), class(Class, Prototype, Predicates), clause, Path, [eq(Path, T ::< Class) | Eq]) :-
	!,
	typeEquationsForArgs((EnvIn->EnvOut), [Class, Prototype, Predicates], [class, T, list(pred)], Path, 0, Eq).
% A functor defined at the local/global environment
typeEquations((EnvIn->EnvOut), Term, Type, Path, Eq) :-
	Term =.. [Func | Args],
	sameSizeList(Args, ArgTypes),
	defined(EnvIn, Func, ArgTypes, DefType, Restrictions, _),
	!,
	typeEquationsForArgs((EnvIn->EnvOut), Args, ArgTypes, Path, 0, ArgEquations),
	doList([Restrictions, RestrictionsWithPath], {[Restriction, eq(Path, Restriction)}),
	append([ArgEquations, RestrictionsWithPath, [eq(Path, Type = DefType)]], Eq).
% A functor NOT defined at the local/global environment
typeEquations((EnvIn->[defined(Func, ArgTypes, Type, [], Path) | EnvOut]), Term, Type, Path, ArgEquations) :-
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
defined(Env, Func, ArgTypes, Type, Restrictions, DefPath) :-
	member(defined(Func, ArgTypes, Type, Restrictions, DefPath), Env).
defined(_, Func, ArgTypes, Type, Restrictions, DefPath) :-
	defined(Func, ArgTypes, Type, Restrictions, DefPath).

% Special definitions
defined(def, [_], clause, [], []).

