package uk.ac.swansea.gibsons.universal.client.types;

import java.io.Serializable;
import java.util.Base64;

import uk.ac.swansea.gibsons.vehicle.client.Client;
import uk.ac.swansea.gibsons.vehicle.client.utils.ECDSA;
import uk.ac.swansea.gibsons.vehicle.client.utils.Hash;

/**
 * the class that represents a vehicle message sent across the network
 * @author 975359
 *
 */
public class VehicleMessagePacket implements Serializable {
	private static final long serialVersionUID = 1L;
	private String publicKey;
	private String message = null;
	private String signature;
	private long timestamp;
	private int activeFor;
	private String license;

	/**
	 * Constructor for a Vehicle Message Packet
	 * @param message the message of the vehicle
	 * @param activeFor the number of milliseconds that the message should be active for
	 */
	public VehicleMessagePacket(String message, int activeFor) {
		this.publicKey = Client.getClientConfiguration().getPublicKey();
		this.timestamp = Client.getTimestamp();
		this.activeFor = activeFor;
		this.message = message;
		this.signature = Base64.getEncoder().encodeToString(ECDSA.sign(Client.getClientConfiguration().getPrivateKey(), Hash.getHashString(message+timestamp)));
		this.license = Client.getClientConfiguration().getLicense();
	}

	/**
	 * gets the public key of the client who sent the message
	 * @return the public key of the client
	 */
	public String getPublicKey() {
		return publicKey;
	}

	/**
	 * gets the message contents
	 * @return the message contents
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * gets the message signature
	 * @return the message signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * get the amount of time that the message is valid for
	 * @return the amount of time that the message is valid for
	 */
	public int getValidFor() {
		return activeFor;
	}

	/**
	 * get the timestamp of the message
	 * @return the local unix timestamp of the client sender
	 */
	public long getTimestamp() {
		return timestamp;
	}
	/**
	 * get the license of the message sender
	 * @return the license of the message sender
	 */
	public String getLicense(){
		return license;
	}
}
