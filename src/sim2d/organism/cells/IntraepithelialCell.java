package sim2d.organism.cells;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim.engine.SimState;
import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;
import sim2d.organism.impl.Cell_Impl;
import sim2d.organism.interfaces.Organism;
import sim2d.organism.microbes.Microbe;
import sim2d.molecules.*;

public class IntraepithelialCell extends Cell_Impl {

    private boolean isInactive = true;

    // Proliferation
    private static double proliferationTimeStdDev;
    private static double proliferationTimeMean;

    // Death
    private static double deathTimeStdDev;
    private static double deathTimeMean;

    // Cytokine Properties
    private static double IL17SecretedPerTimeSlice;
    private static double IL6SecretedPerTimeSlice;
    private static double IL22SecretedPerTimeSlice;
    private static double TGFBSecretedPerTimeSlice;
    private static double IL15ActivationThreshold;

    // IELs should have a location passed in as a param
    private IntraepithelialCell() { super(null); }

    public IntraepithelialCell(Compartment location) {
        super(location);
    }

    public IntraepithelialCell(Compartment location, IntraepithelialCell cell, boolean replaceParent, boolean inActive) {
        super(location, cell, replaceParent);
        isInactive = inActive;
    }

    public boolean isInactive() {return isInactive;}

    @Override
    protected void interactWithOtherOrganism(CrohnsSimulation sim, Organism organism) {
        if (isInactive && organism instanceof Microbe) {
            isInactive = false;
        } else if (organism instanceof IntestinalEpithelialCell && ((IntestinalEpithelialCell) organism).isApoptotic() && !isInactive) {
            compartment.receiveSecretedMolecules(TGFB.instance, TGFBSecretedPerTimeSlice, this);
        }
    }

    @Override
    protected void perceiveMolecules(CrohnsSimulation sim) {
        if (!isInactive) {
            double IL15Amount = compartment.getConcentrationMolecule(IL15.instance, this);
            double currentTime = CrohnsSimulation.sim.schedule.getTime();
            if (IL15Amount > IL15ActivationThreshold && currentTime >= timeOfProliferation) {
                proliferate();
                timeOfProliferation = Double.MAX_VALUE;
            }
        }
    }

    @Override
    protected void secreteMolecules() {
        if (!isInactive) {
            compartment.receiveSecretedMolecules(IL17.instance, IL17SecretedPerTimeSlice, this);
            compartment.receiveSecretedMolecules(IL6.instance, IL6SecretedPerTimeSlice, this);
            compartment.receiveSecretedMolecules(IL22.instance, IL22SecretedPerTimeSlice, this);
        }
    }

    @Override
    protected void proliferate() {
        new IntraepithelialCell(compartment, this, false, true);
    }

    @Override
    protected double getTimeOfDeathStdDev() {
        return deathTimeStdDev;
    }

    @Override
    protected double getTimeOfDeathMean() {
        return deathTimeMean;
    }

    @Override
    protected double getTimeOfProliferationStdDev() {
        return proliferationTimeStdDev;
    }

    @Override
    protected double getTimeOfProliferationMean() {
        return proliferationTimeMean;
    }

    @Override
    public void step(SimState state) {
        if (isDead)                                                        // do nothing if the cell was already phagocytosed earlier in this time frame.
            return;

        CrohnsSimulation simulation = (CrohnsSimulation) state;

        interactWithOtherOrganismsGeneric(simulation);                        // handles interaction with other neighbouring organisms.
        perceiveMolecules(simulation);
        secreteMolecules();
        checkDeathMaintenanceTimer();
    }

    public static void loadParameters(Document params) {
        Element pE = (Element) params.getElementsByTagName("IntraepithelialCell").item(0);

        IL17SecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("IL17SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        IL6SecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("IL6SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        IL22SecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("IL22SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        TGFBSecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("TGFBSecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;

        IL15ActivationThreshold = Double.parseDouble(pE.getElementsByTagName("IL15ActivationThreshold").item(0).getTextContent());

        proliferationTimeStdDev = Double.parseDouble(pE.getElementsByTagName("proliferationTimeStdDev").item(0).getTextContent());
        proliferationTimeMean = Double.parseDouble(pE.getElementsByTagName("proliferationTimeMean").item(0).getTextContent());

        deathTimeStdDev = Double.parseDouble(pE.getElementsByTagName("timeOfDeathStdDev").item(0).getTextContent());
        deathTimeMean = Double.parseDouble(pE.getElementsByTagName("timeOfDeathMean").item(0).getTextContent());
    }

}
