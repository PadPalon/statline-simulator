package ch.neukom.bober.statlinesimulator.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

public class GuiHelper {
    private GuiHelper() {
    }

    public static void configureStage(Stage stage,
                                      Parent parent,
                                      String name,
                                      EventHandler<ActionEvent> closeHandler,
                                      Node... additionalMenuBarItems) {
        HBox menuBar = GuiHelper.createMenuBar(stage, closeHandler, additionalMenuBarItems);
        VBox root = new VBox(menuBar, parent);
        root.setPadding(new Insets(0, 5, 0, 5));
        Scene scene = new Scene(root, 600, 600);
        scene.getStylesheets().add(GuiHelper.class.getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle(name);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setOnCloseRequest(event -> System.exit(0));
    }

    static HBox createMenuBar(Stage stage, EventHandler<ActionEvent> closeHandler, Node... additionalMenuBarItems) {
        FontIcon icon = new FontIcon("mdal-close");
        icon.getStyleClass().add("font-icon-button");
        Button closeMenuItem = new Button("", icon);
        closeMenuItem.setPadding(new Insets(5));
        closeMenuItem.setOnAction(closeHandler);

        HBox menuBar = new HBox();
        menuBar.getStyleClass().add("toolbar");
        menuBar.getChildren().addAll(additionalMenuBarItems);
        menuBar.getChildren().add(closeMenuItem);
        menuBar.setOnMousePressed(pressEvent -> menuBar.setOnMouseDragged(dragEvent -> {
            stage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
            stage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
        }));
        return menuBar;
    }

    public static int getDefaultSpacing() {
        return 5;
    }
}
