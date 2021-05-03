package uk.ac.swansea.gibsons.vehicle.client.network;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.SocketAddress;

import uk.ac.swansea.gibsons.universal.client.types.VehicleMessagePacket;
import uk.ac.swansea.gibsons.vehicle.client.Client;
import uk.ac.swansea.gibsons.vehicle.client.utils.Output;
/**
 * A thread that sends vehicle message
 * @author 975359
 *
 */
public class SendVehicleMessageThread extends Thread {
	private String message;
	private int validFor;

	/**
	 * the constructor for a "SendVehicleMessageThread"
	 * @param message the contents of the message to send
	 * @param validFor the amount of time that the message should be valid for
	 */
	public SendVehicleMessageThread(String message,int validFor) {
		this.message = message;
		this.validFor = validFor;
	}

	@Override
	public void run() {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(8192);
		try {
			Output.out("broadcasting message to all connected vehicle clients");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			objectOutputStream.flush();
			objectOutputStream
					.writeObject(new VehicleMessagePacket(message, validFor));
			objectOutputStream.flush();
			for(SocketAddress socketAddress:Client.getConnectedVehicleAddresses()){
				byte[] buffer = byteStream.toByteArray();
				Output.out(socketAddress.toString());
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length,socketAddress);
				Client.getClientThread().getDatagramSocket().send(packet);
			}
			objectOutputStream.close();

		} catch (IOException e) {
			Output.out(true,"error broadcasting message: "+e);
		}
	}
}
