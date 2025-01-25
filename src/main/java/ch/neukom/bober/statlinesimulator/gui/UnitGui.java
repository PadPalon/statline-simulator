package ch.neukom.bober.statlinesimulator.gui;

import ch.neukom.bober.statlinesimulator.data.Army;
import ch.neukom.bober.statlinesimulator.data.Enhancement;
import ch.neukom.bober.statlinesimulator.data.EnhancementFactory;
import ch.neukom.bober.statlinesimulator.data.Unit;
import com.google.common.collect.Lists;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static ch.neukom.bober.statlinesimulator.statistics.StatisticsCalculator.Statistics;
import static ch.neukom.bober.statlinesimulator.statistics.StatisticsCalculator.calculate;

public class UnitGui {
    private final Unit unit;
    private final Set<Enhancement> armyEnhancements;
    private final Consumer<Unit> unitUpdater;

    public UnitGui(Army army, Unit unit, Consumer<Unit> unitUpdater) {
        this.unit = unit;
        this.armyEnhancements = army.getArmyData(Army.ArmyData::enhancements).orElse(Set.of());
        this.unitUpdater = unitUpdater;
    }

    public void show() {
        VBox content = new VBox();
        content.setSpacing(GuiHelper.getDefaultSpacing());

        AtomicReference<Unit> unitToSave = new AtomicReference<>(unit);

        GridPane statsContent = new GridPane(25, GuiHelper.getDefaultSpacing());
        fillStatsContent(statsContent, unit);
        content.getChildren().add(statsContent);

        VBox enhancementsContent = new VBox();
        enhancementsContent.setSpacing(GuiHelper.getDefaultSpacing());

        VBox enhancementsList = new VBox();
        enhancementsContent.getChildren().add(enhancementsList);

        ObservableList<Enhancement> availableEnhancements = new ObservableListWrapper<>(Lists.newArrayList());
        EnhancementFactory.getUnitEnhancements().stream()
            .filter(enhancement -> !unit.enhancements().contains(enhancement))
            .forEach(availableEnhancements::add);

        ObservableList<Enhancement> addedEnhancements = new ObservableListWrapper<>(Lists.newArrayList());
        addedEnhancements.addListener((ListChangeListener<Enhancement>) change -> {
            if (change.next()) {
                ObservableList<Node> children = enhancementsList.getChildren();
                children.clear();
                change.getList().forEach(enhancement -> {
                    HBox enhancementBox = new HBox();
                    Label enhancementLabel = new Label(enhancement.getName());
                    enhancementLabel.prefHeightProperty().bind(enhancementBox.heightProperty());
                    enhancementBox.getChildren().add(enhancementLabel);
                    Button removeEnhancementButton = new Button("Remove enhancement");
                    removeEnhancementButton.setOnMouseClicked(event -> {
                        availableEnhancements.add(enhancement);
                        addedEnhancements.remove(enhancement);
                        unitToSave.getAndUpdate(unitToUpdate -> unitToUpdate.withoutEnhancement(enhancement));
                        statsContent.getChildren().clear();
                        fillStatsContent(statsContent, unitToSave.get());
                    });
                    enhancementBox.getChildren().add(removeEnhancementButton);
                    children.add(enhancementBox);
                });
            }
        });
        addedEnhancements.addAll(unit.enhancements());

        Node addEnhancementButton = buildLinkEnhancementButton(availableEnhancements, addedEnhancement -> {
            addedEnhancements.add(addedEnhancement);
            availableEnhancements.remove(addedEnhancement);
            unitToSave.getAndUpdate(unitToUpdate -> unitToUpdate.withEnhancement(addedEnhancement));
            statsContent.getChildren().clear();
            fillStatsContent(statsContent, unitToSave.get());
        });
        enhancementsContent.getChildren().add(addEnhancementButton);

        TitledPane enhancementsTitledPane = new TitledPane("Enhancements", enhancementsContent);
        enhancementsTitledPane.setExpanded(false);
        content.getChildren().add(enhancementsTitledPane);

        TitledPane root = new TitledPane(unit.unitName(), content);
        root.setCollapsible(false);
        root.getStyleClass().add("unit-modal");

        Stage dialog = new Stage();
        Button saveButton = new Button("Save");
        saveButton.setOnMouseClicked(event -> {
            unitUpdater.accept(unitToSave.get());
            dialog.close();
        });
        GuiHelper.configureStage(
            dialog,
            root,
            unit.unitName(),
            actionEvent -> dialog.close(),
            saveButton
        );
        dialog.initOwner(GuiContext.getCurrent().primaryStage());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }

    private Node buildLinkEnhancementButton(ObservableList<Enhancement> availableEnhancements, Consumer<Enhancement> enhancementUpdater) {
        HBox root = new HBox();

        ComboBox<Enhancement> comboBox = new ComboBox<>(availableEnhancements);
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(@Nullable Enhancement enhancement) {
                return enhancement == null ? "" : enhancement.getName();
            }

            @Override
            public Enhancement fromString(String enhancementName) {
                return EnhancementFactory.getUnitEnhancements().stream()
                    .filter(enhancement -> enhancement.getName().equals(enhancementName))
                    .findAny()
                    .orElse(EnhancementFactory.getUnitEnhancements().getFirst());
            }
        });
        if (!availableEnhancements.isEmpty()) {
            comboBox.setValue(availableEnhancements.getFirst());
        }
        availableEnhancements.addListener((ListChangeListener<Enhancement>) change -> {
            if (change.next() && !change.getList().isEmpty()) {
                comboBox.setValue(change.getList().getFirst());
            }
        });
        root.getChildren().add(comboBox);

        Button addButton = new Button("Add enhancement");
        addButton.setOnMouseClicked(event -> {
            Enhancement enhancementToAdd = comboBox.getValue();
            if (enhancementToAdd != null) {
                enhancementUpdater.accept(enhancementToAdd);
            }
        });
        root.getChildren().add(addButton);

        return root;
    }

    private void fillStatsContent(GridPane parent, Unit unitToDisplay) {
        Statistics statistics = calculate(unitToDisplay, armyEnhancements);
        AtomicInteger rowIndex = new AtomicInteger(0);
        buildContentLine(parent, rowIndex, "Unit Count", unitToDisplay.count());
        buildContentLine(parent, rowIndex, "Accuracy", unitToDisplay.getAccuracy(armyEnhancements));
        buildContentLine(parent, rowIndex, "Skill", unitToDisplay.getSkill(armyEnhancements));
        buildContentLine(parent, rowIndex, "Shots Per Unit", unitToDisplay.getShots(armyEnhancements));
        buildContentLine(parent, rowIndex, "Total Shots", statistics.attackCount());
        buildContentLinePercentage(parent, rowIndex, "Hit Chance Per Unit", statistics.hitChancePerUnit());
        buildContentLine(parent, rowIndex, "Average Hits Per Attack", statistics.averageHitsPerAttack());
    }

    private void buildContentLine(GridPane content, AtomicInteger rowIndex, String label, int value) {
        Label valueNode = new Label(String.valueOf(value));
        buildContentLine(content, rowIndex, label, valueNode);
    }

    private void buildContentLine(GridPane content, AtomicInteger rowIndex, String label, float value) {
        Label valueNode = new Label("%.2f".formatted(value));
        buildContentLine(content, rowIndex, label, valueNode);
    }

    private void buildContentLinePercentage(GridPane content, AtomicInteger rowIndex, String label, float value) {
        Label valueNode = new Label("%.0f%%".formatted(value * 100));
        buildContentLine(content, rowIndex, label, valueNode);
    }

    private static void buildContentLine(GridPane content, AtomicInteger rowIndex, String label, Label valueNode) {
        GridPane.setHalignment(valueNode, HPos.RIGHT);
        Label labelNode = new Label(label);
        labelNode.setLabelFor(valueNode);
        content.addRow(rowIndex.getAndIncrement(), labelNode, valueNode);
    }
}
