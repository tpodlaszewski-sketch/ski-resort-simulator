package core;

import graph.Node;
import graph.Route;
import graph.Lift;
import entities.AthleteArchetype;
import entities.LocalAthlete;
import entities.GreedyAthlete;
import entities.CollectorAthlete;
import events.AthleteSpawnEvent;
import events.LiftDepartureEvent;

import java.util.Scanner;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    // Returns a fully initialized simulation object
    public static Simulation load(Scanner input) {

        int numNodes = Integer.parseInt(input.nextLine().trim());
        Node[] nodes = new Node[numNodes];

        for (int i = 0; i < numNodes; i++) {
            String line = input.nextLine();
            Scanner lineScanner = new Scanner(line).useLocale(Locale.ENGLISH);

            int height = lineScanner.nextInt();
            int x = lineScanner.nextInt();
            int y = lineScanner.nextInt();
            boolean isConnected = lineScanner.hasNext("s"); // Check for optional 's'

            nodes[i] = new Node(i, height, x, y, isConnected);
            lineScanner.close();
        }

        input.nextLine(); // Skip empty separator line

        int numLifts = Integer.parseInt(input.nextLine().trim());
        List<Lift> lifts = new ArrayList<>();

        for (int i = 0; i < numLifts; i++) {
            String line = input.nextLine();
            Scanner lineScanner = new Scanner(line).useLocale(Locale.ENGLISH);

            int startId = lineScanner.nextInt();
            int endId = lineScanner.nextInt();
            int interval = lineScanner.nextInt();
            int maxCapacity = lineScanner.nextInt();
            int travelTime = lineScanner.nextInt();

            Lift lift = new Lift(i, nodes[startId], nodes[endId], interval, maxCapacity, travelTime);
            lifts.add(lift);

            // Connect the graph by adding the lift to its start node
            nodes[startId].addLift(lift);
            lineScanner.close();
        }

        input.nextLine(); // Skip empty separator line

        int numRoutes = Integer.parseInt(input.nextLine().trim());
        List<Route> routes = new ArrayList<>();

        for (int i = 0; i < numRoutes; i++) {
            String line = input.nextLine();
            Scanner lineScanner = new Scanner(line).useLocale(Locale.ENGLISH);

            int startId = lineScanner.nextInt();
            int endId = lineScanner.nextInt();
            int difficulty = lineScanner.nextInt();
            int travelTime = lineScanner.nextInt();
            double baseAttractiveness = lineScanner.nextDouble();
            double wearResistance = lineScanner.nextDouble();

            Route route = new Route(i, nodes[startId], nodes[endId], difficulty, travelTime, baseAttractiveness,
                    wearResistance);
            routes.add(route);

            // Connect the graph by adding the route to its start node
            nodes[startId].addRoute(route);
            lineScanner.close();
        }

        input.nextLine(); // Skip empty separator line

        Simulation simulation = new Simulation(routes, lifts);

        // Schedule first lift departures at 09:00:00
        for (Lift lift : lifts) {
            simulation.insertEvent(new LiftDepartureEvent(32400, lift, simulation));
        }

        int numGroups = Integer.parseInt(input.nextLine().trim());
        int athleteIdCounter = 0; // Global ID across all groups

        for (int i = 0; i < numGroups; i++) {
            // Group line 1
            Scanner line1Scanner = new Scanner(input.nextLine()).useLocale(Locale.ENGLISH);
            int groupSize = line1Scanner.nextInt();
            int skillLevel = line1Scanner.nextInt();
            double spontaneity = line1Scanner.nextDouble();
            double boredomFactor = line1Scanner.nextDouble();

            String typeStr = line1Scanner.next();
            char athleteType = typeStr.charAt(0);

            boolean isTracked = line1Scanner.hasNext("s");
            line1Scanner.close();

            // Group line 2
            Scanner line2Scanner = new Scanner(input.nextLine()).useLocale(Locale.ENGLISH);
            double diffWeight = line2Scanner.nextDouble();
            double surfaceWeight = line2Scanner.nextDouble();
            double boredomWeight = line2Scanner.nextDouble();
            line2Scanner.close();

            // Group line 3
            Scanner line3Scanner = new Scanner(input.nextLine()).useLocale(Locale.ENGLISH);
            int startNodeId = line3Scanner.nextInt();
            String startTimeStr = line3Scanner.next();
            int athleteInterval = line3Scanner.hasNextInt() ? line3Scanner.nextInt() : 0;
            line3Scanner.close();

            int currentStartTime = parseTimeToSeconds(startTimeStr);

            for (int j = 0; j < groupSize; j++) {

                AthleteArchetype athlete;

                // Instantiate the correct archetype based on the parsed character
                if (athleteType == 'Z') {
                    athlete = new GreedyAthlete(athleteIdCounter, skillLevel, spontaneity, isTracked,
                            diffWeight, surfaceWeight, boredomWeight, boredomFactor, nodes[startNodeId]);
                } else if (athleteType == 'K') {
                    athlete = new CollectorAthlete(athleteIdCounter, skillLevel, spontaneity, isTracked,
                            diffWeight, surfaceWeight, boredomWeight, boredomFactor, nodes[startNodeId]);
                } else {
                    // Default to 'L' (LocalAthlete)
                    athlete = new LocalAthlete(athleteIdCounter, skillLevel, spontaneity, isTracked,
                            diffWeight, surfaceWeight, boredomWeight, boredomFactor, nodes[startNodeId]);
                }

                // Register the athlete for map generation if they have the 's' flag
                if (athlete.isTracked()) {
                    simulation.addTrackedAthlete(athlete);
                }

                // Schedule the spawn event using the initialized simulation object
                AthleteSpawnEvent spawnEvent = new AthleteSpawnEvent(currentStartTime, athlete, simulation);
                simulation.insertEvent(spawnEvent);

                athleteIdCounter++;
                currentStartTime += athleteInterval;
            }
        }

        return simulation;
    }

    private static int parseTimeToSeconds(String timeStr) {
        String[] parts = timeStr.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);

        return hours * 3600 + minutes * 60 + seconds;
    }
}