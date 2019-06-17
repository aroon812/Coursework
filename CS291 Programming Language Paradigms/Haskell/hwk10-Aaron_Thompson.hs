--Aaron Thompson
--multiply 3 integers using lambda expressions
mult :: Integer -> Integer -> Integer -> Integer
mult = \x -> \y -> \z -> x * y * z

--insert an element into a list at a specific index
insertAt :: a -> [a] -> Int -> [a]
insertAt e l i = take (i-1) l ++ [e] ++ drop (i-1) l

--remove doubles in a list
remove_doubles :: Eq a => [a] -> [a]
remove_doubles [] = []
remove_doubles (x:xs) = if elem x xs then remove_doubles xs else [x] ++ remove_doubles xs

--produce a list of pairs from a list which add to a given number
sumpairs :: (Eq a, Num a) => [a] -> a -> [(a, a)]
sumpairs [] _ = []
sumpairs (x:xs) num = if elem (num-x) xs then [(x, num-x)] ++ sumpairs xs num else sumpairs xs num


