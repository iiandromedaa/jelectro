package cc.androbohij.jelectro.fx.controllers;

import cc.androbohij.jelectro.objects.ChargedObject;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class EditorInset {

    private ChargedObject selected;

    @FXML
    private Spinner<Double> vx;

    @FXML
    private Spinner<Double> vy;

    @FXML
    private Spinner<Double> vz;

    @FXML
    private Spinner<Double> xpos;

    @FXML
    private Spinner<Double> xrot;

    @FXML
    private Spinner<Double> ypos;

    @FXML
    private Spinner<Double> yrot;

    @FXML
    private Spinner<Double> zpos;

    @FXML
    private Spinner<Double> zrot;

    @FXML 
    private void initialize() {
        vx.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-5, 5));
        vy.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-5, 5));
        vz.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-5, 5));

        xpos.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-5, 5));
        ypos.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-5, 5));
        zpos.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-5, 5));
        
        xrot.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-180, 180));
        yrot.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-180, 180));
        zrot.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-180, 180));

        vx.valueProperty().addListener((obs, oldValue, newValue) -> {
            
        });
        vy.valueProperty().addListener((obs, oldValue, newValue) -> {
            
        });
        vz.valueProperty().addListener((obs, oldValue, newValue) -> {
            
        });

        xpos.valueProperty().addListener((obs, oldValue, newValue) -> {
            
        });
        ypos.valueProperty().addListener((obs, oldValue, newValue) -> {
            
        });
        zpos.valueProperty().addListener((obs, oldValue, newValue) -> {
            
        });

        xrot.valueProperty().addListener((obs, oldValue, newValue) -> {
            
        });
        yrot.valueProperty().addListener((obs, oldValue, newValue) -> {
            
        });
        zrot.valueProperty().addListener((obs, oldValue, newValue) -> {
            
        });
    }

    public void loadObject(ChargedObject chargedObject) {
        vx.getValueFactory().setValue(chargedObject.getVelocity().getX());
        vy.getValueFactory().setValue(chargedObject.getVelocity().getY());
        vz.getValueFactory().setValue(chargedObject.getVelocity().getZ());

        xpos.getValueFactory().setValue(chargedObject.getPositionX());
        ypos.getValueFactory().setValue(chargedObject.getPositionY());
        zpos.getValueFactory().setValue(chargedObject.getPositionZ());

        xrot.getValueFactory().setValue(chargedObject.getRx());
        yrot.getValueFactory().setValue(chargedObject.getRy());
        zrot.getValueFactory().setValue(chargedObject.getRz());

        selected = chargedObject;
    }

}
