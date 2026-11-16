package cc.androbohij.jelectro.fx.controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
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
import cc.androbohij.jelectro.objects.LineCharge;
import cc.androbohij.jelectro.objects.Dipole;
import cc.androbohij.jelectro.objects.PlaneCharge;
import cc.androbohij.jelectro.objects.PointCharge;

public class Sidebar implements ListObserver {

    private View app;
    private CreatorInset ci;
    // private AdditionalCreatorInset aci;

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
    private CheckBox cbShowB;

    @FXML
    private CheckBox cbShowE;

    @FXML
    private CheckBox cbShowField;

    @FXML
    private CheckBox cbShowGrid;

    @FXML
    private CheckBox cbShowStreamlines;

    @FXML
    private Button clearSim;

    @FXML
    private ComboBox<String> combObjectCreateType;

    @FXML
    private ComboBox<ChargedObject> combObjectSelect;

    @FXML
    private Spinner<Integer> spSmlPasses;

    @FXML
    private Spinner<Integer> spSmlRes;

    @FXML
    private Spinner<Double> spSmlDist;

    @FXML
    private void initialize() {
        setupSimulationPane();
        EField.getInstance().subscribe(this);
        populateComboBox();
        combObjectCreateType.getItems()
            .addAll(
                "Select", "Point Charge", "Dipole", 
                "Line of Charge", "Plane of Charge"
        );
    }

    public void setApp(View app) {
        this.app = app;
        app.refresh();
    }

    private void setupSimulationPane() {
        cbDrawArrowheads.setSelected(drawArrowHeads);
        cbDrawVelocities.setSelected(showVelocities);
        cbShowB.setSelected(drawB);
        cbShowE.setSelected(drawE);
        cbShowField.setSelected(showField);
        cbShowGrid.setSelected(showGrid);
        cbShowStreamlines.setSelected(showStreamlines);
        cb3dField.setSelected(!field2DOnly);

        spSmlPasses.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10));
        spSmlRes.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20));
        spSmlDist.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 5));

        spSmlPasses.getValueFactory().setValue(streamlinePasses);
        spSmlRes.getValueFactory().setValue(streamlineResolution);
        spSmlDist.getValueFactory().setValue(streamlineStartDistance);
    }

    @FXML
    private void clearSim(ActionEvent event) {
        EField.getInstance().clearObjects();
    }


    @FXML
    private void commitSettings(ActionEvent event) {
        drawArrowHeads = cbDrawArrowheads.isSelected();
        drawE = cbShowE.isSelected();
        drawB = cbShowB.isSelected();
        showField = cbShowField.isSelected();
        showGrid = cbShowGrid.isSelected();
        showStreamlines = cbShowStreamlines.isSelected();
        field2DOnly = !cb3dField.isSelected();
        showVelocities = cbDrawVelocities.isSelected();

        streamlinePasses = spSmlPasses.getValue();
        streamlineResolution = spSmlRes.getValue();
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
        switch (combObjectCreateType.getValue().split(" ")[0]) {
            case "Point":
                break;
            case "Dipole":

                break;
            case "Line":

                break;
            case "Plane":

                break;
            default:
                apCreateInset1.getChildren().clear();
                // apCreateInset2.getChildren().clear();
                return;
        }
        // TODO: load a second inset with object specifics like s or A or lambda
        if (apCreateInset1.getChildren().isEmpty()) {
            FXMLLoader inset = new FXMLLoader(
                getClass().getResource("/fxml/objEditInset.fxml"));
            inset.setController(new CreatorInset());
            AnchorPane anchorPane = inset.load();
            ci = inset.getController();
            apCreateInset1.getChildren().add(anchorPane);    
        }
    }

    @FXML
    void createObject(ActionEvent event) {
        switch (combObjectCreateType.getValue().split(" ")[0]) {
            case "Point":
                PointCharge pc = new PointCharge(
                    new Point3D(ci.getXpos(), ci.getYpos(), ci.getZpos()), ci.getCharge()/1e9);
                pc.setVelocity(new Point3D(ci.getVx(), ci.getVy(), ci.getVz()));
                EField.getInstance().createObject(pc);
                break;
            case "Dipole":
                Dipole dp = new Dipole(
                    new Point3D(ci.getXpos(), ci.getYpos(), ci.getZpos()), ci.getCharge()/1e9,
                    ci.getXrot(), ci.getYrot(), ci.getZrot(), 0.01);
                dp.setVelocity(new Point3D(ci.getVx(), ci.getVy(), ci.getVz()));
                EField.getInstance().createObject(dp);
                break;
            case "Line":
                LineCharge ln = new LineCharge(
                    new Point3D(ci.getXpos(), ci.getYpos(), ci.getZpos()), ci.getCharge()/1e9,
                    ci.getXrot(), ci.getYrot(), ci.getZrot(), 1);
                ln.setVelocity(new Point3D(ci.getVx(), ci.getVy(), ci.getVz()));
                EField.getInstance().createObject(ln);
                break;
            case "Plane":
                PlaneCharge pl = new PlaneCharge(
                    new Point3D(ci.getXpos(), ci.getYpos(), ci.getZpos()), ci.getCharge()/1e9,
                    ci.getXrot(), ci.getYrot(), ci.getZrot(), 1);
                pl.setVelocity(new Point3D(ci.getVx(), ci.getVy(), ci.getVz()));
                EField.getInstance().createObject(pl);
                break;
            default:
                return;
        }
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
