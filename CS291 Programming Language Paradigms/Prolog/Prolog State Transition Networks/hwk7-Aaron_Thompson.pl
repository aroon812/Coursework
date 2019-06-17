% state transition networks %
test(TestNetFileName) :-
  [TestNetFileName],
  write("Red edge sequences:"), nl,
  bagof(X, red_edge_sequence(X), Xs),
  writeOut(Xs), nl,
  write("Alternating edge sequences:"), nl,
  bagof(Y, alternating_edge_sequence(Y), Ys),
  writeOut(Ys), nl,
  write("Matching edge/state sequences:"), nl,
  bagof(Z, matching_edge_state_sequence(Z), Zs),
  writeOut(Zs), nl.
  
writeOut([]).
writeOut([H|T]) :-
  write(H), nl,
  writeOut(T). 

% red_edge_sequence/1
red_edge_sequence([H|T]) :- 
  red_chain(End, [H|T]),
  start_state(H),
  end_state(End).

red_chain(_End, [H1, H2|T]) :-
  trans(H1, H2, red),
  red_chain(_End, [H2|T]).
red_chain(End, [End|[]]) :-
  end_state(End), 
  trans(_Prev, End, red), !.

% alternating_edge_sequence/1
alternating_edge_sequence([H|T]):-
  alternating_chain(End, _color, [H|T]),
  start_state(H),
  end_state(End).

alternating_chain(_End, Color, [H1, H2|T]) :-
  trans(H1, H2, Color),
  opposite(Color, OtherColor),
  alternating_chain(_End, OtherColor, [H2|T]).
alternating_chain(End, Color, [End|[]]) :-
  end_state(End).

opposite(green, red).
opposite(red, green).

% matching_edge_state_sequence/1
matching_edge_state_sequence([H|T]):-
  matching_chain(End, _Color, [H|T]),
  start_state(H),
  end_state(End).

matching_chain(_End, Color, [H1, H2|T]) :-
  trans(H1, H2, Color),
  state(H1, Color),
  state(H2, Color),
  matching_chain(_End, Color, [H2|T]).
matching_chain(End, Color, [End|[]]) :-
  end_state(End), 
  state(End, Color),
  trans(_Prev, End, Color), !.