package dev.racoonlab;

import com.google.gdata.util.ServiceException;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static String getTokenScreenID = "Welcome";
    public static String getTokenScreenFile = "FxmlData/getToken.fxml";
    public static String getMainScreenID = "Main Screen";
    public static String getMainScreenFile = "FxmlData/mainScreen.fxml";
    public static String getModellingScreenID = "Modelling screen";
    public static String getModellingScreenFile = "FxmlData/modellingScreen.fxml";
   // public static String screen3ID = "screen3";
   // public static String screen3File = "Screen3.fxml";


    /**
     * Start method. Loads scenes
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{

        ScreensController mainContainer = new ScreensController();
        mainContainer.loadScreen(Main.getTokenScreenID, Main.getTokenScreenFile);
        mainContainer.loadScreen(Main.getMainScreenID, Main.getMainScreenFile);
        mainContainer.loadScreen(Main.getModellingScreenID, Main.getModellingScreenFile);

        mainContainer.setStage(primaryStage);

        mainContainer.setScreen(Main.getTokenScreenID);

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root);
        primaryStage.setTitle("BattleModelling alpha");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * Main method
     * @param args
     * @throws IOException
     * @throws ServiceException
     */
    public static void main(String[] args) throws IOException, ServiceException {
        launch(args);
    }



}
