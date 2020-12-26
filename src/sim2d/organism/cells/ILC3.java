package sim2d.organism.cells;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim.engine.SimState;
import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;
import sim2d.organism.impl.Cell_Impl;
import sim2d.organism.interfaces.Organism;
import sim2d.molecules.GMCSF;
import sim2d.molecules.IL17;
import sim2d.molecules.IL22;

public class ILC3 extends Cell_Impl {

    // Activation Thresholds
    private static double IL23ActivationThreshold;
    private static double IL1BActivationThreshold;
    private static double IL2ActivationThreshold;
    private static double IL7ActivationThreshold;
    public static double getIL23ActivationThreshold() {
        return IL23ActivationThreshold;
    }
    public static double getIL1BActivationThreshold() {
        return IL1BActivationThreshold;
    }
    public static double getIL2ActivationThreshold() {
        return IL2ActivationThreshold;
    }
    public static double getIL7ActivationThreshold() {
        return IL7ActivationThreshold;
    }

    // Spawn Properties
    private static int amountWhenSpawned;
    public static int getAmountWhenSpawned() { return amountWhenSpawned; }
    private static boolean hasSpawned = false;
    public static void setSpawn() { hasSpawned = true; }
    public static boolean getSpawn() { return hasSpawned; }

    // Secretion Level
    private static double IL17SecretedPerTimeSlice;
    private static double GMCSFSecretedPerTimeSlice;
    private static double IL22SecretedPerTimeSlicePositive;
    private static double IL22SecretedPerTimeSliceNegative;

    // States
    private enum STATE {NKp44Positive, NKp44Negative}
    private STATE currentState;

    public boolean isPositive() {
        return currentState == STATE.NKp44Positive;
    }

    public ILC3(Compartment location, boolean positive) {
        super(location);
        if (positive) {
            currentState = STATE.NKp44Positive;
        } else {
            currentState = STATE.NKp44Negative;
        }
    }

    public ILC3(Compartment location, Organism parent, boolean replaceParent, boolean positive) {
        super(location, parent, replaceParent);
        if (positive) {
            currentState = STATE.NKp44Positive;
        } else {
            currentState = STATE.NKp44Negative;
        }
    }

    @Override
    protected void interactWithOtherOrganism(CrohnsSimulation sim, Organism organism) {
        return; // Don't interact with other organisms for now
    }

    @Override
    protected void perceiveMolecules(CrohnsSimulation sim) {
        return; // ILC3s Don't perceive molecules
    }

    @Override
    protected void secreteMolecules() {
        switch(currentState) {
            case NKp44Positive:
                compartment.receiveSecretedMolecules(IL22.instance, IL22SecretedPerTimeSlicePositive, this);
                break;
            case NKp44Negative:
                compartment.receiveSecretedMolecules(IL22.instance, IL22SecretedPerTimeSliceNegative, this);
                compartment.receiveSecretedMolecules(IL17.instance, IL17SecretedPerTimeSlice, this);
                compartment.receiveSecretedMolecules(GMCSF.instance, GMCSFSecretedPerTimeSlice, this);
                break;
        }
    }

    @Override
    protected void proliferate() {
        return; // ILC3 will not proliferate
    }
    @Override
    protected double getTimeOfDeathStdDev() {
        return Double.MAX_VALUE; // ILC3 will not proliferate
    }
    @Override
    protected double getTimeOfDeathMean() {
        return Double.MAX_VALUE; // ILC3 will not proliferate
    }
    @Override
    protected double getTimeOfProliferationStdDev() {
        return Double.MAX_VALUE; // ILC3 will not proliferate
    }
    @Override
    protected double getTimeOfProliferationMean() {
        return Double.MAX_VALUE; // ILC3 will not proliferate
    }

    @Override
    public void step(SimState state) {
        if (isDead)                                                        // do nothing if the cell was already phagocytosed earlier in this time frame.
            return;

        secreteMolecules();
    }

    public static void loadParameters(Document params) {
        Element pE = (Element) params.getElementsByTagName("ILC3").item(0);

        IL23ActivationThreshold = Double.parseDouble(pE.getElementsByTagName("IL23ActivationThreshold").item(0).getTextContent());
        IL1BActivationThreshold = Double.parseDouble(pE.getElementsByTagName("IL1BActivationThreshold").item(0).getTextContent());
        IL2ActivationThreshold = Double.parseDouble(pE.getElementsByTagName("IL2ActivationThreshold").item(0).getTextContent());
        IL7ActivationThreshold = Double.parseDouble(pE.getElementsByTagName("IL7ActivationThreshold").item(0).getTextContent());
        amountWhenSpawned = Integer.parseInt(pE.getElementsByTagName("amountWhenSpawned").item(0).getTextContent());

        Element negative = (Element) pE.getElementsByTagName("NKp44Negative").item(0);
        IL22SecretedPerTimeSliceNegative = Double.parseDouble(negative.getElementsByTagName("IL22SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        IL17SecretedPerTimeSlice = Double.parseDouble(negative.getElementsByTagName("IL17SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        GMCSFSecretedPerTimeSlice = Double.parseDouble(negative.getElementsByTagName("GMCSFSecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;


        Element positive = (Element) pE.getElementsByTagName("NKp44Positive").item(0);
        IL22SecretedPerTimeSlicePositive = Double.parseDouble(positive.getElementsByTagName("IL22SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
    }
}