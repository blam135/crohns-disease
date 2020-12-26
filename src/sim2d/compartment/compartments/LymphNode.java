package sim2d.compartment.compartments;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim2d.CrohnsSimulation;
import sim2d.compartment.impl.Compartment_Impl2D_Crohns;
import sim2d.organism.cells.DendriticCell;
import sim2d.organism.cells.Macrophage;
import sim2d.organism.cells.NaiveTCell;
import sim2d.organism.interfaces.Phagocyte;
import sim2d.organism.interfaces.Organism;
import sim2d.organism.interfaces.TCell;

public class LymphNode extends Compartment_Impl2D_Crohns {
    private static int width;
    private static int height;
    private static double timeToCrossOrgan;
    private static VerticalMovementBoundaries vmb;

    public LymphNode(CrohnsSimulation sim) {
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

    /*
     0 1 2 3 4 5
    0 - - - - - -
    1 - - - - - -
    2 - - - - - -
    3 - - - - - -
    4 - - - - - -
    5 - - - - - -
     */
    private int moveDendriticCellVertical(DendriticCell cell) {
        double verticalBoundaryBottom = Math.round( ((double)getHeight()) * (6.0/8.0) );
        double verticalBoundaryTop = Math.round( ((double)getHeight()) * (3.0/8.0) );

        int dy = calculateMovementVerticalUniform();
        int currY = cellsGrid.getObjectLocation(cell).getY();

        int proposedLocationY = currY + dy;
        if (proposedLocationY > cell.getXPositionWhenStationary()) {
            dy = -1;
        } else if (proposedLocationY < cell.getYPositionWhenStationary()) {
            dy = 1;
        } else {
            dy = 0;
        }
        return dy;
    }

    private int moveDendriticCellHorizontal(DendriticCell cell) {
        double horizontalBoundaryBottom = Math.round(getWidth() * 0.75);
        double horizontalBoundaryTop = Math.round(getWidth() * 0.25);

        int dx = calculateMovementHorrizontalUniform();
        int currX = cellsGrid.getObjectLocation(cell).getX();

        int proposedLocationX = currX + dx;
        if (proposedLocationX > cell.getXPositionWhenStationary()) {
            dx = -1;
        } else if (proposedLocationX < cell.getXPositionWhenStationary()) {
            dx = 1;
        } else {
            dx = 0;
        }
        return dx;
    }

    @Override
    protected int calculateMovementVertical(Organism organism) {
        // DC to move in the middle somewhere

        if (organism instanceof DendriticCell) {
            return moveDendriticCellVertical((DendriticCell)organism);
        }

        if (organism instanceof Macrophage && !((Macrophage) organism).isImmature()) {
            return vmb.getMovement();
        } else if (organism instanceof TCell && ((TCell) organism).isEffector()) {
            return vmb.getMovement();
        }
        return calculateMovementVerticalUniform();
    }

    @Override
    protected int calculateMovementHorizontal(Organism organism) {
        // DC to move in the middle somewhere
        if (organism instanceof DendriticCell) {
            return moveDendriticCellHorizontal((DendriticCell)organism);
        }

        if (organism instanceof Phagocyte && (((Phagocyte) organism).isImmature())) {
            return 0;
        }
        return calculateMovementHorrizontalUniform();
    }

    @Override
    public boolean canEnter(Organism organism) {

        boolean isMatureDendriticCell = organism instanceof DendriticCell &&
                !((DendriticCell) organism).isImmature();

        return isMatureDendriticCell;
    }

    @Override
    public boolean canLeave(Organism organism) {
        boolean nonNaiveTCell = organism instanceof TCell && !(organism instanceof NaiveTCell);

        return nonNaiveTCell;
    }

    public static void loadParameters(Document params) {
        Element pE = (Element) params.getElementsByTagName("LymphNode").item(0);

        width = Integer.parseInt(pE.getElementsByTagName("width").item(0).getTextContent());
        height = Integer.parseInt(pE.getElementsByTagName("height").item(0).getTextContent());
        timeToCrossOrgan = Double.parseDouble(pE.getElementsByTagName("timeToCrossOrgan").item(0).getTextContent());

        vmb = calculateVerticalMovementBoundaries(height, timeToCrossOrgan);
    }
}
