:- op(1000, xfy, then).
:- op(1000, xfy, else).
 
myLength(List, Length) :-
	if(List == []) then {
		Length = 0
	} else {
		List = [_|Shorter],
		myLength(Shorter, ShorterLength),
		Length is ShorterLength + 1
	}.

classify(Term, Class) :-
	if(atom(Term)) then {
		Class = atom
	} else if(var(Term)) then {
		Class = var
	} else if(number(Term)) then {
		Class = number
	} else {
		if(Term =.. [_, _]) then {
			Class = termWithOneArg
		} else {
			Class = termWithMoreThanOneArg			
		}
	}.
if(Cond) then {Then} else ElseBlock :-
	Cond,
	!,	% The last cut
	Then,
	afterElse(ElseBlock).

if(_) then {_} else ElseBlock :-
	doElse(ElseBlock),
	afterElse(ElseBlock).

doElse({Else}) :-
	Else.
doElse((if(Cond) then ThenBlock)) :-
	if(Cond) then ThenBlock.
doElse((ElseBlock, _)) :-
	doElse(ElseBlock).

afterElse({_}).
afterElse((if(_) then _)).
afterElse((_, After)) :-
	After.

