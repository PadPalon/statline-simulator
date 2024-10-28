package ch.neukom.bober.statlinesimulator.data;

import ch.neukom.bober.statlinesimulator.properties.Properties;

public record Unit(String unitName, int count) implements Printable {
    @Override
    public String toString() {
        return unitName;
    }

    @Override
    public String print() {
        return unitName;
    }

    public int getSkill() {
        return Properties.get().baseSkill();
    }

    public int getAccuracy() {
        return Properties.get().baseAccuracy();
    }

    public int getShots() {
        return Properties.get().baseShots();
    }
}
