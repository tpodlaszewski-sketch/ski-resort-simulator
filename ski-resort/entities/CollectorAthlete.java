package entities;

import java.util.List;
import graph.Edge;
import graph.Route;
import graph.Lift;
import graph.Node;
import graph.BFS;
import events.StartRouteEvent;
import events.EnterLiftQueueEvent;
import core.Simulation;

public class CollectorAthlete extends AthleteArchetype {

    private List<Edge> currentPlan;

    public CollectorAthlete(int id, int skillLevel, double spontaneity, boolean isTracked,
            double diffWeight, double surfaceWeight, double boredomWeight, double boredomFactor, Node startNode) {
        super(id, skillLevel, spontaneity, isTracked, diffWeight, surfaceWeight, boredomWeight, boredomFactor,
                startNode);
        this.currentPlan = null;
    }

    @Override
    public void chooseNextAction(Simulation simulation, int currentTime) {
        if (currentPlan == null || currentPlan.isEmpty()) {

            // Spontaneous choice
            double randVal = simulation.getRandomGenerator().nextDouble();
            if (randVal < this.spontaneity) {
                List<Route> routes = currentNode.getOutgoingRoutes();
                List<Lift> lifts = currentNode.getOutgoingLifts();
                int totalOptions = routes.size() + lifts.size();

                if (totalOptions > 0) {
                    int choice = simulation.getRandomGenerator().nextInt(totalOptions);
                    if (choice < routes.size()) {
                        simulation.insertEvent(new StartRouteEvent(currentTime, this, routes.get(choice), simulation));
                    } else {
                        simulation.insertEvent(
                                new EnterLiftQueueEvent(currentTime, this, lifts.get(choice - routes.size())));
                    }
                }
                return;
            }

            // Collector search strategy
            Route bestRoute = null;
            int minRides = Integer.MAX_VALUE;
            int shortestDistance = Integer.MAX_VALUE;
            double maxAttr = -1.0;
            List<Edge> bestPath = null;

            for (Route r : simulation.getRoutes()) {
                int rides = this.routeRideCounts.getOrDefault(r, 0);

                if (rides <= minRides) {
                    // We need the path to evaluate distance
                    List<Edge> path = BFS.findShortestPath(this.currentNode, r.getStartNode());

                    if (path != null) {
                        int distance = path.size();
                        double attr = calculateRouteAttractiveness(r);

                        boolean isBetter = false;
                        if (rides < minRides) {
                            isBetter = true;
                        } else if (rides == minRides) {
                            if (distance < shortestDistance) {
                                isBetter = true;
                            } else if (distance == shortestDistance) {
                                if (attr > maxAttr) {
                                    isBetter = true;
                                }
                            }
                        }

                        if (isBetter) {
                            minRides = rides;
                            shortestDistance = distance;
                            maxAttr = attr;
                            bestRoute = r;
                            bestPath = path;
                        }
                    }
                }
            }

            // Assign the best path as the new plan
            if (bestRoute != null && bestPath != null) {
                currentPlan = bestPath;
                currentPlan.add(bestRoute); // Add the route itself to the end of the journey
            }
        }

        // Execute the next step of the plan
        if (currentPlan != null && !currentPlan.isEmpty()) {
            Edge nextStep = currentPlan.remove(0);

            if (nextStep instanceof Route) {
                simulation.insertEvent(new StartRouteEvent(currentTime, this, (Route) nextStep, simulation));
            } else if (nextStep instanceof Lift) {
                simulation.insertEvent(new EnterLiftQueueEvent(currentTime, this, (Lift) nextStep));
            }
        }
    }
}