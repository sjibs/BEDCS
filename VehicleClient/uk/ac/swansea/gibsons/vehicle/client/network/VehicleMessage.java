package uk.ac.swansea.gibsons.vehicle.client.network;

import java.text.SimpleDateFormat;
import java.util.Date;

import uk.ac.swansea.gibsons.vehicle.client.Client;
import uk.ac.swansea.gibsons.vehicle.client.utils.Output;
/**
 * the class that stores a vehicle message
 * @author 975359
 *
 */
public class VehicleMessage {
	private String publicKey;
	private String message;
	private String signiture;
	private String license;
	private long timestamp;
	private int activeFor;
	/**
	 * the constructor for a vehicle message
	 * @param publicKey the public key of the message originator
	 * @param message the contents of the message
	 * @param license the license of the message originator
	 * @param signiture the signature of the message originator
	 * @param timestamp the timestamp of the message
	 * @param activeFor the amount of time that the message should be active for
	 */
	public VehicleMessage(String publicKey,String message,String license, String signiture,long timestamp,int activeFor){
		this.publicKey = publicKey;
		this.message = message;
		this.license = license;
		this.signiture= signiture;
		this.timestamp = timestamp;
		this.activeFor = activeFor;
	}
	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	
	public boolean isStillActive(){
		return (activeFor+timestamp+Client.getConnectedClient(publicKey).getTimestampOffset()>Client.getTimestamp());
	}
	public String toString(){
		Output.out("Timestamp: "+ timestamp + " offset: "+Client.getConnectedClient(publicKey).getTimestampOffset());
		Date d = new Date((long)(timestamp+Client.getConnectedClient(publicKey).getTimestampOffset()));
		Output.out(d.toString());
		return "["+DATE_FORMAT.format(new Date((long)(timestamp+Client.getConnectedClient(publicKey).getTimestampOffset())))+"] "+message;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public String getMessage() {
		return message;
	}
	public String getSigniture() {
		return signiture;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public int getActiveFor() {
		return activeFor;
	}
	public String getLicense(){
		return license;
	}
	
}
