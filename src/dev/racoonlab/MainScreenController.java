package dev.racoonlab;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.ServiceException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Created by Nikita on 30.01.2015.
 */
public class MainScreenController implements Initializable, ControlledScreen {

    ScreensController myController;

    //Contains all ships from Google Spreadsheet
    public Map<String, ShipObject> team1ShipsMap = new HashMap<String, ShipObject>();
    //Contains all ships from Google Spreadsheet
    public Map<String, ShipObject> team2ShipsMap = new HashMap<String, ShipObject>();

    //Used for populating ship choice box
    public static final ObservableList shipNames = FXCollections.observableArrayList();
    //Used for populating race choice box
    public static final ObservableList raceNames = FXCollections.observableArrayList("Земляне", "Аксотеотли", "Дредды", "СайберМаар", "ТуанТэ");

    //Used for populating team 1 ship list
    public static final ObservableList team1ShipNames = FXCollections.observableArrayList();
    //Used for populating team 2 ship list
    public static final ObservableList team2ShipNames = FXCollections.observableArrayList();

    public String team1Race = "";
    public String team2Race = "";

    public List<CellEntry> shipShields = new ArrayList<CellEntry>();
    public List<CellEntry> shipShieldsRegen = new ArrayList<CellEntry>();
    public List<CellEntry> shipArmors = new ArrayList<CellEntry>();
    public List<CellEntry> shipArmorsRegen = new ArrayList<CellEntry>();

    public boolean shipLoaded = false;

    @FXML
    private ChoiceBox team1ShipPicker;
    @FXML
    private ChoiceBox team2ShipPicker;

    @FXML
    private ChoiceBox team1RacePicker;
    @FXML
    private ChoiceBox team2RacePicker;

    @FXML
    private ListView team1ShipList;
    @FXML
    private ListView team2ShipList;

    @FXML
    private Label team1WeaponCount;
    @FXML
    private Label team2WeaponCount;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        team1RacePicker.setItems(raceNames);
        team2RacePicker.setItems(raceNames);
    }

    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }

    public void getWorkSpreadsheet () throws IOException, ServiceException, URISyntaxException {

        //Setting up team 1 ship list
        team1ShipList.setItems(team1ShipNames);
        team1ShipList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ShipObject selectedShip = team1ShipsMap.get(team1ShipList.getSelectionModel().getSelectedItem().toString());
                team1WeaponCount.setText(selectedShip.getName()+ "(" + selectedShip.getShield() + "/" + selectedShip.getArmor() +")" + " weapons : \r\n Close range - "
                        + selectedShip.getCloseWeaponCount() + "\r\n Middle range - " +
                        + selectedShip.getMiddleWeaponCount() + "\r\n Long range - " + selectedShip.getLongWeaponCount());
            }
        });
        //Setting up team 2 ship list
        team2ShipList.setItems(team2ShipNames);
        team2ShipList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ShipObject selectedShip = team2ShipsMap.get(team2ShipList.getSelectionModel().getSelectedItem().toString());
                team2WeaponCount.setText(selectedShip.getName()+ "(" + selectedShip.getShield() + "/" + selectedShip.getArmor() +")" + " weapons : \r\n Close range - "
                        + selectedShip.getCloseWeaponCount() + "\r\n Middle range - " +
                        + selectedShip.getMiddleWeaponCount() + "\r\n Long range - " + selectedShip.getLongWeaponCount());
            }
        });


        loadShipsData();
    }

    //Preload ships data
    public void loadShipsData() throws IOException, ServiceException, URISyntaxException {
        /** Our view of Google Spreadsheets as an authenticated Google user. */
        SpreadsheetService service =
                new SpreadsheetService("BattleModeling2.0");
        service.setOAuth2Credentials(myController.getCredential());

        // Load sheet
        URL metafeedUrl = new URL("https://spreadsheets.google.com/feeds/spreadsheets/1MPLaDZJ-Y4OfPwiRpTZqsfh8lBNgUL_XSMZ4my5bFsU");
        SpreadsheetEntry spreadsheet = service.getEntry(metafeedUrl,SpreadsheetEntry.class);

        //Loading worksheets
        WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
        List<WorksheetEntry> worksheets = worksheetFeed.getEntries();

        //First, lets load armor and shield stats
        WorksheetEntry armorAndShieldWorksheet = worksheets.get(2);

        //Hardcoded...TODO
        //Getting shields
        URL shieldFeedUrl = createURI(armorAndShieldWorksheet.getCellFeedUrl().toString(), 5, "ShieldAndArmor").toURL();
        CellFeed shieldFeed = service.getFeed(shieldFeedUrl, CellFeed.class);
        shipShields = shieldFeed.getEntries();


        //Getting shields regen
        URL shieldRegenFeedUrl = createURI(armorAndShieldWorksheet.getCellFeedUrl().toString(), 6, "ShieldAndArmor").toURL();
        CellFeed shieldRegenFeed = service.getFeed(shieldRegenFeedUrl, CellFeed.class);
        shipShieldsRegen = shieldRegenFeed.getEntries();


        //Getting armor
        URL armorFeedUrl = createURI(armorAndShieldWorksheet.getCellFeedUrl().toString(), 8, "ShieldAndArmor").toURL();
        CellFeed armorFeed = service.getFeed(armorFeedUrl, CellFeed.class);
        shipArmors = armorFeed.getEntries();



        //Getting armor regen
        URL armorsRegenFeedUrl = createURI(armorAndShieldWorksheet.getCellFeedUrl().toString(), 9, "ShieldAndArmor").toURL();
        CellFeed armorsRegenFeed = service.getFeed(armorsRegenFeedUrl, CellFeed.class);
        shipArmorsRegen = armorsRegenFeed.getEntries();



        //Lets load ships stats
        WorksheetEntry shipsWorksheet = worksheets.get(3);
        //Hardcoded too...TODO
        for (int i = 3; i<= 18; i++)
        {

            URL cellFeedUrl = createURI(shipsWorksheet.getCellFeedUrl().toString(), i, "Ships").toURL();
            CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);
            List<CellEntry> cellEntryList = cellFeed.getEntries();
            team1ShipsMap.put(cellEntryList.get(0).getCell().getValue(),  //Key value by name
                                    new ShipObject(cellEntryList.get(0).getCell().getValue(), // Ships name
                                                    Integer.valueOf(cellEntryList.get(1).getCell().getValue()), // Ships close range weapon count
                                                    Integer.valueOf(cellEntryList.get(2).getCell().getValue()), // Ships middle range weapon count
                                                    Integer.valueOf(cellEntryList.get(3).getCell().getValue()), // Ships long range weapon count
                                                    calcShield( cellEntryList.get(4).getCell().getValue(),
                                                            cellEntryList.get(5).getCell().getValue(),
                                                            cellEntryList.get(6).getCell().getValue(),
                                                            "team1"), // Ships shield
                                                    calcArmor( cellEntryList.get(8).getCell().getValue(),
                                                            cellEntryList.get(9).getCell().getValue(),
                                                            cellEntryList.get(10).getCell().getValue(),
                                                            "team1"), // Ships armor
                                                    calcShieldRegen(cellEntryList.get(4).getCell().getValue(),
                                                            cellEntryList.get(5).getCell().getValue(),
                                                            cellEntryList.get(6).getCell().getValue(),
                                                            "team1"), // Ships shield regen
                                                    calcArmorRegen(cellEntryList.get(8).getCell().getValue(),
                                                            cellEntryList.get(9).getCell().getValue(),
                                                            cellEntryList.get(10).getCell().getValue(),
                                                            "team1") // Ships armor regen
                                    ));
            team2ShipsMap.put(cellEntryList.get(0).getCell().getValue(),  //Key value by name
                                    new ShipObject(cellEntryList.get(0).getCell().getValue(), // Ships name
                                                    Integer.valueOf(cellEntryList.get(1).getCell().getValue()), // Ships close range weapon count
                                                    Integer.valueOf(cellEntryList.get(2).getCell().getValue()), // Ships middle range weapon count
                                                    Integer.valueOf(cellEntryList.get(3).getCell().getValue()), // Ships long range weapon count
                                                    calcShield( cellEntryList.get(4).getCell().getValue(),
                                                                cellEntryList.get(5).getCell().getValue(),
                                                                cellEntryList.get(6).getCell().getValue(),
                                                                "team2"), // Ships shield
                                                    calcArmor( cellEntryList.get(8).getCell().getValue(),
                                                                cellEntryList.get(9).getCell().getValue(),
                                                                cellEntryList.get(10).getCell().getValue(),
                                                                "team2"), // Ships armor
                                                    calcShieldRegen(cellEntryList.get(4).getCell().getValue(),
                                                            cellEntryList.get(5).getCell().getValue(),
                                                            cellEntryList.get(6).getCell().getValue(),
                                                            "team2"), // Ships shield regen
                                                    calcArmorRegen(cellEntryList.get(8).getCell().getValue(),
                                                            cellEntryList.get(9).getCell().getValue(),
                                                            cellEntryList.get(10).getCell().getValue(),
                                                            "team2") // Ships armor regen
                                    ));
            shipNames.add(cellEntryList.get(0).getCell().getValue());
            System.out.println(cellEntryList.get(0).getCell().getValue());

        }

        team1ShipPicker.setItems(shipNames);
        team2ShipPicker.setItems(shipNames);

        //Set that ships are loaded
        shipLoaded = true;

    }

    public float calcShield(String _smallCount, String _midCount, String _bigCount, String _team)
    {


        String race =  _team.equals("team1") ? team1Race : team2Race;
        int small;
        int mid;
        int big;
        switch(race){
            default: System.out.print("");
            case "Земляне":
            {
                small = Integer.valueOf(shipShields.get(0).getCell().getValue().replace(" ",""));
                mid = Integer.valueOf(shipShields.get(1).getCell().getValue());
                big = Integer.valueOf(shipShields.get(2).getCell().getValue());
                break;
            }
            case "Аксотеотли":
            {
                small = Integer.valueOf(shipShields.get(3).getCell().getValue());
                mid = Integer.valueOf(shipShields.get(4).getCell().getValue());
                big = Integer.valueOf(shipShields.get(5).getCell().getValue());
                break;
            }
            case "Дредды":
            {
                small = Integer.valueOf(shipShields.get(6).getCell().getValue());
                mid = Integer.valueOf(shipShields.get(7).getCell().getValue());
                big = Integer.valueOf(shipShields.get(8).getCell().getValue());
                break;
            }
            case "СайберМаар":
            {
                small = Integer.valueOf(shipShields.get(9).getCell().getValue());
                mid = Integer.valueOf(shipShields.get(10).getCell().getValue());
                big = Integer.valueOf(shipShields.get(11).getCell().getValue().replace(" ",""));
                break;
            }
            case "ТуанТэ":
            {
                small = Integer.valueOf(shipShields.get(12).getCell().getValue());
                mid = Integer.valueOf(shipShields.get(13).getCell().getValue());
                big = Integer.valueOf(shipShields.get(14).getCell().getValue());
                break;
            }
        }
        return small*Integer.valueOf(_smallCount) + mid*Integer.valueOf(_midCount) + big*Integer.valueOf(_bigCount);
    }

    public float calcShieldRegen(String _smallCount, String _midCount, String _bigCount, String _team)
    {
        String race =  _team.equals("team1") ? team1Race : team2Race;
        int small;
        int mid;
        int big;
        switch(race){
            default: System.out.print("");
            case "Земляне":
            {
                small = Integer.valueOf(shipShieldsRegen.get(0).getCell().getValue());
                mid = Integer.valueOf(shipShieldsRegen.get(1).getCell().getValue().replace(" ",""));
                big = Integer.valueOf(shipShieldsRegen.get(2).getCell().getValue());
                break;
            }
            case "Аксотеотли":
            {
                small = Integer.valueOf(shipShieldsRegen.get(3).getCell().getValue());
                mid = Integer.valueOf(shipShieldsRegen.get(4).getCell().getValue());
                big = Integer.valueOf(shipShieldsRegen.get(5).getCell().getValue());
                break;
            }
            case "Дредды":
            {
                small = Integer.valueOf(shipShieldsRegen.get(6).getCell().getValue());
                mid = Integer.valueOf(shipShieldsRegen.get(7).getCell().getValue());
                big = Integer.valueOf(shipShieldsRegen.get(8).getCell().getValue());
                break;
            }
            case "СайберМаар":
            {
                small = Integer.valueOf(shipShieldsRegen.get(9).getCell().getValue());
                mid = Integer.valueOf(shipShieldsRegen.get(10).getCell().getValue());
                big = Integer.valueOf(shipShieldsRegen.get(11).getCell().getValue());
                break;
            }
            case "ТуанТэ":
            {
                small = Integer.valueOf(shipShieldsRegen.get(12).getCell().getValue());
                mid = Integer.valueOf(shipShieldsRegen.get(13).getCell().getValue());
                big = Integer.valueOf(shipShieldsRegen.get(14).getCell().getValue());
                break;
            }
        }
        return small*Integer.valueOf(_smallCount) + mid*Integer.valueOf(_midCount) + big*Integer.valueOf(_bigCount);
    }

    public float calcArmor(String _smallCount, String _midCount, String _bigCount, String _team)
    {
        String race =  _team.equals("team1") ? team1Race : team2Race;
        int small;
        int mid;
        int big;
        switch(race){
            default: System.out.print("");
            case "Земляне":
            {
                small = Integer.valueOf(shipArmors.get(0).getCell().getValue());
                mid = Integer.valueOf(shipArmors.get(1).getCell().getValue());
                big = Integer.valueOf(shipArmors.get(2).getCell().getValue());
                break;
            }
            case "Аксотеотли":
            {
                small = Integer.valueOf(shipArmors.get(3).getCell().getValue());
                mid = Integer.valueOf(shipArmors.get(4).getCell().getValue());
                big = Integer.valueOf(shipArmors.get(5).getCell().getValue());
                break;
            }
            case "Дредды":
            {
                small = Integer.valueOf(shipArmors.get(6).getCell().getValue());
                mid = Integer.valueOf(shipArmors.get(7).getCell().getValue());
                big = Integer.valueOf(shipArmors.get(8).getCell().getValue());
                break;
            }
            case "СайберМаар":
            {
                small = Integer.valueOf(shipShields.get(9).getCell().getValue());
                mid = Integer.valueOf(shipShields.get(10).getCell().getValue());
                big = Integer.valueOf(shipShields.get(11).getCell().getValue());
                break;
            }
            case "ТуанТэ":
            {
                small = Integer.valueOf(shipShields.get(12).getCell().getValue());
                mid = Integer.valueOf(shipShields.get(13).getCell().getValue());
                big = Integer.valueOf(shipShields.get(14).getCell().getValue());
                break;
            }
        }
        return small*Integer.valueOf(_smallCount) + mid*Integer.valueOf(_midCount) + big*Integer.valueOf(_bigCount);
    }

    public float calcArmorRegen(String _smallCount, String _midCount, String _bigCount, String _team)
    {
        String race =  _team.equals("team1") ? team1Race : team2Race;
        int small;
        int mid;
        int big;
        switch(race){
            default: System.out.print("");
            case "Земляне":
            {
                small = Integer.valueOf(shipArmorsRegen.get(0).getCell().getValue());
                mid = Integer.valueOf(shipArmorsRegen.get(1).getCell().getValue());
                big = Integer.valueOf(shipArmorsRegen.get(2).getCell().getValue());
                break;
            }
            case "Аксотеотли":
            {
                small = Integer.valueOf(shipArmorsRegen.get(3).getCell().getValue());
                mid = Integer.valueOf(shipArmorsRegen.get(4).getCell().getValue());
                big = Integer.valueOf(shipArmorsRegen.get(5).getCell().getValue());
                break;
            }
            case "Дредды":
            {
                small = Integer.valueOf(shipArmorsRegen.get(6).getCell().getValue());
                mid = Integer.valueOf(shipArmorsRegen.get(7).getCell().getValue());
                big = Integer.valueOf(shipArmorsRegen.get(8).getCell().getValue());
                break;
            }
            case "СайберМаар":
            {
                small = Integer.valueOf(shipShieldsRegen.get(9).getCell().getValue());
                mid = Integer.valueOf(shipShieldsRegen.get(10).getCell().getValue());
                big = Integer.valueOf(shipShieldsRegen.get(11).getCell().getValue());
                break;
            }
            case "ТуанТэ":
            {
                small = Integer.valueOf(shipShieldsRegen.get(12).getCell().getValue());
                mid = Integer.valueOf(shipShieldsRegen.get(13).getCell().getValue());
                big = Integer.valueOf(shipShieldsRegen.get(14).getCell().getValue());
                break;
            }
        }
        return small*Integer.valueOf(_smallCount) + mid*Integer.valueOf(_midCount) + big*Integer.valueOf(_bigCount);
    }

    public URI createURI (String _uriStart, int _rowNumber, String _type)
    {
        if (_type.equals("ShieldAndArmor"))
        {
            try {
                return new URI(_uriStart + "?min-row=" + _rowNumber + "&max-row="+ _rowNumber +"&min-col=2&max-col=16");
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return null;
            }
        }
        else if (_type.equals("Ships"))
        {
            try {
                return new URI(_uriStart + "?min-row=" + _rowNumber + "&max-row="+ _rowNumber +"&min-col=1&max-col=12");
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return null;
            }
        }
        else
            return null;

    }

    //Team 1 controls
    public void team1SetRace(ActionEvent actionEvent) throws ServiceException, IOException, URISyntaxException {
        if (team1Race.equals(""))
        {
            team1Race = team1RacePicker.getValue().toString();
            getWorkSpreadsheet();
        }
    }

    public void addShipTeam1(ActionEvent actionEvent) {
        //Add selected ship to array
        team1ShipNames.add(team1ShipPicker.getValue().toString());
        //Add ship to list
        team1ShipList.setItems(team1ShipNames);
    }

    public void deleteSelectedShipTeam1(ActionEvent actionEvent) {
        final int selectedIdx = team1ShipList.getSelectionModel().getSelectedIndex();
        if (selectedIdx != -1) {
            final int newSelectedIdx =
                    (selectedIdx == team1ShipList.getItems().size() - 1)
                            ? selectedIdx - 1
                            : selectedIdx;

            team1ShipList.getItems().remove(selectedIdx);
            team1ShipList.getSelectionModel().select(newSelectedIdx);
        }
    }

    public void addWeaponTeam1(ActionEvent actionEvent) {
    }



    public void deleteSelectedWeaponTeam1(ActionEvent actionEvent) {

    }

    //Team 2 controls
    public void team2SetRace(ActionEvent actionEvent) {
        if (team2Race.equals(""))
          team2Race = team2RacePicker.getValue().toString();
    }

    public void addShipTeam2(ActionEvent actionEvent) {
        //Add selected ship to array
        team2ShipNames.add(team2ShipPicker.getValue().toString());
        //Add ship to list
        team2ShipList.setItems(team2ShipNames);
    }

    public void deleteSelectedShipTeam2(ActionEvent actionEvent) {
        final int selectedIdx = team2ShipList.getSelectionModel().getSelectedIndex();
        if (selectedIdx != -1) {
            final int newSelectedIdx =
                    (selectedIdx == team2ShipList.getItems().size() - 1)
                            ? selectedIdx - 1
                            : selectedIdx;

            team2ShipList.getItems().remove(selectedIdx);
            team2ShipList.getSelectionModel().select(newSelectedIdx);
        }
    }

    public void addWeaponTeam2(ActionEvent actionEvent) {
    }

    public void deleteSelectedWeaponTeam2(ActionEvent actionEvent) {
    }

}
