package ch.neukom.bober.statlinesimulator;

import ch.neukom.bober.statlinesimulator.gui.MainGui;
import ch.neukom.bober.statlinesimulator.loader.ArmyWatcher;
import ch.neukom.bober.statlinesimulator.loader.DataLoader;

public class StatLineSimulator {
    public static void main(String[] args) {
        DataLoader dataLoader = new DataLoader();
        ArmyWatcher armyWatcher = dataLoader.watchForChanges();
        new MainGui(armyWatcher).run();
    }
}
