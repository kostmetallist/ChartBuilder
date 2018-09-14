package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javafx.util.Pair;

import com.github.javafx.charts.zooming.ZoomManager;


public class GUI {

    private final XYChart.Series<Number, Number> series;
    private ZoomManager<Number, Number> zm;
    //private final ScatterChart<Number,Number> sc;

    GUI() {
        series = new XYChart.Series<>();
    }

    StackPane initializeChartArea() {

        final StackPane stackPane = new StackPane();
        final NumberAxis xAxis = new NumberAxis(-100, 100, 1);
        final NumberAxis yAxis = new NumberAxis(-100, 100, 1);

        xAxis.setAutoRanging(true);
        xAxis.setForceZeroInRange(false);
        yAxis.setAutoRanging(true);
        yAxis.setForceZeroInRange(false);
        xAxis.setLabel("t");
        yAxis.setLabel("x(t)");

        final ScatterChart<Number,Number> sc = new
                ScatterChart<Number,Number>(xAxis, yAxis);
        
        series.setName("Approximate function values");
        
        series.getData().add(new XYChart.Data(0.0, 2.0));
        series.getData().add(new XYChart.Data(1.0, 3.0));
        series.getData().add(new XYChart.Data(2.0, 4.0));
        series.getData().add(new XYChart.Data(3.0, 5.0));
        series.getData().add(new XYChart.Data(4.0, 6.0));
        series.getData().add(new XYChart.Data(5.0, 7.0));
        series.getData().add(new XYChart.Data(6.0, 8.0));
        series.getData().add(new XYChart.Data(7.0, 9.0));
        series.getData().add(new XYChart.Data(8.0, 10.0));
        series.getData().add(new XYChart.Data(9.0, 11.0));

        sc.getData().add(series);

        stackPane.getChildren().add(sc);
        //zm = new ZoomManager(stackPane, sc, series);

        return stackPane;
    }

    StackPane initializeSettingsArea() {

        final StackPane root = new StackPane();
        final TabPane tabPane = new TabPane();
        Logic logic = new Logic();

        //////////////////////////////

        final GridPane rungeGridPane = new GridPane();

        rungeGridPane.setAlignment(Pos.TOP_LEFT);
        rungeGridPane.setHgap(5);
        rungeGridPane.setVgap(10);
        rungeGridPane.setPadding(new Insets(5, 5, 5, 5));

        Label rungeDxdtLabel = new Label("dx/dt =  f(x, y, t) =");
        rungeGridPane.add(rungeDxdtLabel, 0,0);

        TextField rungeDxdtField = new TextField();
        rungeGridPane.add(rungeDxdtField, 1, 0);

        Label rungeDydtLabel = new Label("dy/dt = g(x, y, t) =");
        rungeGridPane.add(rungeDydtLabel, 0,1);

        TextField rungeDydtField = new TextField();
        rungeGridPane.add(rungeDydtField, 1, 1);

        Label rungeInitXLabel = new Label("Initial x0 =");
        rungeGridPane.add(rungeInitXLabel, 0, 2);

        TextField rungeInitXField = new TextField();
        rungeGridPane.add(rungeInitXField, 1, 2);

        Label rungeInitYLabel = new Label("Initial y0 =");
        rungeGridPane.add(rungeInitYLabel, 0, 3);

        TextField rungeInitYField = new TextField();
        rungeGridPane.add(rungeInitYField, 1, 3);

        Label rungeInitTLabel = new Label("Initial t0 =");
        rungeGridPane.add(rungeInitTLabel, 0, 4);

        TextField rungeInitTField = new TextField();
        rungeGridPane.add(rungeInitTField, 1, 4);

        Label rungeDeltaTLabel = new Label("Increment delta t =");
        rungeGridPane.add(rungeDeltaTLabel, 0, 5);

        TextField rungeDeltaTField = new TextField();
        rungeGridPane.add(rungeDeltaTField, 1, 5);

        Label rungeIterLabel = new Label("Number of iterations =");
        rungeGridPane.add(rungeIterLabel, 0, 6);

        TextField rungeIterField = new TextField();
        rungeGridPane.add(rungeIterField, 1, 6);

        Button rungeGoButton = new Button("Build");
        rungeGridPane.add(rungeGoButton, 2, 7);

        rungeGoButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {

//                series.getData().clear();
//
//                InitialData initData = new InitialData(Double.parseDouble(rungeInitXField.getText()),
//                                                        Double.parseDouble(rungeInitYField.getText()),
//                                                        Double.parseDouble(rungeInitTField.getText()),
//                                                        Double.parseDouble(rungeDeltaTField.getText()));
//
//                List<Pair<Double, Double>> data = logic.rungeKutter(rungeDxdtField.getText(),
//                                                                    rungeDydtField.getText(),
//                                                                    initData,
//                                                                    Integer.parseInt(rungeIterField.getText()));
//
//                for (Iterator<Pair<Double, Double>> iter = data.iterator(); iter.hasNext(); ) {
//
//                    Pair<Double, Double> dot = iter.next();
//                    series.getData().add(new XYChart.Data(dot.getKey(), dot.getValue()));
//                }




//                CellularArea cArea = new CellularArea(-2.0, -1.0, 3.0, 3.0, 5, 4, new ArrayList<>());
//
//                System.out.println("(-1.5; -2.5) in " + cArea.getCellNumber(-1.5, -2.5));
//                System.out.println("(-2.1; 1.2) in " + cArea.getCellNumber(-2.1, 1.2));
//                System.out.println("(0.5; 0.5) in " + cArea.getCellNumber(0.5, 0.5));


                series.getData().clear();

                CellularArea cArea = new CellularArea(-2.0, -1.0,
                                                    3.0, 3.0,
                                                    5, 4, new ArrayList<>());

//                cArea.getChildren().get(6).setStatus(CellularArea.CellStatus.DISCARDED);
//                cArea.getChildren().get(12).setStatus(CellularArea.CellStatus.DISCARDED);
//                cArea.getChildren().get(18).setStatus(CellularArea.CellStatus.DISCARDED);

                List<Pair<Double, Double>> data = logic.crBuilder("1+y-1.4*x*x", "0.3*x", cArea, 1);

                for (Iterator<Pair<Double, Double>> iter = data.iterator(); iter.hasNext(); ) {

                    Pair<Double, Double> dot = iter.next();
                    series.getData().add(new XYChart.Data(dot.getKey(), dot.getValue()));
                }
            }
        });

        Tab tabRunge = new Tab();
        tabRunge.setText("Runge-Kutta");
        tabRunge.setContent(rungeGridPane);

        //////////////////////////////

        final GridPane eulerGridPane = new GridPane();

        eulerGridPane.setAlignment(Pos.TOP_LEFT);
        eulerGridPane.setHgap(5);
        eulerGridPane.setVgap(10);
        eulerGridPane.setPadding(new Insets(5, 5, 5, 5));

        Label eulerDxdtLabel = new Label("dx/dt =  f(x, y, t) =");
        eulerGridPane.add(eulerDxdtLabel, 0,0);

        TextField eulerDxdtField = new TextField();
        eulerGridPane.add(eulerDxdtField, 1, 0);

        Label eulerDydtLabel = new Label("dy/dt = g(x, y, t) =");
        eulerGridPane.add(eulerDydtLabel, 0,1);

        TextField eulerDydtField = new TextField();
        eulerGridPane.add(eulerDydtField, 1, 1);

        Label eulerInitXLabel = new Label("Initial x0 =");
        eulerGridPane.add(eulerInitXLabel, 0, 2);

        TextField eulerInitXField = new TextField();
        eulerGridPane.add(eulerInitXField, 1, 2);

        Label eulerInitYLabel = new Label("Initial y0 =");
        eulerGridPane.add(eulerInitYLabel, 0, 3);

        TextField eulerInitYField = new TextField();
        eulerGridPane.add(eulerInitYField, 1, 3);

        Label eulerInitTLabel = new Label("Initial t0 =");
        eulerGridPane.add(eulerInitTLabel, 0, 4);

        TextField eulerInitTField = new TextField();
        eulerGridPane.add(eulerInitTField, 1, 4);

        Label eulerDeltaTLabel = new Label("Increment delta t =");
        eulerGridPane.add(eulerDeltaTLabel, 0, 5);

        TextField eulerDeltaTField = new TextField();
        eulerGridPane.add(eulerDeltaTField, 1, 5);

        Label eulerIterLabel = new Label("Number of iterations =");
        eulerGridPane.add(eulerIterLabel, 0, 6);

        TextField eulerIterField = new TextField();
        eulerGridPane.add(eulerIterField, 1, 6);

        Button eulerGoButton = new Button("Build");
        eulerGridPane.add(eulerGoButton, 2, 7);

        eulerGoButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                series.getData().clear();
                InitialData initData = new InitialData(Double.parseDouble(eulerInitXField.getText()),
                        Double.parseDouble(eulerInitYField.getText()),
                        Double.parseDouble(eulerInitTField.getText()),
                        Double.parseDouble(eulerDeltaTField.getText()));

                List<Pair<Double, Double>> data = logic.eulerPolycurver(eulerDxdtField.getText(),
                                                                        eulerDydtField.getText(),
                                                                        initData,
                                                                        Integer.parseInt(eulerIterField.getText()));

                for (Iterator<Pair<Double, Double>> iter = data.iterator(); iter.hasNext(); ) {

                    Pair<Double, Double> dot = iter.next();
                    series.getData().add(new XYChart.Data(dot.getKey(), dot.getValue()));
                }
            }
        });

        Tab tabEulerCurves = new Tab();
        tabEulerCurves.setText("Euler Poly-curves");
        tabEulerCurves.setContent(eulerGridPane);

        //////////////////////////////

        final GridPane arbitGridPane = new GridPane();

        arbitGridPane.setAlignment(Pos.TOP_LEFT);
        arbitGridPane.setHgap(5);
        arbitGridPane.setVgap(10);
        arbitGridPane.setPadding(new Insets(5, 5, 5, 5));

        Label arbitXLabel = new Label("x is mapped to  f(x, y) =");
        arbitGridPane.add(arbitXLabel, 0,0);

        TextField arbitXField = new TextField();
        arbitGridPane.add(arbitXField, 1, 0);

        Label arbitYLabel = new Label("y is mapped to g(x, y) =");
        arbitGridPane.add(arbitYLabel, 0,1);

        TextField arbitYField = new TextField();
        arbitGridPane.add(arbitYField, 1, 1);

        Label arbitInitXLabel = new Label("Initial x0 =");
        arbitGridPane.add(arbitInitXLabel, 0, 2);

        TextField arbitInitXField = new TextField();
        arbitGridPane.add(arbitInitXField, 1, 2);

        Label arbitInitYLabel = new Label("Initial y0 =");
        arbitGridPane.add(arbitInitYLabel, 0, 3);

        TextField arbitInitYField = new TextField();
        arbitGridPane.add(arbitInitYField, 1, 3);
        
        Label arbitIterLabel = new Label("Number of iterations =");
        arbitGridPane.add(arbitIterLabel, 0, 4);

        TextField arbitIterField = new TextField();
        arbitGridPane.add(arbitIterField, 1, 4);

        Button arbitGoButton = new Button("Build");
        arbitGridPane.add(arbitGoButton, 2, 5);

        arbitGoButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {

                series.getData().clear();
                InitialData initData = new InitialData(Double.parseDouble(arbitInitXField.getText()),
                                                        Double.parseDouble(arbitInitYField.getText()),
                                                        0.0,
                                                        0.0);

                List<Pair<Double, Double>> data = logic.arbitraryMapper(arbitXField.getText(),
                                                                        arbitYField.getText(),
                                                                        initData,
                                                                        Integer.parseInt(arbitIterField.getText()));

                for (Iterator<Pair<Double, Double>> iter = data.iterator(); iter.hasNext(); ) {

                    Pair<Double, Double> dot = iter.next();
                    series.getData().add(new XYChart.Data(dot.getKey(), dot.getValue()));
                }
            }
        });

        Tab tabArbitMapping = new Tab();
        tabArbitMapping.setText("Arbitrary Mapping");
        tabArbitMapping.setContent(arbitGridPane);

        //////////////////////////////

        tabPane.getSelectionModel().select(0);
        tabPane.getTabs().addAll(tabRunge, tabEulerCurves, tabArbitMapping);

        root.getChildren().add(tabPane);

        return root;
    }
}
