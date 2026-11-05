package cc.androbohij.jelectro.fx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class CreatorInset {
    
    @FXML
    private Spinner<Double> charge;

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
        charge.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-100, 100, 0, 0.1));

        vx.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-10, 10, 0, 0.25));
        vy.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-10, 10, 0, 0.25));
        vz.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-10, 10, 0, 0.25));

        xpos.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-5, 5, 0, 0.25));
        ypos.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-5, 5, 0, 0.25));
        zpos.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-5, 5, 0, 0.25));
        
        xrot.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-180, 180, 0));
        yrot.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-180, 180, 0));
        zrot.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-180, 180, 0));
    }

    public Double getVx() {
        return vx.getValue();
    }
    public Double getVy() {
        return vy.getValue();
    }
    public Double getVz() {
        return vz.getValue();
    }

    public Double getXpos() {
        return xpos.getValue();
    }
    public Double getYpos() {
        return ypos.getValue();
    }
    public Double getZpos() {
        return zpos.getValue();
    }

    public Double getXrot() {
        return xrot.getValue();
    }
    public Double getYrot() {
        return yrot.getValue();
    }
    public Double getZrot() {
        return zrot.getValue();
    }

    public Double getCharge() {
        return charge.getValue();
    }
    
}
