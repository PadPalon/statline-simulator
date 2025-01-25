package ch.neukom.bober.statlinesimulator.data;

import java.util.List;

public class EnhancementFactory {
    private EnhancementFactory() {}

    private static final Enhancement NO_OP = () -> "Nothing";
    private static final Enhancement SCOPE = new Enhancement() {
        @Override
        public String getName() {
            return "Scope";
        }

        @Override
        public Integer adjustAccuracy(Integer accuracy) {
            return accuracy + 2;
        }
    };
    private static final Enhancement FULL_AUTO = new Enhancement() {
        @Override
        public String getName() {
            return "Full Auto";
        }

        @Override
        public Integer adjustShots(Integer shots) {
            return shots * 5;
        }
    };
    private static final Enhancement UNTRAINED = new Enhancement() {
        @Override
        public String getName() {
            return "Untrained";
        }

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

    public static List<Enhancement> getUnitEnhancements() {
        return List.of(
            SCOPE,
            FULL_AUTO,
            UNTRAINED
        );
    }
}
