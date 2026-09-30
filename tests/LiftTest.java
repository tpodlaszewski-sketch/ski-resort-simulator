package tests;

import org.junit.Test;
import static org.junit.Assert.*;

import graph.Node;
import graph.Lift;
import entities.AthleteArchetype;
import entities.LocalAthlete;
import events.EnterLiftQueueEvent;
import events.LiftDepartureEvent;
import core.Simulation;
import java.util.ArrayList;

public class LiftTest {

    // Helper method to create a dummy athlete for testing
    private AthleteArchetype createDummyAthlete(int id) {
        Node dummyNode = new Node(0, 0, 0, 0, false);
        return new LocalAthlete(id, 5, 0.0, false, 1.0, 0.0, 0.0, 0.0, dummyNode);
    }

    @Test
    public void testLiftDeparture_PartialBoarding() {
        // Lift capacity is 3. Queue has 4 athletes. The lift departs.
        // Expected result: 3 athletes board the lift, 1 remains in the queue.
        Node n1 = new Node(1, 0, 0, 0, false);
        Node n2 = new Node(2, 100, 0, 0, false);
        Lift lift = new Lift(1, n1, n2, 10, 3, 100); // maxCapacity = 3
        Simulation sim = new Simulation(new ArrayList<>(), new ArrayList<>());

        lift.getWaitQueue().add(createDummyAthlete(1));
        lift.getWaitQueue().add(createDummyAthlete(2));
        lift.getWaitQueue().add(createDummyAthlete(3));
        lift.getWaitQueue().add(createDummyAthlete(4));

        LiftDepartureEvent departure = new LiftDepartureEvent(32400, lift, sim);
        departure.execute();

        assertEquals("The lift should carry exactly 3 passengers.", 3, lift.getTotalPassengers());
        assertEquals("One passenger should remain in the queue.", 1, lift.getWaitQueue().size());
    }

    @Test
    public void testLiftDeparture_FullBoarding() {
        // Lift capacity is 3. Queue has 2 athletes. The lift departs.
        // Expected result: Both athletes board the lift. Queue becomes empty.
        Node n1 = new Node(1, 0, 0, 0, false);
        Node n2 = new Node(2, 100, 0, 0, false);
        Lift lift = new Lift(1, n1, n2, 10, 3, 100); // maxCapacity = 3
        Simulation sim = new Simulation(new ArrayList<>(), new ArrayList<>());

        lift.getWaitQueue().add(createDummyAthlete(1));
        lift.getWaitQueue().add(createDummyAthlete(2));

        LiftDepartureEvent departure = new LiftDepartureEvent(32400, lift, sim);
        departure.execute();

        assertEquals("The lift should carry exactly 2 passengers.", 2, lift.getTotalPassengers());
        assertEquals("The queue should be empty.", 0, lift.getWaitQueue().size());
    }

    @Test
    public void testLiftMaxQueueLength() {
        // 4 athletes queue up. Lift departs (takes 3). 1 more athlete
        // queues up.
        // Expected result: The maximum calculated queue length should be 4.
        Node n1 = new Node(1, 0, 0, 0, false);
        Node n2 = new Node(2, 100, 0, 0, false);
        Lift lift = new Lift(1, n1, n2, 10, 3, 100);
        Simulation sim = new Simulation(new ArrayList<>(), new ArrayList<>());

        // Simulating queue entry over time
        new EnterLiftQueueEvent(10, createDummyAthlete(1), lift).execute();
        new EnterLiftQueueEvent(11, createDummyAthlete(2), lift).execute();
        new EnterLiftQueueEvent(12, createDummyAthlete(3), lift).execute();
        new EnterLiftQueueEvent(13, createDummyAthlete(4), lift).execute();

        // Queue currently has 4 people. Lift departs.
        new LiftDepartureEvent(15, lift, sim).execute();

        // One more person joins the queue
        new EnterLiftQueueEvent(16, createDummyAthlete(5), lift).execute();

        assertEquals("The maximum queue length should be 4.", 4, lift.getMaxQueueLength());
    }
}