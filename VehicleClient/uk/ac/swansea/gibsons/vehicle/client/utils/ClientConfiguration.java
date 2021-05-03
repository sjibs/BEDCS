package uk.ac.swansea.gibsons.vehicle.client.utils;

import uk.ac.swansea.gibsons.universal.client.types.ClientInformationPacket.ClientType;
/**
 * the class that stores the client's configuration
 * @author 975359
 *
 */
public class ClientConfiguration {
	private final ClientType TYPE;
	private final String VERSION;
	private final String NAME;
	private final String PUBLIC_KEY;
	private final String PRIVATE_KEY;
	private final String LICENSE;
	private final int PORT;
	/**
	 * the constructor for the client configuration
	 * @param type the client type
	 * @param version the version that the client is running
	 * @param name the name of the client
	 * @param publicKey the public key of the client
	 * @param privateKey the private key of the client
	 * @param license the license of the client
	 * @param port the port that the client recieves messages on
	 */
	public ClientConfiguration(ClientType type, String version, String name, String publicKey, String privateKey, String license, int port){
		this.TYPE = type;
		this.VERSION= version;
		this.NAME=name;
		this.PUBLIC_KEY=publicKey;
		this.PRIVATE_KEY = privateKey;
		this.LICENSE = license;
		this.PORT=port;
	}
	public ClientType getClientType(){
		return TYPE;
	}
	public String getVersion() {
		return VERSION;
	}
	public String getName() {
		return NAME;
	}
	public String getPublicKey() {
		return PUBLIC_KEY;
	}
	public String getPrivateKey() {
		return PRIVATE_KEY;
	}
	public String getLicense() {
		return LICENSE;
	}
	public int getPort(){
		return PORT;
	}
}
