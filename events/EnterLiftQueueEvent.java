package events;

import entities.AthleteArchetype;
import graph.Lift;

public class EnterLiftQueueEvent extends Event {
    private AthleteArchetype athlete;
    private Lift lift;

    public EnterLiftQueueEvent(int time, AthleteArchetype athlete, Lift lift) {
        super(time);
        this.athlete = athlete;
        this.lift = lift;
    }

    @Override
    public void execute() {
        if (athlete.isTracked()) {
            System.out.println(formatTime() + ": Sportowiec " + athlete.getId()
                    + " ustawil sie w kolejce do wyciagu nr " + lift.getId() + ".");
        }

        // ecord time elapsed beofre changing the size
        lift.recordQueueChange(getTime());

        // Add athlete to the queue
        lift.getWaitQueue().add(athlete);

        // Record again to update MaxQueueLength if necessary
        lift.recordQueueChange(getTime());
    }
}