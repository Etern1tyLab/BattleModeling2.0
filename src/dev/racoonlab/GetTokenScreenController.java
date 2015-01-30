package dev.racoonlab;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gdata.util.ServiceException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Nikita on 30.01.2015.
 */
public class GetTokenScreenController implements Initializable,
        ControlledScreen{

    ScreensController myController;


    // Retrieve the CLIENT_ID and CLIENT_SECRET from an APIs Console project:
    //     https://code.google.com/apis/console
    static String CLIENT_ID = "";
    static String CLIENT_SECRET = "";
    // Change the REDIRECT_URI value to your registered redirect URI for web
    // applications.
    static String REDIRECT_URI = "";

    // Add other requested scopes.
    static List<String> SCOPES = Arrays.asList("https://spreadsheets.google.com/feeds");

    @FXML private TextField textField;

    @FXML
    protected void handleGetToken(ActionEvent event) throws IOException {
        getCredentials();
    }

    @FXML
    protected void handleSignIn(ActionEvent event) throws IOException, ServiceException {
        checkToken();
    }

    @Override
    public void setScreenParent(ScreensController screenParent) {
        myController = screenParent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    static void getCredentials() throws IOException {

        // Step 1: Authorize -->
        String authorizationUrl =
                new GoogleAuthorizationCodeRequestUrl(CLIENT_ID, REDIRECT_URI, SCOPES).build();

        // Point or redirect your user to the authorizationUrl.
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        try {
            Desktop.getDesktop().browse(new URL(authorizationUrl).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkToken() throws IOException, ServiceException {
        HttpTransport transport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        // Step 2: Exchange -->
        GoogleTokenResponse response =
                new GoogleAuthorizationCodeTokenRequest(transport, jsonFactory, CLIENT_ID, CLIENT_SECRET,
                        textField.getText(), REDIRECT_URI).execute();
        // End of Step 2 <--

        // Build a new GoogleCredential instance and return it.
        Credential credential = new GoogleCredential.Builder().setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                .setJsonFactory(jsonFactory).setTransport(transport).build()
                .setAccessToken(response.getAccessToken()).setRefreshToken(response.getRefreshToken());

        //LoginController.printDocuments(credential);
        myController.setCredential(credential);
        myController.setScreen(Main.getMainScreenID);

    }
}
