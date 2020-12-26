package sim2d.organism.cells;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;
import sim2d.organism.impl.TCell_Impl;
import sim2d.molecules.IL17;
import sim2d.molecules.IL22;

public class Th17Cell extends TCell_Impl {

    private static double IL17SecretedPerTimeSlice;
    private static double IL22SecretedPerTimeSlice;

    private static double proliferationStdDev;
    private static double proliferationMean;
    private static double timeOfDeathMean;                                // the mean length of time that an Phagocyte will remain in a mature state before dying.
    private static double timeOfDeathStdDev;                            // the standard deviation for the above distribution.

    public Th17Cell(Compartment location) {
        super(location);
    }

    public Th17Cell(Compartment location, TCell_Impl cell, boolean replaceParent) {
        super(location, cell, replaceParent);
    }

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
        compartment.receiveSecretedMolecules(IL17.instance, IL17SecretedPerTimeSlice, this);
        compartment.receiveSecretedMolecules(IL22.instance, IL22SecretedPerTimeSlice, this);
    }

    @Override
    protected void proliferate() {
        new Th17Cell(this.compartment, this, false);
    }

    public static void loadParameters(Document params) {
        Element pE = (Element) params.getElementsByTagName("Th17Cell").item(0);

        IL17SecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("IL17SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        IL22SecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("IL22SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        proliferationStdDev = Double.parseDouble(pE.getElementsByTagName("proliferationStdDev").item(0).getTextContent());
        proliferationMean = Double.parseDouble(pE.getElementsByTagName("proliferationMean").item(0).getTextContent());
        timeOfDeathMean = Double.parseDouble(pE.getElementsByTagName("timeOfDeathMean").item(0).getTextContent());
        timeOfDeathStdDev = Double.parseDouble(pE.getElementsByTagName("timeOfDeathStdDev").item(0).getTextContent());
    }

}
