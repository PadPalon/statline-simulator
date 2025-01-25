package ch.neukom.bober.statlinesimulator.gui;

import atlantafx.base.theme.PrimerDark;
import ch.neukom.bober.statlinesimulator.data.Army;
import ch.neukom.bober.statlinesimulator.data.Army.ArmyData;
import ch.neukom.bober.statlinesimulator.data.Unit;
import ch.neukom.bober.statlinesimulator.loader.ArmyWatcher;
import ch.neukom.bober.statlinesimulator.serializer.ArmySerializer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;

public class MainGui extends Application {
    private final ArmyWatcher armyWatcher;
    private final ArmySerializer armySerializer;

    public MainGui(ArmyWatcher armyWatcher, ArmySerializer armySerializer) {
        this.armyWatcher = armyWatcher;
        this.armySerializer = armySerializer;
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

        Button saveButton = new Button("Save");
        saveButton.setOnMouseClicked(event -> armySerializer.write(armyWatcher.getArmies().values()));
        GuiHelper.configureStage(
            stage,
            contentRoot,
            "Project Bober Statline Simulator",
            actionEvent -> System.exit(0),
            saveButton
        );
        stage.show();
    }

    private BiConsumer<String, Army> createArmyPanel(Pane root) {
        return (name, army) -> {
            VBox armyContent = new VBox();
            ObservableList<Node> children = armyContent.getChildren();

            List<Unit> units = army.units();
            VBox unitsContent = new VBox();
            units.stream().map(unit -> createUnitPanel(unit, army)).forEach(unitsContent.getChildren()::add);
            unitsContent.getChildren().add(createUnitCreatePanel(army));
            children.add(new TitledPane("Units", unitsContent));

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

            Button deleteButton = new Button("Delete army");
            armyContent.getChildren().add(deleteButton);
            deleteButton.setOnMouseClicked(event -> {
                root.getChildren().remove(armyRoot);
                armyWatcher.getArmies().remove(army.armyId());
            });

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
        HBox unitRoot = new HBox();
        unitRoot.getStyleClass().add("unit-panel");

        Label unitLabel = new Label("%s (%s)".formatted(unit.unitName(), unit.count()));
        unitRoot.getChildren().add(unitLabel);

        HBox buttonRoot = new HBox();
        HBox.setHgrow(buttonRoot, Priority.ALWAYS);
        buttonRoot.setAlignment(Pos.CENTER_RIGHT);

        Button editButton = new Button("Edit unit");
        editButton.setOnMouseClicked(mouseEvent -> new UnitGui(
            army,
            unit,
            updatedUnit -> armyWatcher.addArmy(army.replaceUnit(unit, updatedUnit))
        ).show());
        buttonRoot.getChildren().add(editButton);

        Button deleteButton = new Button("Delete unit");
        deleteButton.setOnMouseClicked(event -> armyWatcher.addArmy(army.withoutUnit(unit)));
        buttonRoot.getChildren().add(deleteButton);

        unitRoot.getChildren().add(buttonRoot);
        return unitRoot;
    }

    private Node createUnitCreatePanel(Army army) {
        VBox content = new VBox();
        content.setSpacing(GuiHelper.getDefaultSpacing());

        TextField nameField = new TextField("Unit Name");
        content.getChildren().add(nameField);

        HBox countBox = new HBox();
        countBox.setPadding(new Insets(0, 0, 0, GuiHelper.getDefaultSpacing()));
        countBox.setSpacing(GuiHelper.getDefaultSpacing());
        content.getChildren().add(countBox);

        Label countLabel = new Label("Unit Count");
        countLabel.prefHeightProperty().bind(countBox.heightProperty());
        countBox.getChildren().add(countLabel);

        TextField countField = new TextField("1");
        countBox.getChildren().add(countField);

        Button createButton = new Button("Create unit");
        createButton.setOnMouseClicked(event -> {
            Unit unit = new Unit(UUID.randomUUID().toString(), nameField.getText(), Integer.parseInt(countField.getText()), Set.of());
            armyWatcher.addArmy(army.withUnit(unit));
        });
        countBox.getChildren().add(createButton);

        return content;
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
