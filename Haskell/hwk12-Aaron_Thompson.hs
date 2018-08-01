--Aaron Thompson

--returns the sum of all the numbers in a tree
data Tree = Leaf Int | Node Tree Int Tree
sumtree :: Tree -> Int
sumtree (Leaf x) = x
sumtree (Node t1 x t2) = x + sumtree t1 + sumtree t2

--returns a boolean stating if a tree is balanced
data Tree2 = Leaf2 Int | Node2 Tree2 Tree2
balanced :: Tree2 -> Bool
balanced (Leaf2 x) = True
balanced (Node2 t1 t2) | abs (numleaves t1 - numleaves t2) <= 1 = balanced t1 && balanced t2
                       | otherwise = False

--returns the number of leaves on a tree
numleaves :: Tree2 -> Int
numleaves (Leaf2 x) = 1
numleaves (Node2 t1 t2) = numleaves t1 + numleaves t2 

