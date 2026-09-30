package events;

import entities.AthleteArchetype;
import core.Simulation;

public class AthleteSpawnEvent extends Event {

    private AthleteArchetype athlete;
    private Simulation simulation;

    public AthleteSpawnEvent(int time, AthleteArchetype athlete, Simulation simulation) {
        super(time);
        this.athlete = athlete;
        this.simulation = simulation;
    }

    @Override
    public void execute() {
        if (athlete.isTracked()) {
            System.out.println(formatTime() + ": Sportowiec " + athlete.getId() + " rozpoczal dzien w wezle "
                    + athlete.getStartNode().getId() + ".");
        }

        athlete.chooseNextAction(simulation, getTime());
    }
}