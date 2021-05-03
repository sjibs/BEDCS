package uk.ac.swansea.gibsons.vehicle.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import uk.ac.swansea.gibsons.vehicle.client.utils.ClientConfiguration;
import uk.ac.swansea.gibsons.vehicle.client.utils.Output;

/**
 * The thread that the background processes are run from
 * @author 975359
 *
 */
public class ClientThread extends Thread {
	private DatagramSocket datagramSocket = null;
	private boolean running = true;
	private ClientConfiguration config;
	public ClientThread(ClientConfiguration config) {
		this.config = config;
	}

	@Override
	public void run() {

		try {
			datagramSocket = new DatagramSocket(config.getPort());
			Output.out("datagram socket succesfully created on: "+ InetAddress.getLocalHost().getHostAddress()+":"+config.getPort());
			byte[] buffer;
			while(running){
				buffer = new byte[8192];
				DatagramPacket packet = new DatagramPacket(buffer, 8192);
				datagramSocket.receive(packet);
				IncomingPacketHandler packetHandler= new IncomingPacketHandler(packet, buffer);
				packetHandler.start();
			}

		} catch (IOException e) {
			Output.out(true,"failed to accept incoming connection: " +e);
		}

	}

	/**
	 * closes the thread
	 */
	public void close() {
		running = false;
		if (datagramSocket != null) {
			datagramSocket.close();
		}
	}
	/**
	 * gets the datagram socket that is hosting the server
	 * @return the datagramsocket that is hosting the server
	 */
	public DatagramSocket getDatagramSocket(){
		return this.datagramSocket;
	}
}
