package uk.ac.swansea.gibsons.vehicle.client.network;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.SocketAddress;

import uk.ac.swansea.gibsons.universal.client.types.WalletFetchRequest;
import uk.ac.swansea.gibsons.vehicle.client.Client;
import uk.ac.swansea.gibsons.vehicle.client.utils.Output;

/**
 * A thread that sends a wallet fetch request
 * @author 975359
 *
 */
public class SendWalletFetchRequestThread extends Thread {
	private final String WALLET_ID;
	/**
	 * sends a wallet fetch request
	 * @param walletID the ID of the vehicle to get the score of 
	 */
	public SendWalletFetchRequestThread(String walletID) {
		this.WALLET_ID = walletID;
	}

	@Override
	public void run() {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(8192);
		try {
			Output.out("broadcasting a wallet fetch request to all connected rsu clients");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			objectOutputStream.flush();
			objectOutputStream
					.writeObject(new WalletFetchRequest(WALLET_ID));
			objectOutputStream.flush();
			for(SocketAddress socketAddress:Client.getConnectedRSUAddresses()){//TODO:change to getConnectedRSUAddresses()
				byte[] buffer = byteStream.toByteArray();
				Output.out(socketAddress.toString());
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length,socketAddress);
				Client.getClientThread().getDatagramSocket().send(packet);
			}
			objectOutputStream.close();

		} catch (IOException e) {
			Output.out(true,"error sending wallet fetch request: "+e);
		}
	}
}
