package dev.racoonlab;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.controlsfx.control.spreadsheet.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;


public class ModellingScreenController implements Initializable, ControlledScreen{

	ScreensController myController;
	boolean isLoaded = false;

	//Contains team 1 ships
	private int[] team1Stats;
	private List<ShipObject> team1Ships = new ArrayList();
	//Contains team 2 ships
	private List<ShipObject> team2Ships = new ArrayList();
	private int[] team2Stats;
	private int battleTime;

	GridBase grid;

	//TODO Change to scaled
	public int ShipMobility = 50;
	public double ShipSignature = 0.5d;
	public double TurretSignature = 0.5d;
	public double TurretMod = 0.5d;

	@FXML
	private AnchorPane modSpreadSheet;
	@FXML
	private TreeView<String> team1TreeView;
	@FXML
	private TreeView<String> team2TreeView;
	@FXML
	private LineChart<Integer,Double> team1DamageChart;
	@FXML
	private LineChart<Integer,Double> team2DamageChart;
	@FXML
	private LineChart<Integer,Double> team1ShieldChart;
	@FXML
	private LineChart<Integer,Double> team2ShieldChart;
	@FXML
	private LineChart<Integer,Double> team1ArmorChart;
	@FXML
	private LineChart<Integer,Double> team2ArmorChart;


	@Override
	public void setScreenParent(ScreensController _screenPage) {
		myController = _screenPage;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	public void loadData(Event event) {
		if (!isLoaded)
		{
			team1Stats = myController.getTeam1Stats();
			team2Stats = myController.getTeam2Stats();
			team1Ships = myController.getTeam1Ships();
			team2Ships = myController.getTeam2Ships();
			battleTime = myController.getTime();

			isLoaded = true;

			startModellingTable();
			loadTreeView();
		}

	}


	private void startModellingTable()
	{
		final ObservableList<ObservableList<SpreadsheetCell>> rows
				= FXCollections.<ObservableList<SpreadsheetCell>>observableArrayList();

		//Adding header
		rows.add(addHeader());
		//Adding data
		for (int time = 1; time <= battleTime; time++) {

			rows.add(calculateSpreadSheet(time));
		}

		grid = new GridBase(battleTime, 1 + team1Ships.size()*3 + team2Ships.size()*3);
		grid.setRows(rows);

		SpreadsheetView spv = new SpreadsheetView(grid);
		spv.setMinSize(724, 500);
		modSpreadSheet.getChildren().add(spv);


	}

	private void loadTreeView() {

		//Team 1 tree view
		TreeItem<String> team1RootItem = new TreeItem<String> ("Team 1 ships");
		team1RootItem.setExpanded(true);
		for (int i = 1; i <= team1Ships.size(); i++) {
			TreeItem<String> item = new TreeItem<String>(team1Ships.get(i - 1).getName());
			team1RootItem.getChildren().add(item);
		}
		team1TreeView.setRoot(team1RootItem);

		team1TreeView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener() {

			@Override
			public void changed(ObservableValue observable, Object oldValue,
								Object newValue) {

				TreeItem<String> selectedItem = (TreeItem<String>) newValue;
				System.out.println("Selected Text : " + selectedItem.getValue() + " Id :" + team1TreeView.getSelectionModel().getSelectedIndex());
				// do what ever you want

				int selectedIndex = team1TreeView.getSelectionModel().getSelectedIndex() - 1;

				ObservableList<XYChart.Series<Integer, Double>> lineChartDataDamage = FXCollections.observableArrayList();
				lineChartDataDamage.add(createDamageChart(selectedIndex));
				team1DamageChart.setData(lineChartDataDamage);
				team1DamageChart.createSymbolsProperty();

				ObservableList<XYChart.Series<Integer, Double>> lineChartDataShield = FXCollections.observableArrayList();
				lineChartDataShield.add(createShieldChart(selectedIndex));
				team1ShieldChart.setData(lineChartDataShield);
				team1ShieldChart.createSymbolsProperty();

				ObservableList<XYChart.Series<Integer, Double>> lineChartDataArmor = FXCollections.observableArrayList();
				lineChartDataArmor.add(createArmorChart(selectedIndex));
				team1ArmorChart.setData(lineChartDataArmor);
				team1ArmorChart.createSymbolsProperty();
			}

		});

		//Team 1 tree view
		TreeItem<String> team2RootItem = new TreeItem<String> ("Team 2 ships");
		team2RootItem.setExpanded(true);
		for (int i = 1; i <= team2Ships.size(); i++) {
			TreeItem<String> item = new TreeItem<String>(team2Ships.get(i - 1).getName());
			team2RootItem.getChildren().add(item);
		}
		team2TreeView.setRoot(team2RootItem);

		team2TreeView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener() {

			@Override
			public void changed(ObservableValue observable, Object oldValue,
								Object newValue) {

				TreeItem<String> selectedItem = (TreeItem<String>) newValue;
				System.out.println("Selected Text : " + selectedItem.getValue());
				// do what ever you want

				int selectedIndex = team2TreeView.getSelectionModel().getSelectedIndex() + team1Ships.size() - 1;

				ObservableList<XYChart.Series<Integer, Double>> lineChartDataDamage = FXCollections.observableArrayList();
				lineChartDataDamage.add(createDamageChart(selectedIndex));
				team2DamageChart.setData(lineChartDataDamage);
				team2DamageChart.createSymbolsProperty();

				ObservableList<XYChart.Series<Integer, Double>> lineChartDataShield = FXCollections.observableArrayList();
				lineChartDataShield.add(createShieldChart(selectedIndex));
				team2ShieldChart.setData(lineChartDataShield);
				team2ShieldChart.createSymbolsProperty();

				ObservableList<XYChart.Series<Integer, Double>> lineChartDataArmor = FXCollections.observableArrayList();
				lineChartDataArmor.add(createArmorChart(selectedIndex));
				team2ArmorChart.setData(lineChartDataArmor);
				team2ArmorChart.createSymbolsProperty();


			}

		});
	}

	public LineChart.Series<Integer, Double> createDamageChart(int _selectedIndex)
	{
		LineChart.Series<Integer, Double> series = new LineChart.Series<Integer, Double>();
		for (int i = 1; i < grid.getRowCount(); i++)
		{
			ObservableList<SpreadsheetCell> cell = grid.getRows().get(i);
			Integer time = Integer.valueOf(cell.get(0).getText());
			Double damage = Double.parseDouble(cell.get(1 + _selectedIndex * 3).getText());
			series.getData().add(new XYChart.Data(time, damage));
		}
		return series;
	}

	public LineChart.Series<Integer, Double> createShieldChart(int _selectedIndex)
	{
		LineChart.Series<Integer, Double> series = new LineChart.Series<Integer, Double>();
		for (int i = 1; i < grid.getRowCount(); i++)
		{
			ObservableList<SpreadsheetCell> cell = grid.getRows().get(i);
			Integer time = Integer.valueOf(cell.get(0).getText());
			Double damage = Double.parseDouble(cell.get(2 + _selectedIndex * 3).getText());
			series.getData().add(new XYChart.Data(time, damage));
		}
		return series;
	}

	public LineChart.Series<Integer, Double> createArmorChart(int _selectedIndex)
	{
		LineChart.Series<Integer, Double> series = new LineChart.Series<Integer, Double>();
		for (int i = 1; i < grid.getRowCount(); i++)
		{
			ObservableList<SpreadsheetCell> cell = grid.getRows().get(i);
			Integer time = Integer.valueOf(cell.get(0).getText());
			Double damage = Double.parseDouble(cell.get(3 + _selectedIndex * 3).getText());
			series.getData().add(new XYChart.Data(time, damage));
		}
		return series;
	}

	private ObservableList<SpreadsheetCell> addHeader ()
	{
		final ObservableList<SpreadsheetCell> cells = FXCollections.<SpreadsheetCell>observableArrayList();
		//Add header
		SpreadsheetCellBase cell = new SpreadsheetCellBase(0, 0, 1, 1);
		cell.setItem("Time");
		cells.add(cell);

		int maxTeam1Index = team1Ships.size() * 3;
		System.out.println("addHeader:maxTeam1Index:" + maxTeam1Index);

		int ind = 0;
		for (int col = 0; col < maxTeam1Index; col++) {

			SpreadsheetCellBase cellDamage = new SpreadsheetCellBase(0, col, 1, 1);
			cellDamage.setItem(team1Ships.get(ind).getName() + ".Damage");
			col++;

			SpreadsheetCellBase cellShield = new SpreadsheetCellBase(0, col, 1, 1);
			cellShield.setItem(team1Ships.get(ind).getName() + ".Shield");
			col++;

			SpreadsheetCellBase cellArmor = new SpreadsheetCellBase(0, col, 1, 1);
			cellArmor.setItem(team1Ships.get(ind).getName() + ".Armor");

			ind++;

			cells.add(cellDamage);
			cells.add(cellShield);
			cells.add(cellArmor);
		}

		int maxTeam2Index = maxTeam1Index + team2Ships.size() * 3;
		System.out.println("addHeader:maxTeam2Index:" + maxTeam2Index);

		ind = 0;
		for (int col = maxTeam1Index; col < maxTeam2Index; col = col + 3) {

			SpreadsheetCellBase cellDamage = new SpreadsheetCellBase(0, col, 1, 1);
			cellDamage.setItem(team2Ships.get(ind).getName() + ".Damage");
			col++;

			SpreadsheetCellBase cellShield = new SpreadsheetCellBase(0, col, 1, 1);
			cellShield.setItem(team2Ships.get(ind).getName() + ".Shield");
			col++;

			SpreadsheetCellBase cellArmor = new SpreadsheetCellBase(0, col, 1, 1);
			cellArmor.setItem(team2Ships.get(ind).getName() + ".Armor");

			ind++;

			cells.add(cellDamage);
			cells.add(cellShield);
			cells.add(cellArmor);
		}
		return cells;
	}


	private ObservableList<SpreadsheetCell> calculateSpreadSheet(int _time)
	{

			List<Double> team1ShipsDamage = new ArrayList<>();
			List<Double> team2ShipsDamage = new ArrayList<>();
			team1ShipsDamage = calculateTeamDamage(_time, "team1");
			team2ShipsDamage = calculateTeamDamage(_time, "team2");

			double team1Damage = 0;
			for (int i = 0; i < team1ShipsDamage.size(); i++)
			{
				team1Damage = team1Damage + team1ShipsDamage.get(i);
			}

			double team2Damage = 0;
			for (int i = 0; i < team2ShipsDamage.size(); i++)
			{
				team2Damage = team2Damage + team2ShipsDamage.get(i);
			}

			//Set current target for team2
			int team1TargetIndex = getTargetedTeamShip("team1");
			if (team1TargetIndex >= 0)
			{
				ShipObject targetedShip = team1Ships.get(team1TargetIndex);
				if (targetedShip.getCurrentShield() > 0)
				{
					if (targetedShip.getCurrentShield() > 0 && targetedShip.getCurrentShield() != targetedShip.getShield())
						targetedShip.setCurrentShield(targetedShip.getCurrentShield() + targetedShip.getShieldRegen());
					targetedShip.setCurrentShield(targetedShip.getCurrentShield() - team2Damage);
					System.out.println(targetedShip.getName() + ":" + targetedShip.getCurrentShield() + "/" + targetedShip.getShield());

				}
				else
				{
					if (targetedShip.getCurrentArmor() > 0 && targetedShip.getCurrentArmor() != targetedShip.getArmor())
						targetedShip.setCurrentArmor(targetedShip.getCurrentArmor() + targetedShip.getArmorRegen());
					targetedShip.setCurrentArmor(targetedShip.getCurrentArmor() - team2Damage);
					System.out.println(targetedShip.getName() + ":" + targetedShip.getCurrentShield() + "/" + targetedShip.getShield());
				}

			}
			else
			{
				//Team1 lost
			}

			//Set current target for team1
			int team2TargetIndex = getTargetedTeamShip("team2");
			if (team2TargetIndex >= 0)
			{
				ShipObject targetedShip = team2Ships.get(team2TargetIndex);
				if (targetedShip.getCurrentShield() > 0)
				{
					if (targetedShip.getCurrentShield() > 0 && targetedShip.getCurrentShield() != targetedShip.getShield())
						targetedShip.setCurrentShield(targetedShip.getCurrentShield() + targetedShip.getShieldRegen());
					targetedShip.setCurrentShield(targetedShip.getCurrentShield() - team1Damage);
					System.out.println(targetedShip.getName() + ":" + targetedShip.getCurrentShield() + "/" + targetedShip.getShield());
				}
				else
				{
					if (targetedShip.getCurrentArmor() > 0 && targetedShip.getCurrentArmor() != targetedShip.getArmor())
						targetedShip.setCurrentArmor(targetedShip.getCurrentArmor() + targetedShip.getArmorRegen());
					targetedShip.setCurrentArmor(targetedShip.getCurrentArmor() - team1Damage);
					System.out.println(targetedShip.getName() + ":" + targetedShip.getCurrentShield() + "/" + targetedShip.getShield());
				}
			}
			else
			{
				//Team2 lost
			}



		final ObservableList<SpreadsheetCell> cells = FXCollections.<SpreadsheetCell>observableArrayList();

		SpreadsheetCellBase cell = new SpreadsheetCellBase(_time, 0, 1, 1);
		cell.setItem(_time);
		cells.add(cell);

		int maxTeam1Index = 1 + team1Ships.size() * 3;
		System.out.println("calculateSpreadSheet:maxTeam1Index:" + maxTeam1Index);
		int ind = 0;
		for (int col = 1; col < maxTeam1Index; col++) {

			SpreadsheetCellBase cellDamage = new SpreadsheetCellBase(_time,  col, 1, 1);
			cellDamage.setItem(team1ShipsDamage.get(ind));
			col++;

			SpreadsheetCellBase cellShield = new SpreadsheetCellBase(_time,  col, 1, 1);
			cellShield.setItem(team1Ships.get(ind).getCurrentShield());
			col++;

			SpreadsheetCellBase cellArmor = new SpreadsheetCellBase(_time, col, 1, 1);
			cellArmor.setItem(team1Ships.get(ind).getCurrentArmor());

			cells.add(cellDamage);
			cells.add(cellShield);
			cells.add(cellArmor);

			ind++;
		}

		int maxTeam2Index = maxTeam1Index + team2Ships.size() * 3;
		System.out.println("calculateSpreadSheet:maxTeam2Index:" + maxTeam2Index);

		ind = 0;
		for (int col = maxTeam1Index; col < maxTeam2Index; col++) {

			SpreadsheetCellBase cellDamage = new SpreadsheetCellBase(_time,  col, 1, 1);
			cellDamage.setItem(team2ShipsDamage.get(ind));
			col++;

			SpreadsheetCellBase cellShield = new SpreadsheetCellBase(_time,  col, 1, 1);
			cellShield.setItem(team2Ships.get(ind).getCurrentShield());
			col++;

			SpreadsheetCellBase cellArmor = new SpreadsheetCellBase(_time, col, 1, 1);
			cellArmor.setItem(team2Ships.get(ind).getCurrentArmor());

			cells.add(cellDamage);
			cells.add(cellShield);
			cells.add(cellArmor);

			ind++;
		}

		return cells;
	}

	public int getTargetedTeamShip(String _team) {
		List<ShipObject> teamShips = new ArrayList<>();
		if (_team.equals("team1")) {
			teamShips = team1Ships;
		}
		else if (_team.equals("team2"))
		{
			teamShips = team2Ships;
		}

		int targetedShipIndex = -1;
		for (int i = 0; i < teamShips.size(); i++)
		{
			if (teamShips.get(i).getCurrentArmor() > 0 || teamShips.get(i).getCurrentShield() > 0)
			{
				targetedShipIndex =  i;
				break;
			}
		}
		return targetedShipIndex;
	}

	public List<Double> calculateTeamDamage(int _time, String _team)
	{
		List<ShipObject> teamShips = new ArrayList<>();
		if (_team.equals("team1")) {
			teamShips = team1Ships;
		}
		else if (_team.equals("team2"))
		{
			teamShips = team2Ships;
		}

		List<Double> shipsDamage = new ArrayList<>();

		for (int i = 0; i < teamShips.size(); i++)
		{
			ShipObject ship = teamShips.get(i);
			double currentDamage = 0;
			if (_time <= 60 && !ship.isDead()) //Long weapon time
			{
				for (int j = 0; j < ship.getLongWeapons().size(); j++)
				{
					WeaponObject weapon = ship.getLongWeapons().get(j);
					currentDamage = currentDamage + proceedTurret(weapon, _team);
				}
			}
			else if (_time > 60 && _time <= 120  && !ship.isDead()) //Mid weapon time
			{
				for (int j = 0; j < ship.getMidWeapons().size(); j++)
				{
					WeaponObject weapon = ship.getMidWeapons().get(j);
					currentDamage = currentDamage + proceedTurret(weapon, _team);
				}
			}
			else if (_time > 120  && !ship.isDead()) //Close weapon time
			{
				for (int j = 0; j < ship.getCloseWeapons().size(); j++)
				{
					WeaponObject weapon = ship.getCloseWeapons().get(j);
					currentDamage = currentDamage + proceedTurret(weapon, _team);
				}
			}
			System.out.println(_team + ":" + currentDamage);
			shipsDamage.add(currentDamage);
		}
		return shipsDamage;
	}

	public double proceedTurret (WeaponObject _weapon , String _team)
	{
		double damage;
		int[] stats = {};
		if (_team.equals("team1")) {
			stats = team1Stats;
		}
		else if (_team.equals("team2"))
		{
			stats = team2Stats;
		}

		if (_weapon.getCurrentMagazine() != 0)
		{

				damage = calculateDamage(_weapon, stats, _team);
				//currentDPS += currentTurret["damage"];
				_weapon.setCurrentMagazine(_weapon.getCurrentMagazine() - 1);

		}
		else
		{
			if (_weapon.getCurrentReloadTime() == 0)
			{

				_weapon.setCurrentReloadTime(_weapon.getReloadTime());
				damage = 0;
				//currentTurretRange.setValue(0);
			}
			else
			{
				_weapon.setCurrentReloadTime(_weapon.getCurrentReloadTime() - 1);

				if (_weapon.getCurrentReloadTime() == 0)
				{
					_weapon.setCurrentMagazine(_weapon.getMagazine());
				}
				damage = 0;
				//currentTurretRange.setValue(0);

			}

		}
		System.out.println("Damage:" + damage);
		return Math.round(damage * 100.0)/100.0;
	}

	public double calculateDamage(WeaponObject _weapon, int[] _teamStats, String _team)
	{
		String attackedTeam = "";
		int mobility = 0;
		if (_team.equals("team1")) {
			attackedTeam = "team2";
			mobility = team2Ships.get(getTargetedTeamShip(attackedTeam)).getMobility();
		}
		else if (_team.equals("team2"))
		{
			attackedTeam = "team1";
			mobility = team1Ships.get(getTargetedTeamShip(attackedTeam)).getMobility();
		}


		int Enviroment = _teamStats[0];
		int WillPower = _teamStats[1];
		int Morale = _teamStats[2];
		int Reaction = _teamStats[3];

		double chanceToHit = Math.pow(0.5f, Math.abs((mobility/_weapon.getRotationSpeed())  * (TurretSignature/ ShipSignature))) + Math.sqrt(WillPower + Morale + Reaction )/ 100 * Enviroment ;
		Random generator = new Random();
		double number = generator.nextDouble();

		System.out.println("Random:" +  number + " Chance:" + chanceToHit );
		if (number < chanceToHit)
		{

			return (chanceToHit + TurretMod) * _weapon.getDamageInSec();
		}
		else
		{
			return 0;
		}
	}
}
