package ch.neukom.bober.statlinesimulator;

import ch.neukom.bober.statlinesimulator.loader.DataLoader;

public class StatLineSimulator {
    public static void main(String[] args) {
        DataLoader dataLoader = new DataLoader();
        dataLoader.loadOnce();
        dataLoader.printData();
    }
}
