package dev.racoonlab;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.ServiceException;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.controlsfx.dialog.Dialogs;
import sun.rmi.runtime.Log;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;


public class MainScreenController implements Initializable, ControlledScreen {

    static ScreensController myController;

    //Contains all ships from Google Spreadsheet
    public static Map<String, ShipObject> team1ShipsMap = new HashMap<String, ShipObject>();
    //Contains all ships from Google Spreadsheet
    public static Map<String, ShipObject> team2ShipsMap = new HashMap<String, ShipObject>();

    //Contains all ships from Google Spreadsheet
    public Map<String, WeaponObject> team1WeaponsMap = new HashMap<String, WeaponObject>();
    //Contains all ships from Google Spreadsheet
    public Map<String, WeaponObject> team2WeaponsMap = new HashMap<String, WeaponObject>();

    //Contains team 1 ships
    public List<ShipObject> team1Ships = new ArrayList();
    //Contains team 2 ships
    public List<ShipObject> team2Ships = new ArrayList();

    //Used for populating team ship box
    public static final ObservableList teamShipNamesPicker = FXCollections.observableArrayList();
    //Used for populating team 1 weapon box
    public static final ObservableList teamWeaponNamesPicker = FXCollections.observableArrayList();

    //Used for populating race choice box
    public static final ObservableList raceNames = FXCollections.observableArrayList("Земляне", "Аксотеотли", "Дредды", "СайберМаар", "ТуанТэ");

    //Used for populating team 1 ship list
    public static final ObservableList team1ShipNames = FXCollections.observableArrayList();
    //Used for populating team 2 ship list
    public static final ObservableList team2ShipNames = FXCollections.observableArrayList();

    public static String team1Race = "";
    public static String team2Race = "";

    public static List<CellEntry> shipShields = new ArrayList<CellEntry>();
    public static List<CellEntry> shipShieldsRegen = new ArrayList<CellEntry>();
    public static List<CellEntry> shipArmors = new ArrayList<CellEntry>();
    public static List<CellEntry> shipArmorsRegen = new ArrayList<CellEntry>();

    public boolean team1ShipLoaded = false;
    public boolean team2ShipLoaded = false;

    //Current selected ship from lists
    public int team1CurrentSelectedShip;
    public int team2CurrentSelectedShip;

    @FXML
    private ChoiceBox<String> team1ShipPicker;
    @FXML
    private ChoiceBox<String> team2ShipPicker;

    @FXML
    private ChoiceBox<String> team1WeaponPicker;
    @FXML
    private ChoiceBox<String> team2WeaponPicker;

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

    @FXML
    private TextField battleTime;
    @FXML
    private Slider team1Env;
    @FXML
    private Slider team1Will;
    @FXML
    private Slider team1Morale;
    @FXML
    private Slider team1Reaction;
    @FXML
    private Slider team2Env;
    @FXML
    private Slider team2Will;
    @FXML
    private Slider team2Morale;
    @FXML
    private Slider team2Reaction;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        team1RacePicker.setItems(raceNames);
        team2RacePicker.setItems(raceNames);
    }

    /**
     * @param _screenPage
     */
    @Override
    public void setScreenParent(ScreensController _screenPage) {
        myController = _screenPage;
    }

    /**
     * @param _team
     * @throws IOException
     * @throws ServiceException
     * @throws URISyntaxException
     */
    public void getWorkSpreadsheet (String _team)  {

        /** Our view of Google Spreadsheets as an authenticated Google user. */
        SpreadsheetService service =
                new SpreadsheetService("BattleModeling2.0");
        service.setOAuth2Credentials(myController.getCredential());

        // Load sheet
        URL metafeedUrl = null;
        try {
            metafeedUrl = new URL("https://spreadsheets.google.com/feeds/spreadsheets/1hjg-U4bq94FDgQj7iJnjOtFZAVXWkEYPGaoLCKJMQzc");
        } catch (MalformedURLException e) {
            showDialogException(e.getMessage());
        }
        SpreadsheetEntry spreadsheet = null;
        try {
            spreadsheet = service.getEntry(metafeedUrl,SpreadsheetEntry.class);
        } catch (IOException e) {
            showDialogException(e.getMessage());
        } catch (ServiceException e) {
            showDialogException(e.getMessage());
        }

        //Loading worksheets
        WorksheetFeed worksheetFeed = null;
        try {
            worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
        } catch (IOException e) {
            showDialogException(e.getMessage());
        } catch (ServiceException e) {
            showDialogException(e.getMessage());
        }
        List<WorksheetEntry> worksheets = worksheetFeed.getEntries();

        if (_team.equals("team1"))
        {

            //Setting up team 1 ship list
            updateShipList(_team);
            team1ShipList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    System.out.println("Selected item id " + team1ShipList.getSelectionModel().getSelectedIndex());

                    if (team1ShipList.getSelectionModel().getSelectedIndex() != -1)
                        team1CurrentSelectedShip = team1ShipList.getSelectionModel().getSelectedIndex();

                    System.out.println(team1CurrentSelectedShip);
                    updateShipInfo(team1Ships.get(team1CurrentSelectedShip), "team1");


                    team1WeaponList.setItems(null);
                    team1Ships.get(team1CurrentSelectedShip).updateCurrentWeaponsList();
                    team1WeaponList.setItems(team1Ships.get(team1CurrentSelectedShip).getCurrentWeaponsList());

                }
            });

            //Loading team 1 ships
            loadShipsData(worksheets, service, _team);

        }
        else if (_team.equals("team2"))
        {
            //Setting up team 2 ship list
            updateShipList(_team);
            team2ShipList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    System.out.println("Selected item id " + team2ShipList.getSelectionModel().getSelectedIndex());

                    if (team2ShipList.getSelectionModel().getSelectedIndex() != -1)
                        team2CurrentSelectedShip = team2ShipList.getSelectionModel().getSelectedIndex();

                    System.out.println(team1CurrentSelectedShip);
                    updateShipInfo(team2Ships.get(team2CurrentSelectedShip), "team2");


                    team2WeaponList.setItems(null);
                    team2Ships.get(team2CurrentSelectedShip).updateCurrentWeaponsList();
                    team2WeaponList.setItems(team2Ships.get(team2CurrentSelectedShip).getCurrentWeaponsList());

                }
            });


            //Loading team 2 ships
            loadShipsData(worksheets, service, _team);

        }

    }

    //Load ships data
    public void loadShipsData(List<WorksheetEntry> _worksheets, SpreadsheetService _service, String _team) {

        //TODO refactor code duplicates
        //First, lets load armor and shield stats
        WorksheetEntry armorAndShieldWorksheet = _worksheets.get(1);

        //Hardcoded...TODO
        //Getting shields
        URL shieldFeedUrl = createURI(armorAndShieldWorksheet.getCellFeedUrl().toString(), 5, "ShieldAndArmor");
        CellFeed shieldFeed =getFeed(_service, shieldFeedUrl);
        shipShields = shieldFeed.getEntries();

        //Getting shields regen
        URL shieldRegenFeedUrl = createURI(armorAndShieldWorksheet.getCellFeedUrl().toString(), 6, "ShieldAndArmor");
        CellFeed shieldRegenFeed = getFeed(_service, shieldRegenFeedUrl);
        shipShieldsRegen = shieldRegenFeed.getEntries();

        //Getting armor
        URL armorFeedUrl = createURI(armorAndShieldWorksheet.getCellFeedUrl().toString(), 8, "ShieldAndArmor");
        CellFeed armorFeed = getFeed(_service, armorFeedUrl);
        shipArmors = armorFeed.getEntries();

        //Getting armor regen
        URL armorsRegenFeedUrl = createURI(armorAndShieldWorksheet.getCellFeedUrl().toString(), 9, "ShieldAndArmor");
        CellFeed armorsRegenFeed = getFeed(_service, armorsRegenFeedUrl);
        shipArmorsRegen = armorsRegenFeed.getEntries();

        //Lets load ships stats

        //Hardcoded too...TODO
        WorksheetEntry shipsWorksheet = _worksheets.get(0);

        //Creating  dialog and  service to get data
        ShipDataService shipService = new ShipDataService();
        shipService.setWorksheet(shipsWorksheet);
        shipService.setService(_service);
        shipService.setTeam(_team);
        shipService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                if (_team.equals("team1"))
                {
                    team1ShipPicker.setItems(teamShipNamesPicker);
                    team1ShipLoaded = shipService.getValue();
                    loadWeaponData(_worksheets, _service, _team);
                }

                else
                {
                    team2ShipLoaded = shipService.getValue();
                    team2ShipPicker.setItems(teamShipNamesPicker);
                    loadWeaponData(_worksheets, _service, _team);
                }

            }
        });

        Dialogs.create()
                .owner(myController.getStage())
                .title("Ship's loading.")
                .masthead("Loading ship's . . .")
                .showWorkerProgress(shipService);

        shipService.start();

    }


    //Load ships data
    public void loadWeaponData(List<WorksheetEntry> _worksheets, SpreadsheetService _service, String _team){

        String race;
        if (_team.equals("team1"))
            race = team1Race;
        else
            race = team2Race;

		System.out.println("Loading weapons");
        WorksheetEntry weaponWorksheet;
        switch(race){
            default: System.out.print("");
            case "Земляне":
            {
                weaponWorksheet = _worksheets.get(2);
                break;
            }
            case "Аксотеотли":
            {
                weaponWorksheet = _worksheets.get(3);
                break;
            }
            case "Дредды":
            {
                weaponWorksheet = _worksheets.get(4);
                break;
            }
            case "СайберМаар":
            {
                weaponWorksheet = _worksheets.get(5);
                break;
            }
            case "ТуанТэ":
            {
                weaponWorksheet = _worksheets.get(6);
                break;
            }
        }

        WeaponDataService weaponDataService = new WeaponDataService();
        weaponDataService.setWorksheet(weaponWorksheet);
        weaponDataService.setService(_service);
        weaponDataService.setTeam(_team);
		System.out.println("Starting service");
        weaponDataService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                if (_team.equals("team1")) {
                    team1WeaponPicker.setItems(teamWeaponNamesPicker);
                    team1WeaponsMap = weaponDataService.getValue();
                } else {
                    team2WeaponPicker.setItems(teamWeaponNamesPicker);
                    team2WeaponsMap = weaponDataService.getValue();
                }

            }
        });

        Dialogs.create()
                .owner(myController.getStage())
                .title("Weapon's loading.")
                .masthead("Loading weapons . . .")
                .showWorkerProgress(weaponDataService);

        weaponDataService.start();
    }

    public static double calcShield(String _smallCount, String _midCount, String _bigCount, String _team)
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

    public static double calcShieldRegen(String _smallCount, String _midCount, String _bigCount, String _team)
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

    public static double calcArmor(String _smallCount, String _midCount, String _bigCount, String _team)
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

    public static double calcArmorRegen(String _smallCount, String _midCount, String _bigCount, String _team)
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

    public static URL createURI(String _uriStart, int _rowNumber, String _type)
    {
        if (_type.equals("ShieldAndArmor"))
        {
            try {
                return new URI(_uriStart + "?min-row=" + _rowNumber + "&max-row="+ _rowNumber +"&min-col=2&max-col=16").toURL();
            } catch (URISyntaxException e) {
                showDialogException(e.getMessage());
                return null;
            } catch (MalformedURLException e) {
                showDialogException(e.getMessage());
                return null;
            }
        }
        else if (_type.equals("Ships"))
        {
            try {
                return new URI(_uriStart + "?min-row=" + _rowNumber + "&max-row="+ _rowNumber +"&min-col=1&max-col=14").toURL();
            } catch (URISyntaxException e) {
                showDialogException(e.getMessage());
                return null;
            } catch (MalformedURLException e) {
                showDialogException(e.getMessage());
                return null;
            }
        }
        else if (_type.equals("Weapons"))
        {
            try {
                return new URI(_uriStart + "?min-row=" + _rowNumber + "&max-row="+ _rowNumber +"&min-col=2&max-col=11").toURL();
            } catch (URISyntaxException e) {
                showDialogException(e.getMessage());
                return null;
            } catch (MalformedURLException e) {
                showDialogException(e.getMessage());
                return null;
            }
        }
        else
            return null;

    }

    public void updateShipInfo(ShipObject _ship, String _team)
    {
        if (_team.equals("team1"))
        {
            team1WeaponCount.setText(_ship.getName() + "("
                    + _ship.getShield()
                    + "/" + _ship.getArmor() + ")" + " weapons : \r\n " +
                    "Close range: " + _ship.currentCloseRangeCount()+ " of " + _ship.getCloseWeaponCount() + "\r\n " +
                    "Middle range: " + _ship.currentMidRangeCount()+ " of " + _ship.getMiddleWeaponCount() + "\r\n " +
                    "Long range: " + _ship.currentLongRangeCount()+ " of " + _ship.getLongWeaponCount()+ "\r\n" +
					"Current energy: " + _ship.getMaxWeaponEnergy() + "/" + _ship.getCurrentWeaponEnergy());
        }
        else if (_team.equals("team2"))
        {
            team2WeaponCount.setText(_ship.getName() + "("
                    + _ship.getShield()
                    + "/" + _ship.getArmor() + ")" + " weapons : \r\n " +
                    "Close range: " + _ship.currentCloseRangeCount()+ " of " + _ship.getCloseWeaponCount() + "\r\n " +
                    "Middle range: " + _ship.currentMidRangeCount()+ " of " + _ship.getMiddleWeaponCount() + "\r\n " +
                    "Long range: " + _ship.currentLongRangeCount()+ " of " + _ship.getLongWeaponCount() + "\r\n" +
					"Current energy: " + _ship.getMaxWeaponEnergy() + "/" + _ship.getCurrentWeaponEnergy());
        }
    }

    public void updateShipList (String _team)
    {
        if (_team.equals("team1"))
        {
            team1ShipNames.clear();
            for (int i = 0; i < team1Ships.size(); i++)
            {

                team1ShipNames.add(team1Ships.get(i).getName());
            }
            team1ShipList.setItems(team1ShipNames);

        }
        else if (_team.equals("team2"))
        {
            team2ShipNames.clear();
            for (int i = 0; i < team2Ships.size(); i++)
            {

                team2ShipNames.add(team2Ships.get(i).getName());
            }
            team2ShipList.setItems(team2ShipNames);
        }

    }

    public static void showDialogException(String e)
    {
        Exception exception= new Exception(e);
        Dialogs.create()
                .owner(myController.getStage())
                .title("Exception Dialog")
                .masthead("Look, an Exception Dialog")
                .message("Ooops, there was an exception!")
                .showException(exception);
    }

    /**
     *
     * @param _service
     * @param armorsRegenFeedUrl
     * @return
     */
    public static CellFeed getFeed(SpreadsheetService _service, URL armorsRegenFeedUrl)
    {
        try {
            return _service.getFeed(armorsRegenFeedUrl, CellFeed.class);
        } catch (IOException e) {
            showDialogException(e.getMessage());
            return null;
        } catch (ServiceException e) {
            showDialogException(e.getMessage());
            return null;
        }
    }

    //Team 1 controls
    public void team1SetRace(ActionEvent actionEvent)  {
        if (team1Race.equals("") && team1ShipLoaded != true)
        {
            team1Race = team1RacePicker.getValue().toString();
            getWorkSpreadsheet("team1");
        }
    }

    public void addShipTeam1(ActionEvent actionEvent)  {
        //Add selected ship to list array
        team1ShipNames.add(team1ShipPicker.getValue().toString());
        //Add selected ship to team1

        ShipObject ship = (team1ShipsMap.get(team1ShipPicker.getValue().toString())).getClone();
        team1Ships.add(ship);
        System.out.println("Added + " + team1Ships.get(team1Ships.size() - 1).getName() + " + ship to team1 collection! Now there is " + team1Ships.size() + " ships");

        updateShipList("team1");

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

            updateShipList("team1");
            System.out.println("Removed ship from team1 collection! Now there is " + team1Ships.size() + " ships");
            team1ShipList.getSelectionModel().select(newSelectedIdx);
        }
    }

    public void addWeaponTeam1(ActionEvent actionEvent) {
        //Add selected weapon to ship
        String weaponName = team1WeaponPicker.getValue().toString();
        if (!weaponName.equals("<Дальняя дистанция>") && !weaponName.equals("<Средняя дистанция>") && !weaponName.equals("<Ближняя дистанция>"))
        {
            ShipObject ship = team1Ships.get(team1CurrentSelectedShip);
            WeaponObject weapon = team1WeaponsMap.get(weaponName).getClone();
			double weaponConsuption = ship.getCurrentWeaponEnergy() + weapon.getEnergyConsumption();
			if (weapon.getDistance() == 1 && ship.closeRangeAllowed() && ship.getMaxWeaponEnergy() >= weaponConsuption)
            {
                ship.addWeapon(weapon);
				ship.setCurrentWeaponEnergy(ship.getCurrentWeaponEnergy() + weapon.getEnergyConsumption());
                System.out.println("Added weapon to team1 ship!");
            } else  if (weapon.getDistance() == 2 && ship.midRangeAllowed() && ship.getMaxWeaponEnergy() >= weaponConsuption)
            {
                ship.addWeapon(weapon);
				ship.setCurrentWeaponEnergy(ship.getCurrentWeaponEnergy() + weapon.getEnergyConsumption());
                System.out.println("Added weapon to team1 ship!");
            }
            else  if (weapon.getDistance() == 3 && ship.longRangeAllowed() && ship.getMaxWeaponEnergy() >= weaponConsuption)
            {
                ship.addWeapon(weapon);
				ship.setCurrentWeaponEnergy(ship.getCurrentWeaponEnergy() + weapon.getEnergyConsumption());
                System.out.println("Added weapon to team1 ship!");
            }
            updateShipInfo(ship, "team1");
        }

    }

    public void deleteSelectedWeaponTeam1(ActionEvent actionEvent) {
        final int selectedIdx = team1WeaponList.getSelectionModel().getSelectedIndex();
        if (selectedIdx != -1) {
            final int newSelectedIdx =
                    (selectedIdx == team1ShipList.getItems().size() - 1)
                            ? selectedIdx - 1
                            : selectedIdx;

            //Remove ship from team collection
            ShipObject ship = team1Ships.get(team1CurrentSelectedShip);
			if (selectedIdx > 0 && selectedIdx < ship.getMidSepartorIndex() - 1) //Close range
			{
				ship.getCloseWeapons().remove(selectedIdx);
				ship.setCurrentWeaponEnergy(ship.getCurrentWeaponEnergy() - ship.getCloseWeapons().get(selectedIdx).getEnergyConsumption());
			} else if (selectedIdx > ship.getMidSepartorIndex() && selectedIdx < ship.getLongSepartorIndex()) // Mid range
			{
				ship.getMidWeapons().remove(selectedIdx - ship.getMidSepartorIndex() - 1);
				ship.setCurrentWeaponEnergy(ship.getCurrentWeaponEnergy() - ship.getMidWeapons().get(selectedIdx).getEnergyConsumption());
			} else if (selectedIdx > ship.getLongSepartorIndex()) // Long range
			{
				ship.getLongWeapons().remove(selectedIdx - ship.getLongSepartorIndex() - 1);
				ship.setCurrentWeaponEnergy(ship.getCurrentWeaponEnergy() - ship.getLongWeapons().get(selectedIdx).getEnergyConsumption());
			}

            ship.updateCurrentWeaponsList();
            updateShipInfo(ship, "team1");

            team1WeaponList.getSelectionModel().select(newSelectedIdx);
        }
    }

    //Team 2 controls
    public void team2SetRace(ActionEvent actionEvent)  {
        if (team2Race.equals("") && team2ShipLoaded != true)
        {
            team2Race = team2RacePicker.getValue().toString();
            getWorkSpreadsheet("team2");
        }
    }

    public void addShipTeam2(ActionEvent actionEvent) {
        //Add selected ship to list array
        team2ShipNames.add(team2ShipPicker.getValue().toString());
        //Add selected ship to team1

        ShipObject ship = (team2ShipsMap.get(team2ShipPicker.getValue().toString())).getClone();
        team2Ships.add(ship);
        System.out.println("Added + " + team2Ships.get(team2Ships.size() - 1).getName() + " + ship to team1 collection! Now there is " + team2Ships.size() + " ships");

        updateShipList("team1");

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

            updateShipList("team2");
            System.out.println("Removed ship from team2 collection! Now there is " + team2Ships.size() + " ships");
            team2ShipList.getSelectionModel().select(newSelectedIdx);
        }
    }

    public void addWeaponTeam2(ActionEvent actionEvent) {
        //Add selected weapon to ship
        String weaponName = team2WeaponPicker.getValue().toString();
        if (!weaponName.equals("<Дальняя дистанция>") && !weaponName.equals("<Средняя дистанция>") && !weaponName.equals("<Ближняя дистанция>"))
        {
            ShipObject ship = team2Ships.get(team2CurrentSelectedShip);
            WeaponObject weapon = team2WeaponsMap.get(weaponName);
			double weaponConsuption = ship.getCurrentWeaponEnergy() + weapon.getEnergyConsumption();
            if (weapon.getDistance() == 1 && ship.closeRangeAllowed() && ship.getMaxWeaponEnergy() >= weaponConsuption)
            {
                ship.addWeapon(weapon);
				ship.setCurrentWeaponEnergy(ship.getCurrentWeaponEnergy() + weapon.getEnergyConsumption());
                System.out.println("Added weapon to team1 ship!");
            }
            else  if (weapon.getDistance() == 2 && ship.midRangeAllowed() && ship.getMaxWeaponEnergy() >= weaponConsuption)
            {
                ship.addWeapon(weapon);
				ship.setCurrentWeaponEnergy(ship.getCurrentWeaponEnergy() + weapon.getEnergyConsumption());
                System.out.println("Added weapon to team1 ship!");
            }
            else  if (weapon.getDistance() == 3 && ship.longRangeAllowed() &&  ship.getMaxWeaponEnergy() >= weaponConsuption)
            {
                ship.addWeapon(weapon);
				ship.setCurrentWeaponEnergy(ship.getCurrentWeaponEnergy() + weapon.getEnergyConsumption());
                System.out.println("Added weapon to team1 ship!");
            }
            updateShipInfo(ship, "team2");
        }


    }

    public void deleteSelectedWeaponTeam2(ActionEvent actionEvent) {
        final int selectedIdx = team2WeaponList.getSelectionModel().getSelectedIndex();
        if (selectedIdx != -1) {
            final int newSelectedIdx =
                    (selectedIdx == team2ShipList.getItems().size() - 1)
                            ? selectedIdx - 1
                            : selectedIdx;

            //Remove ship from team collection
            ShipObject ship = team1Ships.get(team2CurrentSelectedShip);
            if (selectedIdx > 0 && selectedIdx < ship.getMidSepartorIndex() - 1) //Close range
            {
                ship.getCloseWeapons().remove(selectedIdx);
				ship.setCurrentWeaponEnergy(ship.getCurrentWeaponEnergy() - ship.getCloseWeapons().get(selectedIdx).getEnergyConsumption());
            } else if (selectedIdx > ship.getMidSepartorIndex() && selectedIdx < ship.getLongSepartorIndex()) // Mid range
            {
                ship.getMidWeapons().remove(selectedIdx - ship.getMidSepartorIndex() - 1);
				ship.setCurrentWeaponEnergy(ship.getCurrentWeaponEnergy() - ship.getMidWeapons().get(selectedIdx).getEnergyConsumption());
            } else if (selectedIdx > ship.getLongSepartorIndex()) // Long range
            {
                ship.getLongWeapons().remove(selectedIdx - ship.getLongSepartorIndex() - 1);
				ship.setCurrentWeaponEnergy(ship.getCurrentWeaponEnergy() - ship.getLongWeapons().get(selectedIdx).getEnergyConsumption());
            }

            ship.updateCurrentWeaponsList();
            updateShipInfo(ship, "team2");

            team2WeaponList.getSelectionModel().select(newSelectedIdx);
        }
    }


    public void startModeling(ActionEvent actionEvent) {

        myController.setTeam1Ships(team1Ships);
        myController.setTeam2Ships(team2Ships);
        int[] team1Stats = {(int)team1Env.getValue(),(int)team1Will.getValue(), (int)team1Morale.getValue(), (int)team1Reaction.getValue()};
        int[] team2Stats = {(int)team2Env.getValue(),(int)team2Will.getValue(), (int)team2Morale.getValue(), (int)team2Reaction.getValue()};
        myController.setTeam1Stats(team1Stats);
        myController.setTeam2Stats(team2Stats);
        myController.setTime(Integer.valueOf(battleTime.getText()));

        myController.setScreen(Main.getModellingScreenID);

    }



    private static class ShipDataService extends Service<Boolean> {

        private WorksheetEntry shipsWorksheet;
        private SpreadsheetService service;
        private String team;

        public final void setWorksheet(WorksheetEntry _worksheet) {
            this.shipsWorksheet = _worksheet;
        }

        public final void setService(SpreadsheetService _service) {
            this.service = _service;
        }

        public final void setTeam(String _team) {
            this.team = _team;
        }


        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws InterruptedException {
                    //Load data containers
                    Map<String, ShipObject> teamShipsMap = new HashMap<String, ShipObject>();
                    teamShipNamesPicker.clear();

                    //Load dialog
                    updateMessage("Loading ships . . .");
                    updateProgress(0, 41);
                    for (int i = 3; i <= 43; i++) {
                        URL cellFeedUrl = MainScreenController.createURI(shipsWorksheet.getCellFeedUrl().toString(), i, "Ships");
                        CellFeed cellFeed = MainScreenController.getFeed(service, cellFeedUrl);
                        List<CellEntry> cellEntryList = cellFeed.getEntries();
						System.out.println("Adding");
                        try {
							teamShipsMap.put(cellEntryList.get(0).getCell().getValue(),  //Key value by name
									new ShipObject(cellEntryList.get(0).getCell().getValue(), // Ships name
											Integer.valueOf(cellEntryList.get(1).getCell().getValue()), // Ships close range weapon count
											Integer.valueOf(cellEntryList.get(2).getCell().getValue()), // Ships middle range weapon count
											Integer.valueOf(cellEntryList.get(3).getCell().getValue()), // Ships long range weapon count
											MainScreenController.calcShield(cellEntryList.get(4).getCell().getValue(),
													cellEntryList.get(5).getCell().getValue(),
													cellEntryList.get(6).getCell().getValue(),
													team), // Ships shield
											MainScreenController.calcArmor(cellEntryList.get(8).getCell().getValue(),
													cellEntryList.get(9).getCell().getValue(),
													cellEntryList.get(10).getCell().getValue(),
													team), // Ships armor
											MainScreenController.calcShieldRegen(cellEntryList.get(4).getCell().getValue(),
													cellEntryList.get(5).getCell().getValue(),
													cellEntryList.get(6).getCell().getValue(),
													team), // Ships shield regen
											MainScreenController.calcArmorRegen(cellEntryList.get(8).getCell().getValue(),
													cellEntryList.get(9).getCell().getValue(),
													cellEntryList.get(10).getCell().getValue(),
													team), // Ships armor regen
											Integer.valueOf(cellEntryList.get(12).getCell().getValue()), //Mobility
											Double.valueOf(cellEntryList.get(13).getCell().getValue().replace(",", "."))
									));
						}
						catch (Exception e)
						{
							System.out.println(e);
						}

                        updateProgress(i - 2, 41);
                        updateMessage("Loaded " + (i - 2) + " ships!");
                        teamShipNamesPicker.add(cellEntryList.get(0).getCell().getValue());
                        System.out.println("Team 1 - " + cellEntryList.get(0).getCell().getValue());
                    }

                    if (team.equals("team1"))
                        team1ShipsMap = teamShipsMap;
                    else
                        team2ShipsMap = teamShipsMap;

                    updateMessage("ShipsLoaded.");
                    return true;
                }
            };
        }
    }

    private static class WeaponDataService extends Service<Map<String, WeaponObject>> {

        private WorksheetEntry weaponWorksheet;
        private SpreadsheetService service;
        private String team;

        public final void setWorksheet(WorksheetEntry _worksheet) {
            this.weaponWorksheet = _worksheet;
        }

        public final void setService(SpreadsheetService _service) {
            this.service = _service;
        }

        public final void setTeam(String _team) {
            this.team = _team;
        }


        @Override
        protected Task<Map<String, WeaponObject>> createTask() {
            return new Task<Map<String, WeaponObject>>() {
                @Override
                protected Map<String, WeaponObject> call() throws InterruptedException {

                    Map<String, WeaponObject> weaponsMap = new HashMap<String, WeaponObject>();
                    teamWeaponNamesPicker.clear();

                    //Load dialog
                    updateMessage("Loading weapons . . .");
                    updateProgress(0, 26);

                    int index = 0;
                    for (int i = 2; i<= 31; i++) {
                        int distance = 1;
                        if (i > 12)
                            distance = 2;
                        if (i > 22)
                            distance = 3;
						System.out.println("Index = " +  i);
                        if (i != 2 && i != 12 && i != 22)
                        {
							try
							{
								URL cellFeedUrl = createURI(weaponWorksheet.getCellFeedUrl().toString(), i, "Weapons");
								CellFeed cellFeed = getFeed(service, cellFeedUrl);
								List<CellEntry> cellEntryList = cellFeed.getEntries();
								weaponsMap.put(cellEntryList.get(0).getCell().getValue(), // Weapon name key
										new WeaponObject(cellEntryList.get(0).getCell().getValue(),  // Weapon name
												Integer.valueOf(cellEntryList.get(1).getCell().getValue()), // Weapon type
												Double.valueOf(cellEntryList.get(2).getCell().getValue().replace(",",".")), // Weapon damage
												Integer.valueOf(cellEntryList.get(3).getCell().getValue()), // Weapon magazine
												Integer.valueOf(cellEntryList.get(4).getCell().getValue()), // Weapon reload
												distance, // Weapon distance
												Double.valueOf(cellEntryList.get(6).getCell().getValue().replace(",",".")), //wearout
												Double.valueOf(cellEntryList.get(8).getCell().getValue().replace(",",".")), // energy
												Integer.valueOf(cellEntryList.get(9).getCell().getValue()) // rotation
										));
								teamWeaponNamesPicker.add(cellEntryList.get(0).getCell().getValue());

								index ++;

								System.out.println(cellEntryList.get(0).getCell().getValue());
							}
							catch (Exception e)
							{
								updateMessage(e.getMessage());
							}

								updateProgress((index + 1), 26);
								updateMessage("Loaded " + (index + 1) + " weapons!");



                        }
                        else
                        {
                            switch(i){
                                default: System.out.print("");
                                case 2:
                                {
                                    weaponsMap.put("Ближняя дистанция", null);
                                    teamWeaponNamesPicker.add("<Ближняя дистанция>");
                                    System.out.println("Ближняя дистанция");
                                    break;
                                }
                                case 12:
                                {
                                    weaponsMap.put("Средняя дистанция", null);
                                    teamWeaponNamesPicker.add("<Средняя дистанция>");
                                    System.out.println("Средняя дистанция");
                                    break;
                                }
                                case 22:
                                {
                                    weaponsMap.put("Дальняя дистанция", null);
                                    teamWeaponNamesPicker.add("<Дальняя дистанция>");
                                    System.out.println("Дальняя дистанция");
                                    break;
                                }
                            }

                        }
                        System.out.println("teamWeaponNamesPicker size :" + teamWeaponNamesPicker.size());
                    }

                    return weaponsMap;
                }
            };
        }


    }
}
