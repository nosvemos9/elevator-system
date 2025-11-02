# Smart Elevator System (Simulation)

A production-quality, testable Java simulation of a multi-elevator dispatch system.
It logs every step into SQLite (`data/elevator.db`) and prints human-readable state to console.

## Features
- Multi-elevator, multi-floor simulation
- Directional scheduling & en-route pickup
- Predictive traffic hint (simple heuristic)
- Weight & capacity constraints
- Step-by-step state logging to SQLite (`step_logs`)

## Quick Start
```bash
# build
mvn -q clean compile

# run (PowerShell)
mvn -q exec:java "-DskipTests=true"
# or (CMD)
mvn -q exec:java -DskipTests=true
