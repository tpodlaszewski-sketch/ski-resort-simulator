package events;

import entities.AthleteArchetype;
import graph.Lift;
import core.Simulation;

public class LiftDepartureEvent extends Event {
    private Lift lift;
    private Simulation simulation;

    public LiftDepartureEvent(int time, Lift lift, Simulation simulation) {
        super(time);
        this.lift = lift;
        this.simulation = simulation;
    }

    @Override
    public void execute() {
        // Record time elapsed before taking athletes out of the queue
        lift.recordQueueChange(getTime());

        int boardedCount = 0;

        while (!lift.getWaitQueue().isEmpty() && boardedCount < lift.getMaxCapacity()) {
            AthleteArchetype athlete = lift.getWaitQueue().poll();
            boardedCount++;

            if (athlete.isTracked()) {
                System.out.println(formatTime() + ": Sportowiec " + athlete.getId() + " rozpoczal wjazd wyciagiem nr "
                        + lift.getId() + ".");
            }

            athlete.addRideToHistory(lift);

            int finishTime = getTime() + lift.getTravelTime();
            simulation.insertEvent(new FinishLiftRideEvent(finishTime, athlete, lift, simulation));
        }

        if (boardedCount > 0) {
            lift.addPassengers(boardedCount);
        }

        int nextDepartureTime = getTime() + lift.getInterval();
        if (nextDepartureTime <= 57600) {
            simulation.insertEvent(new LiftDepartureEvent(nextDepartureTime, lift, simulation));
        } else if (getTime() <= 57600) {
            // If this is the last departure, finalize the queue stats exactly at
            // 16:00:00
            lift.recordQueueChange(57600);
        }
    }
}