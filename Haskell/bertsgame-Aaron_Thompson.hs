--Aaron Thompson
--solves the bert bos puzzle shown here: http://mathcs.pugetsound.edu/~tmullen/pages/bertspel/

import Prelude hiding (flip)
data Color = Red | Blue deriving (Eq, Show)
data Click = Click | NoClick deriving (Eq, Show)
type Board = [[Color]]

--solve the bert bos puzzle
bertsgame :: Int -> [[Click]]
bertsgame n = generateSolutions n (generateBoard n)

--creates all permutations of click and no click based on the length each row should be
clicklists :: Int -> [[Click]]
clicklists num = [x | x <- sequence (replicate num [Click, NoClick])]

--uses generateRow to create a new, entierly blue board
generateBoard :: Int -> [[Color]]
generateBoard n = replicate n (generateRow n)

--generate each row
generateRow :: Int -> [Color]
generateRow n = replicate n Blue 

--flips a single tile
flip :: Color -> Color
flip c = if c == Red then Blue else Red

--generates a permutation of clicks and no clicks, tests the permutation, and adds it to the list of solutions
generateSolutions :: Int -> Board -> [[Click]]
generateSolutions n b = [y| y <- clicklists n, allRed (flipBoard y b)]

--flips tiles on a board based on a list of clicks
flipBoard :: [Click] -> Board -> [Color]
flipBoard ys [x] = flipCurrentRowFirst ys x
flipBoard ys (x1:x2:xs) = flipBoard (newClickList (flipCurrentRowFirst ys x1)) ((nextRow ys x2):xs)

--makes a click list for a row based on the blue tiles on the row above
newClickList :: [Color] -> [Click]
newClickList [] = []
newClickList (Blue:xs) = [Click] ++ newClickList xs
newClickList (Red:xs) = [NoClick] ++ newClickList xs

--function that flips the first two tiles
flipCurrentRowFirst :: [Click] -> [Color] -> [Color]
flipCurrentRowFirst [Click] [x] = [flip x]
flipCurrentRowFirst [NoClick] [x] = [x]
flipCurrentRowFirst (Click:ys) (x1:x2:xs) = flipCurrentRowNext ys ((flip x1):(flip x2):xs)
flipCurrentRowFirst (NoClick:ys) xs = flipCurrentRowNext ys xs 

--function that flips three tiles at a time
flipCurrentRowNext :: [Click] -> [Color] -> [Color]
flipCurrentRowNext (Click:ys) [x1, x2] = [flip x1, flip x2]
flipCurrentRowNext (NoClick:ys) [x1, x2] = [x1, x2]
flipCurrentRowNext (Click:ys) (x1:x2:x3:xs) = [flip x1] ++ flipCurrentRowNext ys ((flip x2):(flip x3):xs)
flipCurrentRowNext (NoClick:ys) (x:xs) = [x] ++ flipCurrentRowNext ys (xs)

--flipping the row under the row being clicked is easier, 
--because you only need to flip the tiles that correspond directly to the clicks
nextRow :: [Click] -> [Color] -> [Color]
nextRow [] [] = []
nextRow (Click:ys) (x:xs) = [flip x] ++ nextRow ys xs
nextRow (NoClick:ys) (x:xs) = [x] ++ nextRow ys xs

--checks to see if a row is all red
allRed :: [Color] -> Bool
allRed [x] = x == Red
allRed (x:xs) = x == Red && allRed xs







                                  
