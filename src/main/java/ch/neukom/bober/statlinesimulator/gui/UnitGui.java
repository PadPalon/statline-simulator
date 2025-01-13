package ch.neukom.bober.statlinesimulator.gui;

import ch.neukom.bober.statlinesimulator.data.Army;
import ch.neukom.bober.statlinesimulator.data.Enhancement;
import ch.neukom.bober.statlinesimulator.data.Unit;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.neukom.bober.statlinesimulator.statistics.StatisticsCalculator.Statistics;
import static ch.neukom.bober.statlinesimulator.statistics.StatisticsCalculator.calculate;

public class UnitGui {
    private final Unit unit;
    private final Set<Enhancement> armyEnhancements;
    private final Statistics statistics;

    public UnitGui(Army army, Unit unit) {
        this.unit = unit;
        this.armyEnhancements = army.getArmyData(Army.ArmyData::enhancements).orElse(Set.of());
        this.statistics = calculate(army, unit);
    }

    public void show() {
        VBox content = new VBox();
        content.setSpacing(GuiHelper.getDefaultSpacing());

        GridPane statsContent = new GridPane(25, GuiHelper.getDefaultSpacing());
        AtomicInteger rowIndex = new AtomicInteger(0);
        buildContentLine(statsContent, rowIndex, "Unit Count", unit.count());
        buildContentLine(statsContent, rowIndex, "Accuracy", unit.getAccuracy(armyEnhancements));
        buildContentLine(statsContent, rowIndex, "Skill", unit.getSkill(armyEnhancements));
        buildContentLine(statsContent, rowIndex, "Shots Per Unit", unit.getShots(armyEnhancements));
        buildContentLine(statsContent, rowIndex, "Total Shots", statistics.attackCount());
        buildContentLinePercentage(statsContent, rowIndex, "Hit Chance Per Unit", statistics.hitChancePerUnit());
        buildContentLine(statsContent, rowIndex, "Average Hits Per Attack", statistics.averageHitsPerAttack());
        content.getChildren().add(statsContent);

        VBox enhancementsContent = new VBox();
        unit.enhancements()
            .stream()
            .map(enhancement -> new Label(enhancement.getName())).forEach(enhancementsContent.getChildren()::add);
        TitledPane enhancementsTitledPane = new TitledPane("Enhancements", enhancementsContent);
        enhancementsTitledPane.setExpanded(false);
        content.getChildren().add(enhancementsTitledPane);

        TitledPane root = new TitledPane(unit.unitName(), content);
        root.setCollapsible(false);
        root.getStyleClass().add("unit-modal");

        Stage dialog = new Stage();
        GuiHelper.configureStage(
            dialog,
            root,
            unit.unitName(),
            actionEvent -> dialog.close()
        );
        dialog.initOwner(GuiContext.getCurrent().primaryStage());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
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
