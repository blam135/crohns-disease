package sim2d.organism.cells;

import sim.engine.SimState;
import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;
import sim2d.organism.impl.Cell_Impl;
import sim2d.organism.interfaces.Organism;
import sim2d.molecules.*;

/*
This cell has nothing to do with Crohn's Disease. It is there for the purposes of testing molecular secretion and
cellular behaviours.
 */
public class DummyCell extends Cell_Impl {

    public DummyCell(Compartment location) {
        super(location);
    }

    @Override
    protected void interactWithOtherOrganism(CrohnsSimulation sim, Organism organism) {
    }

    @Override
    protected void perceiveMolecules(CrohnsSimulation sim) {
    }

    protected void secreteMolecules() {
        compartment.receiveSecretedMolecules(IL23.instance, 100.0, this);
        compartment.receiveSecretedMolecules(IL1B.instance, 100.0, this);
        compartment.receiveSecretedMolecules(IL2.instance, 100.0, this);
        compartment.receiveSecretedMolecules(IL7.instance, 100.0, this);
    }

    @Override
    protected void proliferate() { }

    @Override
    protected double calculateAbsoluteTimeOfDeath() {
        return Double.MAX_VALUE;
    }

    @Override
    protected double calculateAbsoluteTimeOfProliferation() {
        return Double.MAX_VALUE;
    }

    @Override
    protected double getTimeOfDeathStdDev() {
        return Double.MAX_VALUE;
    }

    @Override
    protected double getTimeOfDeathMean() {
        return Double.MAX_VALUE;
    }

    @Override
    protected double getTimeOfProliferationStdDev() {
        return Double.MAX_VALUE;
    }

    @Override
    protected double getTimeOfProliferationMean() {
        return Double.MAX_VALUE;
    }

    @Override
    public void step(SimState state) {
        if (isDead)                                                        // do nothing if the cell was already phagocytosed earlier in this time frame.
            return;

        CrohnsSimulation simulation = (CrohnsSimulation) state;
        secreteMolecules();
    }

}
