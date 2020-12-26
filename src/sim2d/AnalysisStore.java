package sim2d;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim2d.filesystem.FileSystemIO;
import sim2d.organism.cells.ILC3;
import sim2d.organism.cells.IntestinalEpithelialCell;
import sim2d.organism.cells.Macrophage;
import sim2d.organism.interfaces.Organism;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class AnalysisStore implements Steppable {

    AnalysisStore analysisStore;

    private static ArrayList<Integer> numOfCellInEpithelium = new ArrayList<>();
    private static ArrayList<Integer> numOfCellInLymphNode = new ArrayList<>();
    private static ArrayList<Integer> numOfCellInLaminaPropria = new ArrayList<>();
    private static ArrayList<Integer> numOfCellInSimulation = new ArrayList<>();

    private static ArrayList<Integer> numOfDendriticCell = new ArrayList<>();
    private static ArrayList<Integer> numOfILC3 = new ArrayList<>();
    private static ArrayList<Integer> numOfIEC = new ArrayList<>();
    private static ArrayList<Integer> numOfIEL = new ArrayList<>();
    private static ArrayList<Integer> numOfMacrophage = new ArrayList<>();
    private static ArrayList<Integer> numOfNaiveTCell = new ArrayList<>();
    private static ArrayList<Integer> numOfFOXP3TregCell = new ArrayList<>();
    private static ArrayList<Integer> numOfTh1Cell = new ArrayList<>();
    private static ArrayList<Integer> numOfTh17Cell = new ArrayList<>();

    private static ArrayList<Integer> numofHomeostaticIEC = new ArrayList<>();
    private static ArrayList<Integer> numOfInflammatoryIEC = new ArrayList<>();
    private static ArrayList<Integer> numofApoptoticIEC = new ArrayList<>();

    private static ArrayList<Integer> numOfM1Macrophage = new ArrayList<>();
    private static ArrayList<Integer> numOfM2Macrophage = new ArrayList<>();

    private static ArrayList<Integer> numOfNkp44Positive = new ArrayList<>();
    private static ArrayList<Integer> numOfNkp44Negative = new ArrayList<>();

    private static ArrayList<Double> timeStamp = new ArrayList<>();

    private static HashMap<String, Integer> thisIterationAmount = new HashMap<>();

    public AnalysisStore() {
        analysisStore = this;
        numOfCellInEpithelium.clear();
        numOfCellInLymphNode.clear();
        numOfCellInLaminaPropria.clear();
        numOfCellInSimulation.clear();
        numOfDendriticCell.clear();
        numOfILC3.clear();
        numOfIEC.clear();
        numOfIEL.clear();
        numOfMacrophage.clear();
        numOfNaiveTCell.clear();
        numOfFOXP3TregCell.clear();
        numOfTh1Cell.clear();
        numOfTh17Cell.clear();
        numOfInflammatoryIEC.clear();
        numofApoptoticIEC.clear();
        numofHomeostaticIEC.clear();
        numOfM1Macrophage.clear();
        numOfM2Macrophage.clear();
        numOfNkp44Positive.clear();
        numOfNkp44Negative.clear();
        timeStamp.clear();
        thisIterationAmount.clear();
    }

    private void addAmountToArrayList(HashMap<String, Integer> aggregates) {
        for (String key : aggregates.keySet()) {
            int value = aggregates.get(key);
            if (thisIterationAmount.containsKey(key)) {
                int newValue = thisIterationAmount.get(key) + value;
                thisIterationAmount.put(key, newValue);
            } else {
                thisIterationAmount.put(key, value);
            }
        }
    }

    private void aggregateThisIterationAmount() {
        for (String key : thisIterationAmount.keySet()) {
            switch(key) {
                case "DendriticCell":
                    numOfDendriticCell.add(thisIterationAmount.get(key));
                    break;
                case "FOXP3TregCell":
                    numOfFOXP3TregCell.add(thisIterationAmount.get(key));
                    break;
                case "ILC3":
                    numOfILC3.add(thisIterationAmount.get(key));
                    break;
                case "IntestinalEpithelialCell":
                    numOfIEC.add(thisIterationAmount.get(key));
                    break;
                case "IntraepithelialCell":
                    numOfIEL.add(thisIterationAmount.get(key));
                    break;
                case "Macrophage":
                    numOfMacrophage.add(thisIterationAmount.get(key));
                    break;
                case "NaiveTCell":
                    numOfNaiveTCell.add(thisIterationAmount.get(key));
                    break;
                case "Th1Cell":
                    numOfTh1Cell.add(thisIterationAmount.get(key));
                    break;
                case "Th17Cell":
                    numOfTh17Cell.add(thisIterationAmount.get(key));
                    break;
            }
        }
        thisIterationAmount.clear();
    }

    // Checks if any cells are missing and add the previous iterations amount if there isn't
    private void checkIfAnyCellMissingInThisIteration() {
        if (!thisIterationAmount.containsKey("DendriticCell")) {
            thisIterationAmount.put("DendriticCell", numOfDendriticCell.get(numOfDendriticCell.size()-1));
        }
        if (!thisIterationAmount.containsKey("FOXP3TregCell")) {
            thisIterationAmount.put("FOXP3TregCell", numOfFOXP3TregCell.get(numOfFOXP3TregCell.size()-1));
        }
        if (!thisIterationAmount.containsKey("ILC3")) {
            thisIterationAmount.put("ILC3", numOfILC3.get(numOfILC3.size()-1));
        }
        if (!thisIterationAmount.containsKey("IntestinalEpithelialCell")) {
            thisIterationAmount.put("IntestinalEpithelialCell", numOfIEC.get(numOfIEC.size()-1));
        }
        if (!thisIterationAmount.containsKey("IntraepithelialCell")) {
            thisIterationAmount.put("IntraepithelialCell", numOfIEL.get(numOfIEL.size()-1));
        }
        if (!thisIterationAmount.containsKey("Macrophage")) {
            thisIterationAmount.put("Macrophage", numOfMacrophage.get(numOfMacrophage.size()-1));
        }
        if (!thisIterationAmount.containsKey("NaiveTCell")) {
            thisIterationAmount.put("NaiveTCell", numOfNaiveTCell.get(numOfNaiveTCell.size()-1));
        }
        if (!thisIterationAmount.containsKey("Th1Cell")) {
            thisIterationAmount.put("Th1Cell", numOfTh1Cell.get(numOfTh1Cell.size()-1));
        }
        if (!thisIterationAmount.containsKey("Th17Cell")) {
            thisIterationAmount.put("Th17Cell", numOfTh17Cell.get(numOfTh17Cell.size()-1));
        }
    }

    private int getNumOfInflammatoryIEC() {
        int currNum = 0;
        Collection<Organism> epith = CrohnsSimulation.epithelium.getAllCells();
        Collection<Organism> ln = CrohnsSimulation.lymphNode.getAllCells();
        Collection<Organism> lp = CrohnsSimulation.laminaPropria.getAllCells();

        for (Organism o : epith) {
            if (o instanceof IntestinalEpithelialCell && ((IntestinalEpithelialCell) o).isInflammatory()) {
                currNum += 1;
            }
        }
        for (Organism o : ln) {
            if (o instanceof IntestinalEpithelialCell && ((IntestinalEpithelialCell) o).isInflammatory()) {
                currNum += 1;
            }
        }
        for (Organism o : lp) {
            if (o instanceof IntestinalEpithelialCell && ((IntestinalEpithelialCell) o).isInflammatory()) {
                currNum += 1;
            }
        }
        return currNum;
    }

    private int getNumOfApoptoticIEC() {
        int currNum = 0;
        Collection<Organism> epith = CrohnsSimulation.epithelium.getAllCells();
        Collection<Organism> ln = CrohnsSimulation.lymphNode.getAllCells();
        Collection<Organism> lp = CrohnsSimulation.laminaPropria.getAllCells();

        for (Organism o : epith) {
            if (o instanceof IntestinalEpithelialCell && ((IntestinalEpithelialCell) o).isApoptotic()) {
                currNum += 1;
            }
        }
        for (Organism o : ln) {
            if (o instanceof IntestinalEpithelialCell && ((IntestinalEpithelialCell) o).isApoptotic()) {
                currNum += 1;
            }
        }
        for (Organism o : lp) {
            if (o instanceof IntestinalEpithelialCell && ((IntestinalEpithelialCell) o).isApoptotic()) {
                currNum += 1;
            }
        }
        return currNum;
    }

    private int getNumOfHomeostaticIEC() {
        int currNum = 0;
        Collection<Organism> epith = CrohnsSimulation.epithelium.getAllCells();
        Collection<Organism> ln = CrohnsSimulation.lymphNode.getAllCells();
        Collection<Organism> lp = CrohnsSimulation.laminaPropria.getAllCells();

        for (Organism o : epith) {
            if (o instanceof IntestinalEpithelialCell && ((IntestinalEpithelialCell) o).isHomeostatic()) {
                currNum += 1;
            }
        }
        for (Organism o : ln) {
            if (o instanceof IntestinalEpithelialCell && ((IntestinalEpithelialCell) o).isHomeostatic()) {
                currNum += 1;
            }
        }
        for (Organism o : lp) {
            if (o instanceof IntestinalEpithelialCell && ((IntestinalEpithelialCell) o).isHomeostatic()) {
                currNum += 1;
            }
        }
        return currNum;
    }
    
    private int getNumOfM1Macrophage() {
        int currNum = 0;
        Collection<Organism> epith = CrohnsSimulation.epithelium.getAllCells();
        Collection<Organism> ln = CrohnsSimulation.lymphNode.getAllCells();
        Collection<Organism> lp = CrohnsSimulation.laminaPropria.getAllCells();

        for (Organism o : epith) {
            if (o instanceof Macrophage && !((Macrophage) o).isProtective() && !((Macrophage)o).isImmature()) {
                currNum += 1;
            }
        }
        for (Organism o : ln) {
            if (o instanceof Macrophage && !((Macrophage) o).isProtective() && !((Macrophage)o).isImmature()) {
                currNum += 1;
            }
        }
        for (Organism o : lp) {
            if (o instanceof Macrophage && !((Macrophage) o).isProtective() && !((Macrophage)o).isImmature()) {
                currNum += 1;
            }
        }
        return currNum;
    }
    
    private int getNumOfM2Macrophage() {
        int currNum = 0;
        Collection<Organism> epith = CrohnsSimulation.epithelium.getAllCells();
        Collection<Organism> ln = CrohnsSimulation.lymphNode.getAllCells();
        Collection<Organism> lp = CrohnsSimulation.laminaPropria.getAllCells();

        for (Organism o : epith) {
            if (o instanceof Macrophage && ((Macrophage) o).isProtective() && !((Macrophage) o).isImmature()) {
                currNum += 1;
            }
        }
        for (Organism o : ln) {
            if (o instanceof Macrophage && ((Macrophage) o).isProtective() && !((Macrophage) o).isImmature()) {
                currNum += 1;
            }
        }
        for (Organism o : lp) {
            if (o instanceof Macrophage && ((Macrophage) o).isProtective() && !((Macrophage) o).isImmature()) {
                currNum += 1;
            }
        }
        return currNum;
    }

    private int getNumOfNkP44PositiveILC3() {
        int currNum = 0;
        Collection<Organism> epith = CrohnsSimulation.epithelium.getAllCells();
        Collection<Organism> ln = CrohnsSimulation.lymphNode.getAllCells();
        Collection<Organism> lp = CrohnsSimulation.laminaPropria.getAllCells();

        for (Organism o : epith) {
            if (o instanceof ILC3 && ((ILC3) o).isPositive()) {
                currNum += 1;
            }
        }
        for (Organism o : ln) {
            if (o instanceof ILC3 && ((ILC3) o).isPositive()) {
                currNum += 1;
            }
        }
        for (Organism o : lp) {
            if (o instanceof ILC3 && ((ILC3) o).isPositive()) {
                currNum += 1;
            }
        }
        return currNum;
    }

    private int getNumOfNkP44NegativeILC3() {
        int currNum = 0;
        Collection<Organism> epith = CrohnsSimulation.epithelium.getAllCells();
        Collection<Organism> ln = CrohnsSimulation.lymphNode.getAllCells();
        Collection<Organism> lp = CrohnsSimulation.laminaPropria.getAllCells();

        for (Organism o : epith) {
            if (o instanceof ILC3 && !((ILC3) o).isPositive()) {
                currNum += 1;
            }
        }
        for (Organism o : ln) {
            if (o instanceof ILC3 && !((ILC3) o).isPositive()) {
                currNum += 1;
            }
        }
        for (Organism o : lp) {
            if (o instanceof ILC3 && !((ILC3) o).isPositive()) {
                currNum += 1;
            }
        }
        return currNum;
    }

    // Populate the arraylists at the start
    private void initiateArrayLists() {
        numOfDendriticCell.add(CrohnsSimulation.numDendriticCell);
        numOfILC3.add(CrohnsSimulation.numILC3);
        numOfIEC.add(CrohnsSimulation.numIntestinalEpithelialCell);
        numOfIEL.add(CrohnsSimulation.numIntraEpithelialCell);
        numOfMacrophage.add(CrohnsSimulation.numMacrophage);
        numOfNaiveTCell.add(CrohnsSimulation.numNaiveTCell);
        numOfFOXP3TregCell.add(CrohnsSimulation.numFOXP3TregCell);
        numOfTh1Cell.add(CrohnsSimulation.numTh1Cell);
        numOfTh17Cell.add(CrohnsSimulation.numTh17Cell);
    }

    @Override
    public void step(SimState simState) {
        numOfCellInEpithelium.add(CrohnsSimulation.epithelium.getAllCells().size());
        numOfCellInLymphNode.add(CrohnsSimulation.lymphNode.getAllCells().size());
        numOfCellInLaminaPropria.add(CrohnsSimulation.laminaPropria.getAllCells().size());
        numOfCellInSimulation.add(CrohnsSimulation.totalSimulationOrganism());
        numofHomeostaticIEC.add(getNumOfHomeostaticIEC());
        numOfInflammatoryIEC.add(getNumOfInflammatoryIEC());
        numofApoptoticIEC.add(getNumOfApoptoticIEC());
        numOfM1Macrophage.add(getNumOfM1Macrophage());
        numOfM2Macrophage.add(getNumOfM2Macrophage());
        numOfNkp44Positive.add(getNumOfNkP44PositiveILC3());
        numOfNkp44Negative.add(getNumOfNkP44NegativeILC3());
        timeStamp.add(simState.schedule.getTime());

        // Check if this is the first step. If it is, just get the original amount and add it there
        if (simState.schedule.getTime() == 0) {
            initiateArrayLists();
            return;
        }

        addAmountToArrayList(CrohnsSimulation.epithelium.getListAndNumberOfCellInCompartment());
        addAmountToArrayList(CrohnsSimulation.lymphNode.getListAndNumberOfCellInCompartment());
        addAmountToArrayList(CrohnsSimulation.laminaPropria.getListAndNumberOfCellInCompartment());
        checkIfAnyCellMissingInThisIteration();
        aggregateThisIterationAmount();
    }

    public static void collateAllData() {
        String extension = ".csv";

        String header = "time,numOfCellInEpithelium,numOfCellInLymphNode,numOfCellInLaminaPropria,numOfCellInSimulation,";
        header += "numOfDendriticCell,numOfILC3,numOfIEC,numOfIEL,numOfMacrophage,numOfNaiveTCell,numOfFOXP3TregCell,numOfTh1Cell,numOfTh17Cell,";
        header += "numOfInflammatoryIEC,numofApoptoticIEC,numOfHomeostaticIEC,numOfM1Macrophage,numOfM2Macrophage,Nkp44+,Nkp44-\n";
        try {
            File file = FileSystemIO.createNewFile(extension);
            FileWriter fw = new FileWriter(file);
            fw.write(header);

            for (int i = 0; i < numOfCellInEpithelium.size(); i++) {
                String time = Double.toString(timeStamp.get(i));
                String epitheliumCells = Integer.toString(numOfCellInEpithelium.get(i));
                String lymphNodeCells = Integer.toString(numOfCellInLymphNode.get(i));
                String laminaPropriaCells = Integer.toString(numOfCellInLaminaPropria.get(i));
                String simulationCells = Integer.toString(numOfCellInSimulation.get(i));

                String dc = Integer.toString(numOfDendriticCell.get(i));
                String ilc3 = Integer.toString(numOfILC3.get(i));
                String iec = Integer.toString(numOfIEC.get(i));
                String iel = Integer.toString(numOfIEL.get(i));
                String macrophage = Integer.toString(numOfMacrophage.get(i));
                String naiveTcell = Integer.toString(numOfNaiveTCell.get(i));
                String foxp3 = Integer.toString(numOfFOXP3TregCell.get(i));
                String th1 = Integer.toString(numOfTh1Cell.get(i));
                String th17 = Integer.toString(numOfTh17Cell.get(i));
                String inflameIEC = Integer.toString(numOfInflammatoryIEC.get(i));
                String apoptoticIEC = Integer.toString(numofApoptoticIEC.get(i));
                String homeostaticIEC = Integer.toString(numofHomeostaticIEC.get(i));
                String m1Macrophage = Integer.toString(numOfM1Macrophage.get(i));
                String m2Macrophage = Integer.toString(numOfM2Macrophage.get(i));
                String nkp44Pos = Integer.toString(numOfNkp44Positive.get(i));
                String nkp44Neg = Integer.toString(numOfNkp44Negative.get(i));


                fw.write(time+","+epitheliumCells+","+lymphNodeCells+","+laminaPropriaCells+","+simulationCells+","
                        +dc+","+ilc3+","+iec+","+iel+","+macrophage+","+naiveTcell+","+foxp3+","+th1+","+th17+","+inflameIEC+","
                        +apoptoticIEC+","+homeostaticIEC+","+m1Macrophage+","+m2Macrophage+","+nkp44Pos+","+nkp44Neg+"\n");
            }

            fw.close();
            System.out.println("Finished Writing to File: " + file.getName());
        } catch (IOException ioe) {
            System.err.println("An error occurred when writing to file");
            ioe.printStackTrace();
        }
    }

}
