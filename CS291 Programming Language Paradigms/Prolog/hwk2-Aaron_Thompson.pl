% Aaron Thompson %
% prolog program representing a family %
male(homer).
male(wally).
male(ward).
male(ted).
male(tim).
male(tom).

female(marge).
female(june).
female(maggie).
female(lisa).
female(mary).
female(jenny).
female(kate).

spouse(marge, homer).
spouse(june, ward).
spouse(lisa, wally).
spouse(ted, mary).
spouse(homer, marge).
spouse(ward, june).
spouse(wally, lisa).
spouse(mary, ted).

parent(marge, maggie).
parent(marge, lisa).
parent(homer, maggie).
parent(homer, lisa).
parent(june, wally).
parent(june, ted).
parent(ward, wally).
parent(ward, ted).
parent(lisa, jenny).
parent(lisa, kate).
parent(wally, jenny).
parent(wally, kate).
parent(ted, tim).
parent(ted, tom).
parent(mary, tim).
parent(mary, tom).

father(Dad, Child) :- parent(Dad, Child), male(Dad).
mother(Mom, Child) :- parent(Mom, Child), female(Mom).

sibling(Sibling1, Sibling2):-
  parent(Parent, Sibling1),
  parent(Parent, Sibling2),
  Sibling1 \= Sibling2.

brother(Brother, Sib) :-
  sibling(Brother, Sib),
  male(Brother).

sister(Sister, Sib) :-
  sibling(Sister, Sib),
  female(Sister).

mother_in_law(MIL, Y) :-
  female(MIL),
  spouse(Y, X),
  parent(MIL, X).

daughter_in_law(DIL, X) :-
  female(DIL),
  spouse(DIL, Y),
  parent(X, Y).

brother_in_law(BIL, X) :-
  spouse(X, Y),
  brother(BIL, Y).
brother_in_law(BIL, X) :-
  male(BIL),
  sibling(Y, X),
  spouse(BIL, Y).
