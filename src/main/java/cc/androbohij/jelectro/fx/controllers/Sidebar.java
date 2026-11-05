package cc.androbohij.jelectro.fx.controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;

import static cc.androbohij.jelectro.Settings.*;

import java.io.IOException;

import cc.androbohij.jelectro.EField;
import cc.androbohij.jelectro.ListObserver;
import cc.androbohij.jelectro.fx.View;
import cc.androbohij.jelectro.objects.ChargedObject;

public class Sidebar implements ListObserver {

    private View app;

    @FXML
    private AnchorPane apCreateInset1;

    @FXML
    private AnchorPane apCreateInset2;

    @FXML
    private AnchorPane apEditInset;

    @FXML
    private CheckBox cb3dField;

    @FXML
    private CheckBox cbDrawArrowheads;

    @FXML 
    private CheckBox cbDrawVelocities;

    @FXML
    private CheckBox cbShowField;

    @FXML
    private CheckBox cbShowGrid;

    @FXML
    private CheckBox cbShowStreamlines;

    @FXML
    private Button clearSim;

    @FXML
    private ComboBox<? extends ChargedObject> combObjectCreateType;

    @FXML
    private ComboBox<ChargedObject> combObjectSelect;

    @FXML
    private Spinner<Integer> spSmlPasses;

    @FXML
    private Spinner<Integer> spSmlPerObject;

    @FXML
    private Spinner<Double> spSmlDist;

    @FXML 
    private void initialize() {
        setupSimulationPane();
        EField.getInstance().subscribe(this);
        populateComboBox();
    }

    public void setApp(View app) {
        this.app = app;
    }

    private void setupSimulationPane() {
        cbDrawArrowheads.setSelected(drawArrowHeads);
        cbDrawVelocities.setSelected(showVelocities);
        cbShowField.setSelected(showField);
        cbShowGrid.setSelected(showGrid);
        cbShowStreamlines.setSelected(showStreamlines);
        cb3dField.setSelected(!field2DOnly);

        spSmlPasses.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10));
        spSmlPerObject.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100));
        spSmlDist.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 5));

        spSmlPasses.getValueFactory().setValue(streamlinePasses);
        spSmlPerObject.getValueFactory().setValue(streamlinesPerObject);
        spSmlDist.getValueFactory().setValue(streamlineStartDistance);
    }

    @FXML
    private void clearSim(ActionEvent event) {
        EField.getInstance().clearObjects();
    }


    @FXML
    private void commitSettings(ActionEvent event) {
        drawArrowHeads = cbDrawArrowheads.isSelected();
        showField = cbShowField.isSelected();
        showGrid = cbShowGrid.isSelected();
        showStreamlines = cbShowStreamlines.isSelected();
        field2DOnly = !cb3dField.isSelected();
        showVelocities = cbDrawVelocities.isSelected();

        streamlinePasses = spSmlPasses.getValue();
        streamlinesPerObject = spSmlPerObject.getValue();
        streamlineStartDistance = spSmlDist.getValue();

        app.refresh();
    }

    @FXML
    private void objectSelectUpdate(ActionEvent event) throws IOException {
        app.select(combObjectSelect.getValue());
        apEditInset.getChildren().clear();
        if (combObjectSelect.getValue() == null)
            return;
        FXMLLoader inset = new FXMLLoader(
            getClass().getResource("/fxml/objEditInset.fxml"));
        inset.setController(new EditorInset());
        AnchorPane anchorPane = inset.load();
        ((EditorInset)inset.getController()).loadObject(combObjectSelect.getValue(), app);
        apEditInset.getChildren().add(anchorPane);
    }

    @FXML
    void objectCreateUpdate(ActionEvent event) throws IOException {
        // TODO: load the edit inset with separate controller for linking to object creation
        // TODO: load a second inset with object specifics like s or A or lambda
    }

    @FXML
    void createObject(ActionEvent event) {

    }

    @FXML
    private void deleteObject(ActionEvent event) {
        app.select(null);
        EField.getInstance().removeObject(combObjectSelect.getValue());
    }

    private void populateComboBox() {
        combObjectSelect.getItems().clear();
        combObjectSelect.getItems().add(null);
        combObjectSelect.getItems().addAll(EField.getInstance().getObjects());
    }

    @Override
    public void listAdd(ChargedObject chargedObject) {
        populateComboBox();
    }

    @Override
    public void listRemove(ChargedObject chargedObject) {
        if (combObjectSelect.getValue() == chargedObject) {
            combObjectSelect.setValue(null);
            try {
                objectSelectUpdate(new ActionEvent(combObjectSelect, combObjectSelect));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        populateComboBox();
    }

    @Override
    public void listClear() {
        combObjectSelect.setValue(null);
        populateComboBox();
    }

}
