package sim2d.organism.cells;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;
import sim2d.organism.impl.Phagocyte_Impl;
import sim2d.organism.interfaces.Organism;
import sim2d.molecules.*;

public class Macrophage extends Phagocyte_Impl {

    // AntiInflammatory
    private static double TGFSecretedPerTimeSlice;
    private static double IL10SecretedPerTimeSlice;

    // ProInflammatory
    // Low Secretion
    private static double IL1BSecretedPerTimeSlice;
    private static double IL18SecretedPerTimeSlice;
    private static double IL12SecretedPerTimeSlice;
    // High Secretion
    private static double IL23SecretedPerTimeSlice;
    private static double TNFSecretedPerTimeSlice;
    private static double IL6SecretedPerTimeSlice;

    // Activation Thresholds
    private static double IL10ActivationThreshold;
    private static double IFNyActivationThreshold;
    private static double IL17ActivationThreshold;
    private static double GMCSFActivationThreshold;


    // State Related
    private enum STATE {M1, M2, RECRUITED}
    private STATE currentState;

    public Macrophage(Compartment location) {
        super(location);
        currentState = STATE.RECRUITED;
    }

    @Override
    public boolean isImmature() {
        return currentState == STATE.RECRUITED;
    }

    @Override
    public boolean isProtective() {
        return currentState == STATE.M2;
    }

    @Override
    protected void interactWithOtherOrganism(CrohnsSimulation sim, Organism organism) {
        if (organism instanceof IntestinalEpithelialCell && ((IntestinalEpithelialCell) organism).isApoptotic() ) {
            phagocytoseCell(sim, organism);
        }
    }

    @Override
    protected void perceiveMolecules(CrohnsSimulation sim) {
        if (currentState == STATE.RECRUITED) {
            double quantityIFNy = compartment.getConcentrationMolecule(IFNy.instance, this);
            double quantityIL10 = compartment.getConcentrationMolecule(IL10.instance, this);
            double quantityIL17 = compartment.getConcentrationMolecule(IL17.instance, this);
            double quantityGMCSF = compartment.getConcentrationMolecule(GMCSF.instance, this);

            if (quantityIFNy >= IFNyActivationThreshold || quantityIL17 >= IL17ActivationThreshold || quantityGMCSF >= GMCSFActivationThreshold) {
                currentState = STATE.M1;
            } else if (quantityIL10 >= IL10ActivationThreshold) {
                currentState = STATE.M2;
            }
        }
    }

    @Override
    protected void secreteMolecules() {
        switch (currentState) {
            case M1:
                // Low Level Secretion
                {
                    compartment.receiveSecretedMolecules(IL1B.instance, IL1BSecretedPerTimeSlice, this);
                    compartment.receiveSecretedMolecules(IL18.instance, IL18SecretedPerTimeSlice, this);
                    compartment.receiveSecretedMolecules(IL12.instance, IL12SecretedPerTimeSlice, this);
                }
                // High level Secretion
                {
                    compartment.receiveSecretedMolecules(IL23.instance, IL23SecretedPerTimeSlice, this);
                    compartment.receiveSecretedMolecules(TNF.instance, TNFSecretedPerTimeSlice, this);
                    compartment.receiveSecretedMolecules(IL6.instance, IL6SecretedPerTimeSlice, this);
                }
                break;
            case M2:
                compartment.receiveSecretedMolecules(IL10.instance, IL10SecretedPerTimeSlice, this);
                compartment.receiveSecretedMolecules(TGFB.instance, TGFSecretedPerTimeSlice, this);
                break;
        }
    }

    @Override
    protected void performPhagocytosisOfCell(Organism organism) {
        return;
        // Only injest IECs but that is handled by the IECs via interaction.
        // No need to do anything on Macrophage's end
    }

    @Override
    protected double getTimeOfDeathStdDev() {
        return Double.MAX_VALUE;
    }

    @Override
    protected double getTimeOfDeathMean() {
        return Double.MAX_VALUE;
    }

    public static void loadParameters(Document params) {
        Element pE = (Element) params.getElementsByTagName("Macrophage").item(0);

        // Activation Thresholds
        IFNyActivationThreshold = Double.parseDouble(pE.getElementsByTagName("IFNyActivationThreshold").item(0).getTextContent());
        IL10ActivationThreshold = Double.parseDouble(pE.getElementsByTagName("IL10ActivationThreshold").item(0).getTextContent());
        IL17ActivationThreshold = Double.parseDouble(pE.getElementsByTagName("IL17ActivationThreshold").item(0).getTextContent());
        GMCSFActivationThreshold = Double.parseDouble(pE.getElementsByTagName("GMCSFActivationThreshold").item(0).getTextContent());

        // M1
        IL23SecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("IL23SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        TNFSecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("TNFSecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        IL6SecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("IL6SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        IL1BSecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("IL1BSecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        IL18SecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("IL18SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        IL12SecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("IL12SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;

        // M2
        IL10SecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("IL10SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        TGFSecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("TGFBSecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;

    }
}
