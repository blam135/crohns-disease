package sim2d;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.Schedule;
import sim.portrayal.Inspector;
import sim.portrayal.grid.FastValueGridPortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim2d.compartment.impl.Compartment_Impl2D_Crohns;
import sim2d.organism.cells.*;
import sim2d.filesystem.FileSystemIO;
import sim2d.organism.microbes.Microbe;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the top level class that runs a simulation, called from the driver classes that are used for automation (such as can be found in the package sim2d.experiment).
 * <p>
 * The simulation is set-up and torn-down in this class. This class runs the simulation absent of any GUI related function - the reason that a lot of the variables here (and
 * elsewhere in the project) are public is so that the GUI can be placed on top of simulation logic without any need to change the logic to accommodate it. It is a MASON design pattern.
 *
 * @author mark
 * @version updated by Brendon Lam for Crohn's Disease context
 */
public class CrohnsDisease_GUI extends GUIState {
    public Console console;                    // we keep a reference to the console so that we can attach JFrames to it.

    // Epithelium
    public Display2D epitheliumDisplay;
    public JFrame epitheliumDisplayFrame;
    public SparseGridPortrayal2D epitheliumCellsGridPortrayal = new SparseGridPortrayal2D();
    public FastValueGridPortrayal2D epitheliumAntiMicrobialPeptidePortrayal = new FastValueGridPortrayal2D("AntiMicrobialPeptide");
    public FastValueGridPortrayal2D epitheliumGMCSFPortrayal = new FastValueGridPortrayal2D("GMCSF");
    public FastValueGridPortrayal2D epitheliumMucusPortrayal = new FastValueGridPortrayal2D("Mucus");
    public FastValueGridPortrayal2D epitheliumIFNyPortrayal = new FastValueGridPortrayal2D("IFNy");
    public FastValueGridPortrayal2D epitheliumIL1BPortrayal = new FastValueGridPortrayal2D("IL1B");
    public FastValueGridPortrayal2D epitheliumIL2Portrayal = new FastValueGridPortrayal2D("IL2");
    public FastValueGridPortrayal2D epitheliumIL4Portrayal = new FastValueGridPortrayal2D("IL4");
    public FastValueGridPortrayal2D epitheliumIL6Portrayal = new FastValueGridPortrayal2D("IL6");
    public FastValueGridPortrayal2D epitheliumIL7Portrayal = new FastValueGridPortrayal2D("IL7");
    public FastValueGridPortrayal2D epitheliumIL8Portrayal = new FastValueGridPortrayal2D("IL8");
    public FastValueGridPortrayal2D epitheliumIL10Portrayal = new FastValueGridPortrayal2D("IL10");
    public FastValueGridPortrayal2D epitheliumIL12Portrayal = new FastValueGridPortrayal2D("IL12");
    public FastValueGridPortrayal2D epitheliumIL13Portrayal = new FastValueGridPortrayal2D("IL13");
    public FastValueGridPortrayal2D epitheliumIL15Portrayal = new FastValueGridPortrayal2D("IL15");
    public FastValueGridPortrayal2D epitheliumIL17Portrayal = new FastValueGridPortrayal2D("IL17");
    public FastValueGridPortrayal2D epitheliumIL18Portrayal = new FastValueGridPortrayal2D("IL18");
    public FastValueGridPortrayal2D epitheliumIL21Portrayal = new FastValueGridPortrayal2D("IL21");
    public FastValueGridPortrayal2D epitheliumIL22Portrayal = new FastValueGridPortrayal2D("IL22");
    public FastValueGridPortrayal2D epitheliumIL23Portrayal = new FastValueGridPortrayal2D("IL23");
    public FastValueGridPortrayal2D epitheliumIL33Portrayal = new FastValueGridPortrayal2D("IL33");
    public FastValueGridPortrayal2D epitheliumIL35Portrayal = new FastValueGridPortrayal2D("IL35");
    public FastValueGridPortrayal2D epitheliumTGFBPortrayal = new FastValueGridPortrayal2D("TGFB");
    public FastValueGridPortrayal2D epitheliumTNFPortrayal = new FastValueGridPortrayal2D("TNF");
    public FastValueGridPortrayal2D epitheliumType1IFNPortrayal = new FastValueGridPortrayal2D("Type1IFN");

    //	LymphNode Layer
    public Display2D lymphNodeDisplay;
    public JFrame lymphNodeDisplayFrame;
    public SparseGridPortrayal2D lymphNodeCellsGridPortrayal = new SparseGridPortrayal2D();
    public FastValueGridPortrayal2D lymphNodeAntiMicrobialPeptidePortrayal = new FastValueGridPortrayal2D("AntiMicrobialPeptide");
    public FastValueGridPortrayal2D lymphNodeGMCSFPortrayal = new FastValueGridPortrayal2D("GMCSF");
    public FastValueGridPortrayal2D lymphNodeMucusPortrayal = new FastValueGridPortrayal2D("Mucus");
    public FastValueGridPortrayal2D lymphNodeIFNyPortrayal = new FastValueGridPortrayal2D("IFNy");
    public FastValueGridPortrayal2D lymphNodeIL1BPortrayal = new FastValueGridPortrayal2D("IL1B");
    public FastValueGridPortrayal2D lymphNodeIL2Portrayal = new FastValueGridPortrayal2D("IL2");
    public FastValueGridPortrayal2D lymphNodeIL4Portrayal = new FastValueGridPortrayal2D("IL4");
    public FastValueGridPortrayal2D lymphNodeIL6Portrayal = new FastValueGridPortrayal2D("IL6");
    public FastValueGridPortrayal2D lymphNodeIL7Portrayal = new FastValueGridPortrayal2D("IL7");
    public FastValueGridPortrayal2D lymphNodeIL8Portrayal = new FastValueGridPortrayal2D("IL8");
    public FastValueGridPortrayal2D lymphNodeIL10Portrayal = new FastValueGridPortrayal2D("IL10");
    public FastValueGridPortrayal2D lymphNodeIL12Portrayal = new FastValueGridPortrayal2D("IL12");
    public FastValueGridPortrayal2D lymphNodeIL13Portrayal = new FastValueGridPortrayal2D("IL13");
    public FastValueGridPortrayal2D lymphNodeIL15Portrayal = new FastValueGridPortrayal2D("IL15");
    public FastValueGridPortrayal2D lymphNodeIL17Portrayal = new FastValueGridPortrayal2D("IL17");
    public FastValueGridPortrayal2D lymphNodeIL18Portrayal = new FastValueGridPortrayal2D("IL18");
    public FastValueGridPortrayal2D lymphNodeIL21Portrayal = new FastValueGridPortrayal2D("IL21");
    public FastValueGridPortrayal2D lymphNodeIL22Portrayal = new FastValueGridPortrayal2D("IL22");
    public FastValueGridPortrayal2D lymphNodeIL23Portrayal = new FastValueGridPortrayal2D("IL23");
    public FastValueGridPortrayal2D lymphNodeIL33Portrayal = new FastValueGridPortrayal2D("IL33");
    public FastValueGridPortrayal2D lymphNodeIL35Portrayal = new FastValueGridPortrayal2D("IL35");
    public FastValueGridPortrayal2D lymphNodeTGFBPortrayal = new FastValueGridPortrayal2D("TGFB");
    public FastValueGridPortrayal2D lymphNodeTNFPortrayal = new FastValueGridPortrayal2D("TNF");
    public FastValueGridPortrayal2D lymphNodeType1IFNPortrayal = new FastValueGridPortrayal2D("Type1IFN");

    // Lamina Propria Layer
    public Display2D laminaPropriaDisplay;
    public JFrame laminaPropriaDisplayFrame;
    public SparseGridPortrayal2D laminaPropriaCellsGridPortrayal = new SparseGridPortrayal2D();
    public FastValueGridPortrayal2D laminaPropriaAntiMicrobialPeptidePortrayal = new FastValueGridPortrayal2D("AntiMicrobialPeptide");
    public FastValueGridPortrayal2D laminaPropriaGMCSFPortrayal = new FastValueGridPortrayal2D("GMCSF");
    public FastValueGridPortrayal2D laminaPropriaMucusPortrayal = new FastValueGridPortrayal2D("Mucus");
    public FastValueGridPortrayal2D laminaPropriaIFNyPortrayal = new FastValueGridPortrayal2D("IFNy");
    public FastValueGridPortrayal2D laminaPropriaIL1BPortrayal = new FastValueGridPortrayal2D("IL1B");
    public FastValueGridPortrayal2D laminaPropriaIL2Portrayal = new FastValueGridPortrayal2D("IL2");
    public FastValueGridPortrayal2D laminaPropriaIL4Portrayal = new FastValueGridPortrayal2D("IL4");
    public FastValueGridPortrayal2D laminaPropriaIL6Portrayal = new FastValueGridPortrayal2D("IL6");
    public FastValueGridPortrayal2D laminaPropriaIL7Portrayal = new FastValueGridPortrayal2D("IL7");
    public FastValueGridPortrayal2D laminaPropriaIL8Portrayal = new FastValueGridPortrayal2D("IL8");
    public FastValueGridPortrayal2D laminaPropriaIL10Portrayal = new FastValueGridPortrayal2D("IL10");
    public FastValueGridPortrayal2D laminaPropriaIL12Portrayal = new FastValueGridPortrayal2D("IL12");
    public FastValueGridPortrayal2D laminaPropriaIL13Portrayal = new FastValueGridPortrayal2D("IL13");
    public FastValueGridPortrayal2D laminaPropriaIL15Portrayal = new FastValueGridPortrayal2D("IL15");
    public FastValueGridPortrayal2D laminaPropriaIL17Portrayal = new FastValueGridPortrayal2D("IL17");
    public FastValueGridPortrayal2D laminaPropriaIL18Portrayal = new FastValueGridPortrayal2D("IL18");
    public FastValueGridPortrayal2D laminaPropriaIL21Portrayal = new FastValueGridPortrayal2D("IL21");
    public FastValueGridPortrayal2D laminaPropriaIL22Portrayal = new FastValueGridPortrayal2D("IL22");
    public FastValueGridPortrayal2D laminaPropriaIL23Portrayal = new FastValueGridPortrayal2D("IL23");
    public FastValueGridPortrayal2D laminaPropriaIL33Portrayal = new FastValueGridPortrayal2D("IL33");
    public FastValueGridPortrayal2D laminaPropriaIL35Portrayal = new FastValueGridPortrayal2D("IL35");
    public FastValueGridPortrayal2D laminaPropriaTGFBPortrayal = new FastValueGridPortrayal2D("TGFB");
    public FastValueGridPortrayal2D laminaPropriaTNFPortrayal = new FastValueGridPortrayal2D("TNF");
    public FastValueGridPortrayal2D laminaPropriaType1IFNPortrayal = new FastValueGridPortrayal2D("Type1IFN");

    // Molecule Colours
    private final Color AntiMicrobialPeptideColor = Color.white;
    private final Color GMCSFColor = Color.white;
    private final Color MucusColor = Color.green;
    private final Color IFNyColor = Color.white;
    private final Color IL1BColor = Color.white;
    private final Color IL2Color = Color.white;
    private final Color IL4Color = Color.white;
    private final Color IL6Color = Color.white;
    private final Color IL7Color = Color.white;
    private final Color IL8Color = Color.white;
    private final Color IL10Color = Color.white;
    private final Color IL12Color = Color.white;
    private final Color IL13Color = Color.white;
    private final Color IL15Color = Color.white;
    private final Color IL17Color = Color.white;
    private final Color IL18Color = Color.white;
    private final Color IL21Color = Color.white;
    private final Color IL22Color = Color.white;
    private final Color IL23Color = Color.white;
    private final Color IL33Color = Color.white;
    private final Color IL35Color = Color.white;
    private final Color TGFBColor = Color.white;
    private final Color TNFColor = Color.white;
    private final Color Type1IFNColor = Color.white;

    // Molecule Max Values
    private final double AntiMicrobialPeptideMax = 15.0;
    private final double GMCSFMax = 15.0;
    private final double MucusMax = 15.0;
    private final double IFNyMax = 15.0;
    private final double IL1BMax = 15.0;
    private final double IL2Max = 15.0;
    private final double IL4Max = 15.0;
    private final double IL6Max = 15.0;
    private final double IL7Max = 15.0;
    private final double IL8Max = 15.0;
    private final double IL10Max = 15.0;
    private final double IL12Max = 15.0;
    private final double IL13Max = 15.0;
    private final double IL15Max = 15.0;
    private final double IL17Max = 15.0;
    private final double IL18Max = 15.0;
    private final double IL21Max = 15.0;
    private final double IL22Max = 15.0;
    private final double IL23Max = 15.0;
    private final double IL33Max = 15.0;
    private final double IL35Max = 15.0;
    private final double TGFBMax = 15.0;
    private final double TNFMax = 15.0;
    private final double Type1IFNMax = 15.0;

    // Organism Color Portrayals
    public static Color MicrobeCol = Color.WHITE;
    public static Color DendriticCellCol = Color.RED;
    public static Color FOXP3TregCellCol = new Color(0, 100, 0); // Dark green
    public static Color ILC3Col = Color.WHITE;
    public static Color IntestinalEpithelialCellCol = Color.BLUE;
    public static Color IntraepithelialCellCol = new Color(135, 169, 107); // Asparagus Color
    public static Color MacrophageCol = new Color(168, 68, 250); // Purple
    public static Color NaiveTCellCol = new Color(128, 128, 128); // Grey
    public static Color Th1CellCol = Color.YELLOW;
    public static Color Th17CellCol = Color.GREEN;
    public static Color DummyCellCol = Color.RED;
    private Map<String, Color> colsMap = new HashMap<>();

    private String fileName;

    public CrohnsDisease_GUI(long seed, String fileName) {

        super(new CrohnsSimulation(seed,
                FileSystemIO.openXMLFile(fileName),
                1000000
        ));
        this.fileName = fileName;

        CrohnsSimulation.sim.setupSimulationParameters();                        // essential that we do this here.
        colsMap.put("Microbe", MicrobeCol);
        colsMap.put("DendriticCell", DendriticCellCol);
        colsMap.put("FOXP3TregCell", FOXP3TregCellCol);
        colsMap.put("ILC3", ILC3Col);
        colsMap.put("IntestinalEpithelialCell", IntestinalEpithelialCellCol);
        colsMap.put("IntraepithelialCell", IntraepithelialCellCol);
        colsMap.put("Macrophage", MacrophageCol);
        colsMap.put("NaiveTCell", NaiveTCellCol);
        colsMap.put("Th1Cell", Th1CellCol);
        colsMap.put("Th17Cell", Th17CellCol);
        colsMap.put("DummyCell", DummyCellCol);
    }

    public static String getName() {
        return "Crohn's Disease Simulation";
    }

    public static Object getInfo() {
        return "No information at this time.";
    }

    public void start() {
        // this forces the simulation to re-read the parameters file, such that any changes made by the user can be reflected immediately
        // without having to restart the GUI.
        ((CrohnsSimulation) this.state).setParametersDocument(FileSystemIO.openXMLFile(fileName));
        super.start();
        setupPortrayals();
    }

    private void setupPortrayals() { // tell the portrayals what to portray and how to portray them

        // set the portrayals to portray the cellsGrid in their respective compartments.
        lymphNodeCellsGridPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).cellsGrid);
        epitheliumCellsGridPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).cellsGrid);
        laminaPropriaCellsGridPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).cellsGrid);


        // Molecule Field Portrayal for Epithelium
        // NOTE: MAKE SURE THAT THE GRID MAP IN Compartment_Impl2D_Crohns EXIST
        epitheliumAntiMicrobialPeptidePortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("AntiMicrobialPeptideGrid"));
        epitheliumAntiMicrobialPeptidePortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, AntiMicrobialPeptideMax, Color.black, AntiMicrobialPeptideColor));
        epitheliumGMCSFPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("GMCSFGrid"));
        epitheliumGMCSFPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, GMCSFMax, Color.black, GMCSFColor));
        epitheliumMucusPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("MucusGrid"));
        epitheliumMucusPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, MucusMax, Color.black, MucusColor));
        epitheliumIFNyPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IFNyGrid"));
        epitheliumIFNyPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IFNyMax, Color.black, IFNyColor));
        epitheliumIL1BPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL1BGrid"));
        epitheliumIL1BPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL1BMax, Color.black, IL1BColor));
        epitheliumIL2Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL2Grid"));
        epitheliumIL2Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL2Max, Color.black, IL2Color));
        epitheliumIL4Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL4Grid"));
        epitheliumIL4Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL4Max, Color.black, IL4Color));
        epitheliumIL6Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL6Grid"));
        epitheliumIL7Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL7Max, Color.black, IL7Color));
        epitheliumIL7Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL7Grid"));
        epitheliumIL6Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL6Max, Color.black, IL6Color));
        epitheliumIL8Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL8Grid"));
        epitheliumIL8Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL8Max, Color.black, IL8Color));
        epitheliumIL10Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL10Grid"));
        epitheliumIL10Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL10Max, Color.black, IL10Color));
        epitheliumIL12Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL12Grid"));
        epitheliumIL12Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL12Max, Color.black, IL12Color));
        epitheliumIL13Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL13Grid"));
        epitheliumIL13Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL13Max, Color.black, IL13Color));
        epitheliumIL15Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL15Grid"));
        epitheliumIL15Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL15Max, Color.black, IL15Color));
        epitheliumIL17Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL17Grid"));
        epitheliumIL17Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL17Max, Color.black, IL17Color));
        epitheliumIL18Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL18Grid"));
        epitheliumIL18Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL18Max, Color.black, IL18Color));
        epitheliumIL21Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL21Grid"));
        epitheliumIL21Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL21Max, Color.black, IL21Color));
        epitheliumIL22Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL22Grid"));
        epitheliumIL22Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL22Max, Color.black, IL22Color));
        epitheliumIL23Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL23Grid"));
        epitheliumIL23Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL23Max, Color.black, IL23Color));
        epitheliumIL33Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL33Grid"));
        epitheliumIL33Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL33Max, Color.black, IL33Color));
        epitheliumIL35Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("IL35Grid"));
        epitheliumIL35Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL35Max, Color.black, IL35Color));
        epitheliumTGFBPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("TGFBGrid"));
        epitheliumTGFBPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, TGFBMax, Color.black, TGFBColor));
        epitheliumTNFPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("TNFGrid"));
        epitheliumTNFPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, TNFMax, Color.black, TNFColor));
        epitheliumType1IFNPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).epithelium).gridMap.get("Type1IFNGrid"));
        epitheliumType1IFNPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, Type1IFNMax, Color.black, Type1IFNColor));

        // Molecule Field Portrayal for LymphNode
        lymphNodeAntiMicrobialPeptidePortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("AntiMicrobialPeptideGrid"));
        lymphNodeAntiMicrobialPeptidePortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, AntiMicrobialPeptideMax, Color.black, AntiMicrobialPeptideColor));
        lymphNodeGMCSFPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("GMCSFGrid"));
        lymphNodeGMCSFPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, GMCSFMax, Color.black, GMCSFColor));
        lymphNodeMucusPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("MucusGrid"));
        lymphNodeMucusPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, MucusMax, Color.black, MucusColor));
        lymphNodeIFNyPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IFNyGrid"));
        lymphNodeIFNyPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IFNyMax, Color.black, IFNyColor));
        lymphNodeIL1BPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL1BGrid"));
        lymphNodeIL1BPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL1BMax, Color.black, IL1BColor));
        lymphNodeIL2Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL2Grid"));
        lymphNodeIL2Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL2Max, Color.black, IL2Color));
        lymphNodeIL4Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL4Grid"));
        lymphNodeIL4Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL4Max, Color.black, IL4Color));
        lymphNodeIL6Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL6Grid"));
        lymphNodeIL6Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL6Max, Color.black, IL6Color));
        lymphNodeIL7Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL7Grid"));
        lymphNodeIL7Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL7Max, Color.black, IL7Color));
        lymphNodeIL8Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL8Grid"));
        lymphNodeIL8Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL8Max, Color.black, IL8Color));
        lymphNodeIL10Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL10Grid"));
        lymphNodeIL10Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL10Max, Color.black, IL10Color));
        lymphNodeIL12Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL12Grid"));
        lymphNodeIL12Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL12Max, Color.black, IL12Color));
        lymphNodeIL13Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL13Grid"));
        lymphNodeIL13Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL13Max, Color.black, IL13Color));
        lymphNodeIL15Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL15Grid"));
        lymphNodeIL15Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL15Max, Color.black, IL15Color));
        lymphNodeIL17Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL17Grid"));
        lymphNodeIL17Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL17Max, Color.black, IL17Color));
        lymphNodeIL18Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL18Grid"));
        lymphNodeIL18Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL18Max, Color.black, IL18Color));
        lymphNodeIL21Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL21Grid"));
        lymphNodeIL21Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL21Max, Color.black, IL21Color));
        lymphNodeIL22Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL22Grid"));
        lymphNodeIL22Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL22Max, Color.black, IL22Color));
        lymphNodeIL23Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL23Grid"));
        lymphNodeIL23Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL23Max, Color.black, IL23Color));
        lymphNodeIL33Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL33Grid"));
        lymphNodeIL33Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL33Max, Color.black, IL33Color));
        lymphNodeIL35Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("IL35Grid"));
        lymphNodeIL35Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL35Max, Color.black, IL35Color));
        lymphNodeTGFBPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("TGFBGrid"));
        lymphNodeTGFBPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, TGFBMax, Color.black, TGFBColor));
        lymphNodeTNFPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("TNFGrid"));
        lymphNodeTNFPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, TNFMax, Color.black, TNFColor));
        lymphNodeType1IFNPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("Type1IFNGrid"));
        lymphNodeType1IFNPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, Type1IFNMax, Color.black, Type1IFNColor));

        // Molecule Field Portrayal for LaminaPropria
        laminaPropriaAntiMicrobialPeptidePortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("AntiMicrobialPeptideGrid"));
        laminaPropriaAntiMicrobialPeptidePortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, AntiMicrobialPeptideMax, Color.black, AntiMicrobialPeptideColor));
        laminaPropriaGMCSFPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("GMCSFGrid"));
        laminaPropriaGMCSFPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, GMCSFMax, Color.black, GMCSFColor));
        lymphNodeMucusPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).lymphNode).gridMap.get("MucusGrid"));
        lymphNodeMucusPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, MucusMax, Color.black, MucusColor));
        laminaPropriaIFNyPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IFNyGrid"));
        laminaPropriaIFNyPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IFNyMax, Color.black, IFNyColor));
        laminaPropriaIL1BPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL1BGrid"));
        laminaPropriaIL1BPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL1BMax, Color.black, IL1BColor));
        laminaPropriaIL2Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL2Grid"));
        laminaPropriaIL2Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL2Max, Color.black, IL2Color));
        laminaPropriaIL4Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL4Grid"));
        laminaPropriaIL4Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL4Max, Color.black, IL4Color));
        laminaPropriaIL6Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL6Grid"));
        laminaPropriaIL6Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL6Max, Color.black, IL6Color));
        laminaPropriaIL7Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL7Grid"));
        laminaPropriaIL7Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL7Max, Color.black, IL7Color));
        laminaPropriaIL8Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL8Grid"));
        laminaPropriaIL8Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL8Max, Color.black, IL8Color));
        laminaPropriaIL10Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL10Grid"));
        laminaPropriaIL10Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL10Max, Color.black, IL10Color));
        laminaPropriaIL12Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL12Grid"));
        laminaPropriaIL12Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL12Max, Color.black, IL12Color));
        laminaPropriaIL13Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL13Grid"));
        laminaPropriaIL13Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL13Max, Color.black, IL13Color));
        laminaPropriaIL15Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL15Grid"));
        laminaPropriaIL15Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL15Max, Color.black, IL15Color));
        laminaPropriaIL17Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL17Grid"));
        laminaPropriaIL17Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL17Max, Color.black, IL17Color));
        laminaPropriaIL18Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL18Grid"));
        laminaPropriaIL18Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL18Max, Color.black, IL18Color));
        laminaPropriaIL21Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL21Grid"));
        laminaPropriaIL21Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL21Max, Color.black, IL21Color));
        laminaPropriaIL22Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL22Grid"));
        laminaPropriaIL22Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL22Max, Color.black, IL22Color));
        laminaPropriaIL23Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL23Grid"));
        laminaPropriaIL23Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL23Max, Color.black, IL23Color));
        laminaPropriaIL33Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL33Grid"));
        laminaPropriaIL33Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL33Max, Color.black, IL33Color));
        laminaPropriaIL35Portrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("IL35Grid"));
        laminaPropriaIL35Portrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, IL35Max, Color.black, IL35Color));
        laminaPropriaTGFBPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("TGFBGrid"));
        laminaPropriaTGFBPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, TGFBMax, Color.black, TGFBColor));
        laminaPropriaTNFPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("TNFGrid"));
        laminaPropriaTNFPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, TNFMax, Color.black, TNFColor));
        laminaPropriaType1IFNPortrayal.setField(((Compartment_Impl2D_Crohns) ((CrohnsSimulation) state).laminaPropria).gridMap.get("Type1IFNGrid"));
        laminaPropriaType1IFNPortrayal.setMap(new sim.util.gui.SimpleColorMap(0.0, Type1IFNMax, Color.black, Type1IFNColor));

        /* set the colour to portray organisms in */
        setupCellsPortrayal(epitheliumCellsGridPortrayal);
        setupCellsPortrayal(lymphNodeCellsGridPortrayal);
        setupCellsPortrayal(laminaPropriaCellsGridPortrayal);

        // reschedule the displayers
        epitheliumDisplay.reset();
        lymphNodeDisplay.reset();
        laminaPropriaDisplay.reset();

        // Set Backdrops for each compartments
        epitheliumDisplay.setBackdrop(Color.BLACK);
        lymphNodeDisplay.setBackdrop(Color.BLACK);
        laminaPropriaDisplay.setBackdrop(Color.BLACK);

        // redraw the displays
        epitheliumDisplay.repaint();
        lymphNodeDisplay.repaint();
        laminaPropriaDisplay.repaint();
    }

    /**
     * Sets up the specified portrayal to draw organisms in the correct colours.
     */
    private void setupCellsPortrayal(SparseGridPortrayal2D portrayal) {
        portrayal.setPortrayalForClass(Microbe.class, new OvalPortrayal2D(MicrobeCol));
        portrayal.setPortrayalForClass(DendriticCell.class, new OvalPortrayal2D(DendriticCellCol));
        portrayal.setPortrayalForClass(FOXP3TregCell.class, new OvalPortrayal2D(FOXP3TregCellCol));
        portrayal.setPortrayalForClass(ILC3.class, new OvalPortrayal2D(ILC3Col));
        portrayal.setPortrayalForClass(IntestinalEpithelialCell.class, new OvalPortrayal2D(IntestinalEpithelialCellCol));
        portrayal.setPortrayalForClass(IntraepithelialCell.class, new OvalPortrayal2D(IntraepithelialCellCol));
        portrayal.setPortrayalForClass(Macrophage.class, new OvalPortrayal2D(MacrophageCol));
        portrayal.setPortrayalForClass(NaiveTCell.class, new OvalPortrayal2D(NaiveTCellCol));
        portrayal.setPortrayalForClass(Th1Cell.class, new OvalPortrayal2D(Th1CellCol));
        portrayal.setPortrayalForClass(Th17Cell.class, new OvalPortrayal2D(Th17CellCol));
        portrayal.setPortrayalForClass(DummyCell.class, new OvalPortrayal2D(DummyCellCol));
        portrayal.setPortrayalForNull(new sim.portrayal.simple.OvalPortrayal2D(Color.black));
        portrayal.setPortrayalForRemainder(new sim.portrayal.simple.OvalPortrayal2D(Color.white));
    }

    /*
     * Called when display windows and the such need initialization.
     */
    public void init(Controller c) {
        int width = 300;
        int height = 300;
        int height_extra = 27;
        super.init(c);

        /* make the displayers */
        epitheliumDisplay = new Display2D(width, height, this, 1);
        lymphNodeDisplay = new Display2D(width, height, this, 1);
        laminaPropriaDisplay = new Display2D(width, height, this, 1);

        /* turn off clipping */
        epitheliumDisplay.setClipping(false);
        lymphNodeDisplay.setClipping(false);
        laminaPropriaDisplay.setClipping(false);

        /* set up the compartment display frames */
        epitheliumDisplayFrame = epitheliumDisplay.createFrame();
        lymphNodeDisplayFrame = lymphNodeDisplay.createFrame();
        laminaPropriaDisplayFrame = laminaPropriaDisplay.createFrame();

        /* Set Title of each compartment */
        epitheliumDisplayFrame.setTitle("Epithelium");
        lymphNodeDisplayFrame.setTitle("Lymph Node");
        laminaPropriaDisplayFrame.setTitle("Lamina Propria");

        /* Register the frames */
        c.registerFrame(epitheliumDisplayFrame);
        c.registerFrame(lymphNodeDisplayFrame);
        c.registerFrame(laminaPropriaDisplayFrame);

        /* Set Frames to be visible */
        epitheliumDisplayFrame.setVisible(true);
        lymphNodeDisplayFrame.setVisible(true);
        laminaPropriaDisplayFrame.setVisible(true);

        /* Attach all the organisms and molecules to the epithelium Display */
        epitheliumDisplay.attach(epitheliumAntiMicrobialPeptidePortrayal, "AntiMicrobialPeptide", false);
        epitheliumDisplay.attach(epitheliumGMCSFPortrayal, "GMCSF", false);
        epitheliumDisplay.attach(epitheliumMucusPortrayal, "Mucus", false);
        epitheliumDisplay.attach(epitheliumIFNyPortrayal, "IFNy", false);
        epitheliumDisplay.attach(epitheliumIL1BPortrayal, "IL1B", false);
        epitheliumDisplay.attach(epitheliumIL2Portrayal, "IL2", false);
        epitheliumDisplay.attach(epitheliumIL4Portrayal, "IL4", false);
        epitheliumDisplay.attach(epitheliumIL6Portrayal, "IL6", false);
        epitheliumDisplay.attach(epitheliumIL7Portrayal, "IL7", false);
        epitheliumDisplay.attach(epitheliumIL8Portrayal, "IL8", false);
        epitheliumDisplay.attach(epitheliumIL10Portrayal, "IL10", false);
        epitheliumDisplay.attach(epitheliumIL12Portrayal, "IL12", false);
        epitheliumDisplay.attach(epitheliumIL13Portrayal, "IL13", false);
        epitheliumDisplay.attach(epitheliumIL15Portrayal, "IL15", false);
        epitheliumDisplay.attach(epitheliumIL17Portrayal, "IL17", false);
        epitheliumDisplay.attach(epitheliumIL18Portrayal, "IL18", false);
        epitheliumDisplay.attach(epitheliumIL21Portrayal, "IL21", false);
        epitheliumDisplay.attach(epitheliumIL22Portrayal, "IL22", false);
        epitheliumDisplay.attach(epitheliumIL23Portrayal, "IL23", false);
        epitheliumDisplay.attach(epitheliumIL33Portrayal, "IL33", false);
        epitheliumDisplay.attach(epitheliumIL35Portrayal, "IL35", false);
        epitheliumDisplay.attach(epitheliumTGFBPortrayal, "TGFB", false);
        epitheliumDisplay.attach(epitheliumTNFPortrayal, "TNF", false);
        epitheliumDisplay.attach(epitheliumType1IFNPortrayal, "Type1IFN", false);
        epitheliumDisplay.attach(epitheliumCellsGridPortrayal, "Cells", true); // Important Cells is at the bottom

        /* Attach all the organisms and molecules to the lymphNode Display */
        lymphNodeDisplay.attach(lymphNodeAntiMicrobialPeptidePortrayal, "AntiMicrobialPeptide", false);
        lymphNodeDisplay.attach(lymphNodeGMCSFPortrayal, "GMCSF", false);
        lymphNodeDisplay.attach(lymphNodeMucusPortrayal, "Mucus", false);
        lymphNodeDisplay.attach(lymphNodeIFNyPortrayal, "IFNy", false);
        lymphNodeDisplay.attach(lymphNodeIL1BPortrayal, "IL1B", false);
        lymphNodeDisplay.attach(lymphNodeIL2Portrayal, "IL2", false);
        lymphNodeDisplay.attach(lymphNodeIL4Portrayal, "IL4", false);
        lymphNodeDisplay.attach(lymphNodeIL6Portrayal, "IL6", false);
        lymphNodeDisplay.attach(lymphNodeIL7Portrayal, "IL7", false);
        lymphNodeDisplay.attach(lymphNodeIL8Portrayal, "IL8", false);
        lymphNodeDisplay.attach(lymphNodeIL10Portrayal, "IL10", false);
        lymphNodeDisplay.attach(lymphNodeIL12Portrayal, "IL12", false);
        lymphNodeDisplay.attach(lymphNodeIL13Portrayal, "IL13", false);
        lymphNodeDisplay.attach(lymphNodeIL15Portrayal, "IL15", false);
        lymphNodeDisplay.attach(lymphNodeIL17Portrayal, "IL17", false);
        lymphNodeDisplay.attach(lymphNodeIL18Portrayal, "IL18", false);
        lymphNodeDisplay.attach(lymphNodeIL21Portrayal, "IL21", false);
        lymphNodeDisplay.attach(lymphNodeIL22Portrayal, "IL22", false);
        lymphNodeDisplay.attach(lymphNodeIL23Portrayal, "IL23", false);
        lymphNodeDisplay.attach(lymphNodeIL33Portrayal, "IL33", false);
        lymphNodeDisplay.attach(lymphNodeIL35Portrayal, "IL35", false);
        lymphNodeDisplay.attach(lymphNodeTGFBPortrayal, "TGFB", false);
        lymphNodeDisplay.attach(lymphNodeTNFPortrayal, "TNF", false);
        lymphNodeDisplay.attach(lymphNodeType1IFNPortrayal, "Type1IFN", false);
        lymphNodeDisplay.attach(lymphNodeCellsGridPortrayal, "Cells", true); // Important Cells is at the bottom

        /* Attach all the organisms and molecules to the laminaPropria Display */
        laminaPropriaDisplay.attach(laminaPropriaAntiMicrobialPeptidePortrayal, "AntiMicrobialPeptide", false);
        laminaPropriaDisplay.attach(laminaPropriaGMCSFPortrayal, "GMCSF", false);
        laminaPropriaDisplay.attach(laminaPropriaMucusPortrayal, "Mucus", false);
        laminaPropriaDisplay.attach(laminaPropriaIFNyPortrayal, "IFNy", false);
        laminaPropriaDisplay.attach(laminaPropriaIL1BPortrayal, "IL1B", false);
        laminaPropriaDisplay.attach(laminaPropriaIL2Portrayal, "IL2", false);
        laminaPropriaDisplay.attach(laminaPropriaIL4Portrayal, "IL4", false);
        laminaPropriaDisplay.attach(laminaPropriaIL6Portrayal, "IL6", false);
        laminaPropriaDisplay.attach(laminaPropriaIL7Portrayal, "IL7", false);
        laminaPropriaDisplay.attach(laminaPropriaIL8Portrayal, "IL8", false);
        laminaPropriaDisplay.attach(laminaPropriaIL10Portrayal, "IL10", false);
        laminaPropriaDisplay.attach(laminaPropriaIL12Portrayal, "IL12", false);
        laminaPropriaDisplay.attach(laminaPropriaIL13Portrayal, "IL13", false);
        laminaPropriaDisplay.attach(laminaPropriaIL15Portrayal, "IL15", false);
        laminaPropriaDisplay.attach(laminaPropriaIL17Portrayal, "IL17", false);
        laminaPropriaDisplay.attach(laminaPropriaIL18Portrayal, "IL18", false);
        laminaPropriaDisplay.attach(laminaPropriaIL21Portrayal, "IL21", false);
        laminaPropriaDisplay.attach(laminaPropriaIL22Portrayal, "IL22", false);
        laminaPropriaDisplay.attach(laminaPropriaIL23Portrayal, "IL23", false);
        laminaPropriaDisplay.attach(laminaPropriaIL33Portrayal, "IL33", false);
        laminaPropriaDisplay.attach(laminaPropriaIL35Portrayal, "IL35", false);
        laminaPropriaDisplay.attach(laminaPropriaTGFBPortrayal, "TGFB", false);
        laminaPropriaDisplay.attach(laminaPropriaTNFPortrayal, "TNF", false);
        laminaPropriaDisplay.attach(laminaPropriaType1IFNPortrayal, "Type1IFN", false);
        laminaPropriaDisplay.attach(laminaPropriaCellsGridPortrayal, "Cells", true); // Important Cells is at the bottom

        /* place the display frames at the correct place on the screen */
        int bleed = 80;
        epitheliumDisplayFrame.setBounds(0, 0, width + bleed, height + bleed);                                            // params: (topleft x, topleft y, width, height).
        lymphNodeDisplayFrame.setBounds(epitheliumDisplayFrame.getX() + epitheliumDisplayFrame.getWidth() / 2, height + height_extra + bleed, width + bleed, height + bleed);                // params: (topleft x, topleft y, width, height).
        laminaPropriaDisplayFrame.setBounds(width + bleed, 0, width + bleed, height + bleed);
    }

    /**
     * Method called when the simulation terminates. At this point we need to draw the graphs.
     */
    public void finish() {
        super.finish();
    }

    public Object getSimulationInspectedObject() {
        return state;        // stored in the GUIState class, which we are a subclass of. It corresponds with a TregSim object.
    }

    /*
     * Used to set the inspector state to ****NOT*** 'volatile'. We do not require the inspector to update in real time, once the model parameters are set, the run
     * continues until it is stopped.
     */
    public Inspector getInspector() {
        Inspector i = super.getInspector();
        i.setVolatile(false);                // set this to true if you want the inspector to update.
        return i;
    }


    public void quit() {
        super.quit();

        if (epitheliumDisplayFrame != null) epitheliumDisplayFrame.dispose();
        epitheliumDisplayFrame = null;
        epitheliumDisplay = null;

        if (lymphNodeDisplayFrame != null) lymphNodeDisplayFrame.dispose();
        lymphNodeDisplayFrame = null;
        lymphNodeDisplay = null;

        if (laminaPropriaDisplayFrame != null) laminaPropriaDisplayFrame.dispose();
        laminaPropriaDisplayFrame = null;
        laminaPropriaDisplay = null;
    }

    /**
     * The main method that starts a CrohnsSimulation with the GUI.
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Please provide the name of the XML file in the arguments");
            return;
        }

        CrohnsDisease_GUI sim = new CrohnsDisease_GUI(100, args[0]);

        Console c = new Console(sim);
        sim.console = c;
        c.setVisible(true);
    }
}
