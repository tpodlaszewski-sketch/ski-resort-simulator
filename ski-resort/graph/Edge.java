package graph;

// Lifts and Routes are Edges
public abstract class Edge {
    private Node startNode;
    private Node endNode;
    private int travelTime;
    private int id;

    public Edge(int id, Node startNode, Node endNode, int travelTime) {
        this.id = id;
        this.startNode = startNode;
        this.endNode = endNode;
        this.travelTime = travelTime;
    }

    public int getId() {
        return id;
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public int getTravelTime() {
        return travelTime;
    }

    public boolean isRoute() {
        return false;
    }
}
