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

public class GreedyAthlete extends AthleteArchetype {

    private List<Edge> currentPlan;

    public GreedyAthlete(int id, int skillLevel, double spontaneity, boolean isTracked,
            double diffWeight, double surfaceWeight, double boredomWeight, double boredomFactor, Node startNode) {
        super(id, skillLevel, spontaneity, isTracked, diffWeight, surfaceWeight, boredomWeight, boredomFactor,
                startNode);
        this.currentPlan = null;
    }

    @Override
    public void chooseNextAction(Simulation simulation, int currentTime) {
        // If there is no plan or the plan is finished, make a new one
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

            // Greedy search
            Route bestRoute = null;
            double maxAttr = -1.0;

            for (Route r : simulation.getRoutes()) {
                double attr = calculateRouteAttractiveness(r);
                if (attr > maxAttr) {
                    maxAttr = attr;
                    bestRoute = r;
                }
            }

            // Create the plan to the best route
            if (bestRoute != null) {
                currentPlan = BFS.findShortestPath(this.currentNode, bestRoute.getStartNode());
                if (currentPlan != null) {
                    currentPlan.add(bestRoute); // Add the actual ride down the target route
                }
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