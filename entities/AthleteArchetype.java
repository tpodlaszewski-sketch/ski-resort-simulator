package entities;

import graph.Node;
import graph.Route;
import graph.Edge;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import core.Simulation;

public abstract class AthleteArchetype {
    protected int id;
    protected int skillLevel;
    protected double spontaneity;
    protected boolean isTracked;

    // Weights for attractiveness calculation
    protected double diffWeight;
    protected double surfaceWeight;
    protected double boredomWeight;
    protected double boredomFactor;

    protected Node startNode;
    protected Node currentNode;

    // Exponential smoothing tracking variables
    protected int totalRidesCounter;
    protected Map<Route, Double> lastZ;
    protected Map<Route, Integer> lastRideIndex;
    protected Map<Route, Integer> routeRideCounts;

    // History tracking for the maps
    protected List<Edge> rideHistory;

    public AthleteArchetype(int id, int skillLevel, double spontaneity, boolean isTracked,
            double diffWeight, double surfaceWeight, double boredomWeight, double boredomFactor, Node startNode) {
        this.id = id;
        this.skillLevel = skillLevel;
        this.spontaneity = spontaneity;
        this.isTracked = isTracked;
        this.diffWeight = diffWeight;
        this.surfaceWeight = surfaceWeight;
        this.boredomWeight = boredomWeight;
        this.boredomFactor = boredomFactor;
        this.startNode = startNode;
        this.currentNode = startNode;

        this.totalRidesCounter = 0;
        this.lastZ = new HashMap<>();
        this.lastRideIndex = new HashMap<>();
        this.routeRideCounts = new HashMap<>();
        this.rideHistory = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public boolean isTracked() {
        return isTracked;
    }

    public void setCurrentNode(Node node) {
        this.currentNode = node;
    }

    // Adds the ride to the athlete's personal history
    public void addRideToHistory(Edge edge) {
        this.rideHistory.add(edge);
    }

    // Retrieves the ride history
    public List<Edge> getRideHistory() {
        return this.rideHistory;
    }

    // Method called only when the athlete actually skis down a specific route
    public void updateBoredomAfterRide(Route route) {
        double currentZ = getBoredom(route);
        // Applying the formula: z_t = beta * 1 + (1 - beta) * z_{t-1}
        double newZ = boredomFactor * 1.0 + (1.0 - boredomFactor) * currentZ;

        totalRidesCounter++;

        lastZ.put(route, newZ);
        lastRideIndex.put(route, totalRidesCounter);
        routeRideCounts.put(route, routeRideCounts.getOrDefault(route, 0) + 1);
    }

    // Calculates current boredom simulating intermediate rides on other routes
    protected double getBoredom(Route route) {
        double zk = lastZ.getOrDefault(route, 0.0);
        int k = lastRideIndex.getOrDefault(route, 0);
        int missedRides = totalRidesCounter - k;

        // Applying the decay formula for rides on other routes
        return zk * Math.pow(1.0 - boredomFactor, missedRides);
    }

    // Complete new attractiveness formula
    public double calculateRouteAttractiveness(Route route) {
        int pt = route.getDifficulty();
        double bt = route.getBaseAttractiveness();
        double ot = route.getWearResistance();
        int kt = route.getRideCount();
        int pn = this.skillLevel;

        double d;
        if (pt >= pn + 5) {
            d = 0.0;
        } else if (pt >= pn + 5) {
            d = 1.0 - ((double) (pt - pn) / 5.0);
        } else {
            double calculatedD = 1.0 - ((double) (pn - pt) / 5.0);
            d = Math.max(0.2, calculatedD);
        }

        double w = bt + (1.0 - bt) * Math.pow(ot, kt);
        double currentBoredom = getBoredom(route);

        return (this.diffWeight * d) + (this.surfaceWeight * w) + (this.boredomWeight * (1.0 - currentBoredom));
    }

    // Abstract method to be implemented by Local, Greedy, and Collector athletes
    public abstract void chooseNextAction(Simulation simulation, int currentTime);
}