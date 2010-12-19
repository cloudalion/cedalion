% Unit testing for Prolog programs
% Perform unit testing
unitTest(BaseName) :-
	loadFile(BaseName, test),
	loadFile(BaseName, result),
	runTests.

% Build a result file
buildResults(BaseName) :-
	loadFile(BaseName, test),
	atom_concat([BaseName, '.', result], ResultFileName),
	open(ResultFileName, write, Stream),
	runBuildResults(Stream),
	close(Stream).

% Run unit tests once the tests and results are loaded
runTests:-
	unitTest(TestName, Goal, ResultPattern),
	runTest(TestName, Goal, ResultPattern),
	fail.
runTests.

% Run a single test
runTest(TestName, Goal, ResultPattern):-
	write(TestName), write('... '),
	findall(ResultPattern, Goal, Results),
	testResult(TestName, Results),
	write('[PASS]'), nl,
	!.
runTest(_, _, _) :-
	write('[FAIL]'), nl.

% Load the tests and results for the given test-suite name
loadFile(BaseName, Ext) :-
	atom_concat([BaseName, '.', Ext], FileName),
	consult(FileName).

% Run the tests and write the results to a stream
runBuildResults(Output) :-
	unitTest(TestName, Goal, ResultPattern),
	write(TestName), write('... '),
	findall(ResultPattern, Goal, Results),
	write_term(Output, testResult(TestName, Results), [quoted(true)]),
	write(Output, '.\n'),
	write('[DONE]'), nl,
	fail.
runBuildResults(_).

