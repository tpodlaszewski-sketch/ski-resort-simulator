package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BFS {

    // Finds the shortest path in a graph using BFS
    public static List<Edge> findShortestPath(Node start, Node end) {
        // If the athlete is already at the destination node, the path is empty
        if (start == end) {
            return new ArrayList<>();
        }

        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();

        // Maps a node to the edge that was used to discover it.
        Map<Node, Edge> edgeTo = new HashMap<>();

        queue.add(start);
        visited.add(start);

        boolean found = false;

        // Standard BFS loop
        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // Destination reached
            if (current == end) {
                found = true;
                break;
            }

            // Check all outgoing routes
            for (Route r : current.getOutgoingRoutes()) {
                Node next = r.getEndNode();
                if (!visited.contains(next)) {
                    visited.add(next);
                    edgeTo.put(next, r);
                    queue.add(next);
                }
            }

            // Check all outgoing lifts
            for (Lift l : current.getOutgoingLifts()) {
                Node next = l.getEndNode();
                if (!visited.contains(next)) {
                    visited.add(next);
                    edgeTo.put(next, l);
                    queue.add(next);
                }
            }
        }

        // If the queue is empty and we haven't found the end node, no path exists
        if (!found) {
            return null;
        }

        // Backtrack to reconstruct the path from destination to start
        LinkedList<Edge> path = new LinkedList<>();
        Node current = end;

        while (current != start) {
            Edge incomingEdge = edgeTo.get(current);
            // Add to the front of the list so the final path is in chronological order
            path.addFirst(incomingEdge);
            current = incomingEdge.getStartNode();
        }

        return new ArrayList<>(path);
    }
}