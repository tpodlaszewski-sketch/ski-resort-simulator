package graph;

import java.util.ArrayDeque;
import java.util.Queue;
import entities.AthleteArchetype;

public class Lift extends Edge {
    private int interval;
    private int maxCapacity;

    // Built in FIFO queue
    private Queue<AthleteArchetype> waitQueue;
    private int totalPassengers;

    // Statistics tracking
    private int maxQueueLength;
    private long queueLengthSum;
    private int lastUpdateTime;

    public Lift(int id, Node startNode, Node endNode, int interval, int maxCapacity, int travelTime) {
        super(id, startNode, endNode, travelTime);
        this.interval = interval;
        this.maxCapacity = maxCapacity;
        this.waitQueue = new ArrayDeque<>();

        this.totalPassengers = 0;
        this.maxQueueLength = 0;
        this.queueLengthSum = 0;
        this.lastUpdateTime = 32400; // Lifts open at 09:00:00
    }

    // Call this method before someone enters or leaves the queue
    public void recordQueueChange(int currentTime) {
        int currentSize = waitQueue.size();
        int timeElapsed = currentTime - lastUpdateTime;

        if (timeElapsed > 0) {
            queueLengthSum += (long) currentSize * timeElapsed;
            lastUpdateTime = currentTime;
        }

        // We update max length assuming this might be called right after someone joined
        if (currentSize > maxQueueLength) {
            maxQueueLength = currentSize;
        }
    }

    public void addPassengers(int count) {
        this.totalPassengers += count;
    }

    // Statistics Getters
    public int getMaxQueueLength() {
        return maxQueueLength;
    }

    // Average over the 7 hoursbetween 09:00:00 and 16:00:00
    public double getAverageQueueLength() {
        return (double) queueLengthSum / 25200.0;
    }

    public double getCapacityPercentage() {
        int numberOfDepartures = (25200 / interval) + 1;
        int absoluteMaxPassengers = numberOfDepartures * maxCapacity;

        if (absoluteMaxPassengers == 0)
            return 0.0;
        return ((double) totalPassengers / absoluteMaxPassengers) * 100.0;
    }

    public int getTotalPassengers() {
        return totalPassengers;
    }

    public int getInterval() {
        return interval;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public Queue<AthleteArchetype> getWaitQueue() {
        return waitQueue;
    }
}