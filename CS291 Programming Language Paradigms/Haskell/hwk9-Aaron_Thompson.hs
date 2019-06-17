--Aaron Thompson
--divide a list into a tuple containing it's two halves
halve :: [a] -> ([a], [a])
halve (x) = (take (length x `div` 2) x, drop (length x `div` 2) x)

--a reimplimentation of the product function
product' :: Num a => [a] -> a
product' [] = 1
product' [x] = x
product' (x:xs) = x * product' xs

--a version of tail that works with an empty list, implimented with conditionals, guards, and pattern matching
safetail :: [a] -> [a]
safetail t = if length t == 0 then t else tail t

safetail' :: [a] -> [a]
safetail' t | length t == 0 = t
            | otherwise = tail t

safetail'' :: [a] -> [a]
safetail'' [] = []
safetail'' t = tail t

--an implementation of the function replicate
replicate' :: Int -> a -> [a]
replicate' n x = [x | _ <- [1..n]]

--returns the scalar product of two lists
scalarproduct :: [Int] -> [Int] -> Int
scalarproduct xs ys = sum [x*y| (x, y) <- zip xs ys]