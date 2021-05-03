package uk.ac.swansea.gibsons.vehicle.client.network;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.SocketAddress;

import uk.ac.swansea.gibsons.universal.client.types.VehicleTransaction;
import uk.ac.swansea.gibsons.vehicle.client.Client;
import uk.ac.swansea.gibsons.vehicle.client.utils.Output;
/**
 * A thread that sends vehicle transaction
 * @author 975359
 *
 */
public class SendVehicleTrustThread extends Thread {
	private final VehicleMessage message;
	private final boolean trusted;
	/**
	 * the constructor for a "SendVehicleTrustThread"
	 * @param message the message that this transaction is in response to
	 * @param trusted whether or not the message should be trusted
	 */
	public SendVehicleTrustThread(VehicleMessage message,boolean trusted) {
		this.message = message;
		this.trusted = trusted;
	}

	@Override
	public void run() {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(8192);
		try {
			Output.out("broadcasting message to all connected vehicle clients");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			objectOutputStream.flush();
			objectOutputStream
					.writeObject(new VehicleTransaction(trusted,message));
			objectOutputStream.flush();
			for(SocketAddress socketAddress:Client.getConnectedRSUAddresses()){//TODO:change to getConnectedRSUAddresses()
				byte[] buffer = byteStream.toByteArray();
				Output.out(socketAddress.toString());
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length,socketAddress);
				Client.getClientThread().getDatagramSocket().send(packet);
			}
			objectOutputStream.close();

		} catch (IOException e) {
			Output.out(true,"error broadcasting trust: "+e);
		}
	}
}
