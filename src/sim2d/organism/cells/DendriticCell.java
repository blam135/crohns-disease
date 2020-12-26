package sim2d.organism.cells;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;
import sim2d.organism.impl.Phagocyte_Impl;
import sim2d.organism.interfaces.Organism;
import sim2d.organism.microbes.Microbe;
import sim2d.molecules.*;

/**
 * @author mark
 * @version updated by Brendon Lam for the context of Crohn's Disease
 */
public class DendriticCell extends Phagocyte_Impl {

    // general properties of all dendritic cells.
    private boolean hasSampled = false;
    private boolean isTolerogenic = false;
    private int xPositionWhenStationary;
    private int yPositionWhenStationary;

    private static double timeOfDeathStdDev;
    private static double timeOfDeathMean;
    private static double probabilityOfNonTolerogenic;

    // Th1 Priming
    private static double Th1IL12SecretedPerTimeslice;

    // Th17 Tolerogenic Priming
    private static double NonTolerogenicTh17IL6SecretedPerTimeslice;
    private static double NonTolerogenicTh17IL23SecretedPerTimeslice;
    private static double NonTolerogenicTh17IL1BSecretedPerTimeslice;
    private static double NonTolerogenicTh17TGFBSecretedPerTimeslice;

    // FOXP3 Priming
    private static double FOXP3TGFBSecretedPerTimeslice;

    // Th17 Tolerogenic Priming
    private static double TolerogenicTh17IL6SecretedPerTimeslice;
    private static double TolerogenicTh17IL23SecretedPerTimeslice;
    private static double TolerogenicTh17IL1BSecretedPerTimeslice;
    private static double TolerogenicTh17TGFBSecretedPerTimeslice;

    public DendriticCell(Compartment location) {
        super(location);
        xPositionWhenStationary = calculateXPosition();
        yPositionWhenStationary = calculateYPosition();
    }

    public int getXPositionWhenStationary() {
        return xPositionWhenStationary;
    }

    public int getYPositionWhenStationary() {
        return yPositionWhenStationary;
    }

    private int calculateXPosition() {
        int lymphNodeWidth = CrohnsSimulation.sim.lymphNode.getWidth();
        int bottomBoundary = (int) Math.round(lymphNodeWidth * (3.0/4.0));
        int topBoundary = (int) Math.round(lymphNodeWidth * (1.0/4.0));
        return (int) (topBoundary + (bottomBoundary - topBoundary)*Math.random()) ;
    }

    private int calculateYPosition() {
        int lymphNodeHeight = CrohnsSimulation.sim.lymphNode.getHeight();
        int bottomBoundary = (int) Math.round(lymphNodeHeight * (3.0/4.0));
        int topBoundary = (int) Math.round(lymphNodeHeight * (1.0/4.0));
        return (int) (topBoundary + (bottomBoundary - topBoundary)*Math.random()) ;
    }

    @Override
    protected void secreteMolecules() {
        if (hasSampled) {
            if (isTolerogenic) {
                primeFOXP3();
                primeTh17Tolerogenic();
            } else {
                primeTh1();
                primeTh17NonTolerogenic();
            }
        }
    }

    @Override
    protected void performPhagocytosisOfCell(Organism organism) {
        if (organism instanceof Microbe) {
            hasSampled = true;

            if (CrohnsSimulation.sim.random.nextDouble() <= probabilityOfNonTolerogenic) {
                isTolerogenic = false;
            } else {
                isTolerogenic = true;
            }
        }
    }

    @Override
    protected double getTimeOfDeathStdDev() {
        return timeOfDeathStdDev;
    }

    @Override
    protected double getTimeOfDeathMean() {
        return timeOfDeathMean;
    }

    @Override
    protected void perceiveMolecules(CrohnsSimulation sim) {
        // DCs do not perceive anything
    }

    @Override
    protected void interactWithOtherOrganism(CrohnsSimulation sim, Organism otherOrganism) {
        if (otherOrganism instanceof Microbe) {
            phagocytoseCell(sim, otherOrganism);
        } else if (otherOrganism instanceof IntestinalEpithelialCell && ((IntestinalEpithelialCell) otherOrganism).isApoptotic() ) {
            phagocytoseCell(sim, otherOrganism);
        }
    }

    @Override
    public boolean isImmature() {
        return !hasSampled;
    }

    @Override
    public boolean isProtective() {
        return isTolerogenic;
    }

    private void primeTh1() {
        compartment.receiveSecretedMolecules(IL12.instance, Th1IL12SecretedPerTimeslice, this);
    }

    private void primeTh17NonTolerogenic() {
        compartment.receiveSecretedMolecules(IL6.instance, NonTolerogenicTh17IL6SecretedPerTimeslice,this);
        compartment.receiveSecretedMolecules(IL23.instance, NonTolerogenicTh17IL23SecretedPerTimeslice, this);
        compartment.receiveSecretedMolecules(IL1B.instance, NonTolerogenicTh17IL1BSecretedPerTimeslice, this);
        compartment.receiveSecretedMolecules(TGFB.instance, NonTolerogenicTh17TGFBSecretedPerTimeslice, this);
    }

    private void primeFOXP3() {
        compartment.receiveSecretedMolecules(TGFB.instance, FOXP3TGFBSecretedPerTimeslice, this);
    }

    private void primeTh17Tolerogenic() {
        compartment.receiveSecretedMolecules(IL6.instance, TolerogenicTh17IL6SecretedPerTimeslice,this);
        compartment.receiveSecretedMolecules(IL23.instance, TolerogenicTh17IL23SecretedPerTimeslice, this);
        compartment.receiveSecretedMolecules(IL1B.instance, TolerogenicTh17IL1BSecretedPerTimeslice, this);
        compartment.receiveSecretedMolecules(TGFB.instance, TolerogenicTh17TGFBSecretedPerTimeslice, this);
    }

    public static void loadParameters(Document params) {
        Element pE = (Element) params.getElementsByTagName("DendriticCell").item(0);

        timeOfDeathStdDev = Double.parseDouble(pE.getElementsByTagName("timeOfDeathStdDev").item(0).getTextContent());
        timeOfDeathMean = Double.parseDouble(pE.getElementsByTagName("timeOfDeathMean").item(0).getTextContent());
        probabilityOfNonTolerogenic = Double.parseDouble(pE.getElementsByTagName("probabilityOfNonTolerogenic").item(0).getTextContent());

        Element nonTolerogeneic = (Element) pE.getElementsByTagName("NonTolerogenic").item(0);
        Element Th1nonTolerogenic = (Element) nonTolerogeneic.getElementsByTagName("Th1Priming").item(0);
        Th1IL12SecretedPerTimeslice = Double.parseDouble(Th1nonTolerogenic.getElementsByTagName("IL12SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        Element Th17nonTolerogenic = (Element) nonTolerogeneic.getElementsByTagName("Th17Priming").item(0);
        NonTolerogenicTh17IL6SecretedPerTimeslice = Double.parseDouble(Th17nonTolerogenic.getElementsByTagName("IL6SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        NonTolerogenicTh17IL23SecretedPerTimeslice = Double.parseDouble(Th17nonTolerogenic.getElementsByTagName("IL23SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        NonTolerogenicTh17IL1BSecretedPerTimeslice = Double.parseDouble(Th17nonTolerogenic.getElementsByTagName("IL1BSecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        NonTolerogenicTh17TGFBSecretedPerTimeslice = Double.parseDouble(Th17nonTolerogenic.getElementsByTagName("TGFBSecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;

        Element tolerogenic = (Element) pE.getElementsByTagName("Tolegeneric").item(0);
        Element FOXP3Tolerogenic = (Element) tolerogenic.getElementsByTagName("FOXP3Priming").item(0);
        FOXP3TGFBSecretedPerTimeslice = Double.parseDouble(FOXP3Tolerogenic.getElementsByTagName("TGFBSecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        Element Th17Tolerogenic = (Element) tolerogenic.getElementsByTagName("Th17Priming").item(0);
        TolerogenicTh17IL6SecretedPerTimeslice = Double.parseDouble(Th17Tolerogenic.getElementsByTagName("IL6SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        TolerogenicTh17IL23SecretedPerTimeslice = Double.parseDouble(Th17Tolerogenic.getElementsByTagName("IL23SecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        TolerogenicTh17IL1BSecretedPerTimeslice = Double.parseDouble(Th17Tolerogenic.getElementsByTagName("IL1BSecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
        TolerogenicTh17TGFBSecretedPerTimeslice = Double.parseDouble(Th17Tolerogenic.getElementsByTagName("TGFBSecretedPerHour").item(0).getTextContent()) * CrohnsSimulation.timeSlice;
    }
}
