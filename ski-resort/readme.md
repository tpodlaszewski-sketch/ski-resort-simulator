# Ski Resort Discrete Event Simulation

An object-oriented Discrete Event Simulation (DES) modeling the daily operations of a ski resort, skier decision-making behaviors, and infrastructure load. Developed in Java 21.

## Overview

The application simulates athletes navigating a ski resort represented as a directed multigraph (interchange nodes connected by ski lifts and downhill routes). Using a priority event queue, the simulation models bottlenecks, snow surface degradation, and the behavior of distinct skier archetypes.

## Features

* **Discrete Event Simulation Engine:** Processes time-ordered events (`AthleteSpawnEvent`, `EnterLiftQueueEvent`, `LiftDepartureEvent`, `StartRouteEvent`, `FinishRouteEvent`) using a priority queue wrapper (`EventWrapper`) to guarantee FIFO stability for simultaneous events.
* **Athlete Archetypes:**
  * **Local (`LocalAthlete`):** Makes decisions at each node based on the current attractiveness of directly connected routes.
  * **Greedy (`GreedyAthlete`):** Searches the entire resort graph to find the single most attractive route and follows the shortest path to reach it.
  * **Collector (`CollectorAthlete`):** Prioritizes the least-visited trails in the resort, breaking ties by graph distance and route attractiveness.
* **Graph Pathfinding (BFS):** Implements Breadth-First Search over a directed multigraph to compute shortest paths (sequences of lifts and routes) with incoming-edge tracking for path reconstruction.
* **Route Degradation and Boredom Mechanics:**
  * Route attractiveness decreases dynamically as snow conditions degrade with each pass.
  * Athlete boredom per route is modeled using exponential smoothing: $z_t = \beta \cdot x_t + (1 - \beta)z_{t-1}$.
* **Statistics and Telemetry:** Tracks total lift ridership, capacity utilization, maximum queue lengths, and time-weighted average queue lengths.
* **LaTeX Visualization:** Exports `.tex` graph visualizations displaying resort parameters, end-of-day infrastructure statistics, and step-by-step trajectories of tracked athletes.

## Project Structure

```text
├── core/          # Simulation engine (Simulation), entry point (Main), and input parser (DataLoader)
├── entities/      # Athlete class hierarchy (AthleteArchetype, LocalAthlete, GreedyAthlete, CollectorAthlete)
├── events/        # DES event definitions and queue stability wrapper (Event, EventWrapper, etc.)
├── graph/         # Directed multigraph representation (Node, Edge, Route, Lift) and BFS algorithm
├── tests/         # JUnit unit tests (BFSTest, LiftTest)
└── kadra/mapki/   # External library for generating LaTeX graph visualizations
```

## Getting Started

### Prerequisites

* Java 21 or higher
* JUnit 5 (for running unit tests)
* Optional: LaTeX distribution (`pdflatex`) to compile `.tex` files into PDFs

### Compilation and Execution

1. Compile the source files:
   ```bash
   javac core/Main.java
   ```
2. Run the simulation by passing the output directory for `.tex` maps as a command-line argument and redirecting the input dataset via standard input:
   ```bash
   java -ea core.Main "output/maps/directory" < input-data.txt
   ```

### Compiling Visualizations to PDF

To render the generated `.tex` files into PDF maps, run:
```bash
pdflatex map-filename.tex
```

## Unit Tests

The `tests/` package contains JUnit test suites covering core logic:
* `BFSTest` – verifies shortest-path distance calculations and path reconstruction in directed multigraphs.
* `LiftTest` – verifies lift boarding capacity constraints, queue state transitions, and maximum queue length tracking.