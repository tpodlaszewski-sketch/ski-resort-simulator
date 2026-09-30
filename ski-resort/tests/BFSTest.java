package tests;

import org.junit.Test;
import static org.junit.Assert.*;

import graph.Node;
import graph.Route;
import graph.BFS;

public class BFSTest {

    @Test
    public void testFindShortestPath() {
        // Create nodes according to the assignment
        Node n0 = new Node(0, 100, 0, 0, true);
        Node n1 = new Node(1, 80, 0, 0, true);
        Node n2 = new Node(2, 60, 0, 0, true);
        Node n3 = new Node(3, 90, 0, 0, true);
        Node n4 = new Node(4, 40, 0, 0, true);
        Node n5 = new Node(5, 60, 0, 0, true); // Auxiliary node to build an alternative path

        // Build edges (routes)
        Route r0_1 = new Route(1, n0, n1, 5, 100, 1.0, 1.0);
        n0.addRoute(r0_1);
        Route r1_2 = new Route(2, n1, n2, 5, 100, 1.0, 1.0);
        n1.addRoute(r1_2);
        Route r2_4 = new Route(3, n2, n4, 5, 100, 1.0, 1.0);
        n2.addRoute(r2_4);
        Route r3_1 = new Route(4, n3, n1, 5, 100, 1.0, 1.0);
        n3.addRoute(r3_1);

        // Path from 4 to 3 (Option A: length 2) e.g., 4 -> 0 -> 3
        Route r4_0 = new Route(5, n4, n0, 5, 100, 1.0, 1.0);
        n4.addRoute(r4_0);
        Route r0_3 = new Route(6, n0, n3, 5, 100, 1.0, 1.0);
        n0.addRoute(r0_3);

        // Path from 4 to 3 (Option B: length 2) e.g., 4 -> 5 -> 3
        Route r4_5 = new Route(7, n4, n5, 5, 100, 1.0, 1.0);
        n4.addRoute(r4_5);
        Route r5_3 = new Route(8, n5, n3, 5, 100, 1.0, 1.0);
        n5.addRoute(r5_3);

        // 1. Path from 0 to 4: goes through 0 -> 1 -> 2 -> 4 (expected distance: 3)
        assertEquals(3, BFS.findShortestPath(n0, n4).size());

        // 2. Direct path from 3 to 1 (expected distance: 1)
        assertEquals(1, BFS.findShortestPath(n3, n1).size());

        // 3. Empty path from 2 to itself (expected distance: 0)
        assertEquals(0, BFS.findShortestPath(n2, n2).size());

        // 4. Path from 4 to 3 (algorithm should pick one of the two paths of length 2)
        assertEquals(2, BFS.findShortestPath(n4, n3).size());
    }
}