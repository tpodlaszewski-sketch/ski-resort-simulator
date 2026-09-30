package entities;

import graph.Node;
import graph.Route;
import graph.Lift;
import events.StartRouteEvent;
import events.EnterLiftQueueEvent;
import core.Simulation;
import java.util.List;

public class LocalAthlete extends AthleteArchetype {

    public LocalAthlete(int id, int skillLevel, double spontaneity, boolean isTracked,
            double diffWeight, double surfaceWeight, double boredomWeight, double boredomFactor, Node startNode) {
        super(id, skillLevel, spontaneity, isTracked, diffWeight, surfaceWeight, boredomWeight, boredomFactor,
                startNode);
    }

    @Override
    public void chooseNextAction(Simulation simulation, int currentTime) {
        Node current = this.currentNode;
        List<Route> routes = current.getOutgoingRoutes();
        List<Lift> lifts = current.getOutgoingLifts();

        int routeCount = routes.size();
        int liftCount = lifts.size();

        // Spontaneous choice
        double randVal = simulation.getRandomGenerator().nextDouble();
        if (randVal < this.spontaneity) {
            int totalOptions = routeCount + liftCount;
            if (totalOptions > 0) {
                int choice = simulation.getRandomGenerator().nextInt(totalOptions);
                if (choice < routeCount) {
                    simulation.insertEvent(new StartRouteEvent(currentTime, this, routes.get(choice), simulation));
                } else {
                    int liftIndex = choice - routeCount;
                    simulation.insertEvent(new EnterLiftQueueEvent(currentTime, this, lifts.get(liftIndex)));
                }
            }
            return;
        }

        // Calculated choice
        double maxAttractiveness = -1.0;
        Route bestRoute = null;
        Lift liftToTake = null;

        // Check immediate routes
        for (Route r : routes) {
            double attr = calculateRouteAttractiveness(r);
            if (attr > maxAttractiveness) {
                maxAttractiveness = attr;
                bestRoute = r;
                liftToTake = null;
            }
        }

        // Check routes accessible via immediate lifts
        for (Lift lift : lifts) {
            Node topNode = lift.getEndNode();
            List<Route> topRoutes = topNode.getOutgoingRoutes();

            for (Route r : topRoutes) {
                double attr = calculateRouteAttractiveness(r);
                if (attr > maxAttractiveness) {
                    maxAttractiveness = attr;
                    bestRoute = r;
                    liftToTake = lift; // Must take this lift first
                }
            }
        }

        // Execute the best choice
        if (liftToTake != null) {
            simulation.insertEvent(new EnterLiftQueueEvent(currentTime, this, liftToTake));
        } else if (bestRoute != null) {
            simulation.insertEvent(new StartRouteEvent(currentTime, this, bestRoute, simulation));
        }
    }
}