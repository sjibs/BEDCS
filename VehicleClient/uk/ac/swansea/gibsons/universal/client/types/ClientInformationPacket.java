package uk.ac.swansea.gibsons.universal.client.types;

import java.io.Serializable;

import uk.ac.swansea.gibsons.vehicle.client.Client;

/**
 * Received from a vehicle node when a vehicle is requesting to join the network
 * @author 975359
 */
public class ClientInformationPacket implements Serializable{
	public static enum ClientType{
		RSU,
		VEHICLE;
	}
	private static final long serialVersionUID = 1L;
	private final ClientType type;
	private final String name;
	private final String publicKey;
	private final String taSignature;
	private final boolean returnInformation;
	private final long timestamp;
	
	/**
	 * Constructor for a Client Information Packet
	 * @param type The client type that this packet represents
	 * @param name The name of the client
	 * @param publicKey The public Key of the client
	 * @param taSignature The Trusted Authority Signature of the client
	 * @param returnInformation Whether or not the receiving client should return their client information
	 */
	public ClientInformationPacket(ClientType type,String name, String publicKey, String taSignature,boolean returnInformation){
		this.type = type;
		this.name = name;
		this.publicKey = publicKey;
		this.taSignature = taSignature;
		this.returnInformation=returnInformation;
		this.timestamp = Client.getTimestamp();
	}
	/**
	 * gets the name of the client
	 * @return the name of the client
	 */
	public String getName() {
		return name;
	}
	/**
	 * gets the public key of the client
	 * @return the public key of the client
	 */
	public String getPublicKey() {
		return publicKey;
	}
	/**
	 * gets the license for the client
	 * @return the license of the client
	 */
	public String getTASignature() {
		return taSignature;
	}
	/**
	 * gets the type of the client
	 * @return the type of the client
	 */
	public ClientType getType(){
		return type;
	}
	/**
	 * gets a boolean weather the client is requesting the recievers information too
	 * @return true if this client is requesting the recievers information, otherwise false
	 */
	public boolean shouldReturn(){
		return returnInformation;
	}
	/**
	 * gets the timestamp at the time of sending the client information
	 * Useful for getting the timstamp difference between clients when initiating a connection
	 * @return the local timestamp of the client
	 */
	public long getTimestamp(){
		return timestamp;
	}
	
}
