package uk.ac.swansea.gibsons.vehicle.client.utils;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import uk.ac.swansea.gibsons.vehicle.client.Client;
import uk.ac.swansea.gibsons.vehicle.client.network.ConnectedClient;
import uk.ac.swansea.gibsons.vehicle.client.network.NetworkUtilities;
import uk.ac.swansea.gibsons.vehicle.client.network.VehicleMessage;
/**
 * The controller class for the Vehicle Client
 * @author 975359
 *
 */
public class VehicleClientController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Button btnClientConnect;

	@FXML
	private Button btnInspect;

	@FXML
	private Button btnSendMessage;

	@FXML
	private Label lblBroadcastMessage;

	@FXML
	private Label lblClientName;

	@FXML
	private Label lblConnectedClients;

	@FXML
	private Label lblMessages;

	@FXML
	private ListView<ConnectedClient> lstConnectedClients;

	@FXML
	private ListView<VehicleMessage> lstMessages;

	@FXML
	private ListView<String> lstOutput;

	@FXML
	private TextField txtConnectClientAddress;

	@FXML
	private TextArea txtMessage;

	@FXML
	private TextField txtValidFor;

	@FXML
	void btnClientConnectPressed(ActionEvent event) {
		NetworkUtilities.sendClientInformation(txtConnectClientAddress.getText(), true);
		txtConnectClientAddress.clear();
	}

	@FXML
	void btnInspectPressed(ActionEvent event) {
		Client.openMessageInspect(lstMessages.getSelectionModel().getSelectedItem());
	}

	@FXML
	void btnSendMessagePressed(ActionEvent event) {
		if (!txtMessage.getText().equals("")) {
			int i = 0;
			try {
				i = Integer.parseInt(txtValidFor.getText());
			} catch (NumberFormatException e) {}
			if (i > 0) {
				//sends a message if the message is valid
				NetworkUtilities.sendVehicleMessage(txtMessage.getText(), i);
				txtMessage.setText("");
				txtValidFor.setText("");
			} else {
				Output.out(true, "Invalid integer given for \"valid for\"");
			}

		} else {
			Output.out(true, "No message input");
		}
	}

	public void setClientName(String clientName) {
		lblClientName.setText(clientName);
	}

	/**
	 * adds a message to the "console" output
	 * @param s the string to output
	 */
	public void addOutput(String out) {
		if (lstOutput.getItems().size() == 50) {
			lstOutput.getItems().remove(0);
		}
		lstOutput.getItems().add(out);
	}
	/**
	 * adds a connected client to the list of clients
	 * @param client the client to add
	 */
	public void addConnectedClient(ConnectedClient client) {
		lstConnectedClients.getItems().add(client);
		lblConnectedClients.setText("Nearby Clients (" + lstConnectedClients.getItems().size() + "):");
	}
	/**
	 * adds a message to the list of vehicle messages
	 * @param m the message to add
	 */
	public void addMessage(VehicleMessage m) {
		lstMessages.getItems().add(m);

	}
	/**
	 * gets the message selected by the used
	 * @return the message selected by the user
	 */
	public VehicleMessage getMessageSelected() {
		return lstMessages.getSelectionModel().getSelectedItem();
	}

}
