package sim2d.organism.interfaces;

import sim2d.CrohnsSimulation;

public interface Phagocyte extends Cell {
    void phagocytoseCell(CrohnsSimulation sim, Organism organism);
    boolean isImmature();
}
