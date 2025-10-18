package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override public void start(Stage stage) {
        var controller = new SimulatorController();
        Scene scene = controller.createScene();
        stage.setTitle("Virtual Memory Simulator");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) { launch(args); }
}
