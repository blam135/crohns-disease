package sim2d.organism.microbes;

import sim.engine.SimState;
import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;
import sim2d.organism.impl.Organism_Impl;
import sim2d.organism.interfaces.Organism;

public class Microbe extends Organism_Impl {

    public Microbe(Compartment location) {
        super(location);
    }

    @Override
    protected void interactWithOtherOrganism(CrohnsSimulation sim, Organism organism) {

    }

    @Override
    protected void perceiveMolecules(CrohnsSimulation sim) {

    }

    @Override
    public void step(SimState simState) {
        // Doesnt' do much but roam around, which is controlled by the epithelium class
    }
}
