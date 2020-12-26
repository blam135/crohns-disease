package sim2d.organism.interfaces;

import sim.engine.Steppable;
import sim2d.CrohnsSimulation;
import sim2d.compartment.Compartment;

public interface Organism extends Steppable {
    void migrateIntoCompartment(Compartment newCompartment);
    void removeOrganismFromSimulation(CrohnsSimulation sim);
    boolean isDead();
}
