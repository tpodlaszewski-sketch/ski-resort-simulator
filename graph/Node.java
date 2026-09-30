package graph;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private int id;
    private int height;
    private int x;
    private int y;
    private boolean isConnected;

    private List<Route> outgoingRoutes;
    private List<Lift> outgoingLifts;

    public Node(int id, int height, int x, int y, boolean isConnected) {
        this.id = id;
        this.height = height;
        this.x = x;
        this.y = y;
        this.isConnected = isConnected;

        this.outgoingRoutes = new ArrayList<>();
        this.outgoingLifts = new ArrayList<>();
    }

    public void addRoute(Route route) {
        outgoingRoutes.add(route);
    }

    public void addLift(Lift lift) {
        outgoingLifts.add(lift);
    }

    public int getId() {
        return id;
    }

    public int getHeight() {
        return height;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public List<Route> getOutgoingRoutes() {
        return outgoingRoutes;
    }

    public List<Lift> getOutgoingLifts() {
        return outgoingLifts;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}