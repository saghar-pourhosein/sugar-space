# SugarSpace 🍬

A Java project that simulates a small artificial society, based on the **Sugarscape** model (Epstein & Axtell).

Agents live on a 51×51 grid. They move around, collect sugar and spice, grow older, have children, trade with each other, give and take loans, and can get sick and recover.

This project was built in 8 phases for a university course. Each phase adds something new.

## The 8 phases

1. **Movement** — Agents move to find sugar. Sugar grows back over time.
2. **Migration waves** — Agents start close together and move in big groups.
3. **Reproduction** — Agents grow old, have children, and can die of old age.
4. **Spice** — A second resource is added. Agents now need both sugar and spice.
5. **Trading** — Agents trade sugar and spice with their neighbors.
6. **Loans** — Rich agents can lend resources to poor agents, with interest.
7. **Disease** — Agents can get sick, pass diseases to others, and heal over time.
8. **Everything together** — All the rules work together at once.

## Project files

```
Main.java                    Starts the simulation and draws it on screen
com/sugarspace/
 ├── World.java               The grid, and all the rules of the simulation
 ├── Agent.java               One agent (a person living in the world)
 ├── Patch.java                One square of the grid
 ├── Disease.java              The list of diseases in the world
 └── Loan.java                 One loan between two agents
```

## How to run it

1. Open the project in IntelliJ IDEA (or another Java IDE).
2. Make sure the `StdDraw` library is added to the project.
3. Run `Main.java`.

A window will open showing the world. You will see:
- Colored squares (orange = sugar, purple = spice).
- Small colored dots = agents (blue = male, red = female, gray = too old to have children).

## About this project

This is a student project. It was made to learn how to build a simulation step by step, not to be a perfect or professional program. You are welcome to read through the code.

