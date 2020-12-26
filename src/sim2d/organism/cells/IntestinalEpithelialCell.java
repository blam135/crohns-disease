package sim2d.organism.cells;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim.engine.SimState;
import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;
import sim2d.organism.impl.Cell_Impl;
import sim2d.organism.interfaces.Organism;
import sim2d.molecules.*;
import sim2d.organism.microbes.Microbe;

import java.util.HashMap;

public class IntestinalEpithelialCell extends Cell_Impl {

    // Homeostasis properties
    private static double homeostaticIL33SecretedPerTimeSlice;
    private static double homeostaticIL17ActivationThreshold;
    private static double homeostaticAntiMicrobialPeptideSecretedPerTimeSlice;
    private static double homeostaticIL15SecretedPerTimeSlice;
    private static double homeostaticIL7SecretedPerTimeSlice;
    private static double homeostaticIL10ActivationThreshold;
    private static double homeostaticTGFBActivationThreshold;

    private static double homeostaticIL6MucusActivationThreshold;
    private static double homeostaticIL22MucusActivationThreshold;
    private static double homeostaticIL18MucusBlockageThreshold;
    private static double homeostaticMucusSecretedPerTimeSlice;

    private static double homeostaticProliferationMean;
    private static double homeostaticProliferationStdDev;
    private static double homeostaticIL10ProliferationActivationThreshold;
    private static double homeostaticIL22ProliferationActivationThreshold;
    private static double homeostaticIL17ProliferationActivationThreshold;
    private static double homeostaticIL6ProliferationActivationThreshold;

    // Inflammatory Properties
    private static double inflammatoryIL18ActivationThreshold;
    private static double inflammatoryIL1BActivationThreshold;
    private static double inflammatoryTNFActivationThreshold;
    private static double inflammatoryTNFSecretedPerTimeSlice;

    // Apoptotic Properties
    private static double apoptoticTNFActivationThreshold;
    private static double apoptoticIL17ActivationThreshold;
    private static double apoptoticIFNyActivationThreshold;

    private enum STATE {HOMEOSTASIS, INFLAMMATORY, APOPTOSIS}

    private STATE currentState;

    public IntestinalEpithelialCell(Compartment location) {
        super(location);
        currentState = STATE.HOMEOSTASIS;
    }

    public IntestinalEpithelialCell(Compartment location, IntestinalEpithelialCell cell, boolean replaceParent) {
        super(location, cell, replaceParent);
        currentState = STATE.HOMEOSTASIS;
    }

    public boolean isInflammatory() {
        return currentState == STATE.INFLAMMATORY;
    }

    public boolean isHomeostatic() { return currentState == STATE.HOMEOSTASIS; }

    @Override
    protected void interactWithOtherOrganism(CrohnsSimulation sim, Organism organism) {
        if (currentState == STATE.HOMEOSTASIS && organism instanceof Microbe) {
            compartment.receiveSecretedMolecules(IL15.instance, homeostaticIL15SecretedPerTimeSlice, this);
            compartment.receiveSecretedMolecules(IL7.instance, homeostaticIL7SecretedPerTimeSlice, this);
        }
    }

    @Override
    protected void secreteMolecules() {
        switch (currentState) {
            case HOMEOSTASIS:
                compartment.receiveSecretedMolecules(IL33.instance, homeostaticIL33SecretedPerTimeSlice, this);
                break;
            case INFLAMMATORY:
                compartment.receiveSecretedMolecules(TNF.instance, inflammatoryTNFSecretedPerTimeSlice, this);
                break;
        }
    }

    @Override
    protected void perceiveMolecules(CrohnsSimulation sim) {
        double getTNF = compartment.getConcentrationMolecule(TNF.instance, this);
        double getIL10 = compartment.getConcentrationMolecule(IL10.instance, this);
        double getTGFB = compartment.getConcentrationMolecule(TGFB.instance, this);
        switch (currentState) {
            case HOMEOSTASIS:
                double getIL18 = compartment.getConcentrationMolecule(IL18.instance, this);
                double getIL1B = compartment.getConcentrationMolecule(IL1B.instance, this);
                double getIFNy = compartment.getConcentrationMolecule(IFNy.instance, this);
                double getIL17 = compartment.getConcentrationMolecule(IL17.instance, this);
                double getIL22 = compartment.getConcentrationMolecule(IL22.instance, this);
                double getIL6 = compartment.getConcentrationMolecule(IL6.instance, this);

                // Anti Microbial Secretion
                if (getIL17 >= homeostaticIL17ActivationThreshold) {
                    compartment.receiveSecretedMolecules(AntiMicrobialPeptide.instance, homeostaticAntiMicrobialPeptideSecretedPerTimeSlice, this);
                }

                // Mucus Secretion
                if ((getIL6 >= homeostaticIL6MucusActivationThreshold ||
                        getIL22 >= homeostaticIL22MucusActivationThreshold)
                        && getIL18 < homeostaticIL18MucusBlockageThreshold) {
                    compartment.receiveSecretedMolecules(Mucus.instance, homeostaticMucusSecretedPerTimeSlice, this);
                }

                // State transitions
                if ((getIFNy >= apoptoticIFNyActivationThreshold &&
                        getTNF >= apoptoticTNFActivationThreshold) ||
                        getIL17 >= apoptoticIL17ActivationThreshold) {
                    currentState = STATE.APOPTOSIS;
                    isApoptotic = true;
                    timeOfProliferation = Double.MAX_VALUE;
                } else if (getIL1B >= inflammatoryIL1BActivationThreshold ||
                        getIL18 >= inflammatoryIL18ActivationThreshold ||
                        getTNF >= inflammatoryTNFActivationThreshold) {
                    currentState = STATE.INFLAMMATORY;
                    timeOfProliferation = Double.MAX_VALUE;
                }
                break;
            case INFLAMMATORY:
                if (getIL10 >= homeostaticIL10ActivationThreshold ||
                        getTGFB >= homeostaticTGFBActivationThreshold) {
                    currentState = STATE.HOMEOSTASIS;
                } else if (getTNF >= apoptoticTNFActivationThreshold) {
                    currentState = STATE.APOPTOSIS;
                    isApoptotic = true;
                }
                break;
        }
    }

    @Override
    protected void checkProliferationMaintenanceTimer() {
        if(CrohnsSimulation.sim.schedule.getTime() >= timeOfProliferation) {
            double IL22Amount = compartment.getConcentrationMolecule(IL22.instance, this);
            double IL10Amount = compartment.getConcentrationMolecule(IL10.instance, this);
            double IL17Amount = compartment.getConcentrationMolecule(IL17.instance, this);
            double IL6Amount = compartment.getConcentrationMolecule(IL6.instance, this);

            double currentTime = CrohnsSimulation.sim.schedule.getTime();

            if (currentState == STATE.HOMEOSTASIS &&
                    currentTime >= timeOfProliferation &&
                    (IL22Amount >= homeostaticIL22ProliferationActivationThreshold ||
                    IL10Amount >= homeostaticIL10ProliferationActivationThreshold ||
                    IL17Amount >= homeostaticIL17ProliferationActivationThreshold ||
                    IL6Amount >= homeostaticIL6ProliferationActivationThreshold))
            {
                proliferate();
                timeOfProliferation = Double.MAX_VALUE;				// clear the timer.
            }
        }
    }

    @Override
    protected void proliferate() {
        new IntestinalEpithelialCell(compartment, this, false);
    }

    @Override
    protected double getTimeOfDeathStdDev() {
        return Double.MAX_VALUE; // IEC will die by being phagocytosed rather than via time
    }

    @Override
    protected double getTimeOfDeathMean() {
        return Double.MAX_VALUE; // IEC will die by being phagocytosed rather than via time
    }

    @Override
    protected double getTimeOfProliferationStdDev() {
        return homeostaticProliferationStdDev;
    }

    @Override
    protected double getTimeOfProliferationMean() {
        return homeostaticProliferationMean;
    }

    @Override
    public void step(SimState state) {
        if (isDead)                                                        // do nothing if the cell was already phagocytosed earlier in this time frame.
            return;

        CrohnsSimulation simulation = (CrohnsSimulation) state;

        interactWithOtherOrganismsGeneric(simulation);						// handles interaction with other neighbouring organisms.
        if (currentState != STATE.APOPTOSIS) {
            secreteMolecules();
        }
        perceiveMolecules(simulation);
        checkProliferationMaintenanceTimer();
    }

    public static void loadParameters(Document params) {
        Element pE = (Element) params.getElementsByTagName("IntestinalEpithelialCell").item(0);
        Element homeostasis = (Element) pE.getElementsByTagName("Homeostasis").item(0);
        Element homeostasisMucusSecretion = (Element) homeostasis.getElementsByTagName("MucusSecretion").item(0);
        Element homeostasisProliferation = (Element) homeostasis.getElementsByTagName("Proliferation").item(0);
        Element inflammation = (Element) pE.getElementsByTagName("Inflammation").item(0);
        Element apoptosis = (Element) pE.getElementsByTagName("Apoptosis").item(0);

        homeostaticIL33SecretedPerTimeSlice = Double.parseDouble(homeostasis.getElementsByTagName("IL33SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        homeostaticIL17ActivationThreshold = Double.parseDouble(homeostasis.getElementsByTagName("IL17ActivationThreshold").item(0).getTextContent()) ;
        homeostaticAntiMicrobialPeptideSecretedPerTimeSlice = Double.parseDouble(homeostasis.getElementsByTagName("antiMicrobialPeptideSecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        homeostaticIL15SecretedPerTimeSlice = Double.parseDouble(homeostasis.getElementsByTagName("IL15SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        homeostaticIL7SecretedPerTimeSlice = Double.parseDouble(homeostasis.getElementsByTagName("IL7SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        homeostaticIL10ActivationThreshold = Double.parseDouble(homeostasis.getElementsByTagName("IL10ActivationThreshold").item(0).getTextContent());
        homeostaticTGFBActivationThreshold = Double.parseDouble(homeostasis.getElementsByTagName("TGFBActivationThreshold").item(0).getTextContent());

        homeostaticIL6MucusActivationThreshold = Double.parseDouble(homeostasisMucusSecretion.getElementsByTagName("IL6ActivationThreshold").item(0).getTextContent());
        homeostaticIL22MucusActivationThreshold = Double.parseDouble(homeostasisMucusSecretion.getElementsByTagName("IL22ActivationThreshold").item(0).getTextContent());
        homeostaticIL18MucusBlockageThreshold = Double.parseDouble(homeostasisMucusSecretion.getElementsByTagName("IL18BlockageThreshold").item(0).getTextContent());
        homeostaticMucusSecretedPerTimeSlice = Double.parseDouble(homeostasisMucusSecretion.getElementsByTagName("MucusSecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;

        homeostaticProliferationMean = Double.parseDouble(homeostasisProliferation.getElementsByTagName("proliferationMean").item(0).getTextContent());
        homeostaticProliferationStdDev = Double.parseDouble(homeostasisProliferation.getElementsByTagName("proliferationStdDev").item(0).getTextContent());
        homeostaticIL10ProliferationActivationThreshold = Double.parseDouble(homeostasisProliferation.getElementsByTagName("IL10ActivationThreshold").item(0).getTextContent());
        homeostaticIL22ProliferationActivationThreshold = Double.parseDouble(homeostasisProliferation.getElementsByTagName("IL22ActivationThreshold").item(0).getTextContent());
        homeostaticIL17ProliferationActivationThreshold = Double.parseDouble(homeostasisProliferation.getElementsByTagName("IL17ActivationThreshold").item(0).getTextContent());
        homeostaticIL6ProliferationActivationThreshold = Double.parseDouble(homeostasisProliferation.getElementsByTagName("IL6ActivationThreshold").item(0).getTextContent());

        inflammatoryIL18ActivationThreshold = Double.parseDouble(inflammation.getElementsByTagName("Il1BActivationThreshold").item(0).getTextContent());
        inflammatoryIL1BActivationThreshold = Double.parseDouble(inflammation.getElementsByTagName("IL18ActivationThreshold").item(0).getTextContent());
        inflammatoryTNFActivationThreshold = Double.parseDouble(inflammation.getElementsByTagName("TNFActivationThreshold").item(0).getTextContent());
        inflammatoryTNFSecretedPerTimeSlice = Double.parseDouble(inflammation.getElementsByTagName("TNFSecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;

        apoptoticTNFActivationThreshold = Double.parseDouble(apoptosis.getElementsByTagName("TNFActivationThreshold").item(0).getTextContent());
        apoptoticIL17ActivationThreshold = Double.parseDouble(apoptosis.getElementsByTagName("IL17ActivationThreshold").item(0).getTextContent());
        apoptoticIFNyActivationThreshold = Double.parseDouble(apoptosis.getElementsByTagName("IFNyActivationThreshold").item(0).getTextContent());

    }
}
