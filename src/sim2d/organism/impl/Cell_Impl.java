package sim2d.organism.impl;

import org.w3c.dom.Document;
import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;
import sim2d.organism.interfaces.Cell;
import sim2d.organism.interfaces.Organism;

public abstract class Cell_Impl extends Organism_Impl implements Cell {
    protected boolean isApoptotic;
    protected double timeOfDeath = Double.MAX_VALUE;
    protected double timeOfProliferation = Double.MAX_VALUE;

    private Cell_Impl() { super(null); }

    public Cell_Impl(Compartment location) {
        super(location);
        timeOfDeath = calculateAbsoluteTimeOfDeath();
        timeOfProliferation = calculateAbsoluteTimeOfProliferation();
    }

    public Cell_Impl(Compartment location, Organism parent, boolean replaceParent) {
        super(location, parent, replaceParent);
        timeOfDeath = calculateAbsoluteTimeOfDeath();
        timeOfProliferation = calculateAbsoluteTimeOfProliferation();
    }

    protected void checkDeathMaintenanceTimer() {
        if (CrohnsSimulation.sim.schedule.getTime() >= timeOfDeath) {
            timeOfDeath = Double.MAX_VALUE;                                // reset timer
            die();
        }
    }

    protected void checkProliferationMaintenanceTimer() {
        if(CrohnsSimulation.sim.schedule.getTime() >= timeOfProliferation) {
            proliferate();								// proliferate, hence releasing a new naive daughter cell
            timeOfProliferation = Double.MAX_VALUE;				// clear the timer.
        }
    }

    // Assumed Normally Distributed, unless overriden in concrete class
    protected double calculateAbsoluteTimeOfDeath() {
        if (getTimeOfDeathMean() == Double.MAX_VALUE || getTimeOfDeathStdDev() == Double.MAX_VALUE) {
            return Double.MAX_VALUE;
        }

        double interval = CrohnsSimulation.sim.random.nextGaussian();
        interval *= (getTimeOfDeathStdDev() / 2.0);
        interval += getTimeOfDeathMean();
        return interval + CrohnsSimulation.sim.schedule.getTime();
    }

    // Assumed Normally Distributed, unless overriden in concrete class
    protected double calculateAbsoluteTimeOfProliferation() {
        if (getTimeOfProliferationMean() == Double.MAX_VALUE || getTimeOfProliferationStdDev() == Double.MAX_VALUE) {
            return Double.MAX_VALUE;
        }
        double interval = CrohnsSimulation.sim.random.nextGaussian();
        interval *= (getTimeOfProliferationStdDev() / 2.0);
        interval += getTimeOfProliferationMean();
        return interval + CrohnsSimulation.sim.schedule.getTime();
    }

    protected void die() {
        /* stop this cell from interacting with others */
        isApoptotic = true;
        timeOfDeath = Double.MAX_VALUE;
        timeOfProliferation = Double.MAX_VALUE;
        removeOrganismFromSimulation(CrohnsSimulation.sim);
    }

    @Override
    public boolean isApoptotic() {
        return isApoptotic;
    }

    // Protected Methods cannot be declared in the interface so do here
    // https://stackoverflow.com/a/9046029/9921724
    protected abstract void secreteMolecules();
    protected abstract void proliferate();
    protected abstract double getTimeOfDeathStdDev();
    protected abstract double getTimeOfDeathMean();
    protected abstract double getTimeOfProliferationStdDev();
    protected abstract double getTimeOfProliferationMean();

    public static void loadParameters(Document parameters) {
        // Do nothing for now
    }
}
