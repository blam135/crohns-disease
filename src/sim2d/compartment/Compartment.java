package sim2d.compartment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim.engine.Steppable;
import sim2d.organism.interfaces.Organism;
import sim2d.molecules.Molecule;

import java.util.Collection;
import java.util.HashMap;

/**
 * As a convention, the flow of organisms enters the top of a compartment and leaves out of the bottom.
 */
public abstract class Compartment implements Steppable {
    /**
     * Indicates whether a organism of a certain type can enter this compartment.
     */
    public abstract boolean canEnter(Organism organism);

    /**
     * Indicates whether the given organism is of a type that may leave this compartment.
     */
    public abstract boolean canLeave(Organism organism);

    /**
     * Method handles the receipt of molecules into a specified location in the compartment from a cell.
     *  @param m        the type of the molecule that was secreted
     * @param quantity the quantity of the molecule that was secreted
     * @param x        x coordinate of secreted molecules
     * @param y        y coordinate of secreted molecules
     */
    public abstract void receiveMolecules(Molecule m, double quantity, int x, int y);

    /**
     * Same as above, but the location is deduced from the specified organism.
     */
    public abstract void receiveSecretedMolecules(Molecule c, double quantity, Organism organism);

    /**
     * The specified organism arrives in this compartment.
     */
    public abstract void enterCompartment(Organism organism);

    /**
     * Places the specified organism into the specified compartment at a random location.
     */
    public abstract void placeCellRandomlyInCompartmentCloseIfOccupied(Organism organism);

    /**
     * This method removes the organism from the compartment, regardless of its location. For example, when it is phagocytosed.
     */
    public abstract void removeOrganismFollowingDeath(Organism organism);

    /**
     * Places the daughter cell in the same location as the parent cell.
     *
     * @param daughter
     * @param parent
     */
    public abstract void receiveDaughterCell(Organism daughter, Organism parent);

    /**
     * Replaces parent cell with daughter cell.
     *
     * @param daughter
     * @param parent
     */
    public abstract void replaceParentCell(Organism daughter, Organism parent);

    /**
     * Called by a organism to retrieve any organisms in neighbouring grid spaces.
     */
    public abstract Organism[] getNeighbours(Organism organism);

    /**
     * Returns the quantity of the specified molecule at the location of the specified cell.
     */
    public abstract double getConcentrationMolecule(Molecule m, Organism c);

    /**
     * Used for visualisation stuff, for drawing graphs of the cell populations.
     */
    public abstract Collection<Organism> getAllCells();

    public abstract int totalOrganism();

    public abstract int getWidth();

    public abstract int getHeight();

    /**
     * Given the parameters.xml file (represented as a 'Document') this method loads the relevant default values for this class.
     *
     * @param params
     */
    public static void loadParameters(Document params) {
        Element pE = (Element) params.getElementsByTagName("Compartment").item(0);
    }

    public abstract HashMap<String, Integer> getListAndNumberOfCellInCompartment();
}
