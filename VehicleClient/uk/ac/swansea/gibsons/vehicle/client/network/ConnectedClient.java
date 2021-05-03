package uk.ac.swansea.gibsons.vehicle.client.network;

import java.net.SocketAddress;

import uk.ac.swansea.gibsons.universal.client.types.ClientInformationPacket.ClientType;

/**
 * A class that stores a connected client
 * @author 975359
 *
 */
public class ConnectedClient {
	private String name;
	private String publicKey;
	private String license;
	private ClientType type;
	private SocketAddress address;
	private long timestampOffset;
	/**
	 * the constructor for a connected client
	 * @param type the type of the client
	 * @param name the name of the client
	 * @param publicKey the public key of the client
	 * @param license the license (provided by the trusted authority) of the client
	 * @param address the socket address of the client
	 * @param timestampOffset the timestamp offset of the client
	 */
	public ConnectedClient(ClientType type,String name, String publicKey,String license, SocketAddress address, long timestampOffset) {
		this.type= type;
		this.name = name;
		this.publicKey = publicKey;
		this.license = license;
		this.address = address;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public String getName() {
		return name;
	}
	public String getLicense() {
		return license;
	}
	public SocketAddress getAddress(){
		return address;
	}
	public long getTimestampOffset(){
		return timestampOffset;
	}
	public String toString(){
		return "["+type+"] "+name;
	}
	
}
