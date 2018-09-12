package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {

    @Override
    public void start(Stage mainStage) {

        GUI gui = new GUI();
        final Scene mainScene = new Scene(gui.initializeChartArea(), 800, 600);

        mainScene.getStylesheets().add("sample/chart.css");
        mainStage.setTitle("ChartBuilder");
        mainStage.setScene(mainScene);
        mainStage.setOnCloseRequest(e -> Platform.exit());
        mainStage.show();

        Stage settingsStage = new Stage();
        final Scene settingsScene = new Scene(gui.initializeSettingsArea(), 400, 350);

        settingsStage.initStyle(StageStyle.UNIFIED);
        settingsStage.setResizable(false);
        settingsStage.setTitle("ChartBuilder Settings");
        settingsStage.setScene(settingsScene);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        settingsStage.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 400);
        settingsStage.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - 650);
        settingsStage.setWidth(400);
        settingsStage.setHeight(350);

        settingsStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}