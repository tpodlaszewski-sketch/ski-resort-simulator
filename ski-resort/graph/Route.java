package graph;

public class Route extends Edge {
    private int difficulty;
    private double baseAttractiveness;
    private double wearResistance;

    private int rideCount; // Tracks the number of rides to calculate surface wear.

    public Route(int id, Node startNode, Node endNode, int difficulty, int travelTime, double baseAttractiveness,
            double wearResistance) {
        super(id, startNode, endNode, travelTime);

        this.difficulty = difficulty;
        this.baseAttractiveness = baseAttractiveness;
        this.wearResistance = wearResistance;
        this.rideCount = 0;
    }

    public void addRide() {
        this.rideCount++;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public double getBaseAttractiveness() {
        return baseAttractiveness;
    }

    public double getWearResistance() {
        return wearResistance;
    }

    public int getRideCount() {
        return rideCount;
    }

    @Override
    public boolean isRoute() {
        return true;
    }
}