package events;

import entities.AthleteArchetype;
import graph.Route;
import core.Simulation;

public class StartRouteEvent extends Event {
    private AthleteArchetype athlete;
    private Route route;
    private Simulation simulation;

    public StartRouteEvent(int time, AthleteArchetype athlete, Route route, Simulation simulation) {
        super(time);
        this.athlete = athlete;
        this.route = route;
        this.simulation = simulation;
    }

    @Override
    public void execute() {
        if (athlete.isTracked()) {
            System.out.println(formatTime() + ": Sportowiec " + athlete.getId() + " rozpoczal zjazd trasa nr "
                    + route.getId() + ".");
        }

        // Increase global ride count for the route
        route.addRide();

        // Update specific athlete's boredom based on formula
        athlete.updateBoredomAfterRide(route);

        athlete.addRideToHistory(route);

        int finishTime = getTime() + route.getTravelTime();
        simulation.insertEvent(new FinishRouteEvent(finishTime, athlete, route, simulation));
    }
}