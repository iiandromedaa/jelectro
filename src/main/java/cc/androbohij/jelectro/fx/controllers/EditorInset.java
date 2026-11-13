package cc.androbohij.jelectro.fx.controllers;

import cc.androbohij.jelectro.fx.View;
import cc.androbohij.jelectro.objects.ChargedObject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class EditorInset {

    private View app;

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

    //TODO have rotation be a separate loaded pane because PCs wont have rotation soon
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
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-180, 180));
        yrot.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-180, 180));
        zrot.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(-180, 180));
    }

    public void loadObject(ChargedObject chargedObject, View app) {
        this.app = app;

        charge.getValueFactory().setValue(chargedObject.getCharge()/1e-9);

        vx.getValueFactory().setValue(chargedObject.getVelocity().getX());
        vy.getValueFactory().setValue(chargedObject.getVelocity().getY());
        vz.getValueFactory().setValue(chargedObject.getVelocity().getZ());

        xpos.getValueFactory().setValue(chargedObject.getPositionX());
        ypos.getValueFactory().setValue(chargedObject.getPositionY());
        zpos.getValueFactory().setValue(chargedObject.getPositionZ());

        xrot.getValueFactory().setValue(chargedObject.getRx());
        yrot.getValueFactory().setValue(chargedObject.getRy());
        zrot.getValueFactory().setValue(chargedObject.getRz());

        ChangeListener<Double> vListener = (obs, oldValue, newValue) -> {
            vChanged(obs, 0, 0, chargedObject);
        };

        ChangeListener<Double> posListener = (obs, oldValue, newValue) -> {
            posChanged(obs, oldValue, newValue, chargedObject);
        };

        ChangeListener<Double> rotListener = (obs, oldValue, newValue) -> {
            rotChanged(obs, 0, 0, chargedObject);
        };

        vx.valueProperty().addListener(vListener);
        vy.valueProperty().addListener(vListener);
        vz.valueProperty().addListener(vListener);

        xpos.valueProperty().addListener(posListener);
        ypos.valueProperty().addListener(posListener);
        zpos.valueProperty().addListener(posListener);

        xrot.valueProperty().addListener(rotListener);
        yrot.valueProperty().addListener(rotListener);
        zrot.valueProperty().addListener(rotListener);

        charge.valueProperty().addListener((obs, newValue, oldValue) -> {
            chargedObject.setCharge(charge.getValue()*1e-9);
            app.refresh();
        });
    }

    private void vChanged(ObservableValue<? extends Double> obs, 
        double oldValue, double newValue, ChargedObject chargedObject) {
        chargedObject.setVelocity(new Point3D(vx.getValue(), vy.getValue(), vz.getValue()));
        app.refresh();
    }

    private void posChanged(ObservableValue<? extends Double> obs, 
        double oldValue, double newValue, ChargedObject chargedObject) {
        chargedObject.setPosition(new Point3D(xpos.getValue(), ypos.getValue(), zpos.getValue()));
        app.refresh();
    }

    private void rotChanged(ObservableValue<? extends Double> obs, 
        double oldValue, double newValue, ChargedObject chargedObject) {
        chargedObject.rotate(xrot.getValue(), yrot.getValue(), zrot.getValue());
        app.refresh();
    }

}
