package ch.neukom.bober.statlinesimulator.data;

public class EnhancementFactory {
    private EnhancementFactory() {}

    private static final Enhancement NO_OP = new Enhancement() {};
    private static final Enhancement SCOPE = new Enhancement() {
        @Override
        public Integer adjustAccuracy(Integer accuracy) {
            return accuracy + 2;
        }
    };
    private static final Enhancement FULL_AUTO = new Enhancement() {
        @Override
        public Integer adjustShots(Integer shots) {
            return shots * 5;
        }
    };
    private static final Enhancement UNTRAINED = new Enhancement() {
        @Override
        public Integer adjustSkill(Integer skill) {
            return skill - 1;
        }
    };

    public static Enhancement fromString(String enhancementString) {
        return switch (enhancementString) {
            case "SCOPE" -> SCOPE;
            case "FULL_AUTO" -> FULL_AUTO;
            case "UNTRAINED" -> UNTRAINED;
            default -> NO_OP;
        };
    }
}
