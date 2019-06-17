package analyzer;

import data.Movie;
import graph.Graph;
import graph.GraphAlgorithms;
import util.DataLoader;
import java.util.*;

/**
 * Takes in two spreadsheets containing movie and user information, and creates a graph which can then be explored.
 * @author Aaron Thompson and Braden Ash
 * @version 11/28/2018
 */
public class MovieLensAnalyzer {

    /**
     * Loads the data from the two movie files
     * @param movieFileName the file containing movie information
     * @param reviewFileName the file containing reviewer information
     * @return an object with all of the movie information
     */
    public static DataLoader loadFiles(String movieFileName, String reviewFileName){
        DataLoader movieData = new DataLoader();
        movieData.loadData(movieFileName, reviewFileName);
        return movieData;
    }

    private static int adjacencyOption(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("There are 3 choices for defining adjacency: ");
        System.out.println("[Option 1] u and v are adjacent if the same 12 users gave the same rating to both movies");
        System.out.println("[Option 2] u and v are adjacent if the same 12 users watched both movies (regardless of rating)");
        System.out.println("[Option 3] u and v are adjacent if at least 33.0% of the users that rated u gave the same rating to v");
        System.out.println();
        System.out.print("Choose an option to build the graph (1-3): ");
        return scanner.nextInt();
    }

    private static int analysisOption(){
        Scanner scanner = new Scanner(System.in);
        System.out.println();
        System.out.println("[Option 1] Print out statistics about the graph");
        System.out.println("[Option 2] Print node information");
        System.out.println("[Option 3] Display shortest path between two nodes");
        System.out.println("[Option 4] Quit");
        System.out.print("Choose an option (1-4): ");
        return scanner.nextInt();
    }

    /**
     * The method that builds a graph of the movies
     * @param choice the choice of the program's prompt that asks how to build the graph
     * @param data the movie data
     * @return the complete graph
     */
	public static Graph buildGraph(int choice, DataLoader data){
        Graph<Integer> graph = new Graph<Integer>();
        Map movies = data.getMovies();

        for (Object movie : movies.keySet()){
            graph.addVertex((Integer) movie);
        }

        Set<Integer> vertices = graph.getVertices();
        System.out.print("Creating graph...");

        if (choice == 1){

            for (Integer m1 : vertices){
                for (Integer m2 : vertices){
                    int sameReviewers = 0;
                    if (!m1.equals(m2)){
                        Movie movie1 = (Movie) movies.get(m1);
                        Movie movie2 = (Movie) movies.get(m2);

                        for (Integer userId : movie1.getRatings().keySet()){
                            if (movie2.rated(userId)){
                                if (movie1.getRating(userId) == movie2.getRating(userId)){
                                    sameReviewers++;
                                }
                            }
                        }
                        if (sameReviewers >= 12 && !graph.edgeExists(m1,m2)){
                            graph.addEdge(m1, m2);
                            graph.addEdge(m2, m1);
                        }
                    }
                }

            }
        }

        else if (choice == 2){

            for (Integer m1 : vertices){
                for (Integer m2 : vertices){
                    int sameReviewers = 0;
                    if (!m1.equals(m2)){
                        Movie movie1 = (Movie) movies.get(m1);
                        Movie movie2 = (Movie) movies.get(m2);

                        for (Integer userId : movie1.getRatings().keySet()){
                            if (movie2.rated(userId)){
                                sameReviewers++;
                            }
                        }
                        if (sameReviewers >= 12 && !graph.edgeExists(m1,m2)){
                            graph.addEdge(m1, m2);
                            graph.addEdge(m2, m1);
                        }
                    }
                }

            }
        }

        else if (choice == 3){

            for (Integer m1 : vertices){
                for (Integer m2 : vertices){
                    int sameReviewers = 0;
                    if (!m1.equals(m2)){
                        Movie movie1 = (Movie) movies.get(m1);
                        Movie movie2 = (Movie) movies.get(m2);

                        for (Integer userId : movie1.getRatings().keySet()){
                            if (movie2.rated(userId)){
                                if (movie1.getRating(userId) == movie2.getRating(userId)){
                                    sameReviewers++;
                                }
                            }
                        }
                        if (sameReviewers > (movie1.getRatings().size()/3) && !graph.edgeExists(m1,m2)){
                            graph.addEdge(m1, m2);
                        }
                    }
                }

            }
        }

        System.out.println("The graph has been created.");
        return graph;
    }

    /**
     * Allows the user to explore the graph
     * @param specificChoice the choice the user makes when prompted to manupilate the graph
     * @param g the graph being explored
     * @param data the movie data
     */
    public static void exploreGraph(int specificChoice, Graph g, DataLoader data){
        System.out.println();
        Scanner scanner = new Scanner(System.in);
        if (specificChoice == 1){
            printGraphInformation(g);
        }
        else if (specificChoice == 2){
            System.out.print("Enter movie id (1-1000): ");
            int nodeNumber = scanner.nextInt();
            printNodeInformation(nodeNumber, data, g);
        }
        else if (specificChoice == 3){
            System.out.print("Enter starting node (1-1000): ");
            int startNode = scanner.nextInt();
            System.out.print("Enter ending node (1-1000): ");
            int endNode = scanner.nextInt();
            shortestPath(startNode, endNode, g, data);
        }
        else if (specificChoice == 4){
            System.out.println("Exiting...bye");
            System.exit(0);
        }
    }

    private static void shortestPath(int start, int end, Graph g, DataLoader data){

        System.out.println();
        int[] pathArray = GraphAlgorithms.dijkstrasAlgorithm(g, start);
        if (pathArray.length == 0){
            System.out.println("No Path Exists between node " + start + " and node " + end +".");
            return;
        }

        Map movies = data.getMovies();
        ArrayList<Integer> path = new ArrayList<Integer>();
        path.add(end);

        while (path.get(0) != start && path.size() < g.numVertices()){
            path.add(0, pathArray[path.get(0)]);
        }

        Movie movie1;
        Movie movie2;
        for (int i = 0; i < path.size()-1; i++){
            movie1 = (Movie) movies.get(path.get(i));
            movie2 = (Movie) movies.get(path.get(i+1));
            System.out.println(movie1.getTitle() + " ===> " + movie2.getTitle());
        }
    }

    private static void printGraphInformation(Graph g){
        int numberOfVertices = g.numVertices();
        int numberOfEdges = g.numEdges();
        double density = (double) numberOfEdges / ((double) numberOfVertices* (double) (numberOfVertices-1));
        int maxDegree = 0;
        int maxDegreeNode = 0;
        for (Object movie : g.getVertices()){
            if (maxDegree < g.degree(movie)){
                maxDegree = g.degree(movie);
                maxDegreeNode = (int) movie;
            }
        }

        int[][] shortestPaths = GraphAlgorithms.floydWarshall(g);
        int diameter = 0;
        int pathLengths = 0;
        int numPaths = 0;
        int startNode = 0;
        int endNode = 0;
        for (int i = 0; i < shortestPaths.length; i++){
            for (int j = 0; j < shortestPaths[i].length; j++){
                pathLengths += shortestPaths[i][j];
                numPaths++;
                if(diameter < shortestPaths[i][j] && shortestPaths[i][j] != Integer.MAX_VALUE){
                    diameter = shortestPaths[i][j];
                    startNode = i;
                    endNode = j;
                }
            }
        }

        System.out.println("|V| =  " + numberOfVertices + " vertices");
        System.out.println("|E| =  " + numberOfEdges + " edges");
        System.out.println("Density = " + density);
        System.out.println("Max. degree = " + maxDegree + " (node " + maxDegreeNode + ")");
        System.out.println("Diameter = " + diameter + " (from " + startNode + " to " + endNode + ")");
        System.out.println("Average Length = " + (pathLengths/numPaths));
    }

    private static void printNodeInformation(int nodeNumber, DataLoader data, Graph graph){
        Map movies = data.getMovies();
        Movie node = (Movie) movies.get(nodeNumber);
        System.out.println(node);
        System.out.println("Neighbors: ");

        List neighbors = graph.getNeighbors(nodeNumber);
        for (int i = 0; i < neighbors.size(); i++){
            Movie movie = (Movie) movies.get(neighbors.get(i));
            System.out.println( "\t" + movie.getTitle());
        }
    }

	public static void main(String[] args){

		if(args.length != 2){
			System.err.println("Usage: java MovieLensAnalyzer [ratings_file] [movie_title_file]");
			System.exit(-1);
		}

        DataLoader data = loadFiles(args[1], args[0]);
		int choice = adjacencyOption();
		Graph g = buildGraph(choice, data);

		while (true) {
            int specificChoice = analysisOption();
            exploreGraph(specificChoice, g, data);
        }

	}
}
