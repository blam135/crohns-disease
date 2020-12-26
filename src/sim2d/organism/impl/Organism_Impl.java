package sim2d.organism.impl;

import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;
import sim2d.organism.interfaces.Organism;

public abstract class Organism_Impl implements Organism {
    public Compartment compartment;            // the compartment where this cell resides.
    protected boolean isDead;

    private Organism_Impl() { }

    public Organism_Impl(Compartment location) {
        if (location == null)
            throw new RuntimeException("null location!");

        scheduleOrganism();

        compartment = location;
        compartment.placeCellRandomlyInCompartmentCloseIfOccupied(this);
    }

    public Organism_Impl(Compartment location, Organism parent, boolean replaceParent) {
        scheduleOrganism();
        compartment = location;
        if (!replaceParent) {
            compartment.receiveDaughterCell(this, parent);
        } else {
            compartment.replaceParentCell(this, parent);
        }
    }

    @Override
    public void migrateIntoCompartment(Compartment newCompartment) {
        compartment = newCompartment;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    protected final void interactWithOtherOrganismsGeneric(CrohnsSimulation simulation) {
        Organism[] neighbours = compartment.getNeighbours(this);
        if (neighbours.length <= 0)                                // if there's nothing to interact with then we can return.
            return;
        for (Organism otherOrganism : neighbours)                        // iterate over neighbours, and perform cell specific interactions
        {
            interactWithOtherOrganism(simulation, otherOrganism);
        }
    }
    private void scheduleOrganism() {
        final CrohnsSimulation sim = CrohnsSimulation.sim;
        double time = sim.schedule.getTime() + CrohnsSimulation.timeSlice;
        if (time < sim.schedule.EPOCH)
            time = sim.schedule.EPOCH;

        sim.addOrganismToSimulationScheduleRepeating(this, time);            // critical that we do this! Or the cell will not be stepped!
    }
    public void removeOrganismFromSimulation(CrohnsSimulation sim) {
        isDead = true;                                                            // prevent further stepping of this cell after it is dead.
        sim.removeFromSimulationSchedule(this);                                    // critical, remove this cell from the simulation's schedule.
        compartment.removeOrganismFollowingDeath(this);                                // remove cell from compartment.
    }

    protected abstract void interactWithOtherOrganism(CrohnsSimulation sim, Organism organism);
    protected abstract void perceiveMolecules(CrohnsSimulation sim);
}
