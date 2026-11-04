package cc.androbohij.jelectro.fx;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import static cc.androbohij.jelectro.Settings.*;

import java.util.List;

import cc.androbohij.jelectro.objects.ChargedObject;

public class Sidebar {

    private View app;

    @FXML
    private Button applySettings;

    @FXML
    private CheckBox cb3dField;

    @FXML
    private CheckBox cbDrawArrowheads;

    @FXML
    private CheckBox cbShowField;

    @FXML
    private CheckBox cbShowGrid;

    @FXML
    private CheckBox cbShowStreamlines;

    @FXML
    private Spinner<Integer> spSmlPasses;

    @FXML
    private Spinner<Integer> spSmlPerObject;

    @FXML
    private Spinner<Double> spSmlDist;

    @FXML 
    public void initialize() {
        cbDrawArrowheads.setSelected(drawArrowHeads);
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
    public void commitSettings(ActionEvent event) {
        drawArrowHeads = cbDrawArrowheads.isSelected();
        showField = cbShowField.isSelected();
        showGrid = cbShowGrid.isSelected();
        showStreamlines = cbShowStreamlines.isSelected();
        field2DOnly = !cb3dField.isSelected();

        streamlinePasses = spSmlPasses.getValue();
        streamlinesPerObject = spSmlPerObject.getValue();
        streamlineStartDistance = spSmlDist.getValue();

        app.refresh();
    }

    public void setApp(View app) {
        this.app = app;
    }

    public void populateObjectList(List<ChargedObject> objects) {

    }

}
