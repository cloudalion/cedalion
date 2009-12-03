% Version 0.9: Added transformation support (for visualization)
% Version 0.8: Bugfixes in bestComprehend
% Version 0.7: Porting to YAP, added the test target.
% Version 0.6: 
	% Added error handling to model comprehension.
	% Added pretty printing of models.
% Version 0.5: Added variable support
% Version 0.4: class/3 -> declare/1
% Version 0.3: 
	% Class definitions take lists as structures
	% Sub-tree extraction using path
	% Removed enumeration
% Version 0.2: Added implicit context support

:- op(200, xfx, '@').
:- op(200, fx, '?').

:- multifile declare/1.
:- multifile declare/2.


member(X, [X|_]).
member(X, [_|Y]):-
	member(X, Y).

forall(A, B) :-
	\+((A, \+B)).
declare(Decl, []) :-
	declare(Decl).

% Meta declaration definition
declare([declare, [':', structure:classMemberList, semanticType:semanticType]] : goal).
declare((semanticTypeName : atom) : semanticType, [semanticType($(semanticTypeName))]).
declare([[]] : classMemberList).
declare(['.', member : classMember/Context, others : classMemberList/[$(member) | Context]] : classMemberList/Context).
declare(['.', atom : atom, others : classMemberList] : classMemberList).
declare([':', name : atom, semanticType : semanticType] : classMember).
declare(['$', name : atom/Context] : SemanticType/Context, [member($(name) : SemanticType, Context)]).
% Some sample definitions
semanticType(goal).
semanticType(classMemberList).
semanticType(atom).
semanticType(classMember).

type(int).
type(float).

declare(['+', a:expr(Type), b:expr(Type)] : expr(Type)).
declare(['*', a:expr(int), b:expr(int)] : expr(int)).
declare((num : integer) : expr(int) ).
declare((num : float) : expr(float)).
declare((val:expr(int)) : expr(float)).

% Compile the schema
:- dynamic parsedClass/6.
compileSchema :-
	retractall(parsedClass(_, _, _, _, _, _)),
	declare(Structure : SemanticType, Conditions),
	addContextToSemanticType(SemanticType, ContextConversion),
	applyContextToSemanticType(ContextConversion, SemanticType, SemanticTypeWithContext),
	parseClass(Structure, Pattern, [], ChildElements, [], Models, [], Args, ContextConversion, Class, SemanticType),
	extractMembers(SemanticTypeWithContext, ExtractedSemanticTypeWithContext, ChildElements, Args),
	extractMembers(Conditions, ExtractedConditions, ChildElements, Args),
	copy_term([Pattern, ChildElements], [CopyOfPattern, CopyOfChildElements]),
	assert(parsedClass(Class, Pattern : ExtractedSemanticTypeWithContext, ChildElements, Models, ExtractedConditions, m(Class:ExtractedSemanticTypeWithContext, Args, CopyOfPattern, CopyOfChildElements))),
	fail.
compileSchema.

% If a semtantic type is given with no context, provide the context and supply the a conversion for other semantic types
addContextToSemanticType(_/_, no) :-
	!.
addContextToSemanticType(_, yes(_)).

applyContextToSemanticType(yes(Context), SemanticType, SemanticType/Context).
applyContextToSemanticType(no, SemanticType, SemanticType).

% Child elements: Var : SemanticType
% Arguments: name=m(...)
% Models: m(...)
parseClass(Name : PackedChildSemanticType, Var, ChildElementsIn, [Var : ChildSemanticType | ChildElementsIn], ModelsIn, [Model | ModelsIn], ArgsIn, [Name=Model|ArgsIn], ContextConversion, (PackedChildSemanticType->SemanticType), SemanticType) :-
	applyContextToSemanticType(ContextConversion, PackedChildSemanticType, PackedChildSemanticTypeWithContext),
	extractMembers(PackedChildSemanticTypeWithContext, ChildSemanticType, ChildElementsIn, ArgsIn).
	
parseClass([Func | SubStructs], Pattern, ChildElementsIn, ChildElementsOut, ModelsIn, ModelsOut, ArgsIn, ArgsOut, ContextConversion, Func, _) :-
	parseClasses(SubStructs, Patterns, ChildElementsIn, ChildElementsOut, ModelsIn, ModelsOut, ArgsIn, ArgsOut, ContextConversion),
	Pattern =.. [Func | Patterns].

parseClasses([], [], ChildElements, ChildElements, Models, Models, Args, Args, _).
parseClasses([FirstSubStructs | RestStructs], [FirstPattern | RestPatterns], ChildElementsIn, ChildElementsOut, ModelsIn, ModelsOut, ArgsIn, ArgsOut, ContextConversion) :-
	parseClass(FirstSubStructs, FirstPattern, ChildElementsIn, ChildElementsMid, ModelsIn, ModelsMid, ArgsIn, ArgsMid, ContextConversion, _, _),
	parseClasses(RestStructs, RestPatterns, ChildElementsMid, ChildElementsOut, ModelsMid, ModelsOut, ArgsMid, ArgsOut, ContextConversion).

% Replace elements of the format $(name) with a variable representing the content of that name in the pattern
extractMembers(Var, Var, _, _) :-
	var(Var),
	!.
extractMembers($(Name), Var, ChildElements, Args) :-
	atom(Name),
	!,
	indexOf(Name = _, Args, Index),
	indexOf(Var : _, ChildElements, Index).
	
extractMembers(PackedTerm, ExtractedTerm, ChildElements, Args) :-
	PackedTerm =.. [Func | TermArgs],
	extractMemberList(TermArgs, ExtractedTermArgs, ChildElements, Args),
	ExtractedTerm =.. [Func | ExtractedTermArgs].
	
extractMemberList([], [], _, _).
extractMemberList([TermIn | TermsIn], [TermOut | TermsOut], ChildElements, Args) :-
	extractMembers(TermIn, TermOut, ChildElements, Args),
	extractMemberList(TermsIn, TermsOut, ChildElements, Args).

indexOf(Element, [Element | _], 0).
indexOf(Element, [_ | List], Index) :-
	indexOf(Element, List, Index1),
	Index is Index1 + 1.


% Comprehend a term
comprehend(Condition, Bindings, Parsed) :-
	comprehend(Condition, Bindings, Parsed, []).

comprehend(Var : _, Bindings, var(Name), _) :-
	var(Var),
	!,
	varName(Var, Bindings, Name).
comprehend(Int : integer/_, _, Int, _) :-
	integer(Int).
comprehend(Float : float/_, _, Float, _) :-
	number(Float),
	\+integer(Float).
comprehend(Atom : atom/_, _, Atom, _) :-
	atom(Atom).

comprehend(Term : SemanticType, Bindings, Parsed, History) :-
	\+member(Term : SemanticType, History),
	parsedClass(_, Term : SemanticType, ChildElements, Models, Conditions, Parsed),
	forall(member(Cond, Conditions), Cond),
	comprehendList(ChildElements, Bindings, Models, [Term : SemanticType | History]).
	
comprehendList([], _, [], _).
comprehendList([First | Rest], Bindings, [Model | Models], History) :-
	comprehend(First, Bindings, Model, History),
	comprehendList(Rest, Bindings, Models, History).

% Reconstruct a term from a model
reconstructTerm(Model, Term, Bindings) :-
	reconstructTerm(Model, Term, [], Bindings).
reconstructTerm(m(_, Args, Term, ChildElements), Term, BindingsIn, BindingsOut) :-
	reconstructTerms(Args, ChildElements, BindingsIn, BindingsOut).

reconstructTerm(Num, Num, Bindings, Bindings) :-
	number(Num).
reconstructTerm(Atom, Atom, Bindings, Bindings) :-
	atom(Atom).
reconstructTerm(var('_'), _, Bindings, Bindings) :-
	!.
reconstructTerm(var(Name), Var, Bindings, Bindings) :-
	member(Var=Name, Bindings),
	!.
reconstructTerm(var(Name), Var, BindingsIn, [Var=Name | BindingsIn]).

reconstructTerms([], [], Bindings, Bindings).
reconstructTerms([_=Model | RestArgs], [Var:_ | RestVars], BindingsIn, BindingsOut) :-
	reconstructTerm(Model, Var, BindingsIn, BindingsMid),
	reconstructTerms(RestArgs, RestVars, BindingsMid, BindingsOut).


% Extract a sub-tree based on the given path
extractSubTree(Tree, [], Tree).
extractSubTree(m(_, Args, _, _), [First | Rest], SubTree) :-
	extractArg(Args, First, SubTree1),
	extractSubTree(SubTree1, Rest, SubTree).

% Extract an argument from an argument list
extractArg([Name = Arg | _], Name, Arg).
extractArg([_ | Args], Name, Arg) :-
	extractArg(Args, Name, Arg).

% Find a variable name in a bindings list
varName(Var, [Var1=Name | _], Name) :-
	Var1 == Var,
	!.
varName(Var, [_ | Bindings], Name) :-
	varName(Var, Bindings, Name).
varName(_, [], '_').

%%%%%%%%%%%%%%%%% More test domain %%%%%%%%%%%%%%%%%%%%

% Statement list
declare(['.', first : statement, rest : statementList] : statementList).
declare(['.', def : definition/Context, rest : statementList/[$(def) | Context]] : statementList/Context).
declare([[]] : statementList).

% Variable definition
declare([varDef, name : atom, type : type] : definition).

% Print statement
declare([print, expr : expr(_)] : statement).

% A variable reference expression
declare((varName : atom/Context) : expr(Type)/Context, [member(varDef($(varName), Type), Context)]).

% types
declare([int] : type).
declare([float] : type).

%%%%%%%%%%%%% Error Handling %%%%%%%%%%%%%%%
bestComprehend(ComprehendWhat, Bindings, BestResult, History) :-
	findall(Result, comprehendEval(ComprehendWhat, Bindings, Result, History), Results),
	bestResult(Results, BestResult).
	
bestResult([First | Rest], Best) :-
	bestResult(First, Rest, Best).
bestResult(Result, [], Result).
bestResult(_, [(Model->0)| _], (Model->0)).
bestResult((_->BestScoreSoFar), [(Model->Score) | Rest], BestResult) :-
	Score =< BestScoreSoFar,
	bestResult((Model->Score), Rest, BestResult).
bestResult((BestModelSoFar->BestScoreSoFar), [(_->Score) | Rest], BestResult) :-
	Score > BestScoreSoFar,
	bestResult((BestModelSoFar->BestScoreSoFar), Rest, BestResult).

% Try to comprehend, and evaluate the level of comprehension.
comprehendEval(Var : _, Bindings, (var(Name)->0), _) :-
	var(Var),
	!,
	varName(Var, Bindings, Name).
comprehendEval(Int : integer/_, _, (Int->0), _) :-
	integer(Int),
	!.
comprehendEval(Float : float/_, _, (Float->0), _) :-
	number(Float),
	\+integer(Float),
	!.
comprehendEval(Atom : atom/_, _, (Atom->0), _) :-
	atom(Atom),
	!.

comprehendEval(Term : SemanticType, Bindings, (ParsedAfterConditions->Score), History) :-
	\+member(Term : SemanticType, History),
	parsedClass(_, Term : SemanticType, ChildElements, Models, Conditions, Parsed),
	evalConditions(Conditions, 0, CondScore, Parsed, ParsedAfterConditions),
	comprehendEvalList(ChildElements, Bindings, Models, [Term : SemanticType | History], 0, SubScore),
	Score is SubScore + CondScore.

comprehendEval(Term : SemanticType, _, (error(undefined, Term : SemanticType)->Score), _) :-
	\+parsedClass(_, Term : SemanticType, _, _, _, _),
	maxDepth(Term, Score).
	
comprehendEvalList([], _, [], _, Score, Score).
comprehendEvalList([First | Rest], Bindings, [Model | Models], History, ScoreIn, ScoreOut) :-
	bestComprehend(First, Bindings, (Model->FirstScore), History),
	max(ScoreIn, FirstScore, ScoreMid),
	comprehendEvalList(Rest, Bindings, Models, History, ScoreMid, ScoreOut).

max(A, B, A) :-
	A >= B,
	!.
max(_, B, B).

maxDepth(Var, 0) :-
	var(Var),
	!.
maxDepth(Term, Score) :-
	Term =.. [_|Args],
	maxDepthList(Args, 0, ArgScore),
	Score is ArgScore + 1.

maxDepthList([], Score, Score).
maxDepthList([First | Rest], ScoreIn, ScoreOut) :-
	maxDepth(First, FirstScore),
	max(FirstScore, ScoreIn, ScoreMid),
	maxDepthList(Rest, ScoreMid, ScoreOut).

evalConditions([], Score, Score, Model, Model).
evalConditions([First | Rest], ScoreIn, ScoreOut, ModelIn, ModelOut) :-
	First,
	evalConditions(Rest, ScoreIn, ScoreOut, ModelIn, ModelOut).
evalConditions([First | Rest], ScoreIn, ScoreOut, ModelIn, ModelOut) :-
	\+First,
	ScoreMid is ScoreIn + 0.5,
	evalConditions(Rest, ScoreMid, ScoreOut, error(false(First), ModelIn), ModelOut).

%%%%%%%%%% Debugging %%%%%%%%%%%%%%%%%5
prettyPrintModel(Model) :-
	prettyPrintModel(Model, -1).
prettyPrintModel(m(Class, Args, _, _), NestingLevel) :-
	!,
	write(Class),
	nl,
	NestingLevelPlusOne is NestingLevel + 1,
	prettyPrintArgs(Args, NestingLevelPlusOne).

prettyPrintModel(error(Reason, Model), NestingLevel) :-
	!,
	write(error(Reason)),
	nl,
	NestingLevelPlusOne is NestingLevel + 1,
	indent(NestingLevelPlusOne),
	prettyPrintModel(Model, NestingLevelPlusOne).

prettyPrintModel(Model, _) :-
	write(Model),
	nl.
	
prettyPrintArgs([], _).
prettyPrintArgs([Name=Model | Rest], NestingLevel) :-
	indent(NestingLevel),
	write('+---'),
	write(Name),
	write(' = '),
	prettyPrintModel(Model, NestingLevel),
	prettyPrintArgs(Rest, NestingLevel).

indent(Num) :-
	Num > 0,
	!,
	write('|   '),
	NumMinus1 is Num - 1,
	indent(NumMinus1).
indent(0).

traceIn :-
	write('[').
traceIn :-
	write(']'),
	fail.

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

% A mechanism for executing targets upon loading
onLoad:-
	onLoadGoal(G),
	write(G), nl,
	G,
	fail.
onLoad.


onLoadGoal(compileSchema).

t1 :-
	bestComprehend([varDef(a, int), varDef(b, int), print(a+B*4)] : statementList/[], [B='B'], (Result->Score), []),
	write(Score), nl,
	prettyPrintModel(Result),
	reconstructTerm(Result, Term, Binding),
	write([Binding, Term]), nl.

%%%%%%%%%%%%%%%%%%%%% Transformations %%%%%%%%%%%%%%%%%%%%%%%%%%%%
declare([trans, name : atom/Ctx, sourceType : semanticType/Ctx, targetType : semanticType/Ctx, target : $(targetType)/transTarget($(name), $(sourceType))] : goal/Ctx).
declare(['@', member : atom/[], asType : semanticType/[]] : $(asType)/transTarget(Name, Type), 
	[addContext(Type, TypeWithCtx),
	parsedClass(Name, _ : TypeWithCtx, _, _, _, m(_, Args, _, _)),
	member($(member)=_, Args)]).

addContext(T/C, T/C) :-
	!.
addContext(T, T/_).


semanticType(vis).
declare([horiz, content : list(vis)] : vis).
declare([label, content : atom] : vis).
declare([expr, type : type] : semanticType).
declare([[]] : list(_)).
declare(['.', first : Type, rest : list(Type)] : list(Type)).

t2 :-
	bestComprehend(trans('+', expr(_), vis, horiz([a@vis, label('+'), b@vis])) : goal/[], [], (Result->Score), []),
	write(Score), nl,
	prettyPrintModel(Result).

% transform(SourceCM, TargetST, ResultTerm) - 
% 	transform the term represented by SourceCM into a term that can be comprehended as TargetST into ResultTerm

transform(m(Class : SourceST, Args, _, _), TargetST, Result) :-
	trans(Class, SourceST, TargetST, InitialResult),
	!,
	transformTerm(InitialResult, Args, Result).

transform(m((_ -> _) : _, [_=SubCM], _, _), TargetST, Result) :-
	!,
	transform(SubCM, TargetST, Result).

transform(Num, ST, TNum) :-
	number(Num),
	transNumber(Num, ST, TNum),
	!.

transform(Atom, _, TAtom) :-
	atom(Atom),
	transAtom(Atom, ST, TAtom),
	!.

transform(CM, TargetST, Error) :-
	transDefault(CM, TargetST, Error).

% transformTerm(InitialResult, Args, Result) -
%	Transform a term containing field@type recursively, using fields in Args.
transformTerm(Field@ST, Args, Result) :-
	!,
	member(Field=CM, Args),
	transform(CM, ST, Result).

transformTerm(TermIn, Args, Result) :-
	transMacro(TermIn, TermExp),
	!,
	transformTerm(TermExp, Args, Result).

transformTerm(Term, Args, Result) :-
	Term =.. [Func | TermArgs],
	transformTermList(TermArgs, Args, ResultArgs),
	Result =.. [Func | ResultArgs].

transformTermList([], _, []).
transformTermList([FirstIn | RestIn], Args, [FirstOut | RestOut]) :-
	transformTerm(FirstIn, Args, FirstOut),
	transformTermList(RestIn, Args, RestOut).

trans('+', expr(_)/_, vis, horiz([vis(a), label('+'), vis(b)])).
transAtom(Atom, vis, label(Atom)).
transNumber(Num, vis, label(Num)).
transMacro(vis(X), visualterm(X, X@vis)).
transDefault(m(Class : _, Args, _, _), vis, vert([label(Class) | VisArgs])) :-
	visualizeArgs(Args, VisArgs).
transDefault(error(undefined, Term : ST/_), vis, errorMarker(PrologTerm, horiz([label('Could not understand'), PrologTerm, label('as'), readOnly(STVis)]))) :-
	prologTerm(Term, PrologTerm),
	visualizeTerm(ST : semanticType/[], [], STVis).
transDefault(error(false(Cond), CM), vis, errorMarker(Vis, Error)) :-
	prologTerm(Cond, Error),
	?transform(CM, vis, Vis).
visualizeArgs([], []).
visualizeArgs([Name=CM | RestIn], [horiz([label(Name), label(':'), visualterm(Name, Vis)]) | RestOut]) :-
	transform(CM, vis, Vis),
	visualizeArgs(RestIn, RestOut).

% visualizeTerm(Term : SemanticType, Bindings, Visualization)
%	Performs the whole cycle of comprehention/transformation for visualization
visualizeTerm(TermAndSemanticType, Bindings, Visualization) :-
	bestComprehend(TermAndSemanticType, Bindings, (CM->_), []),
	transform(CM, vis, Visualization).

% prologTerm(Term, PrologTerm)
%	Transform Term into PrologTerm - a visualization expression that stands for Term in the Prolog syntax
prologTerm(Var, label('_')) :-
	var(Var).
prologTerm(Atom, label(Atom)) :-
	atom(Atom).
prologTerm(Num, label(Num)) :-
	number(Num).
prologTerm(Term, horiz([label(Func), label('('), horiz(ArgVis), label(')')])) :-
	Term =.. [Func | Args],
	prologArgList(Args, ArgVis).
prologArgList([Arg], [ArgVis]) :-
	!,
	prologTerm(Arg, ArgVis).
prologArgList([FirstArg | RestArgs], [FirstArgVis, label(',')  | RestArgsVis]) :-
	prologTerm(FirstArg, FirstArgVis),
	prologArgList(RestArgs, RestArgsVis).

t3 :-
	bestComprehend((1/(2+3)):expr(int)/[], [], (Result->Score), []),
	write(Score), nl,
	prettyPrintModel(Result),
	transform(Result, vis, R),
	write(R), nl.

t4 :-
	prologTerm((1/(2+3)), P),
	write(P), nl.

:-onLoad.

