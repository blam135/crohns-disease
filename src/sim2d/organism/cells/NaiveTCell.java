package sim2d.organism.cells;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim.engine.SimState;
import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;
import sim2d.organism.impl.TCell_Impl;
import sim2d.molecules.*;

public class NaiveTCell extends TCell_Impl {

    private enum STATE {TH1, TH2, TH17, FOXP3} // For differentiation

    private static double IL12ActivationThreshold;
    private static double IL6ActivationThreshold;
    private static double IL23ActivationThreshold;
    private static double TGFBActivationThreshold;
    private static double IL1BActivationThreshold;

    public NaiveTCell(Compartment location) {
        super(location);
        this.effector = false;
    }

    @Override
    protected double getTimeOfProliferationStdDev() { return Double.MAX_VALUE; }

    @Override
    protected double getTimeOfProliferationMean() { return Double.MAX_VALUE; }

    @Override
    protected double getTimeOfDeathMean() {
        // Will not die
        return 0;
    }

    @Override
    protected double getTimeOfDeathStdDev() {
        // Will not die
        return 0;
    }

    @Override
    public void secreteMolecules() {
        // Do nothing because NaiveTCell can't secrete anything
    }

    @Override
    public void perceiveMolecules(CrohnsSimulation sim) {
        double quantityIL12 = compartment.getConcentrationMolecule(IL12.instance, this);
        double quantityIL6 = compartment.getConcentrationMolecule(IL6.instance, this);
        double quantityIL23 = compartment.getConcentrationMolecule(IL23.instance, this);
        double quantityTGFB = compartment.getConcentrationMolecule(TGFB.instance, this);
        double quantityIL1B = compartment.getConcentrationMolecule(IL1B.instance, this);

        // This is the logical assumption I've made on how Naive T Cells Differentiates
        if (quantityIL12 > IL12ActivationThreshold) {
            differentiateIntoSpecificTCell(STATE.TH1);
        } else if (quantityIL6 > IL6ActivationThreshold &&
                quantityIL23 > IL23ActivationThreshold &&
                quantityTGFB > TGFBActivationThreshold &&
                quantityIL1B > IL1BActivationThreshold) {
            differentiateIntoSpecificTCell(STATE.TH17);
        } else if (quantityTGFB > TGFBActivationThreshold) {
            differentiateIntoSpecificTCell(STATE.FOXP3);
        }

    }

    @Override
    public void step(SimState state) {
        if (isDead)                                                        // do nothing if the cell was already phagocytosed earlier in this time frame.
            return;
        CrohnsSimulation simulation = (CrohnsSimulation) state;
        perceiveMolecules(simulation);
    }

    @Override
    protected void proliferate() {
        // Naive T Cells do not proliferate
    }

    private void differentiateIntoSpecificTCell(STATE state) {
        if (this.isDead()) {
            return;
        }
        isDead = true;

        switch (state) {
            case TH1:
                new Th1Cell(compartment, this, true);
                break;
            case TH17:
                new Th17Cell(compartment, this, true);
                break;
            case FOXP3:
                new FOXP3TregCell(compartment, this, true);
                break;
        }

    }

    public static void loadParameters(Document params) {
        Element pE = (Element) params.getElementsByTagName("NaiveTCell").item(0);

        IL12ActivationThreshold = Double.parseDouble(pE.getElementsByTagName("IL12ActivationThreshold").item(0).getTextContent());
        IL6ActivationThreshold = Double.parseDouble(pE.getElementsByTagName("IL6ActivationThreshold").item(0).getTextContent());
        IL23ActivationThreshold = Double.parseDouble(pE.getElementsByTagName("IL23ActivationThreshold").item(0).getTextContent());
        TGFBActivationThreshold = Double.parseDouble(pE.getElementsByTagName("TGFBActivationThreshold").item(0).getTextContent());
        IL1BActivationThreshold = Double.parseDouble(pE.getElementsByTagName("IL1BActivationThreshold").item(0).getTextContent());
    }
}
