# Sokoban Solver

An AI agent that automatically solves [Sokoban](https://en.wikipedia.org/wiki/Sokoban) puzzles by
computing a sequence of moves that pushes every crate onto a goal tile. The solver uses **A\* search**
over crate-configuration states, with a suite of pruning and heuristic techniques to keep the search
tractable within a strict time limit.

## Academic Context

This project was developed for **CSINTSY** (Introduction to Intelligent Systems) as a machine project.

The course provided the base project — the graphical interface, the map/file reader, the driver, the
run scripts, and the collection of `maps/`. Our work as students was to design and implement the
**search agent** that produces a valid solution string. Specifically, the following were **written by
us**:

- [`src/solver/SokoBot.java`](src/solver/SokoBot.java) — the full A\* search agent (`solveSokobanPuzzle`),
  including the heuristic, reachability BFS, path reconstruction, deadlock precomputation, and freeze-deadlock detection.
- [`src/solver/State.java`](src/solver/State.java) — the search-state representation used by A\* (crate
  configuration, cost fields `g`/`h`/`f`, hashing/equality for the closed set, and path reconstruction).

Everything else — the GUI, the file reader, the driver, the maps, and the launch scripts — was
**provided as part of the course template** and was not authored by us.

> The root-level `SokoBot.java` and `State.java` are copies of the solver files kept at the project
> root for convenience; the authoritative versions live in [`src/solver/`](src/solver/).

## Project Structure

```
Sokoban_Solver/
├── src/
│   ├── main/       Driver.java          — entry point (PROVIDED)
│   ├── gui/        GameFrame / GamePanel / BotThread — Swing UI (PROVIDED)
│   ├── reader/     FileReader / MapData — map parsing (PROVIDED)
│   ├── graphics/   PNG sprites for the board (PROVIDED)
│   └── solver/     SokoBot.java, State.java — the AI agent (OUR WORK)
├── maps/           Puzzle definitions, e.g. original1.txt (PROVIDED)
├── sokobot.bat / sokobot.sh    — run a map in bot (solver) mode
└── freeplay.bat / freeplay.sh  — play a map manually
```

## Map Format

Maps in [`maps/`](maps/) are plain-text grids using the standard Sokoban symbols:

| Symbol | Meaning              |
|:------:|----------------------|
| `#`    | Wall                 |
| `@`    | Player               |
| `$`    | Crate (box)          |
| `.`    | Goal / target        |
| `*`    | Crate on a goal      |
| `+`    | Player on a goal     |
| (space)| Empty floor          |

The maps are grouped by difficulty/size: `original1–3`, `twoboxes*`, `threeboxes*`, `fourboxes*`,
`fiveboxes*`, `custom1–8`, plus test maps such as `deadlocktest.txt` and `testlevel.txt`.

## How to Run

**Windows:**
```bat
sokobot.bat original1     :: run the solver on maps/original1.txt
freeplay.bat original1    :: play it yourself
```

**macOS / Linux:**
```bash
./sokobot.sh original1    # run the solver
./freeplay.sh original1   # play it yourself
```

Pass the map name **without** the `maps/` prefix or `.txt` extension. The scripts compile the sources
and launch `main.Driver` with the chosen map and mode.

## How the Solver Works

The agent searches over **crate configurations** (not raw player positions), treating each *push* as a
macro-move. Key components in [`SokoBot.java`](src/solver/SokoBot.java):

- **A\* search** — states are ordered by `f = g + 4·h` (weighted heuristic; see [`State.java`](src/solver/State.java)),
  with a closed set keyed on the crate set plus a *normalized* player position (the smallest reachable
  cell), so states differing only by reachable player position collapse into one.
- **Heuristic** — for each target, a BFS *pull-distance* grid (`computeTargetDistGrids`) precomputes the
  minimum pushes to bring a crate to that target ignoring other crates. `calculateHeuristic` then greedily
  matches crates to targets, penalizing conflicts. Any crate that can reach no target makes the state
  unsolvable.
- **Player reachability** — a BFS (`computeReachability`) determines which push positions the player can
  actually walk to, and `walkPath` reconstructs the exact floor-step sequence between pushes.
- **Deadlock pruning** — `precomputeDeadlocks` marks corner and edge cells from which a crate can never
  reach a goal, and `isFreezeDeadlock` rejects pushes that freeze a crate off-target.
- **Time limit** — the search aborts and returns no solution if it exceeds ~14.5 seconds.

The result is a string of moves (`u`, `d`, `l`, `r`) that the GUI replays to solve the board.
