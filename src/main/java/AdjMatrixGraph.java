
/******************************************************************************
 * Compilation:  javac AdjMatrixGraph.java
 * Execution:    java AdjMatrixGraph VERTICES EDGES
 * Dependencies: StdOut.java
 * <p>
 * A graph, implemented using an adjacency matrix.
 * Parallel edges are disallowed; self-loops are allowd.
 ******************************************************************************/

import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;


public class AdjMatrixGraph {
    private static final String NEWLINE = System.getProperty("line.separator");
    private int VERTICES;
    private int EDGES;
    public Map<Character, Integer> characterIntegerMap = Maps.newHashMap();
    private boolean[][] adj;

    // empty graph with VERTICES vertices
    public AdjMatrixGraph(int VERTICES) {
        if (VERTICES < 0) throw new RuntimeException("Number of vertices must be nonnegative");
        this.VERTICES = VERTICES;
        this.EDGES = 0;
        this.adj = new boolean[VERTICES][VERTICES];
    }

    public AdjMatrixGraph()
    {
        //Make a graph for solver

        characterIntegerMap.put(new Character('a'),0);
        characterIntegerMap.put(new Character('b'),1);
        characterIntegerMap.put(new Character('c'),2);
        characterIntegerMap.put(new Character('d'),3);
        characterIntegerMap.put(new Character('e'),4);
        characterIntegerMap.put(new Character('f'),5);
        characterIntegerMap.put(new Character('g'),6);
        characterIntegerMap.put(new Character('h'),7);
        characterIntegerMap.put(new Character('i'),8);
        characterIntegerMap.put(new Character('j'),9);
        characterIntegerMap.put(new Character('k'),10);
        characterIntegerMap.put(new Character('l'),11);
        characterIntegerMap.put(new Character('m'),12);
        characterIntegerMap.put(new Character('n'),13);
        characterIntegerMap.put(new Character('o'),14);
        characterIntegerMap.put(new Character('p'),15);
        characterIntegerMap.put(new Character('q'),16);
        characterIntegerMap.put(new Character('r'),17);
        characterIntegerMap.put(new Character('s'),18);
        characterIntegerMap.put(new Character('t'),19);
        characterIntegerMap.put(new Character('u'),20);
        characterIntegerMap.put(new Character('v'),21);
        characterIntegerMap.put(new Character('w'),22);
        characterIntegerMap.put(new Character('x'),23);
        characterIntegerMap.put(new Character('y'),24);
        characterIntegerMap.put(new Character('z'),25);
        this.VERTICES = 26;
        this.EDGES =0;
        this.adj = new boolean[VERTICES][VERTICES];
    }


    // number of vertices and edges
    public int V() {
        return VERTICES;
    }

    public int E() {
        return EDGES;
    }


    // add undirected edge v-w
    public void addEdge(int v, int w) {
        if (!adj[v][w]) EDGES++;
        adj[v][w] = true;
        adj[w][v] = true;
    }

    // does the graph contain the edge v-w?
    public boolean contains(int v, int w) {
        return adj[v][w];
    }

    // return list of neighbors of v
    public Iterable<Integer> adj(int v) {
        return new AdjIterator(v);
    }

    // support iteration over graph vertices
    private class AdjIterator implements Iterator<Integer>, Iterable<Integer> {
        private int v;
        private int w = 0;

        AdjIterator(int v) {
            this.v = v;
        }

        public Iterator<Integer> iterator() {
            return this;
        }

        public boolean hasNext() {
            while (w < VERTICES) {
                if (adj[v][w]) return true;
                w++;
            }
            return false;
        }

        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return w++;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    // string representation of Graph - takes quadratic time
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(VERTICES + " " + EDGES + NEWLINE);
        for (int v = 0; v < VERTICES; v++) {
            s.append(v + ": ");
            for (int w : adj(v)) {
                s.append(w + " ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

}

