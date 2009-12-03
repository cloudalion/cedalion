% Text loader for cedalion
:- [util].
:- op(1150, fx, unit).
:- op(1150, fx, def).
:- op(1050, xfx, '=>').
:- op(900, xfx, '--->').

% loadUnit(Stream, Unit, Varnames, Location):
%	Read a whole unit or a single term from Stream.
loadUnit(Stream, OutputTerm, Varnames, Location) :-
	read_term(Stream, Term, [variable_names(VarnamesIn)]),
	stream_property(Stream, position(Location)),
	processNextTerm(Term, Stream, OutputTerm, VarnamesIn, Varnames).

% processNextTerm(Term, Stream, OutputTerm, VarnamesIn, Varnames):
%	Reads a unit up to its end.  for non-units, does nothing.
processNextTerm(end_of_file, _, end_of_file, [], []) :-
	!.
processNextTerm(unit(Head), Stream, unit(Head, Clauses), VarnamesIn, VarnamesOut) :-
	!,
	read_term(Stream, First, [variable_names(VarnamesFirst)]),
	append(VarnamesIn, VarnamesFirst, VarnamesMid),
	readUnitBody(First, Stream, Clauses, VarnamesMid, VarnamesOut).
processNextTerm(Term, Stream, Term, Varnames, Varnames).

% readUnitBody(Stream, Clauses, VarnamesIn, VarnamesOut):
%	Read the body of a unit into Clauses.
readUnitBody(end, _, [], Varnames, Varnames) :-
	!.
readUnitBody(First, Stream, [First | Clauses], VarnamesIn, VarnamesOut) :-
	read_term(Stream, Second, [variable_names(VarnamesSecond)]),
	append(VarnamesIn, VarnamesSecond, VarnamesMid),
	readUnitBody(Second, Stream, Clauses, VarnamesMid, VarnamesOut).

