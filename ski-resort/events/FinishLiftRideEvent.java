package events;

import entities.AthleteArchetype;
import graph.Lift;
import core.Simulation;

public class FinishLiftRideEvent extends Event {
    private AthleteArchetype athlete;
    private Lift lift;
    private Simulation simulation;

    public FinishLiftRideEvent(int time, AthleteArchetype athlete, Lift lift, Simulation simulation) {
        super(time);
        this.athlete = athlete;
        this.lift = lift;
        this.simulation = simulation;
    }

    @Override
    public void execute() {
        if (athlete.isTracked()) {
            System.out.println(
                    formatTime() + ": Sportowiec " + athlete.getId() + " zszedl z wyciagu nr " + lift.getId() + ".");
        }

        athlete.setCurrentNode(lift.getEndNode());

        if (getTime() >= 54000) {
            return;
        }

        athlete.chooseNextAction(simulation, getTime());
    }
}