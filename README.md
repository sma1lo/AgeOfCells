# Age of Cells

An interactive, CLI-based geopolitical world simulation engine written in Java. Age of Cells models emergent historic dynamics on a grid, including territorial expansion, economic resource gathering, and state-driven diplomacy with master-vassal hierarchies and rebellion mechanics.

<p align="center">
  <img src="assets/preview.png" alt="Age of Cells Simulation Preview" width="80%">
</p>

---

## Features

* **Nation Expansion:** Nations expand over land and build limited fleets (max 7 ships).
* **Resources:** Gold, Iron and Coal deposits give significant economic bonuses.
* **Diplomacy:** Peace, War and temporary Unions. Capital capture can turn nations into vassals.
* **Vassal System:** Masters collect tribute. Vassals have Liberty Desire and can rebel.
* **Economy:** Income from land, capitals, ships and resources.

---

## Map Legend

| Symbol | Meaning          |
|--------|------------------|
| `A`    | Capital          |
| `a`    | Controlled land  |
| `^`    | Ship             |
| `~`    | Water            |
| `C`    | Castle           |
| `T`    | Town             |
| `v`    | Village          |
| `$`    | Gold             |
| `#`    | Iron             |
| `*`    | Coal             |
| `0`    | Unclaimed ground |

Colored symbols belong to different nations.  
Uppercase letter = capital of the nation.

---

## Project Structure

```
AgeOfCells/
├── src/
│   └── com/
│       └── aoc/
│           ├── Launcher.java
│           ├── World.java
│           ├── GameLoop.java
│           ├── cell/
│           │   ├── Cell.java
│           │   ├── CellType.java
│           │   └── TerrainType.java
│           ├── diplomacy/
│           │   └── DiplomacyManager.java
│           ├── map/
│           │   └── MapGenerator.java
│           ├── nation/
│           │   ├── Nation.java
│           │   ├── NationType.java
│           │   └── SituationState.java
│           ├── render/
│           │   └── WorldRenderer.java
│           ├── config/
│           │   └── Config.java
│           └── util/
│               ├── Color.java
│               ├── Element.java
│               ├── Screen.java
│               ├── Time.java
│               ├── Loader.java
│               └── Rng.java
├── assets/
│   └── preview.png
├── .gitignore
├── LICENSE
└── README.md

```

---

## Getting Started

### Prerequisites

* JDK 17 or higher
* Git

### Installation & Run

1. **Clone the repository:**
   ```bash
   git clone https://github.com/sma1lo/AgeOfCells.git
   cd AgeOfCells
   ```
2. **Compile the project:**
   ```bash
   javac -d bin src/com/aoc/*.java src/com/aoc/*/*.java
   ```
3. **Run the simulation:**
   ```bash
   java -cp bin com.aoc.Launcher
   ```
