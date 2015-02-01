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
import javafx.scene.layout.AnchorPane;

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

    //Contains all ships from Google Spreadsheet
    public Map<String, WeaponObject> team1WeaponsMap = new HashMap<String, WeaponObject>();
    //Contains all ships from Google Spreadsheet
    public Map<String, WeaponObject> team2WeaponsMap = new HashMap<String, WeaponObject>();

    //Contains team 1 ships
    public Map<Integer, ShipObject> team1Ships = new HashMap<Integer, ShipObject>();
    //Contains team 2 ships
    public Map<Integer, ShipObject> team2Ships = new HashMap<Integer, ShipObject>();

    //Used for populating ship choice box
    public static final ObservableList team1ShipNamesPicker = FXCollections.observableArrayList();
    //Used for populating ship choice box
    public static final ObservableList team2ShipNamesPicker = FXCollections.observableArrayList();
    //Used for populating team 1 weapon box
    public static final ObservableList team1WeaponNames = FXCollections.observableArrayList();
    //Used for populating team 2 weapon box
    public static final ObservableList team2WeaponNames = FXCollections.observableArrayList();
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

    public boolean team1ShipLoaded = false;
    public boolean team2ShipLoaded = false;

    //Current selected ship from lists
    public int team1CurrentSelectedShip;
    public int team2CurrentSelectedShip;

    public ListView team1CurrentSelectedWeaponList;
    public ListView team2CurrentSelectedWeaponList;

    @FXML
    private ChoiceBox team1ShipPicker;
    @FXML
    private ChoiceBox team2ShipPicker;

    @FXML
    private ChoiceBox team1WeaponPicker;
    @FXML
    private ChoiceBox team2WeaponPicker;

    @FXML
    private ChoiceBox team1RacePicker;
    @FXML
    private ChoiceBox team2RacePicker;

    @FXML
    private ListView team1ShipList;
    @FXML
    private ListView team2ShipList;

    @FXML
    private ListView team1WeaponList;
    @FXML
    private ListView team2WeaponList;

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
    public void setScreenParent(ScreensController _screenPage) {
        myController = _screenPage;
    }

    public void getWorkSpreadsheet (String _team) throws IOException, ServiceException, URISyntaxException {

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

        if (_team.equals("team1"))
        {
            //Setting up team 1 ship list
            team1ShipList.setItems(team1ShipNames);
            team1ShipList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    System.out.println("Selected item id " + team1ShipList.getSelectionModel().getSelectedIndex());
                    System.out.println("Ship array size " + team1Ships.size());

                    if (team1ShipList.getSelectionModel().getSelectedIndex() != -1)
                        team1CurrentSelectedShip = team1ShipList.getSelectionModel().getSelectedIndex();

                    team1WeaponCount.setText(team1Ships.get(team1CurrentSelectedShip).getName() + "("
                            + team1Ships.get(team1CurrentSelectedShip).getShield()
                            + "/" + team1Ships.get(team1CurrentSelectedShip).getArmor() + ")" + " weapons : \r\n Close range - "
                            + team1Ships.get(team1CurrentSelectedShip).getCloseWeaponCount() + "\r\n Middle range - " +
                            +team1Ships.get(team1CurrentSelectedShip).getMiddleWeaponCount() + "\r\n Long range - "
                            + team1Ships.get(team1CurrentSelectedShip).getLongWeaponCount());

                    System.out.println(team1Ships.get(team1CurrentSelectedShip).getName());


                    team1WeaponList.setItems(null);
                    team1WeaponList.setItems(team1Ships.get(team1CurrentSelectedShip).getCurrentWeapons());

                }
            });

            //Loading team 1 ships
            loadShipsData(worksheets, service, _team);
            //Loading team 1 weapons
            team1WeaponsMap = loadWeaponData(worksheets, service, _team, team1WeaponNames);

        }
        else if (_team.equals("team2"))
        {
            //Setting up team 2 ship list
            team2ShipList.setItems(team2ShipNames);
            team2ShipList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    System.out.println("Selected item id " + team2ShipList.getSelectionModel().getSelectedIndex());
                    System.out.println("Ship array size " + team2Ships.size());

                    if (team2ShipList.getSelectionModel().getSelectedIndex() != -1)
                        team2CurrentSelectedShip = team2ShipList.getSelectionModel().getSelectedIndex();

                    team2WeaponCount.setText(team2Ships.get(team2CurrentSelectedShip).getName() + "("
                            + team2Ships.get(team2CurrentSelectedShip).getShield()
                            + "/" + team2Ships.get(team2CurrentSelectedShip).getArmor() + ")" + " weapons : \r\n Close range - "
                            + team2Ships.get(team2CurrentSelectedShip).getCloseWeaponCount() + "\r\n Middle range - " +
                            +team2Ships.get(team2CurrentSelectedShip).getMiddleWeaponCount() + "\r\n Long range - "
                            + team2Ships.get(team2CurrentSelectedShip).getLongWeaponCount());

                    System.out.println(team2Ships.get(team2CurrentSelectedShip).getName());


                    team2WeaponList.setItems(null);
                    team2WeaponList.setItems(team2Ships.get(team2CurrentSelectedShip).getCurrentWeapons());

                }
            });

            //Loading team 2 ships
            loadShipsData(worksheets, service, _team);
            //Loading team 2 weapons
            team2WeaponsMap = loadWeaponData(worksheets, service, _team, team2WeaponNames);
        }

    }

    //Load ships data
    public void loadShipsData(List<WorksheetEntry> _worksheets, SpreadsheetService _service, String _team) throws IOException, ServiceException, URISyntaxException {

        //TODO refactor code duplicates
        //First, lets load armor and shield stats
        WorksheetEntry armorAndShieldWorksheet = _worksheets.get(2);

        //Hardcoded...TODO
        //Getting shields
        URL shieldFeedUrl = createURI(armorAndShieldWorksheet.getCellFeedUrl().toString(), 5, "ShieldAndArmor").toURL();
        CellFeed shieldFeed = _service.getFeed(shieldFeedUrl, CellFeed.class);
        shipShields = shieldFeed.getEntries();


        //Getting shields regen
        URL shieldRegenFeedUrl = createURI(armorAndShieldWorksheet.getCellFeedUrl().toString(), 6, "ShieldAndArmor").toURL();
        CellFeed shieldRegenFeed = _service.getFeed(shieldRegenFeedUrl, CellFeed.class);
        shipShieldsRegen = shieldRegenFeed.getEntries();


        //Getting armor
        URL armorFeedUrl = createURI(armorAndShieldWorksheet.getCellFeedUrl().toString(), 8, "ShieldAndArmor").toURL();
        CellFeed armorFeed = _service.getFeed(armorFeedUrl, CellFeed.class);
        shipArmors = armorFeed.getEntries();



        //Getting armor regen
        URL armorsRegenFeedUrl = createURI(armorAndShieldWorksheet.getCellFeedUrl().toString(), 9, "ShieldAndArmor").toURL();
        CellFeed armorsRegenFeed = _service.getFeed(armorsRegenFeedUrl, CellFeed.class);
        shipArmorsRegen = armorsRegenFeed.getEntries();

        if (_team.equals("team1"))
        {
            //Lets load ships stats
            WorksheetEntry shipsWorksheet = _worksheets.get(3);
            //Hardcoded too...TODO
            for (int i = 3; i<= 18; i++)
            {
                URL cellFeedUrl = createURI(shipsWorksheet.getCellFeedUrl().toString(), i, "Ships").toURL();
                CellFeed cellFeed = _service.getFeed(cellFeedUrl, CellFeed.class);
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

                team1ShipNamesPicker.add(cellEntryList.get(0).getCell().getValue());
                System.out.println("Team 1 - " + cellEntryList.get(0).getCell().getValue());
            }
            team1ShipPicker.setItems(team1ShipNamesPicker);
            team1ShipLoaded = true;
        }
        else  if (_team.equals("team2"))
        {
            //Lets load ships stats
            WorksheetEntry shipsWorksheet = _worksheets.get(3);
            //Hardcoded too...TODO
            for (int i = 3; i<= 18; i++)
            {
                URL cellFeedUrl = createURI(shipsWorksheet.getCellFeedUrl().toString(), i, "Ships").toURL();
                CellFeed cellFeed = _service.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> cellEntryList = cellFeed.getEntries();
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
                team2ShipNamesPicker.add(cellEntryList.get(0).getCell().getValue());
                System.out.println("Team 2 - " + cellEntryList.get(0).getCell().getValue());
            }
            team2ShipPicker.setItems(team2ShipNamesPicker);
            team2ShipLoaded = true;
        }

    }

    //Load ships data
    public Map<String, WeaponObject> loadWeaponData(List<WorksheetEntry> _worksheets, SpreadsheetService _service, String _team, ObservableList _weaponNames) throws IOException, ServiceException {

        String race;
        if (_team.equals("team1"))
            race = team1Race;
        else
            race = team2Race;

        WorksheetEntry weaponWorksheet;
        switch(race){
            default: System.out.print("");
            case "Земляне":
            {
                weaponWorksheet = _worksheets.get(4);
                break;
            }
            case "Аксотеотли":
            {
                weaponWorksheet = _worksheets.get(5);
                break;
            }
            case "Дредды":
            {
                weaponWorksheet = _worksheets.get(6);
                break;
            }
            case "СайберМаар":
            {
                weaponWorksheet = _worksheets.get(7);
                break;
            }
            case "ТуанТэ":
            {
                weaponWorksheet = _worksheets.get(8);
                break;
            }
        }

        Map<String, WeaponObject> weaponsMap = new HashMap<String, WeaponObject>();

        for (int i = 2; i<= 31; i++) {
            int distance = 1;
            if (i > 12)
                distance = 2;
            if (i > 22)
                distance = 3;

            if (i != 2 && i != 12 && i != 22)
            {
                URL cellFeedUrl = createURI(weaponWorksheet.getCellFeedUrl().toString(), i, "Weapons").toURL();
                CellFeed cellFeed = _service.getFeed(cellFeedUrl, CellFeed.class);
                List<CellEntry> cellEntryList = cellFeed.getEntries();
                weaponsMap.put(cellEntryList.get(0).getCell().getValue(), // Weapon name key
                        new WeaponObject(cellEntryList.get(0).getCell().getValue(),  // Weapon name
                                Integer.valueOf(cellEntryList.get(1).getCell().getValue()), // Weapon type
                                Float.valueOf(cellEntryList.get(2).getCell().getValue()), // Weapon dame
                                Integer.valueOf(cellEntryList.get(3).getCell().getValue()), // Weapon cd
                                Integer.valueOf(cellEntryList.get(4).getCell().getValue()), // Weapon magazine
                                Integer.valueOf(cellEntryList.get(6).getCell().getValue()), // Weapon reload
                                distance // Weapon distance
                        ));
                _weaponNames.add(cellEntryList.get(0).getCell().getValue());
                System.out.println(cellEntryList.get(0).getCell().getValue());
            }
            else
            {
                switch(i){
                    default: System.out.print("");
                    case 2:
                    {
                        weaponsMap.put("Ближняя дистанция", null);
                        _weaponNames.add("Ближняя дистанция");
                        System.out.println("Ближняя дистанция");
                        break;
                    }
                    case 12:
                    {
                        weaponsMap.put("Средняя дистанция", null);
                        _weaponNames.add("Средняя дистанция");
                        System.out.println("Средняя дистанция");
                        break;
                    }
                    case 22:
                    {
                        weaponsMap.put("Дальняя дистанция", null);
                        _weaponNames.add("Дальняя дистанция");
                        System.out.println("Дальняя дистанция");
                        break;
                    }
                }

            }
        }

        if (_team.equals("team1"))
            team1WeaponPicker.setItems(_weaponNames);
        else
            team2WeaponPicker.setItems(_weaponNames);

        return weaponsMap;
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
        else if (_type.equals("Weapons"))
        {
            try {
                return new URI(_uriStart + "?min-row=" + _rowNumber + "&max-row="+ _rowNumber +"&min-col=3&max-col=9");
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
        if (team1Race.equals("") && team1ShipLoaded != true)
        {
            team1Race = team1RacePicker.getValue().toString();
            getWorkSpreadsheet("team1");
        }
    }

    public void addShipTeam1(ActionEvent actionEvent) {
        //Add selected ship to list array
        team1ShipNames.add((team1ShipNames.size() + 1) + "." +team1ShipPicker.getValue().toString());
        //Add selected ship to team1
        ShipObject ship= team1ShipsMap.get(team1ShipPicker.getValue().toString());
        team1Ships.put(team1Ships.size(), ship);
        System.out.println("Added + " + team1Ships.get(team1Ships.size() - 1).getName() + " + ship to team1 collection! Now there is " + team1Ships.size() + " ships");

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
            //Remove ship from team collection
            team1Ships.remove(selectedIdx);
            System.out.println("Removed ship from team1 collection! Now there is " + team1Ships.size() + " ships");
            team1ShipList.getSelectionModel().select(newSelectedIdx);
        }
    }

    public void addWeaponTeam1(ActionEvent actionEvent) {
        //Add selected weapon to ship
        WeaponObject weapon = team1WeaponsMap.get(team1WeaponPicker.getValue().toString());
        team1Ships.get(team1CurrentSelectedShip).addWeapon(weapon);
        System.out.println("Added weapon to team1 ship!");

    }



    public void deleteSelectedWeaponTeam1(ActionEvent actionEvent) {

    }

    //Team 2 controls
    public void team2SetRace(ActionEvent actionEvent) throws ServiceException, IOException, URISyntaxException {
        if (team2Race.equals("") && team2ShipLoaded != true)
        {
            team2Race = team2RacePicker.getValue().toString();
            getWorkSpreadsheet("team2");
        }
    }

    public void addShipTeam2(ActionEvent actionEvent) {
        //Add selected ship to list array
        team2ShipNames.add((team2ShipNames.size() + 1) + "." +team2ShipPicker.getValue().toString());
        //Add selected ship to team1
        ShipObject ship= team2ShipsMap.get(team2ShipPicker.getValue().toString());
        team2Ships.put(team2Ships.size(), ship);
        System.out.println("Added + " + team2Ships.get(team2Ships.size() - 1).getName() + " + ship to team1 collection! Now there is " + team2Ships.size() + " ships");

        //Add ship to list
        team1ShipList.setItems(team2ShipNames);
    }

    public void deleteSelectedShipTeam2(ActionEvent actionEvent) {
        final int selectedIdx = team2ShipList.getSelectionModel().getSelectedIndex();
        if (selectedIdx != -1) {
            final int newSelectedIdx =
                    (selectedIdx == team2ShipList.getItems().size() - 1)
                            ? selectedIdx - 1
                            : selectedIdx;

            team2ShipList.getItems().remove(selectedIdx);
            //Remove ship from team collection
            team2Ships.remove(selectedIdx);
            System.out.println("Removed ship from team2 collection! Now there is " + team2Ships.size() + " ships");
            team2ShipList.getSelectionModel().select(newSelectedIdx);
        }
    }

    public void addWeaponTeam2(ActionEvent actionEvent) {
        //Add selected weapon to ship
        WeaponObject weapon = team2WeaponsMap.get(team2WeaponPicker.getValue().toString());
        team2Ships.get(team2CurrentSelectedShip).addWeapon(weapon);
        System.out.println("Added weapon to team1 ship!");
    }

    public void deleteSelectedWeaponTeam2(ActionEvent actionEvent) {
    }

}
