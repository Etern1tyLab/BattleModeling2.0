package dev.racoonlab;

import com.google.gdata.util.ServiceException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static String getTokenScreenID = "Welcome";
    public static String getTokenScreenFile = "FxmlData/getToken.fxml";
    public static String getMainScreenID = "Main Screen";
    public static String getMainScreenFile = "FxmlData/mainScreen.fxml";
   // public static String screen3ID = "screen3";
   // public static String screen3File = "Screen3.fxml";


    @Override
    public void start(Stage primaryStage) throws Exception{

        ScreensController mainContainer = new ScreensController();
        mainContainer.loadScreen(Main.getTokenScreenID, Main.getTokenScreenFile);
        mainContainer.loadScreen(Main.getMainScreenID, Main.getMainScreenFile);
      //  mainContainer.loadScreen(Main.screen3ID, Main.screen3File);

        mainContainer.setScreen(Main.getTokenScreenID);

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

    }


    public static void main(String[] args) throws IOException, ServiceException {
        launch(args);
    }
}
