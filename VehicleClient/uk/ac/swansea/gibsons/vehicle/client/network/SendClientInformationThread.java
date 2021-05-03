package uk.ac.swansea.gibsons.vehicle.client.network;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;


import uk.ac.swansea.gibsons.universal.client.types.ClientInformationPacket;
import uk.ac.swansea.gibsons.vehicle.client.Client;
import uk.ac.swansea.gibsons.vehicle.client.utils.ClientConfiguration;
import uk.ac.swansea.gibsons.vehicle.client.utils.Output;

/**
 * A thread that sends the clients information
 * @author 975359
 *
 */
public class SendClientInformationThread extends Thread {
	private String[] address;
	private boolean returnInformation;

	/**
	 * the constructor for a "SendClientInformationThread"
	 * @param address the address to send the client information to
	 * @param returnInformation whether or not the receiving client should return their information
	 */
	public SendClientInformationThread(String[] address,boolean returnInformation) {
		this.address = address;
		this.returnInformation =returnInformation;
	}

	@Override
	public void run() {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(8192);
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			objectOutputStream.flush();
			ClientConfiguration config = Client.getClientConfiguration();
			objectOutputStream
					.writeObject(new ClientInformationPacket(config.getClientType(),config.getName(), config.getPublicKey(), config.getLicense(),returnInformation));
			objectOutputStream.flush();

			byte[] buffer = byteStream.toByteArray();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length,InetAddress.getByName(address[0]) ,
					Integer.parseInt(address[1]));
			Client.getClientThread().getDatagramSocket().send(packet);
			objectOutputStream.close();

		} catch (IOException e) {
			Output.out("error connecting to address: "+e);
		}
	}
}
