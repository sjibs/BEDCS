package uk.ac.swansea.gibsons.vehicle.client.network;


import uk.ac.swansea.gibsons.universal.client.types.Block;
import uk.ac.swansea.gibsons.universal.client.types.Wallet;
import uk.ac.swansea.gibsons.vehicle.client.utils.Output;

/**
 * the class that hosts the methods for sending packets across the network
 * @author 975359
 *
 */
public class NetworkUtilities {
	/**
	 * sends a client information packet to a specified address
	 * @param toAddress the address to send the client information in host:port format
	 * @param returnInformation whether or not the client recieving should send their information back
	 */
	public static void sendClientInformation(String toAddress,boolean returnInformation) {
		String[] address = toAddress.split(":");
		if (address.length >= 2) {
			SendClientInformationThread t = new SendClientInformationThread(address,returnInformation);
			t.start();
		} else {
			Output.out(true, "invalid ip address, please use ip:port format");
		}
	}
	/**
	 * sends a vehicle message to the client
	 * @param message the contents of the message to send
	 * @param validFor the amount of time that the message should be valid for
	 */
	public static void sendVehicleMessage(String message, int validFor) {
		SendVehicleMessageThread t = new SendVehicleMessageThread(message, validFor);
		t.start();
	}
	/**
	 * sends a vehicle trust packet to the connected RSU's
	 * @param message the message to react to
	 * @param trusted whether or not the contents of the message is to be trusted
	 */
	public static void sendVehicleTrust(VehicleMessage message, boolean trusted) {
		SendVehicleTrustThread t = new SendVehicleTrustThread(message,trusted);
		t.start();
	}
	/**
	 * Distributes a block when propagating
	 * @param b the block to broadcast
	 */
	public static void sendBlock(Block b){
		SendBlockThread t = new SendBlockThread(b);
		t.start();
	}
	/**
	 * sends a wallet request all connected RSU's
	 * @param walletID the public key of the owner of the wallet
	 */
	public static void sendWalletRequest(String walletID){
		SendWalletFetchRequestThread t = new SendWalletFetchRequestThread(walletID);
		t.start();
	}
	/**
	 * sends the wallet information to a specified vehicle in response to a wallet request
	 * @param toAddress the vehicle to send the wallet information to
	 * @param w the wallet.
	 */
	public static void sendWalletInformation(String toAddress,Wallet w){
		String[] address = toAddress.split(":");
		if (address.length >= 2) {
			SendWalletThread t = new SendWalletThread(address,w);
			t.start();
		} else {
			Output.out(true, "invalid ip address");
		}
	}
	
}
