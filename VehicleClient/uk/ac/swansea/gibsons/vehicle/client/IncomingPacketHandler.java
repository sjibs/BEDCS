package uk.ac.swansea.gibsons.vehicle.client;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;

import uk.ac.swansea.gibsons.universal.client.types.Block;
import uk.ac.swansea.gibsons.universal.client.types.ClientInformationPacket;
import uk.ac.swansea.gibsons.universal.client.types.VehicleMessagePacket;
import uk.ac.swansea.gibsons.universal.client.types.VehicleTransaction;
import uk.ac.swansea.gibsons.universal.client.types.Wallet;
import uk.ac.swansea.gibsons.universal.client.types.WalletFetchRequest;
import uk.ac.swansea.gibsons.universal.client.types.ClientInformationPacket.ClientType;
import uk.ac.swansea.gibsons.vehicle.client.network.ConnectedClient;
import uk.ac.swansea.gibsons.vehicle.client.network.NetworkUtilities;
import uk.ac.swansea.gibsons.vehicle.client.network.VehicleMessage;
import uk.ac.swansea.gibsons.vehicle.client.utils.Output;
import uk.ac.swansea.gibsons.vehicle.client.utils.Verify;
/**
 * A class for handling any incoming packets
 * @author 975359
 *
 */
public class IncomingPacketHandler extends Thread {
	private DatagramPacket packet;
	private byte[] buffer;

	public IncomingPacketHandler(DatagramPacket packet, byte[] buffer) {
		this.packet = packet;
		this.buffer = buffer;
	}

	@Override
	public void run() {
		try {
			packet.getLength();
			ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer);
			ObjectInputStream inputStream = new ObjectInputStream(new BufferedInputStream(byteStream));
			Output.out("incoming packet ");
			Object o = inputStream.readObject();
			
			if (Client.getClientConfiguration().getClientType().equals(ClientType.VEHICLE)) {
				if (o instanceof ClientInformationPacket) {
					handleClientInformationPacket((ClientInformationPacket) o);
				} else if (o instanceof VehicleMessagePacket) {
					handleVehicleMessagePacket((VehicleMessagePacket) o);
				} else if (o instanceof Wallet){
					handleWalletInformation((Wallet) o);
				}
			} else {
				if (o instanceof ClientInformationPacket) {
					handleClientInformationPacket((ClientInformationPacket) o);
				} else if (o instanceof VehicleTransaction) {
					handleVehicleTransaction((VehicleTransaction) o);
				} else if (o instanceof Block){
					handleIncomingBlockPacket((Block) o);
				} else if (o instanceof WalletFetchRequest){
					handleIncomingWalletFetchRequest((WalletFetchRequest) o);
				}
			}
			inputStream.close();

		} catch (IOException | ClassNotFoundException e) {
			Output.out(true, "error with connection: " + e);
			e.printStackTrace();
		}
	}

	private void handleWalletInformation(Wallet p) {
		Client.setTrustScore(p.getScore());
		
	}

	private void handleIncomingWalletFetchRequest(
			WalletFetchRequest p) {
		NetworkUtilities.sendWalletInformation(packet.getSocketAddress().toString().replace("/", ""), Client.getWallet(p.getWalletID()));
		
	}
	

	private void handleIncomingBlockPacket(Block b) {
		Client.addBlock(b);
		
	}

	private void handleVehicleTransaction(VehicleTransaction t) {
		
		if(Verify.verifyVehicleTransaction(t)){
			//ensure that sender isn't reactor
			//check if transaction has been included in a previous block
			//if isn't in mempool, add to mempool
			if(!Client.isInMempool(t)){
				Output.out("Added incoming transaction to mempool");
				Client.addToMempool(t);
			}else{
				Output.out(true,"Recieved Transaction is already in mempool");
			}
		}else{
			Output.out(true,"Couldn't verify transaction");
		}
	}

	private void handleClientInformationPacket(ClientInformationPacket p) {
		boolean verified = Verify.verifyClientJoinRequest(p);
		Output.out("client " + p.getName() + (verified ? " verified" : " not verified"));
		if (verified) {
			if (!Client.isClientConnected(p.getPublicKey())) {
				Output.out("first time connection from client " + p.getName());
				Output.out(packet.getSocketAddress().toString());
				if (p.getType().equals(ClientType.VEHICLE)) {
					Client.addVehicleClient(new ConnectedClient(p.getType(), p.getName(), p.getPublicKey(),
							p.getTASignature(), packet.getSocketAddress(),
							p.getTimestamp() - (System.currentTimeMillis() / 1000L)));
				} else {
					Client.addRSUClient(new ConnectedClient(p.getType(), p.getName(), p.getPublicKey(),
							p.getTASignature(), packet.getSocketAddress(),
							p.getTimestamp() - (System.currentTimeMillis() / 1000L)));
				}
				// sends client information back to the connected client
			} else {
				Output.out(p.getName() + " has previously connected ");
			}
			if (p.shouldReturn()) {
				NetworkUtilities.sendClientInformation(packet.getSocketAddress().toString().replace("/", ""), false);
			}

		}
	}

	private void handleVehicleMessagePacket(VehicleMessagePacket p) {
		boolean verified = Verify.verifyVehicleClientMessage(p);
		Output.out("message from " + Client.getConnectedClient(p.getPublicKey()).getName()
				+ (verified ? " verifed" : " not verified"));
		Output.out(p.getMessage());
		if (verified) {
			Client.addMessage(new VehicleMessage(p.getPublicKey(), p.getMessage(), p.getLicense(), p.getSignature(),
					p.getTimestamp(), p.getValidFor()));
		}

	}
}
