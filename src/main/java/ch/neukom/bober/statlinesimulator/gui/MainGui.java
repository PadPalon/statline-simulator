package ch.neukom.bober.statlinesimulator.gui;

import atlantafx.base.theme.PrimerDark;
import ch.neukom.bober.statlinesimulator.data.Army;
import ch.neukom.bober.statlinesimulator.data.Army.ArmyData;
import ch.neukom.bober.statlinesimulator.data.Unit;
import ch.neukom.bober.statlinesimulator.loader.ArmyWatcher;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

public class MainGui extends Application {
    private final ArmyWatcher armyWatcher;

    public MainGui(ArmyWatcher armyWatcher) {
        this.armyWatcher = armyWatcher;
    }

    @Override
    public void start(Stage stage) {
        GuiContext.setCurrent(stage);

        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        VBox contentRoot = new VBox();
        contentRoot.setSpacing(GuiHelper.getDefaultSpacing());
        armyWatcher.registerOnUpdate(armies -> Platform.runLater(() -> {
            contentRoot.getChildren().clear();
            armies.forEach(createArmyPanel(contentRoot));
            contentRoot.getChildren().add(createArmyCreatePanel());
        }));

        GuiHelper.configureStage(
            stage,
            contentRoot,
            "Project Bober Statline Simulator",
            actionEvent -> System.exit(0)
        );
        stage.show();
    }

    private BiConsumer<String, Army> createArmyPanel(Pane root) {
        return (name, army) -> {
            VBox armyContent = new VBox();
            ObservableList<Node> children = armyContent.getChildren();

            List<Unit> units = army.units();
            if (!units.isEmpty()) {
                VBox unitsContent = new VBox();
                units.stream().map(unit -> createUnitPanel(unit, army)).forEach(unitsContent.getChildren()::add);
                children.add(new TitledPane("Units", unitsContent));
            }

            army.getArmyData(ArmyData::enhancements)
                .filter(enhancements -> !enhancements.isEmpty())
                .ifPresent(enhancements -> {
                    VBox enhancementsContent = new VBox();
                    enhancements
                        .stream()
                        .map(enhancement -> new Label(enhancement.getName()))
                        .forEach(enhancementsContent.getChildren()::add);
                    TitledPane enhancementsTitledPane = new TitledPane("Enhancements", enhancementsContent);
                    enhancementsTitledPane.setExpanded(false);
                    children.add(enhancementsTitledPane);
                });

            TitledPane armyRoot = new TitledPane(army.getArmyName(), armyContent);
            armyRoot.setBorder(Border.EMPTY);
            root.getChildren().add(armyRoot);
        };
    }

    private Node createArmyCreatePanel() {
        HBox content = new HBox();
        TextField nameField = new TextField("Army Name");
        content.getChildren().add(nameField);
        Button createButton = new Button("Create army");
        createButton.setOnMouseClicked(event -> {
            Army newArmy = new Army(
                UUID.randomUUID().toString(),
                List.of(),
                new ArmyData(nameField.getText(), Set.of())
            );
            armyWatcher.addArmy(newArmy);
        });
        content.getChildren().add(createButton);
        return content;
    }

    private Pane createUnitPanel(Unit unit, Army army) {
        VBox unitRoot = new VBox();
        unitRoot.getStyleClass().add("unit-panel");
        unitRoot.setOnMouseClicked(mouseEvent -> new UnitGui(army, unit).show());
        unitRoot.getChildren().add(new Label(unit.unitName()));
        return unitRoot;
    }

    public void run() {
        Platform.startup(() -> {
            try {
                start(new Stage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
