package graph;
import util.PriorityQueue;
import java.util.List;

/**
 * Implements Dijkstras algorithm and the Floyd Warshall algorithm
 * @author Aaron Thompson and Braden Ash
 * @version 11/28/2018
 */
public class GraphAlgorithms {

    /**
     * Finds shortest path to nodes from a source node
     * @param graph the graph which contains the nodes
     * @param source the source node
     * @return an array containing the previous nodes to all of the nodes in the graph
     */
    public static int[] dijkstrasAlgorithm(Graph<Integer> graph, int source){
        PriorityQueue q = new PriorityQueue();
        Integer[] vertices = graph.getVertices().toArray(new Integer[graph.getVertices().size()]);

        int maxValue = vertices[0];
        for (int i = 0; i < vertices.length; i++){
            if (vertices[i] > maxValue){
                maxValue = vertices[i];
            }
        }

        int[] dist = new int[maxValue+1];
        int[] prev = new int[maxValue+1];
        for (int i = 0; i < prev.length; i++){
            prev[i] = 0;
        }

        for (int i = 0; i < dist.length; i++){
            dist[i] = Integer.MAX_VALUE;
        }

        for (int i = 0; i < vertices.length; i++){
            if(vertices[i] == source){
                dist[vertices[i]] = 0;
            }
        }

        for(int v = 0; v < vertices.length; v++){
            q.push(dist[vertices[v]], vertices[v]);
        }

        int[] noPath = {};
        while (!q.isEmpty()){
            int u =  q.topElement();
            q.pop();

            List adjList = graph.getNeighbors(u);
            for (int v = 0; v < adjList.size(); v++){
                int alt = dist[u] + 1;
                if (alt < dist[(int) adjList.get(v)]){
                    dist[(int) adjList.get(v)] = alt;
                    prev[(int) adjList.get(v)] = u;

                    if (!q.isPresent((int) adjList.get(v))){
                        return noPath;
                    }
                    q.changePriority((int) adjList.get(v), alt);
                }
            }
        }

        return prev;
    }

    /**
     * Returns a matrix containing all shortest paths between all nodes in the graphs
     * @param graph the graph where the shortest paths need to be found
     * @return a matrix containing the distances of the shortest paths
     */
    public static int[][] floydWarshall(Graph<Integer> graph){
        Integer[] gNodes = graph.getVertices().toArray(new Integer[graph.getVertices().size()]);
        int[][] adjMatrix = new int[gNodes.length][gNodes.length];

        for (int i = 0; i < gNodes.length; i++){
            for (int j = 0; j < gNodes.length; j++){
                if (i == j){
                    adjMatrix[i][j] = 0;
                }
                else if (graph.edgeExists(gNodes[i], gNodes[j])){
                    adjMatrix[i][j] = 1;
                }
                else{
                    adjMatrix[i][j] = Integer.MAX_VALUE;
                }
            }
        }

        for (int k = 0; k < gNodes.length; k++){
            for (int i = 0; i < gNodes.length; i++){
                for (int j = 0; j < gNodes.length; j++){
                    if (adjMatrix[i][k] != Integer.MAX_VALUE && adjMatrix[k][j] != Integer.MAX_VALUE){
                        adjMatrix[i][j] = Math.min(adjMatrix[i][j], adjMatrix[i][k] + adjMatrix[k][j]);
                    }
                }
            }
        }
        return adjMatrix;
    }

    private static void printMatrix(int[][] matrix){
        for (int i = 0; i < matrix.length; i++){
            System.out.println();
            for (int j = 0; j < matrix.length; j++){
                System.out.print("["+ matrix[i][j] + "]");
            }
        }
    }
}
