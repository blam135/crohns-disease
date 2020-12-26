package sim2d.organism.cells;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;
import sim2d.molecules.IL33;
import sim2d.organism.impl.TCell_Impl;
import sim2d.molecules.IL10;
import sim2d.molecules.IL35;
import sim2d.molecules.TGFB;

public class FOXP3TregCell extends TCell_Impl {

    private static double IL35SecretedPerTimeSlice;
    private static double TGFBSecretedPerTimeSlice;
    private static double IL10SecretedPerTimeSlice;
    private static double IL33ActivationThreshold;

    private static double proliferationStdDev;
    private static double proliferationMean;
    private static double timeOfDeathMean;                                // the mean length of time that an Phagocyte will remain in a mature state before dying.
    private static double timeOfDeathStdDev;                            // the standard deviation for the above distribution.

    public FOXP3TregCell(Compartment location) {
        super(location);
    }

    public FOXP3TregCell(Compartment location, TCell_Impl organism, boolean replaceParent) { super(location, organism, replaceParent); }

    @Override
    protected double getTimeOfProliferationStdDev() { return proliferationStdDev; }

    @Override
    protected double getTimeOfProliferationMean() { return proliferationMean; }

    @Override
    protected double getTimeOfDeathMean() { return timeOfDeathMean; }

    @Override
    protected double getTimeOfDeathStdDev() { return timeOfDeathStdDev; }

    @Override
    protected void secreteMolecules() {
        compartment.receiveSecretedMolecules(IL35.instance, IL35SecretedPerTimeSlice, this);
        compartment.receiveSecretedMolecules(TGFB.instance, TGFBSecretedPerTimeSlice, this);
    }

    @Override
    protected void proliferate() {
        new FOXP3TregCell(compartment, this, false);
    }

    @Override
    public void perceiveMolecules(CrohnsSimulation sim) {
        double IL33Amount = compartment.getConcentrationMolecule(IL33.instance, this);
        if (IL33Amount >= IL33ActivationThreshold) {
            compartment.receiveSecretedMolecules(IL10.instance, IL10SecretedPerTimeSlice, this);
        }
    }

    public static void loadParameters(Document params) {
        Element pE = (Element) params.getElementsByTagName("FOXP3TregCell").item(0);
        IL35SecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("IL35SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        TGFBSecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("TGFBSecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        IL10SecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("IL10SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        IL33ActivationThreshold = Double.parseDouble(pE.getElementsByTagName("IL33ActivationThreshold").item(0).getTextContent());

        proliferationStdDev = Double.parseDouble(pE.getElementsByTagName("proliferationStdDev").item(0).getTextContent());
        proliferationMean = Double.parseDouble(pE.getElementsByTagName("proliferationMean").item(0).getTextContent());
        timeOfDeathMean = Double.parseDouble(pE.getElementsByTagName("timeOfDeathMean").item(0).getTextContent());
        timeOfDeathStdDev = Double.parseDouble(pE.getElementsByTagName("timeOfDeathStdDev").item(0).getTextContent());
    }

}
