import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A class representing a graph
 * 
 * @author Aaron Thompson, Braden Ash
 */
public class Graph<V> implements GraphIfc<V> {
    private Map<V, List<V>> adjList;

    /**
     * constructs an empty graph
     */
    public Graph() {
        adjList = new HashMap<V, List<V>>();
    }

    /**
     * Returns the number of vertices in the graph
     * 
     * @return The number of vertices in the graph
     */
    public int numVertices() {
        return adjList.size();
    }

    /**
     * Returns the number of edges in the graph
     * 
     * @return The number of edges in the graph
     */
    public int numEdges() {
        int edges = 0;
        Iterator<V> kSet = adjList.keySet().iterator();
        while (kSet.hasNext()) {
            edges += adjList.get(kSet.next()).size();
        }
        return edges;
    }

    /**
     * Removes all vertices from the graph
     */
    public void clear() {
        adjList.clear();
    }

    /**
     * Adds a vertex to the graph. This method has no effect if the vertex already
     * exists in the graph.
     * 
     * @param v The vertex to be added
     */
    public void addVertex(V v) {
        if (adjList.containsKey(v)) {
            return;
        }
        adjList.put(v, new ArrayList<V>());
    }

    /**
     * Adds an edge between vertices u and v in the graph.
     * 
     * @param u A vertex in the graph
     * @param v A vertex in the graph
     * @throws IllegalArgumentException if either vertex does not occur in the
     *                                  graph.
     */
    public void addEdge(V u, V v) {
        if (!adjList.containsKey(u) || !adjList.containsKey(v)) {
            throw new IllegalArgumentException("one of the specified vertices does not exist in the graph.");
        }

        adjList.get(u).add(v);
    }

    /**
     * Returns the set of all vertices in the graph.
     * 
     * @return A set containing all vertices in the graph
     */
    public Set<V> getVertices() {
        return adjList.keySet();
    }

    /**
     * Returns the neighbors of v in the graph. A neighbor is a vertex that is
     * connected to v by an edge. If the graph is directed, this returns the
     * vertices u for which an edge (v, u) exists.
     * 
     * @param v An existing node in the graph
     * @return All neighbors of v in the graph.
     */
    public List<V> getNeighbors(V v) {
        return adjList.get(v);
    }

    /**
     * Determines whether the given vertex is already contained in the graph. The
     * comparison is based on the <code>equals()</code> method in the class V.
     * 
     * @param v The vertex to be tested.
     * @return True if v exists in the graph, false otherwise.
     */
    public boolean containsNode(V v) {
        return adjList.containsKey(v);
    }

    /**
     * Determines whether an edge exists between two vertices. In a directed graph,
     * this returns true only if the edge starts at v and ends at u.
     * 
     * @param v A node in the graph
     * @param u A node in the graph
     * @return True if an edge exists between the two vertices
     * @throws IllegalArgumentException if either vertex does not occur in the graph
     */
    public boolean edgeExists(V v, V u) {
        if (!adjList.containsKey(u) || !adjList.containsKey(v)) {
            throw new IllegalArgumentException("one of the specified vertices does not exist in the graph.");
        }
        return adjList.get(v).contains(u);
    }

    /**
     * Returns the degree of the vertex. In a directed graph, this returns the
     * outdegree of the vertex.
     * 
     * @param v A vertex in the graph
     * @return The degree of the vertex
     * @throws IllegalArgumentException if the vertex does not occur in the graph
     */
    public int degree(V v) {
        if (!adjList.containsKey(v)) {
            throw new IllegalArgumentException("The specified vertex does not exist in the graph.");
        }

        int degree = 0;
        Iterator<V> kSet = adjList.keySet().iterator();
        while (kSet.hasNext()) {
            List<V> list = adjList.get(kSet.next());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == v) {
                    degree++;
                }
            }
        }
        return degree;
    }

    /**
     * Returns a string representation of the graph. The string representation shows
     * all vertices and edges in the graph.
     * 
     * @return A string representation of the graph
     */
    public String toString() {
        String stringBuilder = "";
        Iterator<V> kSet = adjList.keySet().iterator();
        while (kSet.hasNext()) {
            V key = kSet.next();
            stringBuilder += key + ": " + listToString(adjList.get(key)) + "\n";
        }
        return stringBuilder;

    }

    /**
     * Returns a string representation of the adjacency list of an individual vertex
     * 
     * @param list the adjacency list of the vertex
     * @return A string representation of the adjacency list of the individual
     *         vertex
     */
    private String listToString(List<V> list) {
        String s = "";
        for (int i = 0; i < list.size(); i++) {
            if (i < list.size() - 1) {
                s += list.get(i) + ", ";
            } else {
                s += list.get(i);
            }

        }
        return s;
    }
}