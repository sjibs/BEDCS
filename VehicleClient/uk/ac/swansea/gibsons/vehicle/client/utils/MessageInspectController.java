package uk.ac.swansea.gibsons.vehicle.client.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import uk.ac.swansea.gibsons.vehicle.client.Client;
import uk.ac.swansea.gibsons.vehicle.client.network.ConnectedClient;
import uk.ac.swansea.gibsons.vehicle.client.network.NetworkUtilities;
import uk.ac.swansea.gibsons.vehicle.client.network.VehicleMessage;

/**
 * The controller class for the message inspect
 * @author 975359
 *
 */
public class MessageInspectController {

    @FXML
    private Label lblMessage;

    @FXML
    private Label lblSenderLicense;

    @FXML
    private Label lblTimeSent;

    @FXML
    private Button btnDisagree;

    @FXML
    private Label lblActiveFor;

    @FXML
    private Label lblMessageSignature;

    @FXML
    private Label lblPublicKey;

    @FXML
    private Label lblTimeOffset;

    @FXML
    private Label lblTrustRating;

    @FXML
    private Label lblSenderName;

    @FXML
    private Button btnAgree;

    @FXML
    void onBtnAgreePressed(ActionEvent event) {
    	NetworkUtilities.sendVehicleTrust(Client.getVehicleClientController().getMessageSelected(), true);
    }

    @FXML
    void onBtnDisagreePressed(ActionEvent event) {
     	NetworkUtilities.sendVehicleTrust(Client.getVehicleClientController().getMessageSelected(), false);
    }
    
    /**
     * fills in all of the labels of the message inspect UI
     * @param m the message to populate the contents with
     */
    public void populateInspector(VehicleMessage m){
    	ConnectedClient c = Client.getConnectedClient(m.getPublicKey());
    	lblSenderName.setText("Name: "+c.getName());
    	lblPublicKey.setText("Public Key: "+c.getPublicKey());
    	lblSenderLicense.setText("License: "+m.getLicense());
    	lblTimeOffset.setText("Time offset: "+c.getTimestampOffset());
    	lblTrustRating.setText("Trust Score: Pending..");
    	lblMessage.setText("Message: "+m.getMessage());
    	lblMessageSignature.setText("Signature: "+m.getSigniture());
    	lblTimeSent.setText("Timestamp: "+m.getTimestamp());
    	lblActiveFor.setText("Active For: "+m.getActiveFor());
    	
    }

    /**
     * sets the score field once it has been fetched from a nearby RSU
     * @param score the score value of the message originator
     */
	public void setScore(String score) {
		lblTrustRating.setText("Trust Score: "+score);
		
	}
}
