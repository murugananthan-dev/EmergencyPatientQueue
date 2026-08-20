# Emergency Patient Priority Queue System

## Overview
A Java Swing desktop application that simulates a hospital emergency waiting room.
Patients are **automatically prioritized** using Java's `PriorityQueue` with a custom `Comparator`.

## Priority Rules
```
1. Higher Severity → treated first (Critical = 5, Minor = 1)
2. Equal Severity  → Older patient treated first
3. Equal Severity + Age → Patient who arrived earlier treated first
```

## Project Structure
```
EmergencyPatientQueue/
├── src/
│   ├── Patient.java        ← Data model (encapsulated fields, severity mapping)
│   ├── HospitalQueue.java  ← PriorityQueue + custom Comparator logic
│   ├── HospitalGUI.java    ← Java Swing user interface (dark theme)
│   └── Main.java           ← Application entry point (EDT launch)
└── out/                    ← Compiled .class files (generated)
```

## Requirements
- Java 17 or higher
- No external libraries or dependencies

## How to Compile & Run

### Windows (PowerShell)
```powershell
cd e:\EmergencyPatientQueue
javac -d out src\Patient.java src\HospitalQueue.java src\HospitalGUI.java src\Main.java
java -cp out Main
```

### Linux / macOS
```bash
cd /path/to/EmergencyPatientQueue
javac -d out src/Patient.java src/HospitalQueue.java src/HospitalGUI.java src/Main.java
java -cp out Main
```

## Features
| Feature | Description |
|---|---|
| Add Patient | Validates ID, name, age (1–120), severity; rejects duplicates |
| Treat Next | Removes highest-priority patient; shows details before removal |
| Search Patient | Finds by Patient ID; highlights row in table |
| Clear Fields | Resets all input fields |
| Exit | Confirmation dialog before closing |
| Priority Table | JTable auto-refreshes in correct treatment order |
| Severity Colors | Critical=red, Serious=orange, Moderate=yellow, Low=green, Minor=slate |

## Severity Levels
| Code | Label |
|---|---|
| 5 | Critical |
| 4 | Serious |
| 3 | Moderate |
| 2 | Low |
| 1 | Minor |

## Example Priority Order
| Priority | Patient | Severity | Age | Reason |
|---|---|---|---|---|
| 1 | Priya | 5-Critical | 68 | Highest severity |
| 2 | Ramesh | 5-Critical | 55 | Same severity, older |
| 3 | Rahul | 4-Serious | 45 | Next severity |
| 4 | Arjun | 3-Moderate | 21 | — |
| 5 | Neha | 2-Low | 32 | Lowest severity |
