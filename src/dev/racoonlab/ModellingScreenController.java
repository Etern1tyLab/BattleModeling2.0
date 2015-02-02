package dev.racoonlab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class ModellingScreenController implements Initializable, ControlledScreen{

	ScreensController myController;

	//Contains team 1 ships
	private int[] team1Stats;
	private List<ShipObject> team1Ships = new ArrayList();
	//Contains team 2 ships
	private List<ShipObject> team2Ships = new ArrayList();
	private int[] team2Stats;

	@FXML
	private TableView modTable;

	@Override
	public void setScreenParent(ScreensController _screenPage) {
		myController = _screenPage;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		/*team1Stats = myController.getTeam1Stats();
		team2Stats = myController.getTeam2Stats();
		team1Ships = myController.getTeam1Ships();
		team2Ships = myController.getTeam2Ships();

		startModellingTable();*/
	}

	private void startModellingTable() {
		TableColumn firstNameCol = new TableColumn("Team1");
		TableColumn lastNameCol = new TableColumn("Team2");
		TableColumn emailCol = new TableColumn("Email");

		System.out.println(team1Ships.get(0).getName());
		modTable.getColumns().addAll(firstNameCol, lastNameCol, emailCol);
	}

	public void handleGetToken(ActionEvent actionEvent) {
	}

	public void handleSignIn(ActionEvent actionEvent) {
	}
}
