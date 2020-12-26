package sim2d.organism.cells;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;
import sim2d.molecules.IL2;
import sim2d.organism.impl.TCell_Impl;
import sim2d.molecules.IFNy;
import sim2d.molecules.TNF;

public class Th1Cell extends TCell_Impl {

    private static double IFNySecretedPerTimeSlice;
    private static double TNFSecretedPerTimeSlice;
    private static double IL2SecretedPerTimeSlice;

    private static double proliferationStdDev;
    private static double proliferationMean;
    private static double timeOfDeathMean;
    private static double timeOfDeathStdDev;

    public Th1Cell(Compartment location) {
        super(location);
    }

    public Th1Cell(Compartment location, TCell_Impl cell, boolean replaceParent) {
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
        compartment.receiveSecretedMolecules(IFNy.instance, IFNySecretedPerTimeSlice, this);
        compartment.receiveSecretedMolecules(TNF.instance, TNFSecretedPerTimeSlice, this);
        compartment.receiveSecretedMolecules(IL2.instance, IL2SecretedPerTimeSlice, this);
    }

    @Override
    protected void proliferate() {
        new Th1Cell(this.compartment, this, false);
    }

    public static void loadParameters(Document params) {
        Element pE = (Element) params.getElementsByTagName("Th1Cell").item(0);

        IFNySecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("IFNySecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        TNFSecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("TNFSecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        IL2SecretedPerTimeSlice = Double.parseDouble(pE.getElementsByTagName("IL2SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        proliferationStdDev = Double.parseDouble(pE.getElementsByTagName("proliferationStdDev").item(0).getTextContent());
        proliferationMean = Double.parseDouble(pE.getElementsByTagName("proliferationMean").item(0).getTextContent());
        timeOfDeathMean = Double.parseDouble(pE.getElementsByTagName("timeOfDeathMean").item(0).getTextContent());
        timeOfDeathStdDev = Double.parseDouble(pE.getElementsByTagName("timeOfDeathStdDev").item(0).getTextContent());
    }

}
