package dev.racoonlab;

import com.google.api.client.auth.oauth2.Credential;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Nikita on 30.01.2015.
 */
public class MainScreenController implements Initializable, ControlledScreen {
    ScreensController myController;

    @FXML
    protected void loadSpreadsheet(ActionEvent event) throws IOException, ServiceException {
        getWorkSpreadsheet();
    }

    @FXML private AnchorPane leftScrollbar;
    @FXML private AnchorPane rightScrollbar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }

    public void getWorkSpreadsheet () throws IOException, ServiceException {
        String status="";

            /** Our view of Google Spreadsheets as an authenticated Google user. */
            SpreadsheetService service =
                    new SpreadsheetService("BattleModeling2.0");
            service.setOAuth2Credentials(myController.getCredential());

            // Load sheet
            URL metafeedUrl = new URL("https://spreadsheets.google.com/feeds/spreadsheets/CHANGE_ME!!!");
            SpreadsheetEntry spreadsheet = service.getEntry(metafeedUrl,SpreadsheetEntry.class);
            List<WorksheetEntry> worksheets = spreadsheet.getWorksheets();

            // Iterate through each worksheet in the spreadsheet.
            for (WorksheetEntry worksheet : worksheets) {
                // Get the worksheet's title, row count, and column count.
                String title = worksheet.getTitle().getPlainText();
                int rowCount = worksheet.getRowCount();
                int colCount = worksheet.getColCount();

                // Print the fetched information to the screen for this worksheet.
                System.out.println("\t" + title + "- rows:" + rowCount + " cols: " + colCount);

                addShipGroup();
            }


    }

    public void addShipGroup ()
    {
        for (int i = 0; i<3; i++)
        {
            Group ship = new Group();
            ship.getChildren().add(new Button("Hi there !"));
            leftScrollbar.getChildren().add(ship);

            Group ship2 = new Group();
            ship.getChildren().add(new Button("Hi there !"));
            rightScrollbar.getChildren().add(ship);
        }
    }
}
