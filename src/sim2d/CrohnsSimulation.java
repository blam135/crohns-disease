package sim2d;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.field.network.Network;
import sim2d.compartment.Compartment;
import sim2d.compartment.compartments.Epithelium;
import sim2d.compartment.compartments.LaminaPropria;
import sim2d.compartment.compartments.LymphNode;
import sim2d.organism.cells.*;
import sim2d.organism.impl.Phagocyte_Impl;
import sim2d.organism.impl.TCell_Impl;
import sim2d.molecules.Molecule;
import sim2d.organism.microbes.Microbe;

import java.util.HashMap;
import java.util.Map;

/**
 * This class encompasses the whole simulation
 *
 * @author Mark
 * @version updated by Brendon for the context of Crohn's Disease
 */
public class CrohnsSimulation extends SimState {
    public static CrohnsSimulation sim;                            // static so that other objects can get hold of key items such as the schedule.
    public static Document parameters;                            // java representation of the XML document holding parameters for this simulation run.
    public static double timeSlice;                                // how long, in hours, that a simulation timestep corresponds to.

    // Compartment Setups
    public Network compartmentsNetwork;
    public static Compartment epithelium;
    public static Compartment lymphNode;
    public static Compartment laminaPropria;

    // Organism Setups
    public static int numMicrobe;
    public static int numDendriticCell;
    public static int numFOXP3TregCell;
    public static int numIntestinalEpithelialCell;
    public static int numIntraEpithelialCell;
    public static int numMacrophage;
    public static int numNaiveTCell;
    public static int numTh1Cell;
    public static int numTh17Cell;
    public static int numILC3;
    public static int numDummyCell;

    public static double getTimeSlice() { return timeSlice; }

    /*
     * 'stoppables' keeps track of all the stoppable objects associated with steppable objects that are scheduled to repeat during the simulation.
     * Methods are provided below so that new events (such as organisms) can be scheduled, and those that die can be removed. Steppables and Stoppables are MASON housekeeping
     * artefacts.
     */
    private Map<Steppable, Stoppable> stoppables = new HashMap<>();

    public int getNumStoppables() {
        return stoppables.size();
    }

    /* We order compartments to be scheduled before organisms. These figures relate to the Schedule MASON class. */
    public static int cellsOrdering = 0;                        // the ordering at which organisms scheduled. Cells first, makes the visuals match behaviour better (else they lag behind)
    public static int compartmentsOrdering = 1;                    // the ordering at which compartments are scheduled.

    /* if a simulation run takes longer than timeout seconds to complete, then we halt the simulation and throw and exception. If this is caught by a driver, than the experiment/analysisStore can continue rather than
     * the whole test being stopped. This might be necessary (for example) when running sensitivity analysisStore where parameter sets that cause the simulation to run out of physical space for organisms can occur.
     * Under most conditions this should never be a problem, and as such, the timeout can be set to a very high value.
     */
    private int timeout = Integer.MAX_VALUE;                            // as default there is no timeout.

    /**
     * Constructors. Takes a specified seed. Specifying the seed is important - although java will seed experiments based on the internal clock, when running batch experiments on a cluster
     * it is very possible that two machines will start with the same seed (I have explicitly confirmed this). When gathering data to form distributions representing experiments, it is critical
     * that you do not run simulations with the same seed, they skew the distributions. Hence, the seed must be explicitly provided.
     */
    public CrohnsSimulation(long seed, Document params, int timeoutSeconds) {
        super(seed);
        sim = this;                                            // singleton pattern
        parameters = params;
        timeout = timeoutSeconds * 1000;                    // convert from seconds into miliseconds.
    }

    /**
     * This is used for the @CrohnsDisease_GUI. It allows a different parameters file to be set before running the simulation.
     * In the case of @CrohnsDisease_GUI, it simply forces CrohnsSimulation to read the same parameters file
     * again so that any chances made to the file by the user can be reflected in the simulation without having to
     * restart the GUI.
     *
     * @param params
     */
    public void setParametersDocument(Document params) {
        parameters = params;
    }

    /**
     * Runs the simulation. This is the correct entry point into the simulation, the loading of parameters is done, and methods are called in the correct order.
     *
     * @events though all @Steppable objects related to the simulations logic are set up internally, it is sometimes desirable to explicitly add others. As an example, the
     * data stores that log simulation progression might be added; since they are not core to the simulations logic (but are instead observers that monitor and record simulation
     * progression for later analysisStore), they are not created or scheduled in the contents of this method.
     * <p>
     * Note that some of the data loggers are created as part of the simulation logic. There is a separation made between the objects that extract information directly from simulation logic (such
     * as querying a cell type for some information), which can require the simulation logic to know about the loggers, and storing that information for however long the user of the system
     * requires (in this case, the simulation logic does not need to interact directly, so data stores are not instantiated at any point following this method call).
     */
    public void run(double endTime, Steppable... events) {
        start();                                            // sets up the simulation.

        // schedule the events into the schedule.
        for (Steppable event : events) {
            addToSimulationScheduleRepeating(Schedule.EPOCH, 10, event, 1.0);
        }

        long steps;
        double time;
        System.out.println("Simulation will stop at " + endTime + " time");
        do {
            if (!sim.schedule.step(sim))            // performs the step, and if return is false, stops looping.
                break;
            steps = sim.schedule.getSteps();        // How many steps have been performed?
            time = sim.schedule.getTime();            // retrieve the current time in the simulation.
//            System.out.println("Current step: " + steps);
//            System.out.println("Current time: " + time);
        } while (time <= endTime);                    // stopping condition.

        finish();                                    // tears down the simulation
    }

    @Override
    public void finish() {
        super.finish();
        AnalysisStore.collateAllData();
    }

    /**
     * Method is called to start the simulation. The bodily compartments are set up and connected together.
     * The compartments are populated with organisms. If an immunization is required, then a @Steppable object that performs the immunization at the
     * correct time is created and scheduled.
     */
    public void start() {
        super.start();                                                        // call supertype's start method.
        CrohnsSimulation.sim.setupSimulationParameters();                        // essential that we do this here.
        compartmentsNetwork = new Network();                                // compartments in the simulation are connected together as a MASON network.

        /* Set up the correct type of compartments, based on the requested dimensions. */
        epithelium = new Epithelium(this);
        lymphNode = new LymphNode(this);
        laminaPropria = new LaminaPropria(this);

        // treat our compartments as nodes in a network
        compartmentsNetwork.addNode(epithelium);
        compartmentsNetwork.addNode(lymphNode);
        compartmentsNetwork.addNode(laminaPropria);

        /* connect up the network, as edges.
         */
        compartmentsNetwork.addEdge(epithelium, laminaPropria, null);
        compartmentsNetwork.addEdge(epithelium, lymphNode, null);
        compartmentsNetwork.addEdge(lymphNode, epithelium, null);
        compartmentsNetwork.addEdge(lymphNode, laminaPropria, null);
        compartmentsNetwork.addEdge(laminaPropria, epithelium, null);
        compartmentsNetwork.addEdge(laminaPropria, lymphNode, null);

        /* schedule the compartments with the scheduler. That will handle things like cell movements and molecule diffusions. */
        addCompartmentToSimulationScheduleRepeatingEpoch(epithelium);
        addCompartmentToSimulationScheduleRepeatingEpoch(lymphNode);
        addCompartmentToSimulationScheduleRepeatingEpoch(laminaPropria);

        // Populate Compartments with Cells
        populateCompartments();

        // Allow analysisStore class to be steppable
        addToSimulationScheduleRepeating(Schedule.EPOCH, 2, new AnalysisStore(), timeSlice);

        /* The mechanism that handles shutting down the simulation if a timeout has occurred. */
        addToSimulationScheduleRepeating(Schedule.EPOCH, 3,
                /** Anonymous class that checks for how long the simulation has been running for and throws a runtime exception if the timeout is exceeded.*/
                new Steppable() {
                    private long startTime = System.currentTimeMillis();

                    @Override
                    public void step(SimState state) {
                        if (totalSimulationOrganism() > 30000) {
                            System.out.println("Killing simulation, > 30000 organisms in simulation.");
                            throw new RuntimeException("Killing simulation, > 30000 organisms in simulation.");
                        }
                    }
                },
                timeSlice);
    }

    public static int totalSimulationOrganism() {
        int total = 0;
        total += epithelium.totalOrganism();
        total += lymphNode.totalOrganism();
        total += laminaPropria.totalOrganism();
        return total;
    }

    /**
     * Method populates the compartments with organisms. The basal level of organisms are created, since this is what we would expect to see had the simulation been running in absence of immunization
     * for some time. These organisms are randomly assigned to the various compartments in the simulation.
     */
    private void populateCompartments() {

        for (int i = 0; i < numMicrobe; i++) {
            new Microbe(epithelium);
        }

        for (int i = 0; i < numDendriticCell; i++) {
            new DendriticCell(laminaPropria);
        }

        for (int i = 0; i < numIntestinalEpithelialCell; i++) {
            new IntestinalEpithelialCell(epithelium);
        }

        for (int i = 0; i < numIntraEpithelialCell; i++) {
            new IntraepithelialCell(epithelium);
        }

        for (int i = 0; i < numMacrophage; i++) {
            new Macrophage(laminaPropria);
        }

        for (int i = 0; i < numNaiveTCell; i++) {
            new NaiveTCell(lymphNode);
        }

        for (int i = 0; i < numTh1Cell; i++) {
            new Th1Cell(laminaPropria);
        }

        for (int i = 0; i < numTh17Cell; i++) {
            new Th17Cell(laminaPropria);
        }

        for (int i = 0; i < numFOXP3TregCell; i++) {
            new FOXP3TregCell(laminaPropria);
        }

        for (int i = 0; i < numDummyCell; i++) {
            new DummyCell(epithelium);
        }
    }

    /**
     * This method removes the provided @Steppable object from the simulation's schedule. Necessary for when (for example) organisms are killed.
     */
    public void removeFromSimulationSchedule(Steppable event) {
        if (stoppables.containsKey(event)) {
            Stoppable stop = stoppables.remove(event);
            stop.stop();                                            // stop the associated steppable from being scheduled in the schedule.
        }
    }

    /**
     * This method adds the given event to the simulation's schedule, with the given ordering and interval. It keeps track of the event's stoppable object
     * such that the event can later be removed from the schedule.
     *
     * @param event what is to be scheduled.
     * @param time  how frequent this event is to be scheduled. Should (pretty much) always be 1.
     */
    public void addOrganismToSimulationScheduleRepeating(Steppable event, double time) {
        Stoppable stoppable = schedule.scheduleRepeating(time, cellsOrdering, event, timeSlice);
        stoppables.put(event, stoppable);                            // store the stoppable so that we can get at it later.
    }

    private void addCompartmentToSimulationScheduleRepeatingEpoch(Steppable event) {
        Stoppable stoppable = schedule.scheduleRepeating(1.0, cellsOrdering, event, timeSlice);        // not sure why we add 1.0, but it is necessary.
        stoppables.put(event, stoppable);                            // store the stoppable so that we can get at it later.
    }

    /**
     * Use of the @Steppable is not a cell or a compartment.
     */
    public void addToSimulationScheduleRepeating(double time, int ordering, Steppable event, double interval) {
        Stoppable stoppable = schedule.scheduleRepeating(time, ordering, event, interval);
        stoppables.put(event, stoppable);
    }

    /**
     * Given the parameters.xml file (represented as a 'Document') this method loads the relevant default values for the top level simulation.
     *
     * @param params
     */
    private static void loadParameters(Document params) {

        Element pE = (Element) params.getElementsByTagName("Simulation").item(0);            // collect those items under 'Simulation'

        /* Retrieve the cell*/
        numMicrobe = Integer.parseInt(pE.getElementsByTagName("numMicrobe").item(0).getTextContent());
        numDendriticCell = Integer.parseInt(pE.getElementsByTagName("numDendriticCell").item(0).getTextContent());
        numFOXP3TregCell = Integer.parseInt(pE.getElementsByTagName("numFOXP3TregCell").item(0).getTextContent());
        numIntestinalEpithelialCell = Integer.parseInt(pE.getElementsByTagName("numIntestinalEpithelialCell").item(0).getTextContent());
        numIntraEpithelialCell = Integer.parseInt(pE.getElementsByTagName("numIntraEpithelialCell").item(0).getTextContent());
        numMacrophage = Integer.parseInt(pE.getElementsByTagName("numMacrophage").item(0).getTextContent());
        numNaiveTCell = Integer.parseInt(pE.getElementsByTagName("numNaiveTCell").item(0).getTextContent());
        numTh1Cell = Integer.parseInt(pE.getElementsByTagName("numTh1Cell").item(0).getTextContent());
        numTh17Cell = Integer.parseInt(pE.getElementsByTagName("numTh17Cell").item(0).getTextContent());
        numDummyCell = Integer.parseInt(pE.getElementsByTagName("numDummyCell").item(0).getTextContent());

        /* retrieve the timeslice */
        timeSlice = Double.parseDouble(pE.getElementsByTagName("timeSlice").item(0).getTextContent());
    }

    /**
     * Loads the parameters.xml config file and loads the default parameters for all classes in the simulation. Should be the first thing that is done when
     * running the simulation, with GUI or without. Abstract classes must be called before concrete classes.
     */
    public void setupSimulationParameters() {

        /* read in the default parameters for the various classes in the simulation */
        loadParameters(parameters);

        /* Loads Abstract classes before Concrete class */
        Compartment.loadParameters(parameters);
        Phagocyte_Impl.loadParameters(parameters);
//        Cell_Impl.loadParameters(parameters); // Does nothing for now
//        Microbe_Impl.loadParameters(parameters); // Does nothing for now
        TCell_Impl.loadParameters(parameters);
        Molecule.loadParameters(parameters);

        /* Loads the concrete class */
        // Compartments
        Epithelium.loadParameters(parameters);
        LymphNode.loadParameters(parameters);
        LaminaPropria.loadParameters(parameters);

        // Cells
//        Microbe.loadParameters(parameters);
        DendriticCell.loadParameters(parameters);
        FOXP3TregCell.loadParameters(parameters);
        ILC3.loadParameters(parameters);
        IntestinalEpithelialCell.loadParameters(parameters);
        IntraepithelialCell.loadParameters(parameters);
        Macrophage.loadParameters(parameters);
        NaiveTCell.loadParameters(parameters);
        Th1Cell.loadParameters(parameters);
        Th17Cell.loadParameters(parameters);
    }

    /**
     * If the simulation runs for longer than a particular (real world, not simulation world) time, representing the strong possibility that a parameter set has been provided for which simulation
     * execution will not progress (for example, proliferation set so high that there is physically no space for organisms to be place within the simulation anymore), then this exception is thrown.
     * <p>
     * NOTE, that this exception is not actually used anywhere. Throwing a bespoke exception is better use of java, but would require changing mason method signatures, which
     * would require that this simulation be shipped with a specific mason that i have altered, rather than any mason installation.
     *
     * @author mark
     */
    public class SimulationTimeoutException extends Exception {
        private double timeout;

        public SimulationTimeoutException(double timeout) {
            this.timeout = timeout;
        }

        public String toString() {
            return "The simulation timed out, timeout was set to : " + timeout;
        }
    }
}
