import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
public class SavingsCalculator extends Application {

    @Override
    public void start(Stage stage) {
        Parameters p = getParameters();
        stage.setHeight(Double.parseDouble(p.getNamed().get("height")));
        stage.setWidth(Double.parseDouble(p.getNamed().get("width")));

        BorderPane appLayout = new BorderPane();
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        // set the titles for the axes
        xAxis.setLabel("Years");
        yAxis.setLabel("Savings");

        // create the data set that is going to be used for saving data.
        XYChart.Series<Number, Number> savData = new XYChart.Series<>();
        savData.setName("Savings over 30 years");
        // create the data set that is going to be used for interest.
        XYChart.Series<Number, Number> intData = new XYChart.Series<>();
        intData.setName("Savings and Interest over 30 years");

        LineChart<Number, Number> lc = new LineChart<>(xAxis, yAxis);
        lc.getData().add(savData); // Only need to add the series one time
        lc.getData().add(intData); // Only need to add the series one time
        lc.setTitle("Savings Calculator");
        appLayout.setCenter(lc);

        Label monthSavLbl = new Label("Monthly saving");
        Label sliderDescLbl = new Label("25");
        Slider monthSavSlider = new Slider(25, 250, 25);
        // enable the marks
        monthSavSlider.setShowTickMarks(true);
        // enable the Labels
        monthSavSlider.setShowTickLabels(true);
        // set Major tick unit
        monthSavSlider.setMajorTickUnit(25);
        monthSavSlider.setMinorTickCount(1);
        // sets the value of the property blockIncrement
        monthSavSlider.setBlockIncrement(12.5);
        monthSavSlider.setSnapToTicks(true);
        monthSavSlider.valueProperty().addListener(event -> sliderDescLbl.setText(
                String.format("%1$.3f", monthSavSlider.getValue()))
        );
        monthSavSlider.valueChangingProperty().addListener((observableValue, wasChanging, changing) -> {
            if (!changing) { // Sliding stopped
                // Clear out the previous savData
                savData.getData().clear();
                // add single points into the data set for xAxis values 0 - 30
                IntStream stream = IntStream.range(0, 31);
                stream.forEach((value) -> savData.getData().add(
                        new XYChart.Data<>((double)value, value*12*monthSavSlider.getValue())));
            }
        });
        BorderPane topBp = new BorderPane();
        topBp.setLeft(monthSavLbl);
        topBp.setCenter(monthSavSlider);
        topBp.setRight(sliderDescLbl);

        Label yearIntLbl = new Label("Yearly interest");
        Label yearIntDescLbl = new Label("5");
        Slider yearIntSlider = new Slider(0, 10, 0);
        // enable the marks
        yearIntSlider.setShowTickMarks(true);
        // enable the Labels
        yearIntSlider.setShowTickLabels(true);
        // set Major tick unit
        yearIntSlider.setMajorTickUnit(1);
        yearIntSlider.setMinorTickCount(4); // Quarter percents.
        // sets the value of the property blockIncrement
        yearIntSlider.setBlockIncrement(.25);
        yearIntSlider.setSnapToTicks(true);
        yearIntSlider.valueProperty().addListener(event -> yearIntDescLbl.setText(
                String.format("%1$.3f", yearIntSlider.getValue()))
        );
        yearIntSlider.valueChangingProperty().addListener((observableValue, wasChanging, changing) -> {
            if (!changing) { // Sliding stopped
                // Clear out the previous intData
                intData.getData().clear();
                // add single points into the data set for xAxis values 0 - 30
                IntStream stream = IntStream.range(0, 31);

                AtomicReference<Double> savings = new AtomicReference<>(0.0);
                double annualRate = yearIntSlider.getValue()/100;
                double monthlyContribution = 12 * Double.parseDouble(sliderDescLbl.getText());
                stream.forEach((value) -> {
                    if(value > 0) {
                        savings.updateAndGet(v -> (v + monthlyContribution) + ((v + monthlyContribution) * annualRate));
                    }
                    intData.getData().add(
                            new XYChart.Data<>((double)value, savings.get()));
                });
            }
        });
        BorderPane bottomBp = new BorderPane();
        bottomBp.setLeft(yearIntLbl);
        bottomBp.setCenter(yearIntSlider);
        bottomBp.setRight(yearIntDescLbl);

        VBox header = new VBox();
        header.getChildren().addAll(topBp, bottomBp);

        appLayout.setTop(header);

        stage.setScene(new Scene(appLayout));
        stage.show();
    }
}
