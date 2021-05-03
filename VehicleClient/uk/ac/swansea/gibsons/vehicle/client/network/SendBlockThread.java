package uk.ac.swansea.gibsons.vehicle.client.network;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.SocketAddress;

import uk.ac.swansea.gibsons.universal.client.types.Block;
import uk.ac.swansea.gibsons.vehicle.client.Client;
import uk.ac.swansea.gibsons.vehicle.client.utils.Output;
/**
 * A thread that sends a block
 * @author 975359
 *
 */
public class SendBlockThread extends Thread {
	private final Block BLOCK;
/**
 * the constructor for a "SendBlockThread"
 * @param b the block to send
 */
	public SendBlockThread(Block b) {
		BLOCK = b;
	}

	@Override
	public void run() {
		//sends a block
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(8192);
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			objectOutputStream.writeObject(BLOCK);
			objectOutputStream.flush();
			ByteArrayOutputStream byteOutputStream1 = new ByteArrayOutputStream();
		    ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(byteOutputStream1);
		    objectOutputStream1.writeObject(BLOCK);
		    objectOutputStream1.flush();
		    objectOutputStream1.close();
		    
		    
			for (SocketAddress socketAddress : Client.getConnectedRSUAddresses()) {
				byte[] buffer = byteStream.toByteArray();
				Output.out(socketAddress.toString());
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, socketAddress);
				Client.getClientThread().getDatagramSocket().send(packet);
			}
			objectOutputStream.close();

		} catch (IOException e) {
			Output.out("error connecting to address: " + e);
		}
	}

}
