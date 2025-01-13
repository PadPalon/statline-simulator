package ch.neukom.bober.statlinesimulator.gui;

import javafx.stage.Stage;

public class GuiContext {
    private static GuiContext current = null;

    private final Data data;

    private GuiContext(Data data) {
        this.data = data;
    }

    public static void setCurrent(Stage primaryStage) {
        current = new GuiContext(new Data(primaryStage));
    }

    public static Data getCurrent() {
        if (current == null) {
            throw new IllegalStateException("GuiContext not available");
        }
        return current.data;
    }

    public record Data(Stage primaryStage) {
    }
}
