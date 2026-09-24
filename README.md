# Age of Cells

[![Java Version](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk)](https://openjdk.org)
[![Build Tool](https://img.shields.io/badge/Build-Gradle-02303A?logo=gradle)](https://gradle.org)
[![Dependency](https://img.shields.io/badge/Dependency-Lanterna-blue?logo=gnometerminal)](https://github.com/mabe02/lanterna)
[![License](https://img.shields.io/github/license/sma1lo/AgeOfCells)](https://github.com/sma1lo/AgeOfCells/blob/main/LICENSE)


[![Last Commit](https://img.shields.io/github/last-commit/sma1lo/AgeOfCells)](https://github.com/sma1lo/AgeOfCells/commits)
[![Commit Activity](https://img.shields.io/github/commit-activity/m/sma1lo/AgeOfCells)](https://github.com/sma1lo/AgeOfCells/graphs/commit-activity)
[![Repo Size](https://img.shields.io/github/repo-size/sma1lo/AgeOfCells)](https://github.com/sma1lo/AgeOfCells)
[![Code Size](https://img.shields.io/github/languages/code-size/sma1lo/AgeOfCells)](https://github.com/sma1lo/AgeOfCells)
[![Top Language](https://img.shields.io/github/languages/top/sma1lo/AgeOfCells)](https://github.com/sma1lo/AgeOfCells)


[![Stars](https://img.shields.io/github/stars/sma1lo/AgeOfCells?style=social)](https://github.com/sma1lo/AgeOfCells/stargazers)
[![Forks](https://img.shields.io/github/forks/sma1lo/AgeOfCells?style=social)](https://github.com/sma1lo/AgeOfCells/network/members)
[![Watchers](https://img.shields.io/github/watchers/sma1lo/AgeOfCells?style=social)](https://github.com/sma1lo/AgeOfCells/watchers)
[![Issues](https://img.shields.io/github/issues/sma1lo/AgeOfCells)](https://github.com/sma1lo/AgeOfCells/issues)
[![Closed Issues](https://img.shields.io/github/issues-closed/sma1lo/AgeOfCells)](https://github.com/sma1lo/AgeOfCells/issues?q=is%3Aissue+is%3Aclosed)
[![Pull Requests](https://img.shields.io/github/issues-pr/sma1lo/AgeOfCells)](https://github.com/sma1lo/AgeOfCells/pulls)
[![Contributors](https://img.shields.io/github/contributors/sma1lo/AgeOfCells)](https://github.com/sma1lo/AgeOfCells/graphs/contributors)

An interactive, geopolitical world simulation engine written in Java with **[Lanterna](https://github.com/mabe02/lanterna)**.
Age of Cells models emergent historic dynamics on a grid, including territorial expansion, economic resource gathering, and state-driven diplomacy with master-vassal hierarchies and rebellion mechanics.

<p align="center">
  <img src="assets/preview.gif" alt="Age of Cells Simulation Preview" width="80%">
</p>

---

## Features

* **Nation Expansion:** Nations expand over land and build limited fleets (max 7 ships).
* **Resources:** Gold, Iron and Coal deposits give significant economic bonuses.
* **Diplomacy:** Peace, War and temporary Unions. Capital capture can turn nations into vassals.
* **Vassal System:** Masters collect tribute. Vassals have Liberty Desire and can rebel.
* **Economy:** Income from land, capitals, ships and resources.
* **Marauder:** Marauders appear randomly at the start of the game and when states collapse.
* **Lanterna TUI:** Smooth terminal rendering with colors and keyboard controls.

---

## Controls

| Key | Action              |
|-----|---------------------|
| `Q` | Quit the simulation |

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
| `P`    | Port             |
| `c`    | Camp             |
| `$`    | Gold             |
| `#`    | Iron             |
| `*`    | Coal             |
| `m`    | Marauder         |
| `0`    | Unclaimed ground |

> [!NOTE]
>  Colored symbols belong to active sovereign nations. White symbols represent neutral elements.


---

## Project Structure

```
AgeOfCells/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── aoc/
│       │           ├── Launcher.java
│       │           ├── World.java
│       │           ├── GameLoop.java
│       │           ├── cell/
│       │           │   ├── Cell.java
│       │           │   ├── CellType.java
│       │           │   └── TerrainType.java
│       │           ├── diplomacy/
│       │           │   └── DiplomacyManager.java
│       │           ├── economy/
│       │           │   └── EconomyManager.java
│       │           ├── map/
│       │           │   └── MapGenerator.java
│       │           ├── nation/
│       │           │   ├── Nation.java
│       │           │   └── SituationState.java
│       │           ├── render/
│       │           │   └── WorldRenderer.java
│       │           ├── config/
│       │           │   └── Config.java
│       │           └── util/
│       │               ├── Color.java
│       │               ├── Element.java
│       │               ├── Time.java
│       │               ├── NationGenerator.java
│       │               ├── Loader.java
│       │               └── Rng.java
│       └── resources/
│           └── config.yaml
├── assets/
│   └── preview.gif
├── build.gradle
├── gradlew
├── gradlew.bat
├── settings.gradle
├── .gitignore
├── LICENSE
└── README.md

```

---

## Configuration

Edit `src/main/resources/config.yaml`:

```yaml
width: 209
height: 51
smooth: 4
tickDelayMs: 50
nations: 50
```

---

## Getting Started

### Prerequisites

* JDK 17 or higher
* Git

### Installation & Run

1. **Clone the repository:**

    **Linux / macOS:**
   ```bash
   git clone https://github.com/sma1lo/AgeOfCells.git && cd AgeOfCells
   ```
    **Windows:**
   ```bash
   cd Desktop
   ```
   ```bash
   git clone https://github.com/sma1lo/AgeOfCells.git
   ```
   ```bash
   cd AgeOfCells
   ```
2. **Build the executable JAR file:** 

    **Linux / macOS:**
   ```bash
   chmod +x gradlew && ./gradlew build
   ```
   **Windows:**
   ```bash
   .\gradlew.bat build
   ```
   
3. **Run the simulation:**
   ```bash
   java -jar build/libs/AgeOfCells-1.0-SNAPSHOT.jar
   ```
