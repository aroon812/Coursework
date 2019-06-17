--Aaron Thompson
--state transition network assignment similar to the version in the prolog folder

import Data.List -- for using nub

-- Some useful custom types
data Color  = Red   | Green deriving (Eq, Show) -- Represents red and green colors
data Alt    = Alt   | NoAlt  -- Indicates whether the path should alternate colors or not
data Match  = Match | NoMatch -- Indicates whether state colors must match transition colors
type StateName  = Char -- We'll use characters as state names
type State      = (StateName, Color)  -- A state has a name and a color
type Trans      = (StateName, Color, StateName) -- A transition has two state names and a color
type Path       = [StateName] -- A path is a list of state names
type Network    = ([State], [State], [State], [Trans]) -- A full network: starts, finals, ftates, fransitions

-- In order to print out the solution we need to make use of the IO monad. We'll talk 
-- about this further in class, but for now it's enough to know that it's necessary to
-- do I/O such as printing our results to the console. 
{-  -}
main :: IO ()
main = do
  putStrLn "Test Network 1:"
  showSolution testNetwork1 -- Call showSolution on the first network
  putStrLn "Test Network 2:"
  showSolution testNetwork2 -- Call showSolution on the second network

-- We take the network as an argument and call the required functions. Once again
-- we use an IO type. You don't need to touch this code either. 
showSolution :: Network -> IO ()
showSolution nw = 
  do
    putStrLn "Red edge sequences:"
    let reds = red_edge_sequence nw -- produces red edge sequences
    print reds
    putStrLn "Alternating edge sequences:"
    let alts = alternating_edge_sequence nw -- produces alternating edge sequences
    print alts
    putStrLn "Matching edge/state sequences:"
    let matches = matching_edge_state_sequence nw -- produces matching state/edge sequences
    print matches
    putStr "\n"

red_edge_sequence :: Network -> [Path] 
red_edge_sequence = color_edge_sequence Red NoAlt NoMatch

alternating_edge_sequence :: Network -> [Path] 
alternating_edge_sequence nw = 
  (color_edge_sequence Green Alt NoMatch nw) ++  -- alternating edge sequences should include
  (color_edge_sequence Red Alt NoMatch nw)       -- those that start with both green and red states

matching_edge_state_sequence :: Network -> [Path] 
matching_edge_state_sequence nw = 
  (color_edge_sequence Green NoAlt Match nw) ++  -- matching edge state sequences should include
  (color_edge_sequence Red NoAlt Match nw)       -- those that start with both green and red states

-- =============================================================================
-- BEGIN CHANGES: The next eight declared functions need to be implemented by you.
-- Type declarations are as they should be, but the functions need to be defined.
-- It is not necessary to define any other functions than
-- the ones shown here. Use the functions defined further below as necessary.  

-- Generates the list of all paths. Arguments determine parameters of the paths 
-- (alternating, matched state colors). Use a list comprehension to collect the 
-- results of calling forkAllPaths on an initial list of paths consisting of 
-- start states only. Ensure that the paths included are full paths that reach
-- a final state. Remove duplicates as necessary with `nub`

color_edge_sequence :: Color -> Alt -> Match -> Network -> [Path] 
color_edge_sequence c a m nw = nub [x | x <- forkAllPaths a m nw (initPathList (startNames nw)) c []]

forkAllPaths :: Alt -> Match -> Network -> [Path] -> Color -> [Path] -> [Path]
forkAllPaths _ _ _ [] _ path2 = path2
forkAllPaths a m nw path1 c path2 = nub (forkAllPaths a m nw paths (nextColor c a) (path2 ++ [pathf | pathf <- paths, reachesFinal nw pathf]))
                                  where paths = (forkPaths m nw path1 (nextColor c a))

forkPaths :: Match -> Network -> [Path] -> Color -> [Path]
forkPaths m nw path c = foldl (\acc path -> acc ++ forkPath m nw path c) [] path

forkPath :: Match -> Network -> Path -> Color -> [Path]
forkPath m nw path c = [path ++ [x] | x <- nexts m nw (last path) c]

nexts :: Match -> Network -> StateName -> Color -> [StateName]
nexts _ (_, _, _, []) _ _ = []
nexts Match nw@(starts, finals,  states, ((s1, t, s2):xs)) sn c | sn == s1 && colorMatch Match nw s1 s2 c = [s2] ++ nexts Match (starts, finals,  states, (xs)) sn c
                                                                | otherwise = nexts Match (starts, finals,  states, (xs)) sn c
nexts NoMatch nw@(starts, finals,  states, ((s1, t, s2):xs)) sn c | sn == s1 && t == c = [s2] ++ nexts NoMatch (starts, finals,  states, (xs)) sn c
                                                                  | otherwise = nexts NoMatch (starts, finals,  states, (xs)) sn c

initPathList :: [StateName] -> [Path]
initPathList l = [[x] | x <- l]

nextColor :: Color -> Alt -> Color
nextColor c Alt = if c == Green then Red else Green
nextColor c NoAlt = c

colorMatch :: Match -> Network -> StateName -> StateName -> Color -> Bool
colorMatch NoMatch _ _ _ _ = True
colorMatch Match (_, _, states, transitions) s1 s2 c = if (s1, c) `elem` states 
                                                       && (s2, c) `elem` states 
                                                       && (s1, c, s2) `elem` transitions then True else False

-- END CHANGES: The functions defined below this line do not need to be altered. 
-- They are for use in implementing the functions above. 
-- =============================================================================

-- Checks whether the path ends on a final state
reachesFinal :: Network -> Path -> Bool
reachesFinal inNetwork path = (last path) `elem` (finalNames inNetwork)

-- Returns a list of final state names given a network
finalNames :: Network -> [StateName]
finalNames (_, finals,  _, _) = fmap getName finals

-- Returns a list of start state names given a network
startNames :: Network -> [StateName]
startNames (starts, _,  _, _) = fmap getName starts

-- Returns the name of a state
getName :: State -> StateName
getName (statename, _) = statename

-- DATA:
-- Below two test networks are defined which correspond to the graphs shown on 
-- the assignment page. You don't need to change anything here. The networks are
-- refered to by name in the main function call, but they should not be refered 
-- to by name anywhere else.
-- =============================================================================
testNetwork1 :: Network 
testNetwork1 = (
     [('A', Green), ('B', Red)],
     [('J', Green), ('K', Red)],
     [
       ('A', Green), ('B', Red), ('C', Red), ('D', Red),
       ('E', Green), ('F', Green), ('G', Green), ('H', Green),
       ('I', Red), ('J', Green), ('K', Red)
     ],
     [
       ('A', Green, 'C'), ('A', Green, 'F'), ('A', Green, 'D'),
       ('B', Green, 'D'), ('B', Red, 'D'),
       ('D', Red, 'F'), ('D', Green, 'F'),('D', Red, 'E'), ('D', Red, 'I'),
       ('F', Green, 'G'), ('F', Red, 'H'),
       ('E', Green, 'I'),
       ('G', Green, 'H'), ('G', Red, 'J'),
       ('H', Green, 'J'), ('H', Red, 'K'), ('H', Red, 'I'),
       ('I', Red, 'K')
     ]
   )
-- Expected outcome for this network
-- Red edge sequences:
-- ["BDIK","BDFHK","BDFHIK"]
-- Alternating edge sequences:
-- ["AFHJ","ADFGJ","ADEIK","BDFGJ","BDEIK","BDFHJ"]
-- Matching edge/state sequences:
-- ["AFGHJ","BDIK"]

testNetwork2 :: Network 
testNetwork2 = (
     [('A', Green), ('B', Red)],
     [('K', Green), ('L', Red)],
     [
       ('A', Green), ('B', Red), ('C', Red), ('D', Red),
       ('E', Green), ('F', Green), ('G', Green), ('H', Green),
       ('I', Green), ('J', Red), ('K', Green), ('L', Red)
     ],
     [
       ('A', Red, 'C'), ('A', Green, 'F'), ('A', Green, 'D'),
       ('B', Green, 'D'), ('B', Red, 'D'),
       ('C', Red, 'F'), 
       ('D', Red, 'E'), ('D', Red, 'G'), ('D', Green, 'F'), 
       ('G', Red, 'I'),
       ('F', Red, 'H'), ('F', Green, 'I'), 
       ('E', Green, 'J'),
       ('H', Green, 'I'), ('H', Red, 'K'),
       ('I', Green, 'K'), ('I', Red, 'L'), ('I', Red, 'J'),
       ('J', Red, 'L')
     ]
   )
-- Expected outcome for this network
-- Red edge sequences:
-- ["ACFHK","BDGIL","BDGIJL"]
-- Alternating edge sequences:
-- ["AFHIL","ADEJL","BDEJL","BDFHIL"]
-- Matching edge/state sequences:
-- ["AFIK"]