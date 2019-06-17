--Aaron Thompson

import Data.List (tails)
import Data.Char 

--returns every combination of a given length from a given list
combinations :: Int -> [a] -> [[a]]
combinations 0 _ = [[]]
combinations n l = [(x:t) | (x:xs) <- tails l, t <- combinations (n-1) xs] 

--takes a list of ints and turns them into one number
dec2int :: [Int] -> Int
dec2int l = foldl (\multiplier dec -> multiplier*10 + dec) 0 l

--takes in a base and a string representing a number in that base, returns the number in base 10
base2int :: Int -> String -> Int
base2int _ [] = 0
base2int b (x:xs) = (b^(length (x:xs)-1) * digitToInt x) + base2int b xs

