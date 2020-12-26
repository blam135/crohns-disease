package sim2d.organism.impl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim.engine.SimState;
import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;
import sim2d.organism.interfaces.Organism;
import sim2d.organism.interfaces.TCell;

public abstract class TCell_Impl extends Cell_Impl implements TCell {

    protected boolean effector = true;  // Always true except for naive T Cells

    private TCell_Impl() {super(null);}

    public TCell_Impl(Compartment location) {
        super(location);
        timeOfDeath = calculateAbsoluteTimeOfDeath();
        timeOfProliferation = calculateAbsoluteTimeOfProliferation();
    }

    public TCell_Impl(Compartment location, TCell_Impl parent, boolean replaceParent) {
        super(location, parent, replaceParent);
        timeOfDeath = calculateAbsoluteTimeOfDeath();
        timeOfProliferation = calculateAbsoluteTimeOfProliferation();
    }

    @Override
    protected void interactWithOtherOrganism(CrohnsSimulation sim, Organism organism) {
        // Don't interact with other organisms for now
    }

    @Override
    public void step(SimState state) {
        if (isDead)                                                        // do nothing if the cell was already phagocytosed earlier in this time frame.
            return;

        CrohnsSimulation sim = (CrohnsSimulation) state;

        secreteMolecules();
        perceiveMolecules(sim);
        checkDeathMaintenanceTimer();
        checkProliferationMaintenanceTimer();
    }

    @Override
    public void perceiveMolecules(CrohnsSimulation sim) {
        // A T Cell generall doesn't perceive any molecules except FOXP3
    }

    @Override
    public boolean isEffector() {
        return effector;
    }

    public static void loadParameters(Document params) {
        Element pE = (Element) params.getElementsByTagName("TCell").item(0);
    }
}