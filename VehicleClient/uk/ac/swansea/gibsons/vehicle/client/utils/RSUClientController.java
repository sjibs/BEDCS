package uk.ac.swansea.gibsons.vehicle.client.utils;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import uk.ac.swansea.gibsons.universal.client.types.Block;
import uk.ac.swansea.gibsons.universal.client.types.VehicleTransaction;
import uk.ac.swansea.gibsons.vehicle.client.network.ConnectedClient;
import uk.ac.swansea.gibsons.vehicle.client.network.NetworkUtilities;
/**
 * The controller class for the RSU client
 * @author 975359
 *
 */
public class RSUClientController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Button btnClientConnect;

	@FXML
	private Label lblClientName;

	@FXML
	private Label lblClientType;

	@FXML
	private Label lblConnectedClients;

	@FXML
	private Label lblOutput;

	@FXML
	private ListView<Block> lstBlocks;

	@FXML
	private ListView<VehicleTransaction> lstMempool;

	@FXML
	private ListView<ConnectedClient> lstNearbyClients;

	@FXML
	private ListView<String> lstOutput;

	@FXML
	private TextField txtConnectClientAddress;

	@FXML
	void btnClientConnectPressed(ActionEvent event) {
		NetworkUtilities.sendClientInformation(txtConnectClientAddress.getText(), true);
		txtConnectClientAddress.clear();
	}



	/**
	 * adds a message to the "console" output
	 * @param s the string to output
	 */
	public void addOutput(String s) {
		lstOutput.getItems().add(s);
		
	}
	
	/**
	 * adds a connected client to the list of clients
	 * @param client the client to add
	 */
	public void addConnectedClient(ConnectedClient client) {
		lstNearbyClients.getItems().add(client);
		lblConnectedClients.setText("Nearby Clients (" + lstNearbyClients.getItems().size() + "):");
	}

	/**
	 * sets the clients name
	 * @param name the name that this client operates under
	 */
	public void setClientName(String name) {
		lblClientName.setText(name);
		
	}

	public void addTransaction(VehicleTransaction t) {
		lstMempool.getItems().add(t);
	}
	
	public void setTransactions(Collection<VehicleTransaction> t) {
		lstMempool.getItems().setAll(t);
	}

}
