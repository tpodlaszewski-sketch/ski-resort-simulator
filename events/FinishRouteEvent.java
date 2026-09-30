package events;

import entities.AthleteArchetype;
import graph.Route;
import core.Simulation;

public class FinishRouteEvent extends Event {
    private AthleteArchetype athlete;
    private Route route;
    private Simulation simulation;

    public FinishRouteEvent(int time, AthleteArchetype athlete, Route route, Simulation simulation) {
        super(time);
        this.athlete = athlete;
        this.route = route;
        this.simulation = simulation;
    }

    @Override
    public void execute() {
        if (athlete.isTracked()) {
            System.out.println(formatTime() + ": Sportowiec " + athlete.getId() + " zakonczyl zjazd trasa nr "
                    + route.getId() + ".");
        }

        athlete.setCurrentNode(route.getEndNode());

        if (getTime() >= 54000) {
            return;
        }

        athlete.chooseNextAction(simulation, getTime());
    }
}