% Hindley-Milner type system with type classes
:- [util].

% Strings are marked with a $ prefix (e.g. $'Hello, World').
:- op(100, fx, '$').
% Type Assertions are performed in the form Term::Type
:- op(200, xfx, '::').
% A type belongs to a type-class
:- op(200, xfx, ':<').
:- assert(signature(':<', classAssertion, [type, class], [])).
% Class assertions
:- op(1200, xfx, '=>').
:- assert(signature('=>', clause, [list(classAssertion), clause], [])).
% Deduction
:- op(1190, xfx, if).
:- assert(signature(if, clause, [pred, list(pred)], [])).
% List type
:- assert(signature([], list(_), [], [])).
:- assert(signature('.', list(T), [T, list(T)], [])).
% Fact clause
:- assert(signature(fact, clause, [pred], [])).

% compileUnit(unit(Path, Header, Body)):
%	Performs type checks and asserts the clauses in Body.
%	Signatures for the name/arity pairs introduced in Header are defined by type-inference.
%	Path is used as a base for the markers asserted by this predicate.
%	It fails if an error occured in the process.
compileUnit(unit(Path, Header, Body)) :-
	createHeaderSignatures(Header, Signatures),
	duplicateClauses(Body, BodyDup),
	buildTypeTree(BodyDup, list(clause), Signatures, ([]->_), Path, Tree),
	classifyTypes(Tree, [], Path),
	assertSignatures(Signatures, ClassRestrictions),
	assertClauses(BodyDup).

% createHeaderSignatures(Header, Signatures):
%	Transform a list of name/arity pairs into a list of signatures of the type:
%	sig(Name, Type, ArgTypes, Restrictions).
createHeaderSignatures([], []).
createHeaderSignatures([Name/Arity | Header], [sig(Name, _Type, ArgTypes, _Restrictions) | Signatures]) :-
	length(ArgTypes, Arity),
	createHeaderSignatures(Header, Signatures).

% duplicateClauses(Body, BodyDup):
%	For each clause in Body, duplicate the clause to avoid sharing variables between different clauses
duplicateClauses([], []).
duplicateClauses([Clause | Body], [CopyOfClause | BodyDup]) :-
	copy_term(Clause, CopyOfClause),
	duplicateClauses(Body, BodyDup).

% buildTypeTree(Term, Type, Signatures, (VarTypesIn->VarTypesOut), Path, Tree):
%	Perform Hindley-Milner type inference on Term, which is supposed to match type Type.
%	Signatures contains local signature definitions.  VarTypes In->Out provide sharing type for sharing variables.
%	Path provides reference for the location inside a bigger term/unit.
%	The result of this operation is the construction of a type-assigning tree for Term, unified with Tree.
% Case 1: Numbers
buildTypeTree(Num, number, _, (VarTypes->VarTypes), _, number(Num)) :-
	number(Num),
	!.
% Case 2: Seen variables
buildTypeTree(Var, Type, _, (VarTypes->VarTypes), Path, var(Var, Type)) :-
	var(Var),
	findType(Var, VarTypes, VarType),
	!,
	try(safeUnify(Type, VarType), Path, typeMismatch(Type, VarType)).
% Case 3: Unseen variables
buildTypeTree(Var, Type, _, (VarTypes->[varType(Var, Type) | VarTypes]), _, var(Var, Type)):-
	var(Var),
	!.
% Case 4: Strings.  Strings are atoms marked with a $ prefix
buildTypeTree($ Atom, string, _, (VarTypes->VarTypes), Path, string(Atom)) :-
	!,
	try(atom(Atom), Path, notAnAtom(Atom)).
% Case 5: A type assertion (Term::Type)
buildTypeTree(Term::Type, Type, Signatures, VarTypes, Path, compound('::', Type, [], Trees, Term::Type, Path)) :-
	!,
	buildTypeTrees([Term, Type], [Type, type], Signatures, VarTypes, Path, 0, Trees).
% Case 6: A general compound term
buildTypeTree(Term, Type, Signatures, VarTypes, Path, compound(Func, Type, Constraints, ChildTrees, Term, Path)) :-
	Term =.. [Func | Args],
	length(Args, Arity),
	length(ArgTypes, Arity),
	try(findSignature(Signatures, Func, SigType, ArgTypes, Constraints), 
		Path, unknownConcept(Func/Arity)),
	try(safeUnify(Type, SigType), Path, typeMismatch(Type, SigType)),
	buildTypeTrees(Args, ArgTypes, Signatures, VarTypes, Path, 0, ChildTrees).
	
% buildTypeTrees(Args, ArgTypes, Signature, VarTypes, Path, ChildTrees):
%	Run buildTypeTree/5 on all elements in the given list.
buildTypeTrees([], [], _, (VarTypes->VarTypes), _, _, []).
buildTypeTrees([Arg | Args], [Type | Types], Signature, (VarTypesIn->VarTypesOut), Path, PrevIndex, [Tree | Trees]) :-
	Index is PrevIndex + 1,
	append(Path, [Index], NewPath),
	buildTypeTree(Arg, Type, Signature, (VarTypesIn->VarTypesMid), NewPath, Tree),
	buildTypeTrees(Args, Types, Signature, (VarTypesMid->VarTypesOut), Path, Index, Trees).

% findSignature(Signatures, Func, SigType, ArgTypes, Restrictions):
%	Finds the signature of Func/length(ArgTypes) in either the global or local environments.
%	The local environment is manifested by the Signatures list of signatures, while the
%	global environment is manifasted by the signature/4 predicate.
findSignature(Signatures, Func, SigType, ArgTypes, Restrictions) :-
	member(sig(Func, SigType, ArgTypes, Restrictions), Signatures).
findSignature(_, Func, SigType, ArgTypes, Restrictions) :-
	signature(Func, SigType, ArgTypes, Restrictions).

% findType(Var, VarTypes, VarType):
%	Find a type bound to a variable in a list of elements of the sort: varType(Var, VarType), provided as VarTypes.
findType(Var, [varType(Var1, VarType) | _], VarType) :-
	Var1 == Var.
findType(Var, [_ | VarTypes], VarType) :-
	findType(Var, VarTypes, VarType).

% classifyTypes(Tree, ClassAssertions):
%	Crawls a type-assigning Tree, and check that all restrictions are met, 
%	meaning they are provable from ClassAssertions.
%	This predicate also updates restrictions for "open" concepts.
% Case 1: Numbers are ignored
classifyTypes(number(_), _).
% Case 2: Variables are ignored
classifyTypes(var(_, _), _).
% Case 3: Strings are ignored
classifyTypes(string(_), _).
% Case 4: Class assertions ([type :< class] => clause)
classifyTypes(compound(_, clause, _, [_, ClauseTree], (ClassAssertions => _), Path), PrevAssertions) :-
	append(PrevAssertions, ClassAssertions, AllAssertions),
	classifyTypes(ClauseTree, AllAssertions).
% Case 5: A general compound term
classifyTypes(compound(_, _, Constraints, ChildTrees, _, Path), Assertions) :-
	buildConstraints(Constraints, Assertions, ChildTrees),
	prooveConstraints(Constraints, Assertions, Path),
	classifyTypesInList(ChildTrees, Assertions).
	
% buildConstraints(Constraints, Assertions, ChildTrees):
%	For unbound constraints, create them based on the assertions where the types are relevant.
buildConstraints(Constraints, Assertions, ChildTrees) :-
	var(Constraints),
	!,
	filterConstraints(Constraints, Assertions, ChildTrees).

% filterConstraints(Constraints, Assertions, ChildTrees):
%	Copy the elements in Assertions to Constraints, given that the
%	type parts effect ChildTrees.
filterConstraints([], [], _).
filterConstraints([Type :< Class | Constraints], [Type :< Class | Assertions], ChildTrees) :-
	dependsOn(Type, ChildTrees),
	!,
	filterConstraints(Constraints, Assertions, ChildTrees).
filterConstraints(Constraints, [_ | Assertions], ChildTrees) :-
	filterConstraints(Constraints, Assertions, ChildTrees).

% dependsOn(Var, Term):
%	Succeed if Var exists in Term
dependsOn(Var, Term) :-
	var(Term),
	!,
	Term == Var.
dependsOn(Var, Term) :-
	Term =.. [_ | Args],
	dependsOnList(Var, Args).
dependsOnList(Var, [Arg | _]) :-
	dependsOn(Var, Arg).
dependsOnList(Var, [_ | Args]) :-
	dependsOnList(Var, Args).

% classifyTypesInList(Trees, Assertions):
%	Applies classifyTypes on the elements of Trees.
classifyTypesInList([], _).
classifyTypesInList([Tree | Trees], Assertions) :-
	classifyTypes(Tree, Assertions),
	classifyTypesInList(Trees, Assertions).

% prooveConstraints(Constraints, Assertions, Path):
%	Try to proove Constraints based on Assertions.
prooveConstraints([], _, _).
prooveConstraints([Type :< Class | Constraints], Assertions, Path) :-
	try(prooveConstraint(Type, Class, Restrictions),
		Path, typeNotInClass(Type, Class)),
	prooveConstraints(Constraints, Restrictions, Path).

% prooveConstraint(Type, Class, Assertions):
%	Check that Type belongs to type-class Class, where Restrictions are given.
prooveConstraint(Type, Class, Assertions) :-
	var(Type),
	!,
	constraintInAssertions(Type, Class, Assertions).
prooveConstraint(Type, Class, Assertions) :-
	belongsTo(Type, Class, Conditions),
	prooveConstraints(Conditions, Assertions, _).

% constraintInAssertions(Type, Class, Assertions):
%	Check that Type :< Class exists in Assertions
constraintInAssertions(Type, Class, [Type1 :< Class | _]) :-
	Type == Type1.
constraintInAssertions(Type, Class, [_ | Assertions]) :-
	constraintInAssertions(Type, Class, Assertions).

% assertSignatures(Signatures, ClassRestrictions),
% assertClauses(BodyDup).

% try(Goal, Path, Error):
%	Try to run Goal.  If it fails, assert a marker with error(Error) as content, on Path
try(Goal, _, _) :-
	Goal,
	!.
try(_, Path, Error) :-
	assertMarker(Path, error(Error)),
	fail.

% assertMarker(Path, Marker):
%	Asserts a marker with Marker as contents and Path as path
assertMarker(Path, Marker) :-
	assert(marker(Path, Marker)).


