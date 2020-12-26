package sim2d.organism.impl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim.engine.SimState;
import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;
import sim2d.organism.interfaces.Phagocyte;
import sim2d.organism.interfaces.Organism;

public abstract class Phagocyte_Impl extends Cell_Impl implements Phagocyte {

    private static double probOfPhagocytosis;

    private Phagocyte_Impl() { super(null); }

    public Phagocyte_Impl(Compartment location) {
        super(location);
    }

    @Override
    protected void proliferate() {
        return; // APCs do not proliferate
    }
    @Override
    protected double calculateAbsoluteTimeOfProliferation() {
        return Double.MAX_VALUE; // APCs do not proliferate
    }
    @Override
    protected double getTimeOfProliferationMean() {
        return Double.MAX_VALUE;// APCs do not proliferate
    }
    @Override
    protected double getTimeOfProliferationStdDev() {
        return Double.MAX_VALUE; // APCs do not proliferate
    }
    @Override
    public abstract boolean isImmature();
    public abstract boolean isProtective();

    @Override
    // Phagocytes must not die if they're immature
    protected void checkDeathMaintenanceTimer() {
        if (isImmature()) {
            timeOfDeath += CrohnsSimulation.getTimeSlice();
            return;
        }
        super.checkDeathMaintenanceTimer();
    }

    @Override
    public void phagocytoseCell(CrohnsSimulation sim, Organism organism) {
        /* If organism is dead or the current Phagocyte is apoptotic or in the odd chance
        * the probability is AGAINST phagocytosis then don't do anything
        */
        if (organism.isDead() || this.isApoptotic() || sim.random.nextDouble() > probOfPhagocytosis) {
            return;
        }

        organism.removeOrganismFromSimulation(sim);
        performPhagocytosisOfCell(organism);
    }

    @Override
    public void step(SimState state) {
        if (isDead() || isApoptotic())                                        // do nothing if the cell was already phagocytosed earlier in this timeframe.
            return;

        final CrohnsSimulation simulation = (CrohnsSimulation) state;

        perceiveMolecules(simulation);                        // examine the cytokine mix in the APCs neighbourhood, and potentially upregulate co-stimulatory molecules.
        interactWithOtherOrganismsGeneric(simulation);
        secreteMolecules();
        checkDeathMaintenanceTimer();                        // this comes last because it handles this cell becoming apoptotic, and when that happens none of the other steps should be completed.
   }

    protected abstract void performPhagocytosisOfCell(Organism organism);

    public static void loadParameters(Document params) {
        Element pE = (Element) params.getElementsByTagName("Phagocyte").item(0);

        probOfPhagocytosis = Double.parseDouble(pE.getElementsByTagName("probOfPhagocytosis").item(0).getTextContent());
    }

}