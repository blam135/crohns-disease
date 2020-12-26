package sim2d.compartment.compartments;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim.engine.SimState;
import sim2d.CrohnsSimulation;
import sim2d.compartment.impl.Compartment_Impl2D_Crohns;
import sim2d.organism.cells.DummyCell;
import sim2d.organism.cells.ILC3;
import sim2d.organism.cells.Macrophage;
import sim2d.organism.impl.TCell_Impl;
import sim2d.organism.interfaces.Organism;
import sim2d.organism.interfaces.TCell;
import sim2d.molecules.IL23;
import sim2d.molecules.IL1B;
import sim2d.molecules.IL2;
import sim2d.molecules.IL7;

public class LaminaPropria extends Compartment_Impl2D_Crohns {
    private static int width;
    private static int height;
    private static double timeToCrossOrgan;
    private static VerticalMovementBoundaries vmb;

    public LaminaPropria(CrohnsSimulation sim) {
        super(sim);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    protected int calculateMovementVertical(Organism organism) {
        if (organism instanceof Macrophage &&
                ((Macrophage) organism).isImmature()) {
            return 0;
        } else if (organism instanceof DummyCell) {
            return calculateMovementVerticalUniform();
        }
        return vmb.getMovement();
    }

    @Override
    protected int calculateMovementHorizontal(Organism organism) {
        if (organism instanceof Macrophage &&
                ((Macrophage) organism).isImmature()) {
            return 0;
        }
        return calculateMovementHorrizontalUniform();
    }

    @Override
    public boolean canEnter(Organism organism) {
        boolean isNonNaiveTCell = organism instanceof TCell && ((TCell) organism).isEffector();
        boolean isEffectorMacrophage = organism instanceof Macrophage && !((Macrophage) organism).isImmature();
        boolean isILC3 = organism instanceof ILC3;

        return isNonNaiveTCell || isEffectorMacrophage || isILC3;
    }

    @Override
    public boolean canLeave(Organism organism) {
        return true;
    }

    @Override
    public void step(SimState state) {
        super.step(state);
        checkAndSpawnILC3();
    }

    public static void loadParameters(Document params) {
        Element pE = (Element) params.getElementsByTagName("LaminaPropria").item(0);

        width = Integer.parseInt(pE.getElementsByTagName("width").item(0).getTextContent());
        height = Integer.parseInt(pE.getElementsByTagName("height").item(0).getTextContent());
        timeToCrossOrgan = Double.parseDouble(pE.getElementsByTagName("timeToCrossOrgan").item(0).getTextContent());
        vmb = calculateVerticalMovementBoundaries(height, timeToCrossOrgan);
    }
}
