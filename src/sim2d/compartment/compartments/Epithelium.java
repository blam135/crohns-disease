package sim2d.compartment.compartments;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim.engine.SimState;
import sim2d.CrohnsSimulation;
import sim2d.compartment.impl.Compartment_Impl2D_Crohns;
import sim2d.organism.cells.DendriticCell;
import sim2d.organism.cells.DummyCell;
import sim2d.organism.cells.IntestinalEpithelialCell;
import sim2d.organism.cells.IntraepithelialCell;
import sim2d.organism.cells.ILC3;
import sim2d.organism.interfaces.Organism;
import sim2d.organism.microbes.Microbe;

public class Epithelium extends Compartment_Impl2D_Crohns {
    private static int width;
    private static int height;
    private static double timeToCrossOrgan;
    private static VerticalMovementBoundaries vmb;

    public Epithelium(CrohnsSimulation sim) {
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
        if (organism instanceof IntestinalEpithelialCell) {
            return 0;
        }

        boolean immatureDC = organism instanceof DendriticCell &&
                ((DendriticCell) organism).isImmature();
        boolean microbe = organism instanceof Microbe;
        boolean iel = organism instanceof IntraepithelialCell;
        boolean dummy = organism instanceof DummyCell;

        if (microbe || immatureDC || iel || dummy) {
            return calculateMovementVerticalUniform();
        }

        return vmb.getMovement();
    }

    @Override
    protected int calculateMovementHorizontal(Organism organism) {
        if (organism instanceof IntestinalEpithelialCell) {
            return 0;
        }
        return calculateMovementHorrizontalUniform();
    }

    @Override
    public boolean canEnter(Organism organism) {
        return true;
    }

    @Override
    public boolean canLeave(Organism organism) {
        boolean isDefaultCells = (organism instanceof IntestinalEpithelialCell) ||
                (organism instanceof IntraepithelialCell) ||
                organism instanceof Microbe;
        return !isDefaultCells;
    }

    @Override
    public void step(SimState state) {
        super.step(state);
        checkAndSpawnILC3();
    }

    public static void loadParameters(Document params) {
        Element pE = (Element) params.getElementsByTagName("Epithelium").item(0);

        width = Integer.parseInt(pE.getElementsByTagName("width").item(0).getTextContent());
        height = Integer.parseInt(pE.getElementsByTagName("height").item(0).getTextContent());
        timeToCrossOrgan = Double.parseDouble(pE.getElementsByTagName("timeToCrossOrgan").item(0).getTextContent());

        vmb = calculateVerticalMovementBoundaries(height, timeToCrossOrgan);
    }
}
