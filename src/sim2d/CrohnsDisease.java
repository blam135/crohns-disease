package sim2d;

import sim2d.filesystem.FileSystemIO;

import java.io.File;
import java.io.FilenameFilter;

/*
* Run the simulation without the GUI
 */
public class CrohnsDisease {
    private static String rootDir = "resources/";

    public static void main(String[] args) {
        runAllXMLFiles(getAllXMLFileInRoot(rootDir), 400);
    }

    private static File[] getAllXMLFileInRoot(String root) {
        File dir = new File(root);
        File [] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });
        return files;
    }

    private static void runAllXMLFiles(File[] files, int endTime) {
        for (File xmlfile : files) {
            String name = rootDir + xmlfile.getName();
            CrohnsSimulation sim = new CrohnsSimulation(0, FileSystemIO.openXMLFile(name),endTime);
            sim.start();
            try {
                System.out.println("Running " + name);
                sim.run(endTime);
            } catch (Exception e) {
                System.out.println("Failed");
            }
        }
    }

}
