% Aaron Thompson %

% Math maze problem %
mathmaze(X) :- 
    permutation([1,2,3,4,5,6,7,8,9], [A,B,C,D,E,F,G,H,I]), 
    X = [A,B,C,D,E,F,G,H,I],
    66 is (A + ((13 * B) / C) + D + (12 * E) - F - 11 + ((G * H) / I) - 10). 
    
% Double a list %
double([], []).
double(List, DoubledList) :- 
    List = [H|T],
    DoubledList = [H,H|OT],
    double(T, OT).

% Takes in 3 lists and returns a list of ordered triples %
orderedTriples([], [], [], []).
orderedTriples([H1|T1], [H2|T2], [H3|T3], [H4|T4]) :- 
    InitialList = [H1, H2, H3],
    sort(InitialList, SortedList),
    H4 = SortedList,
    orderedTriples(T1, T2, T3, T4).

% removes duplicates from a list %
no_doubles([], []).
no_doubles([H|T], NewList) :-
    select(H, T, X),
    Head = [H],
    append(Head, X, NewList).

no_doubles([H|T], NewList) :-
    Head = [H],
    no_doubles(T, X),
    append(Head, X, NewList).


     
    
    
    

   
