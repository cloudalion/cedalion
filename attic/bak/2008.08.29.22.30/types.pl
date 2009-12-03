%:- [util].

% Operator :: - is of type..
:- op(200, xfx, '::').
% Operator ::< - is a special case of...
:- op(700, xfx, '::<').

% Transform a term containing type defintions to a plain term.
removeTypeDefinitions(TermWithTypeDefs, PlainTerm) :-
	transformTerm({(A::_->A)}, TermWithTypeDefs, PlainTerm).

% checkType(Term, Type, Errors): 
%	Check that term Term is of type Type.  Errors will list the non-conformances
checkType(Term, Type, Errors):-
	checkType(Term, Type, ([]->_), [], ([]->Errors)),
	assertDefinitions(Errors).
% checkType(Term, Type, (VarsIn->VarsOut), Path, (ErrIn->ErrOut)): 
%	Check that Term corresponds to type Type.  VarsIn and VarsOut are variable bindings,
%	Path is a list of numbers providing the path to the current sub term, 
%	and ErrIn and ErrOut are the list of errors encountered.
%	This predicate always succeeds.  If the term is indeed of the given type, ErrOut==ErrIn.
checkType(Var, Type, VarTypes, Path, (Errors->[foundType(Path, Type)|Errors])) :-
	var(Var),
	checkVarType(Var, Type, VarTypes).
checkType(Term, member(Set), (VarIn->VarOut), Path, (ErrIn->[foundType(Path, Type)|ErrOut])) :-
	\+var(Set),
	!,
	checkType(Set, set(ItemType), (VarIn->VarMid), [set | Path], (ErrIn->ErrSet)),
	checkType(Term, ItemType, (VarMid->VarOut), Path, (ErrSet->ErrMid)),
	checkMember(Term, Set, Path, (ErrMid->ErrOut)).
checkType(Term, Type, VarTypes, Path, (ErrIn->[foundType(Path, Type)|ErrOut])) :-
	classify(Term, ClassifiedType, VarTypes, Path, (ErrIn->ErrMid)) ->
		failsafeReconcileTypes(Type, ClassifiedType, Path, (ErrMid->ErrOut));
		createDefinition(Term, Type, VarTypes, Path, (ErrIn->ErrOut)).

% Check that a list of terms is of a list of types
checkTypes([], [], (Vars->Vars), _, _, (Errors->Errors)).
checkTypes([FirstTerm | RestTerms], [FirstType | RestTypes], (VarsIn->VarsOut), Path, First, (ErrIn->ErrOut)) :-
	checkType(FirstTerm, FirstType, (VarsIn->VarsMid), [First | Path], (ErrIn->ErrMid)),
	Next is First + 1,
	checkTypes(RestTerms, RestTypes, (VarsMid->VarsOut), Path, Next, (ErrMid->ErrOut)).

% checkVarType(Var, Type, VarTypes):
%	Check that Var is associated with type Type in VarTypes.  If not listed, a binding is added.
checkVarType(Var, Type, (VarsIn->VarsIn)) :-
	findVarType(Var, VarsIn, VarType),
	!,
	reconcileTypes(Type, VarType).
checkVarType(Var, Type, (VarsIn->[Var=Type|VarsIn])).

findVarType(Var, [FirstVar=Type | _], Type) :-
	Var==FirstVar.
findVarType(Var, [_ | Rest], Type) :-
	findVarType(Var, Rest, Type).
	

% classify(Term, Type, VarTypes, Path, Errors) :
% 	Classify Term - try to provide a type for it (Type).  Errors in child terms are reported in Errors.
%	This predicate fails if the term itself (not child-terms) cannot be classified.
classify(Num, number, (VarsIn->VarsIn), _, (Errors->Errors)) :-
	number(Num),
	!.
classify(Var, Type, (VarsIn->VarsIn), _, (Errors->Errors)) :-
	var(Var),
	!,
	findVarType(Var, VarsIn, Type).
classify(Term::Type, Type, (VarsIn->VarsOut), Path, (ErrorsIn->ErrorsOut)) :-
	!,
	checkType(Type, type, (VarsIn->VarsMid), [2 | Path], (ErrorsIn->ErrorsMid)),
	checkType(Term, Type, (VarsMid->VarsOut), [1 | Path], (ErrorsMid->ErrorsOut)).

classify(Term, Type, VarTypes, Path, (ErrorsIn->ErrorsOut)) :-
	Term =.. [Func | Args],
	sameSizeList(Args, ArgTypes),
	defined(ErrorsIn, Func, ArgTypes, Type),
	checkTypes(Args, ArgTypes, VarTypes, Path, 1, (ErrorsIn->ErrorsOut)).

% classifyList(Terms, Types, VarTypes, Path, First, (ErrIn->ErrOut)):
%	Classify a list of terms.
classifyList([], [], (VarsIn->VarsIn), _, _, (Errors->Errors)).
classifyList([FirstTerm | RestTerms], [FirstType | RestTypes], (VarsIn->VarsOut), Path, First, (ErrIn->ErrOut)) :-
	classify(FirstTerm, FirstType, (VarsIn->VarsMid), [First | Path], (ErrIn->ErrMid)),
	Next is First + 1,
	classifyList(RestTerms, RestTypes, (VarsMid->VarsOut), Path, Next, (ErrMid->ErrOut)).

% reconcileTypes(Type, GivenType)
%	Reconciles between the given type GivenType and the expected type Type.
reconcileTypes(Type, Type) :-
	!.
reconcileTypes(Type, GivenType) :-
	GivenType ::< MoreGeneralType,
	reconcileTypes(Type, MoreGeneralType).

% failsafeReconcileTypes(Type, ClassifiedType, Path, (ErrIn->ErrOut)):
%	Perform a failsafe reconciliation - if fails, an error is recorded in ErrOut.
failsafeReconcileTypes(Type, ClassifiedType, _, (Errors->Errors)) :-
	reconcileTypes(Type, ClassifiedType).
failsafeReconcileTypes(Type, ClassifiedType, Path, (ErrIn->[error(Path, typeMismatch(Type, ClassifiedType))|ErrIn])) :-
	\+ reconcileTypes(Type, ClassifiedType).

% createDefinition(Term, Type, VarTypes, Path, (ErrIn->ErrOut)):
%	Create a difinition for the top level element in Term, to be of type Type, assuming all arguments are classifiable
createDefinition(Var, _, (VarsIn->VarsIn), Path, (ErrIn->[error(Path, untypedVariable)|ErrIn])) :-
	var(Var),
	!.
%createDefinition(_, Type, (VarsIn->VarsIn), Path, (ErrIn->[error(Path, looseType)|ErrIn])) :-
%createDefinition(_, Type, (VarsIn->VarsIn), Path, (ErrIn->ErrIn)) :-
%	var(Type),
%	!.
createDefinition(Term, Type, VarTypes, Path, (ErrIn->[defined(Func, ArgTypes, Type, Path)|ErrOut])) :-
	Term =.. [Func | Args],
	classifyList(Args, ArgTypes, VarTypes, Path, 1, (ErrIn->ErrOut)),
	!.
createDefinition(Term, Type, VarTypes, Path, (ErrIn->[error([Index|Path], unclassified)|ErrIn])) :-
	Term =.. [Func | Args],
	unclassifiedIndex(Args, VarTypes, 1, Index).

unclassifiedIndex([FirstTerm | RestTerms], (VarsIn->VarsOut), First, Index) :-
	Next is First + 1,
	classify(FirstTerm, _, (VarsIn -> VarsMid), _, _),
	!,
	unclassifiedIndex(RestTerms, (VarsIn->VarsOut), Next, Index).
unclassifiedIndex(_, (VarsIn->VarsIn), Index, Index).

% isOfType(Term, Type) :
%	Succeeds if Term is of Type
isOfType(Term, Type) :-
	classify(Term, ClassifiedType, ([]->_), [], ([]->Errors)),
	\+member(error(_, _), Errors),
	reconcileTypes(Type, ClassifiedType).

% Union type
definition(+, [T, T], T, []).
definition(::<, [type, type], goal, []).
A+_ ::< A.
_+B ::< B.

definition(member, [set(_)], type, []).

% checkMember(Item, Set, Path, (ErrIn->ErrOut)):
%	Failsafe membership test - If Item is not a member of Set, an error is added to ErrOut
checkMember(Item, Set, _, (ErrIn->ErrIn)) :-
	member(Item, Set),
	!.
checkMember(_, Set, Path, (ErrIn->[error(Path, notMemberOf(Set)) | ErrIn])).

% Look for definitions
defined(_, Func, ArgTypes, Type) :-
	definition(Func, ArgTypes, Type, _).
defined(Inputs, Func, ArgTypes, Type) :-
	member(defined(Func, ArgTypes, Type, _), Inputs).

% Assert all definitions in the output to the database
assertDefinitions(Inputs) :-
	forall(member(defined(Func, ArgTypes, Type, Path), Inputs), assert(definition(Func, ArgTypes, Type, Path))).

