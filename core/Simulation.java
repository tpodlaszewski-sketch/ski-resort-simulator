package core;

import graph.Route;
import graph.Lift;
import graph.Node;
import graph.Edge;
import entities.AthleteArchetype;
import events.EventWrapper;
import events.Event;

// Imports from the map generator package
import kadra.mapki.GeneratorMapek;
import kadra.mapki.styl.StylWezla;
import kadra.mapki.styl.GruboscKonturu;
import kadra.mapki.styl.StylKrawedzi;
import kadra.mapki.styl.StylLinii;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Locale;

public class Simulation {
    private List<Route> routes;
    private List<Lift> lifts;
    private List<AthleteArchetype> trackedAthletes;

    // Priority queue forevent execution
    private PriorityQueue<EventWrapper> eventQueue;
    private long eventSequenceCounter;
    private Random randomGenerator;

    public Simulation(List<Route> routes, List<Lift> lifts) {
        this.routes = routes;
        this.lifts = lifts;
        this.trackedAthletes = new ArrayList<>();

        this.eventQueue = new PriorityQueue<>();
        this.eventSequenceCounter = 0;
        this.randomGenerator = new Random();
    }

    public void insertEvent(Event event) {
        eventQueue.add(new EventWrapper(event, eventSequenceCounter++));
    }

    public Random getRandomGenerator() {
        return randomGenerator;
    }

    // Required by Greedy and Collector athletes to scan the entire map
    public List<Route> getRoutes() {
        return routes;
    }

    // Registers an athlete to be tracked for the personal history maps
    public void addTrackedAthlete(AthleteArchetype athlete) {
        this.trackedAthletes.add(athlete);
    }

    // Main loop of the simulation. Throws Exception directly to Main.java
    public void run(String mapDirectory) throws Exception {
        while (!eventQueue.isEmpty()) {
            Event currentEvent = eventQueue.poll().getEvent();
            currentEvent.execute();
        }

        // Print text results and generate TeX maps after the simulation ends
        printStatistics();
        generateMaps(mapDirectory);
    }

    private void printStatistics() {
        System.out.println("\nSTATYSTYKI KONCOWE");

        for (Route route : routes) {
            // Calculate final snow condition
            double bt = route.getBaseAttractiveness();
            double ot = route.getWearResistance();
            int kt = route.getRideCount();
            double finalSurface = bt + (1.0 - bt) * Math.pow(ot, kt);

            System.out.printf(Locale.ENGLISH, "Trasa %d: snieg: %.2f, zjazdy: %d\n",
                    route.getId(), finalSurface, route.getRideCount());
        }

        for (Lift lift : lifts) {
            lift.recordQueueChange(54000);

            long avgRounded = Math.round(lift.getAverageQueueLength());
            int maxQ = lift.getMaxQueueLength();
            int passengers = lift.getTotalPassengers();
            long capPercent = Math.round(lift.getCapacityPercentage());

            System.out.printf(Locale.ENGLISH, "Wyciag %d: kol: %d(sr), %d(maks), wjazdy: %d (%d%%)\n",
                    lift.getId(), avgRounded, maxQ, passengers, capPercent);
        }
    }

    // Map generation logic.
    private void generateMaps(String mapDirectory) throws Exception {

        GeneratorMapek generator = new GeneratorMapek(mapDirectory);

        // Extract all unique nodes from routes and lifts
        Set<Node> allNodes = new HashSet<>();
        for (Route r : routes) {
            allNodes.add(r.getStartNode());
            allNodes.add(r.getEndNode());
        }
        for (Lift l : lifts) {
            allNodes.add(l.getStartNode());
            allNodes.add(l.getEndNode());
        }

        // MAP 1
        for (Node n : allNodes) {
            StylWezla style = new StylWezla(n.isConnected() ? GruboscKonturu.POGRUBIONY : GruboscKonturu.ZWYKLY);
            generator.dodajWezel(n.getId(), n.getX(), n.getY(), style);
        }

        for (Route r : routes) {
            List<String> lines = new ArrayList<>();
            lines.add(String.format(Locale.ENGLISH, "t%d: poziom: %d, czas: %ds", r.getId(), r.getDifficulty(),
                    r.getTravelTime()));
            lines.add(String.format(Locale.ENGLISH, "odpornosc: %.2f, %.5f", r.getBaseAttractiveness(),
                    r.getWearResistance()));
            generator.dodajKrawedz(r.getStartNode().getId(), r.getEndNode().getId(), new StylKrawedzi(StylLinii.CIAGLA),
                    lines);
        }

        for (Lift l : lifts) {
            List<String> lines = new ArrayList<>();
            lines.add(String.format(Locale.ENGLISH, "w%d: %d os. co %ds", l.getId(), l.getMaxCapacity(),
                    l.getInterval()));
            lines.add(String.format(Locale.ENGLISH, "czas: %ds", l.getTravelTime()));
            generator.dodajKrawedz(l.getStartNode().getId(), l.getEndNode().getId(),
                    new StylKrawedzi(StylLinii.PRZERYWANA), lines);
        }
        generator.tworzMapke("mapka_parametry.tex");

        // MAP 2
        generator.zeruj(); // Reset generator for the new map
        for (Node n : allNodes) {
            StylWezla style = new StylWezla(n.isConnected() ? GruboscKonturu.POGRUBIONY : GruboscKonturu.ZWYKLY);
            generator.dodajWezel(n.getId(), n.getX(), n.getY(), style);
        }

        for (Route r : routes) {
            double bt = r.getBaseAttractiveness();
            double ot = r.getWearResistance();
            int kt = r.getRideCount();
            double finalSurface = bt + (1.0 - bt) * Math.pow(ot, kt);

            List<String> lines = new ArrayList<>();
            lines.add(String.format(Locale.ENGLISH, "t%d: snieg: %.2f", r.getId(), finalSurface));
            lines.add(String.format(Locale.ENGLISH, "zjazdy: %d", r.getRideCount()));
            generator.dodajKrawedz(r.getStartNode().getId(), r.getEndNode().getId(), new StylKrawedzi(StylLinii.CIAGLA),
                    lines);
        }

        for (Lift l : lifts) {
            int absoluteMaxPassengers = ((25200 / l.getInterval()) + 1) * l.getMaxCapacity();
            long capPercent = Math.round(l.getCapacityPercentage());

            List<String> lines = new ArrayList<>();
            lines.add(String.format(Locale.ENGLISH, "w%d: kol: %d(sr), %d(maks)", l.getId(),
                    Math.round(l.getAverageQueueLength()), l.getMaxQueueLength()));
            lines.add(String.format(Locale.ENGLISH, "wjazdy: %d / %d (%d%%)", l.getTotalPassengers(),
                    absoluteMaxPassengers, capPercent));
            generator.dodajKrawedz(l.getStartNode().getId(), l.getEndNode().getId(),
                    new StylKrawedzi(StylLinii.PRZERYWANA), lines);
        }
        generator.tworzMapke("mapka_statystyki.tex");

        // MAP 3
        for (AthleteArchetype athlete : trackedAthletes) {
            generator.zeruj(); // Reset generator for each athlete
            for (Node n : allNodes) {
                StylWezla style = new StylWezla(n.isConnected() ? GruboscKonturu.POGRUBIONY : GruboscKonturu.ZWYKLY);
                generator.dodajWezel(n.getId(), n.getX(), n.getY(), style);
            }

            // Map edges to their sequence numbers using the athlete's history
            Map<Edge, List<Integer>> edgeHistory = new HashMap<>();
            List<Edge> history = athlete.getRideHistory();
            for (int i = 0; i < history.size(); i++) {
                Edge e = history.get(i);
                edgeHistory.computeIfAbsent(e, k -> new ArrayList<>()).add(i + 1);
            }

            for (Route r : routes) {
                List<Integer> rides = edgeHistory.getOrDefault(r, new ArrayList<>());
                String text = rides.isEmpty() ? String.format(Locale.ENGLISH, "t%d(0): ", r.getId())
                        : String.format(Locale.ENGLISH, "t%d(%d): %s", r.getId(), rides.size(), joinIntegers(rides));

                generator.dodajKrawedz(r.getStartNode().getId(), r.getEndNode().getId(),
                        new StylKrawedzi(StylLinii.CIAGLA), text);
            }

            for (Lift l : lifts) {
                List<Integer> rides = edgeHistory.getOrDefault(l, new ArrayList<>());
                String text = rides.isEmpty() ? String.format(Locale.ENGLISH, "w%d(0): ", l.getId())
                        : String.format(Locale.ENGLISH, "w%d(%d): %s", l.getId(), rides.size(), joinIntegers(rides));

                generator.dodajKrawedz(l.getStartNode().getId(), l.getEndNode().getId(),
                        new StylKrawedzi(StylLinii.PRZERYWANA), text);
            }

            generator.tworzMapke("mapka_historia_sportowca_" + athlete.getId() + ".tex");
        }
    }

    // Helper method to join integers with a comma for the history map string format
    private String joinIntegers(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1)
                sb.append(",");
        }
        return sb.toString();
    }
}