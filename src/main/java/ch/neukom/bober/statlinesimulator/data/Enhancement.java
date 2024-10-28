package ch.neukom.bober.statlinesimulator.data;

public interface Enhancement {
    default Integer adjustSkill(Integer skill) {
        return skill;
    }

    default Integer adjustAccuracy(Integer accuracy) {
        return accuracy;
    }

    default Integer adjustShots(Integer shots) {
        return shots;
    }
}
