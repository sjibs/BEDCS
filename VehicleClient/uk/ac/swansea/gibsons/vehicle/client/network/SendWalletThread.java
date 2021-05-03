package uk.ac.swansea.gibsons.vehicle.client.network;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import uk.ac.swansea.gibsons.universal.client.types.Wallet;
import uk.ac.swansea.gibsons.vehicle.client.Client;
import uk.ac.swansea.gibsons.vehicle.client.utils.Output;

/**
 * A thread that sends a wallet (in response to a fetch request)
 * @author 975359
 *
 */
public class SendWalletThread extends Thread {
	private final Wallet WALLET;
	private final String[] ADDRESS;
	/**
	 * the constructor for a "SendWalletThread"
	 * @param address the address to send the wallet contents to
	 * @param wallet the wallet requested
	 */
	public SendWalletThread(String[] address,Wallet wallet) {
		this.WALLET = wallet;
		ADDRESS= address;
	}

	@Override
	public void run() {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(8192);
		try {
			Output.out("sending wallet information to requesting client");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			objectOutputStream.flush();
			objectOutputStream
					.writeObject(WALLET);
			objectOutputStream.flush();

			byte[] buffer = byteStream.toByteArray();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length,InetAddress.getByName(ADDRESS[0]) ,
					Integer.parseInt(ADDRESS[1]));
			Client.getClientThread().getDatagramSocket().send(packet);
			objectOutputStream.close();


		} catch (IOException e) {
			Output.out(true,"error sending wallet information: "+e);
		}
	}
}
