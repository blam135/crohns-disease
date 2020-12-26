package sim2d.compartment.impl;

import sim.engine.SimState;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.IntGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.field.network.Edge;
import sim.util.Bag;
import sim.util.Int2D;
import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;
import sim2d.organism.cells.ILC3;
import sim2d.organism.impl.TCell_Impl;
import sim2d.organism.interfaces.Organism;
import sim2d.molecules.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author mark
 * @version updated by Brendon Lam for the context of Crohn's Disease
 *
 *
 * <p>
 * Reasoning behind using continuous representation of molecules as opposed to discrete (int).
 * 1) continuous is more efficient when it comes to calculating half life (to get decay right we would have to run the probability for every sinlge discrete molecules,
 * or find a probability distribution that made sense. Also more efficient when calculating diffusion - if you don't move the remainder (less than 8),
 * then they never move, and this gives strange artefacts in the simulation. To be accurate we would have to move each discrete remaining molecule to a random
 * location. With doubles you can work more exactly.
 *
 * NOTES ON HOW THE SPACE IS REPRESENTED.
 * horrizontal: -1 = left. 1 = right
 * vertical 1 = down, -1 = up.
 */
public abstract class Compartment_Impl2D_Crohns extends Compartment {
    private CrohnsSimulation simulation;                            // we need to gain access to features (such as the random num generator) from time to time.

    public abstract int getWidth();

    public abstract int getHeight();

    public SparseGrid2D cellsGrid;            // there may be a lot of empty space in our compartments, so this is more efficient than an ObjectGrid.
    public HashMap<String, DoubleGrid2D> gridMap = new HashMap<>();    /* These grids contain the concentration of molecules in the compartment */

    private Compartment_Impl2D_Crohns() {
    }                            // cannot instantiate a compartment without passing the sim.

    public Compartment_Impl2D_Crohns(CrohnsSimulation sim) {
        simulation = sim;

        if (getWidth() % 2 != 0 || getHeight() % 2 != 0)
            throw new RuntimeException("neither width nor height of this compartment may hold odd values.");    // because we use optimisations that require even numbers.

        /* create new fields for the molecules to be held in */
        gridMap.put("AntiMicrobialPeptideGrid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("GMCSFGrid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("MucusGrid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IFNyGrid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL1BGrid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL2Grid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL4Grid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL6Grid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL7Grid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL8Grid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL10Grid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL12Grid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL13Grid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL15Grid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL17Grid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL18Grid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL21Grid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL22Grid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL23Grid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL33Grid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("IL35Grid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("TGFBGrid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("TNFGrid", new DoubleGrid2D(getWidth(), getHeight()));
        gridMap.put("Type1IFNGrid", new DoubleGrid2D(getWidth(), getHeight()));

        cellsGrid = new SparseGrid2D(getWidth(), getHeight());
    }

    private void spawnILC3(boolean positive) {
        for (int i = 0; i < ILC3.getAmountWhenSpawned(); i++) {
            new ILC3(this, positive);
        }
    }

    // ILC3 Spawns when the environment perceives IL23, IL1B, IL2 or IL7
    protected void checkAndSpawnILC3() {
        int height = cellsGrid.getHeight();
        int width = cellsGrid.getWidth();


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double IL23Concentration = getGrid(IL23.instance).field[x][y];
                double IL1BConcentration = getGrid(IL1B.instance).field[x][y];
                double IL2Concentration = getGrid(IL2.instance).field[x][y];
                double IL7Concentration = getGrid(IL7.instance).field[x][y];

                if (IL23Concentration >= ILC3.getIL23ActivationThreshold() &&
                        IL1BConcentration >= ILC3.getIL1BActivationThreshold() &&
                        IL2Concentration >= ILC3.getIL2ActivationThreshold() &&
                        IL7Concentration >= ILC3.getIL7ActivationThreshold() &&
                        !ILC3.getSpawn()) {
                    spawnILC3(true);
                    ILC3.setSpawn();
                    return;
                } else if (IL23Concentration > ILC3.getIL23ActivationThreshold() &&
                        IL1BConcentration > ILC3.getIL1BActivationThreshold() &&
                        !ILC3.getSpawn()) {
                    spawnILC3(false);
                    ILC3.setSpawn();
                    return;
                }
            }
        }
    }

    /**
     * Method used to diffuse molecules around the compartment.
     */
    @Override
    public void step(SimState state) {
        for (DoubleGrid2D value : gridMap.values()) {
            diffuseGridContinuous((CrohnsSimulation) state, value);
        }

        for (DoubleGrid2D value : gridMap.values()) {
            halflifeDecay((CrohnsSimulation) state, value);
        }

        cellsMovement();                            // there is no replacement of the grid, it is updated serially cell by cell.
    }

    public HashMap<String, Integer> getListAndNumberOfCellInCompartment() {
        ArrayList<Organism> list = (ArrayList<Organism>) getAllCells();
        HashMap<String, Integer> returnList = new HashMap<String, Integer>();

        for (Organism o : list) {
            String currName = o.getClass().getSimpleName();
            if (returnList.containsKey(currName)) {
                int newAmount = returnList.get(currName) + 1;
                returnList.replace(currName, newAmount);
            } else {
                returnList.put(currName, 1);
            }
        }

        return returnList;
    }

    /**
     * Method performs halflife decay on the molecules held within a grid.
     * <p>
     * number of molecules remaining after a timestep calculated as follows:
     * Nt+d = Nt * (1/2)^(d / halflife)   			where d is the duration of the timestep (in hours)
     */
    private DoubleGrid2D halflifeDecay(final CrohnsSimulation sim, DoubleGrid2D grid) {
        final double duration = sim.timeSlice;                    // the duration of a timestep, in hours.
        final double halflife = Molecule.molecularHalflife;
        final double gamma = Math.pow(0.5, (duration / halflife));

        moleculeDecayThresholding(grid);
        return grid.multiply(gamma);
    }

    private void moleculeDecayThresholding(DoubleGrid2D grid) {
        final int xMax = grid.getWidth();
        final int yMax = grid.getHeight();
        final double threshold = Molecule.decayThreshold;
        for (int x = 0; x < xMax; x++) {
            for (int y = 0; y < yMax; y++) {
                // if the grid value is less than the threshold, then we set it to zero.
                if (grid.field[x][y] < threshold) grid.field[x][y] = 0.0;
            }
        }
    }

    /**
     * This returns the vertical movement of the cell, be it down (1), stay (0), or up (-1). Assumes a uniform distribution.
     */
    protected int calculateMovementVerticalUniform() {
        final int i = CrohnsSimulation.sim.random.nextInt(3);                        // returns uniform int in {0,1,2}
        return i - 1;
    }

    /**
     * This returns the horrizontal movement, be it left (-1), stay (0), or right (1). Assumes a uniform distribution.
     */
    protected int calculateMovementHorrizontalUniform() {
        final int i = CrohnsSimulation.sim.random.nextInt(3);                        // returns uniform int in {0,1,2}
        return i - 1;                                                        // return something in range {-1, 0, 1}
    }

    /**
     * Moves all the organisms in the grid, if possible. We iterate through all organisms on the grid attempting movement.
     * <p>
     * 'movements' stores all the possible  movements to neighbouring organisms. 'movementsIndexes' stores the indexes for 'movements'.
     * We randomly select an index from 'movementsIndex' and use it to get a proposed movement for the cell. WE DO NOT ATTEMPT THE SAME MOVEMENT TWICE PER CELL.
     * If the proposed movement is to an occupied cell space, then we attempt another movement.
     * Either a movement will succeed, or, if every neighbouring cell is occupied the cell stays where it is.
     */
    private void cellsMovement() {
        final Iterator<Organism> organisms = cellsGrid.getAllObjects().iterator();

        /* iterate through the organisms in the compartment */
        while (organisms.hasNext()) {
            final Organism organism = organisms.next();
            moveCell(organism);                        // all other organisms
        }
    }

    /**
     * Implements a organism's movement around the compartment. Checks for whether the organism should move around the compartment are done elsewhere.
     * <p>
     * After several attempts to move the organism, the organism remains where it is.
     *
     * @param organism
     */
    private void moveCell(final Organism organism) {
        int attemptsAtMovement = 8;
        final Int2D loc = cellsGrid.getObjectLocation(organism);            // current location of organism.

        while (attemptsAtMovement > 0) {
            final int dx = calculateMovementHorizontal(organism);    // calculate proposed horrizontal movement.
            final int dy = calculateMovementVertical(organism);        // calculate the vertical movement. This will either follow or ignore bloodflow.

            final int newx = cellsGrid.stx(loc.x + dx);        // calculate the new proposed coordinates (toroidal through x axis).
            int newy = loc.y + dy;                            // the proposed new y coordinate.
            if (newy < 0)
                newy = 0;                                    // cannot disappear up the top of the grid.

            if (newy >= cellsGrid.getHeight())                // organism trying to migrate.
                if (migrateCell(organism))                // try to migrate, and if that is successful then we break from the loop
                    break;                                    // break from this loop.
                else
                    // migration failed. The organism is either allowed to stay where it is, else it is moved to teh top of the compartment.
                    newy = verticalMovementLoopOrStay(organism);

            if (spaceInGridSpace(newx, newy, organism)) {
                cellsGrid.setObjectLocation(organism, newx, newy);        // place the organism in the grid space.
                break;                                        // break from the loop.
            } else
                attemptsAtMovement--;                        // record this attempt, and try again.
        }
    }

    private boolean spaceInGridSpace(final int x, final int y, final Organism organism) {
        final Bag organisms = cellsGrid.getObjectsAtLocation(x, y);    // get all organisms at current location
        if (organisms != null)
            for (Organism c : ((Iterable<Organism>) organisms))
                if (!(c instanceof TCell_Impl))            // all organisms other than T organisms are considered to be big organisms.
                    return false;                                // any organism not a T organism is a bit organism, return false

        // at this point there are no big organisms in the specified gridspace
        if (organism instanceof TCell_Impl) {
            final int otherCells = cellsGrid.numObjectsAtLocation(x, y);    // all these organisms will be T organisms, because of the loop above not returning.

//			if(TCell_Impl.spatialTestEquals)
//			{
//				if(otherCells == TCell_Impl.retrieveCellsPerGridspace())
//				{
//					return false;
//				}
//			}
//			else
//			{
//				if(otherCells >= TCell_Impl.retrieveCellsPerGridspace())
//				{
//					return false;
//				}
//			}
//			if(otherCells >= 7)
//				return false;

            return true;
        }
        /* all other organisms.
         *
         * no big organisms, and if the organism we're dealing with isnt a T organism, then any organism occuping space here will mean
         *  there is not space for another organism. Only T organisms can occupy the same space.
         */
        return cellsGrid.numObjectsAtLocation(x, y) == 0;
    }

    /**
     * Method returns a y-co-ordinate for the organism. It will either stay at the bottom of the compartment, or it will loop back to the top.
     */
    private int verticalMovementLoopOrStay(final Organism organism) {
        return cellsGrid.getHeight() - 1;                            // in all other cases the organism stays at the bottom of the compartment
    }

    /**
     * Some organisms have different behaviours in different compartments. This method is overridden by concrete compartment implemenetations to provide that behaviour.
     */
    protected abstract int calculateMovementVertical(final Organism organism);

    /**
     * Some organisms have different behaviours in different compartments. This method is overridden by concrete compartment implemenetations to provide that behaviour.
     */
    protected abstract int calculateMovementHorizontal(final Organism organism);

    /**
     * When a organism leaves down the bottom of the grid (past y=height) it is deemed to have migrated to another location. This method handles that posibility.
     */
    protected boolean migrateCell(Organism organism) {
        final Bag c = simulation.compartmentsNetwork.getEdgesOut(this);        // retrieve the compartments to which a organism can transit from the one it presently occupies.

        // Nothing to migrate so return fail
        if (c.size() == 0) {
            return false;
        }

        final int index = simulation.random.nextInt(c.size());                // index of the next compartment we are going to enter, randomly chosen.
        final Edge newCompartmentEdge = (Edge) c.get(index);
        final Compartment newCompartment = (Compartment) newCompartmentEdge.to();
        if (newCompartment.canEnter(organism) && this.canLeave(organism))            // check to see if this organism can actually enter the proposed new compartment, and if it may leave this one.
        {
            cellsGrid.remove(organism);                                            // remove this organism from this compartment.
            newCompartment.enterCompartment(organism);                            // and enter the new compartment.
            return true;                                                    // organism successfully migrated.
        } else
            return false;                                                    // organism did not migrate out of this compartment.
    }

    /**
     * Places the specified organism in this compartment.
     * <p>
     * Method attempts to place organism in a grid space not already occupied, but after some number of attempts will place it in a space regardless of whether
     * that space is occupied or not.
     */
    public void enterCompartment(final Organism organism) {
        int x, y;                        // the location of the organism in the new compartment.
        y = 0;                            // organisms enter at the top and leave through the bottom of the compartment
        int attempts = getWidth();        // it is conceivable that the entire top of the compartment is occupied, in which case the organism cannot enter.
        do {
            x = simulation.random.nextInt(getWidth());                        // random placement across width of compartment (but still at the top)
            attempts--;                // record the attempt
            if (attempts == 0)            // when we have attempted enough times we move to a different height and try there.
                break;                    // after so many attempts we will place the organism at x, regardless of whether that space is occupied or not.

        } while (!spaceInGridSpace(x, y, organism));                    // if there is no space for the organism in the specified gridspace, try again...
        cellsGrid.setObjectLocation(organism, x, y);                            // place the organism at the location  (x, y)

        organism.migrateIntoCompartment(this);                                    // record that the organism is now within a different comparmtent.
    }

    /**
     * Given a grid (of molecules) this method will diffuse the molecules from each cell into the neighouring organisms.
     * If the number of molecules in a cell does not divide equally amongst the neighbours, then the remainder stays where it is.
     * Before diffusion takes place we take a snap shot of the original grid, and reference the snap shot in making changes to the orginal grid.
     * <p>
     * TODO inherrent assumption about movement of molecules here.
     */
    private void diffuseGridDiscrete(CrohnsSimulation sim, IntGrid2D grid) {
        IntGrid2D newGrid = new IntGrid2D(grid);    // clone the existing grid, and use that snapshot to make alterations to the original 'grid'.
        for (int x = 0; x < grid.getWidth(); x++)            // scan along x
        {
            for (int y = 0; y < grid.getHeight(); y++)        // scal along y
            {
                int here = newGrid.get(x, y);                // pull out the original value for this cell.
                if (here != 0)                                // if this cell does not contain any molecules then we do not need to continue with this cell.
                {
                    int share = here / 8;                                // 8 neighbours. The remainder will stay where it is.

                    grid.field[x][y] -= (share * 8);            // reduce the number of this cell, in the new grid.

                    /* add share to the neighbours */
                    grid.field[grid.stx(x - 1)][grid.sty(y - 1)] += share;        // bottom left
                    grid.field[grid.stx(x - 1)][y] += share;        // bottom
                    grid.field[grid.stx(x - 1)][grid.sty(y + 1)] += share;        // bottom right

                    grid.field[grid.stx(x)][grid.sty(y - 1)] += share;            // middle left
                    grid.field[grid.stx(x)][grid.sty(y + 1)] += share;            // middle right

                    grid.field[grid.stx(x + 1)][grid.sty(y - 1)] += share;        // top left
                    grid.field[grid.stx(x + 1)][y] += share;        // top
                    grid.field[grid.stx(x + 1)][grid.sty(y + 1)] += share;        // top right

                    /* the remainder in each cell diffuse to random locations, because if they don't organisms that contain less than 8 never go anywhere */
                    final int remainder = here - (share * 8);
                    for (int i = 0; i < remainder; i++) {
                        final int xi = sim.random.nextInt(3) - 1;    // the randomly chosen x direction
                        final int yi = sim.random.nextInt(3) - 1;    // the randomly chosen y direction
                        grid.field[x][y] -= 1;                        // take one away from the current cell
                        grid.field[grid.stx(x + xi)][grid.sty(y + yi)] += 1;    // move it to the randomly determined neighbour cell.
                    }
                }
            }
        }
    }

    /**
     * Given a grid (of molecules) this method will diffuse the molecules from each cell into the neighouring organisms.
     * Before diffusion takes place we take a snap shot of the original grid, and reference the snap shot in making changes to the orginal grid.
     * <p>
     */
    private void diffuseGridContinuous(final CrohnsSimulation sim, DoubleGrid2D grid) {
        DoubleGrid2D newGrid = new DoubleGrid2D(grid);        // clone the existing grid, and use that snapshot to make alterations to the original 'grid'.
        for (int x = 0; x < grid.getWidth(); x++)            // scan along x
        {
            for (int y = 0; y < grid.getHeight(); y++)        // scal along y
            {
                double here = newGrid.get(x, y);            // pull out the original value for this cell.
                if (here != 0.0)                                // if this cell does not contain any molecules then we do not need to continue with this cell.
                {
                    double share = here / 8.0;                // 8 neighbours. The remainder will stay where it is.

                    grid.field[x][y] -= (share * 8);        // reduce the number of this cell, in the new grid.

                    /* add share to the neighbours */
                    grid.field[grid.stx(x - 1)][grid.sty(y - 1)] += share;        // bottom left
                    grid.field[grid.stx(x - 1)][y] += share;        // bottom
                    grid.field[grid.stx(x - 1)][grid.sty(y + 1)] += share;        // bottom right

                    grid.field[grid.stx(x)][grid.sty(y - 1)] += share;            // middle left
                    grid.field[grid.stx(x)][grid.sty(y + 1)] += share;            // middle right

                    grid.field[grid.stx(x + 1)][grid.sty(y - 1)] += share;        // top left
                    grid.field[grid.stx(x + 1)][y] += share;        // top
                    grid.field[grid.stx(x + 1)][grid.sty(y + 1)] += share;        // top right
                }
            }
        }
    }

    protected DoubleGrid2D getGrid(Molecule c) {
        if (c instanceof AntiMicrobialPeptide) {
            return gridMap.get("AntiMicrobialPeptideGrid");
        } else if (c instanceof GMCSF) {
            return gridMap.get("GMCSFGrid");
        } else if (c instanceof Mucus) {
            return gridMap.get("MucusGrid");
        } else if (c instanceof IFNy) {
            return gridMap.get("IFNyGrid");
        } else if (c instanceof IL1B) {
            return gridMap.get("IL1BGrid");
        } else if (c instanceof IL2) {
            return gridMap.get("IL2Grid");
        } else if (c instanceof IL4) {
            return gridMap.get("IL4Grid");
        } else if (c instanceof IL6) {
            return gridMap.get("IL6Grid");
        } else if (c instanceof IL7) {
            return gridMap.get("IL7Grid");
        } else if (c instanceof IL8) {
            return gridMap.get("IL8Grid");
        } else if (c instanceof IL10) {
            return gridMap.get("IL10Grid");
        } else if (c instanceof IL12) {
            return gridMap.get("IL12Grid");
        } else if (c instanceof IL13) {
            return gridMap.get("IL13Grid");
        } else if (c instanceof IL15) {
            return gridMap.get("IL15Grid");
        } else if (c instanceof IL17) {
            return gridMap.get("IL17Grid");
        } else if (c instanceof IL18) {
            return gridMap.get("IL18Grid");
        } else if (c instanceof IL21) {
            return gridMap.get("IL21Grid");
        } else if (c instanceof IL22) {
            return gridMap.get("IL22Grid");
        } else if (c instanceof IL23) {
            return gridMap.get("IL23Grid");
        } else if (c instanceof IL33) {
            return gridMap.get("IL33Grid");
        } else if (c instanceof IL35) {
            return gridMap.get("IL35Grid");
        } else if (c instanceof TGFB) {
            return gridMap.get("TGFBGrid");
        } else if (c instanceof TNF) {
            return gridMap.get("TNFGrid");
        }
        return null;
    }

    /**
     * Method through which molecules are secreted into spaces in the compartment.
     */
    public void receiveMolecules(Molecule m, double quantity, int x, int y) {
        DoubleGrid2D grid;
        /* identify the correct molecule type */
        grid = getGrid(m);
        grid.field[x][y] += quantity;        // add the quantity to the appropriate cell.
    }

    /**
     * Receive molecules secreted into the compartment from the location of the specified organism.
     */
    public void receiveSecretedMolecules(Molecule c, double quantity, Organism organism) {
        DoubleGrid2D grid;                // we identify the grid below.
        Int2D location = cellsGrid.getObjectLocation(organism);
        /* identify the correct molecule type */
        grid = getGrid(c);
        grid.field[location.x][location.y] += quantity;        // add the quantity to the appropriate organism.
    }

    /**
     * Places the daughter cell in the same location as the parent cell.
     *
     * @param daughter
     * @param parent
     */
    public void receiveDaughterCell(Organism daughter, Organism parent) {
        final Int2D location = cellsGrid.getObjectLocation(parent);    // location of parent cell. Will try to place the daughter here first
        placeCellAsCloseToLocationAsPossible(daughter, location.x, location.y);
    }

    /**
     * Replaces parent cell with daughter cell.
     *
     * @param daughter
     * @param parent
     */
    public void replaceParentCell(Organism daughter, Organism parent) {
        final Int2D location = cellsGrid.getObjectLocation(parent);    // location of parent cell. Will try to place the daughter here first
        int x = location.x;
        int y = location.y;
        removeOrganismFollowingDeath(parent);
        CrohnsSimulation.sim.removeFromSimulationSchedule(parent);
        placeCellAsCloseToLocationAsPossible(daughter, x, y);
    }

    /**
     * This method will place the given organism as close as (is reasonably) possible to the location (x,y).
     *
     * @param organism
     * @param x
     * @param y
     */
    private void placeCellAsCloseToLocationAsPossible(final Organism organism, final int x, final int y) {
        int attempts = 8;                                            // after 5 attempts to place the organism, it will be placed in a location regardless of how many organisms already occupy it (this stops endless loops)
        int x1 = x;                                                    // temp x and y variables, we do not actually want to chance x and y.
        int y1 = y;

        int distance = 1;
        while (spaceInGridSpace(x1, y1, organism) == false) {
            // assign x and y to either +- distance, randomly.
            x1 = (CrohnsSimulation.sim.random.nextBoolean()) ? (x + distance) : (x - distance);
            y1 = (CrohnsSimulation.sim.random.nextBoolean()) ? y + distance : y - distance;

            // place x and y back into the grid, in case the last operations selected co-ordinates outside of the grid.
            x1 = cellsGrid.stx(x);                                    // toroidal x
            if (y1 < 0) y1 = 0;
            if (y1 >= getHeight()) y1 = getHeight() - 1;
            attempts--;                                            // record this attempt at placement
            if (attempts == 0)                                        // after so many placement attempts we try a distance further away.
            {
                distance++;
                attempts = 8;
            }
        }
        cellsGrid.setObjectLocation(organism, x1, y1);                // place organism in grid
    }

    /**
     * Places the specified organism in a random location within this compartment.
     * We pick the location only once, because during a hard immune response we do not want new organisms (eg, replacement APCs in the CNS) to be pushed away from where inflammation is taking
     * place because of space shortage.
     */
    public void placeCellRandomlyInCompartmentCloseIfOccupied(final Organism organism) {
        // pick a random location.
        final int x = simulation.random.nextInt(getWidth());
        final int y = simulation.random.nextInt(getHeight());

        placeCellAsCloseToLocationAsPossible(organism, x, y);            // place the organism as close to that random location as we can.
    }


    /**
     * Places the specified organism in a random location within this compartment. If the location is occupied, then
     * another is picked, at random.
     */
    public void placeCellRandomlyInCompartment(Organism organism) {
        int x, y;                                                    // where the organism will be placed in the compartment.
        do {
            x = simulation.random.nextInt(getWidth());
            y = simulation.random.nextInt(getHeight());
        } while (!spaceInGridSpace(x, y, organism));            // if there is no space for an additional (specified) organism, then try again.
        cellsGrid.setObjectLocation(organism, x, y);                    // place the organism at that location
    }

    /**
     * This method removes the organism from the compartment, regardless of its location. For example, when it is phagocytosed.
     */
    public void removeOrganismFollowingDeath(final Organism organism) {
        cellsGrid.remove(organism);                                        // remove organism from field
        organism.migrateIntoCompartment(null);                            // record that organism no longer occupies any compartment.
    }

    /**
     * Method will pull all the organisms that lie within some distance of the specified co-ordinates.
     * <p>
     * We assume toroidal world along x axis, but not along y axis.
     */
    public Organism[] getNeighbours(final Organism organism) {
        final int distance = 1;                                            // so that we can change it at a later date, if we choose. CANNOT EXCEED MIN(WIDTH, HEIGHT)
        final Int2D loc = cellsGrid.getObjectLocation(organism);            // location of the organism in the grid.
        ArrayList<Organism> neighbours = new ArrayList<Organism>();                // where we will store the neighbouring organisms.

        for (int x0 = loc.x - distance; x0 <= loc.x + distance; x0++)    // iterate over x coordinates
        {
            final int x1 = cellsGrid.stx(x0);                            // toroidal world around the x axis. (but not the y axis)
            for (int y0 = loc.y - distance; y0 <= loc.y + distance; y0++)    // iterate over y coordinates
                if ((y0 < getHeight()) && (y0 >= 0))                    // if this is not the case then we skip examination of this grid space (off the edge of the field)
                {
                    final Bag cellsBag = cellsGrid.getObjectsAtLocation(x1, y0);
                    if (cellsBag != null)                                // cellsBag will be null if there were no objects in that location of the SparseGrid2D.
                        for (Object o : cellsBag)
                            neighbours.add((Organism) o);                    // cast objects into Cells and place into array list.
                }
        }
        neighbours.remove(organism);                                        // remove the organism from being in its own neighbourhood.
        Organism[] organisms = new Organism[neighbours.size()];                        // cast into array of Organism objects...
        return neighbours.toArray(organisms);                                // ... and return.
    }

    /**
     * Returns the quantity of the specified molecule at the location of the specified cell.
     */
    public double getConcentrationMolecule(final Molecule m, final Organism c) {
        Int2D location = cellsGrid.getObjectLocation(c);
        return getGrid(m).field[location.x][location.y];
    }

    /**
     * Removes the specified quantity of the specified molecule from the location occupied by the spefied organism. This is used for phagocytosis of molecules by APCs.
     */
    public void removeQuantityMolecule(Molecule m, int quantity, Organism organism) {
        Int2D location = cellsGrid.getObjectLocation(organism);
        getGrid(m).field[location.x][location.y] -= quantity;
    }


    /**
     * Returns an array of all organisms in the compartment. Used for collecting data on cell populations
     * and drawing graphs.
     */
    public Collection<Organism> getAllCells() {
        Bag b = cellsGrid.allObjects;
        return new ArrayList<Organism>(b);            // do not return the bag itself, because modifying it is dangerous.
    }

    public int totalOrganism() {
        return cellsGrid.allObjects.size();
    }

    /**
     * See the documentation on 'VerticalMovementBoundaries' below for a detailed description of how this algorithm works. Alternatively, look in log book 'A', pages
     * 64 - 72.
     *
     * @param compartmentHeight
     * @param timeToCrossCompartment
     * @return
     */
    public static VerticalMovementBoundaries calculateVerticalMovementBoundaries(int compartmentHeight, double timeToCrossCompartment) {
        final int timestepsInCompartment = (int) (timeToCrossCompartment / CrohnsSimulation.sim.timeSlice);
        final double alpha = (double) compartmentHeight / (double) timestepsInCompartment;

        final double x = (1.0 + (1.5 * alpha)) / 3.0;        // size of region representing 'down'

        final double z = x - alpha;                            // size of region representing 'up'

        final double y = (x + z) / 2.0;                        // size of region representing 'stay' (is half way between up and down)

        final double boundary1 = x;
        final double boundary2 = x + y;

        return new VerticalMovementBoundaries(boundary1, boundary2);
    }

    /**
     * This class represents a mechanism for blood flow bias and migration through a compartment that replaces the previous gaussian based one.
     * <p>
     * This one operates with a flat distribution, and defines three regions (two boundaries) within the space of 0.0 to 1.0. The firs region (x) represents the
     * probability that the cell will move down, the second region (y) represents the probability that the cell will stay at the current horrizontal position, and the
     * third region (z) represents the probability that the cell will move up. Hence, we draw random numbers from a uniform distribution, and the boundaires
     * are placed in a way that implements the blood flow bias.
     * <p>
     * There were a few constraints on the sizes of the boundaries. they must all add to 1.0. We can calculate from the number of steps that a cell can
     * have in the compartment before it is meant to have moved, and the height of the compartment, the 'rate' at which the cell must move through the space, which we
     * call 'alpha'. region X, after subtracting 'z' must equal alpha. Y is set to lie half way between x and z, since it would make sense that the cell stays where it
     * is more than it moves up, given blood flow. With these three constraints the sizes of the regions can be calculated.
     * <p>
     * Note that strange things will happen if it is not physically possible for the cell to traverse the space in the time allocated. However, this is used
     * deliberately to engineer a strong downwards movement in the Dendritic organisms that have recently migrated to a new space.
     * <p>
     * More information can be found in logbook A, pages 64 - 72.
     *
     * @author mark
     */
    public static class VerticalMovementBoundaries {
        /*
         * 0.0 -> bounary1 = move down
         * boundary1 -> boundary2 = no vertical movement
         * boundary2 -> 1.0 = move up
         */
        private double boundary1;
        private double boundary2;

        public VerticalMovementBoundaries(double b1, double b2) {
            boundary1 = b1;
            boundary2 = b2;
        }

        public int getMovement() {
            final double rand = CrohnsSimulation.sim.random.nextDouble();
            if (rand < boundary1)
                return 1;
            else if (rand < boundary2)
                return 0;
            else
                return -1;
        }
    }

}
