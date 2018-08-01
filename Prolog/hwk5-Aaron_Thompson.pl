% Aaron Thompson %
% this program solves the international hobbyists puzzle detailed in the comment below %
/**
There are 5 houses in five different colors.

In each house lives a person with a different nationality.

These five homeowners each listen to a certain type of music, enjoy a certain hobby and like a certain food.

No two homeowners listen to the same music, have the same hobby, or like the same food.

The question is: Who likes pie?

The Irish person lives next to the yellow house.

The person who likes donuts lives next to the person who enjoys embroidery.

The person who enjoys train spotting lives next to the one who likes pizza.

The person who enjoys pachinko likes beets.

The Czech lives in the purple house.

The person living in the center house listens to grunge.

The person who enjoys glassblowing listens to hip hop.

The American likes tofu.

The Dutch person listens to J-Pop.

The pink house's owner listens to country.

The owner of the green house enjoys embroidery.

The Irish person lives in the first (leftmost) house.

The pink house is on the left of the orange house.

The Japanese person enjoys gardening.

The person who enjoys train spotting has a next-door neighbor who listens to jazz.
*/
pie_eater(PieEater) :-
            intl_hobbyists(X),
            member(house(PieEater, _, _, _, pie), X).

intl_hobbyists(Street) :- 
            Street = [H1, H2, H3, H4, H5],
             
            H1 = house(Nat1, Col1, Mus1, Hob1, Food1),
            H2 = house(Nat2, Col2, Mus2, Hob2, Food2),
            H3 = house(Nat3, Col3, Mus3, Hob3, Food3),
            H4 = house(Nat4, Col4, Mus4, Hob4, Food4),
            H5 = house(Nat5, Col5, Mus5, Hob5, Food5),
            
            length(Street, 5),
            next_to(house(irish, _, _, _, _), house(_, yellow, _, _, _), Street),
            next_to(house(_, _, _, _, donuts), house(_, _, _, embroidery, _), Street),
            next_to(house(_, _, _, trainspotting, _), house(_, _, _, _, pizza), Street),
            member(house(_, _, _, pachinko, beets), Street),
            member(house(czech, purple, _, _, _), Street),
            H3 = house(Nat3, Col3, grunge, Hob3, Food3),
            member(house(_, _, hip_hop, glassblowing, _), Street),
            member(house(american, _, _, _, tofu), Street),
            member(house(dutch, _, j-pop, _, _), Street),
            member(house(_, pink, country, _, _), Street),
            member(house(_, green, _, embroidery, _), Street),
            H1 = house(irish, Col1, Mus1, Hob1, Food1),
            left_of(house(_, pink, _, _, _), house(_, orange, _, _, _), Street),
            member(house(japanese, _, _, gardening, _), Street),
            next_to(house(_, _, jazz, _, _), house(_, _, _, trainspotting, _), Street).
            
            left_of(X, Y, List) :- nth0(Index, List, X), nth0(Index2, List, Y), Index is Index2-1.
            right_of(X, Y, List) :- nth0(Index, List, X), nth0(Index2, List, Y), Index is Index2+1.

            next_to(X, Y, List) :- left_of(X, Y, List).
            next_to(X, Y, List) :- right_of(X, Y, List).
             

          

           
