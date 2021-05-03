package uk.ac.swansea.gibsons.vehicle.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import uk.ac.swansea.gibsons.universal.client.types.Block;
import uk.ac.swansea.gibsons.universal.client.types.ClientInformationPacket.ClientType;
import uk.ac.swansea.gibsons.universal.client.types.VehicleTransaction;
import uk.ac.swansea.gibsons.universal.client.types.Wallet;
import uk.ac.swansea.gibsons.vehicle.client.network.ConnectedClient;
import uk.ac.swansea.gibsons.vehicle.client.network.NetworkUtilities;
import uk.ac.swansea.gibsons.vehicle.client.network.VehicleMessage;
import uk.ac.swansea.gibsons.vehicle.client.utils.ClientConfiguration;
import uk.ac.swansea.gibsons.vehicle.client.utils.FileHandler;
import uk.ac.swansea.gibsons.vehicle.client.utils.MessageInspectController;
import uk.ac.swansea.gibsons.vehicle.client.utils.Output;
import uk.ac.swansea.gibsons.vehicle.client.utils.RSUClientController;
import uk.ac.swansea.gibsons.vehicle.client.utils.VehicleClientController;
import uk.ac.swansea.gibsons.vehicle.client.utils.Verify;

/**
 * The main client class for both RSU and Vehicle clients
 * @author 975359
 *
 */
public class Client extends Application {

	public final static String TA_PUBLIC_KEY = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAElWHuSs4LTuw+DmeXGNE8EYEsxMpMNqsG9sHGOziioMjLONyT/dqG1LfGk9A/TLRTFQC9hoWJz1GDXYnhYDR+MA==";
	public final static int MEM_MAX = 5;//the max size of the mempool
	private static VehicleClientController vehicleClientController;
	private static RSUClientController rsuClientController;
	private static ClientThread clientThread;
	private static ClientConfiguration config;
	private static HashMap<String, ConnectedClient> connectedClients = new HashMap<String, ConnectedClient>();
	private static HashMap<String, VehicleTransaction> mempool = new HashMap<String, VehicleTransaction>();
	private static Stage primaryStage;
	private static ArrayList<SocketAddress> connectedVehicleAddresses = new ArrayList<SocketAddress>();
	private static ArrayList<SocketAddress> connectedRSUAddresses = new ArrayList<SocketAddress>();
	private static HashMap<String,Wallet> wallets = new HashMap<String,Wallet>();
	private static String mostRecentBlock;
	public static int blockchainLength = 0;
	public static MessageInspectController messageInspectController;
	/**
	 * the main method
	 * @param args the following args are needed to create a client on first launch: [client_name][client_public_key][client_private_key][client_ta_license]
	 */
	public static void main(String[] args) {
		config = FileHandler.loadConfiguration(args);
		if (config == null) {
			Output.out(true,
					"No config file loaded, please run with arguments: [name][publickey][privatekey][signature]");
		} else {
			Client.launch();
		}
	}

	/**
	 * starts the application
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		if (config.getClientType().equals(ClientType.VEHICLE)) {
			loadVehicleClient(primaryStage);
		} else {
			loadRSUClient(primaryStage);
		}
	}

	/**
	 * loads the UI and background processes for an RSU client
	 * @param primaryStage the application stage to load the UI onto
	 * @throws Exception whenever the stage fails to load
	 */
	public void loadRSUClient(Stage primaryStage) throws Exception {
		Client.primaryStage = primaryStage;
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(FileHandler.loadResourceURL("/fxml/RSUClient.fxml"));
		rsuClientController = new RSUClientController();
		loader.setController(rsuClientController);
		AnchorPane root = (AnchorPane) loader.load();
		Scene scene = new Scene(root);
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Client UI");
		rsuClientController.setClientName(config.getName());
		primaryStage.show();
		primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
		clientThread = new ClientThread(config);
		FileHandler.loadMempool();
		wallets = FileHandler.loadWallets();
		clientThread.start();
	}

	/**
	 * loads the UI and background processes for a Vehicle client
	 * @param primaryStage the application stage to load the UI onto
	 * @throws Exception whenever the stage fails to load
	 */
	public void loadVehicleClient(Stage primaryStage) throws Exception {
		Client.primaryStage = primaryStage;
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(FileHandler.loadResourceURL("/fxml/VehicleClient.fxml"));
		vehicleClientController = new VehicleClientController();
		loader.setController(vehicleClientController);
		AnchorPane root = (AnchorPane) loader.load();
		Scene scene = new Scene(root);
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Client UI");
		vehicleClientController.setClientName(config.getName());
		primaryStage.show();
		primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
		clientThread = new ClientThread(config);
		clientThread.start();
	}

	/**
	 * Opens a message inspect window
	 * @param m the message to inspect within the window
	 */
	public static void openMessageInspect(VehicleMessage m) {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(FileHandler.loadResourceURL("/fxml/MessageInspect.fxml"));
			messageInspectController = new MessageInspectController();
			loader.setController(messageInspectController);
			AnchorPane root;
			root = (AnchorPane) loader.load();
			Scene scene = new Scene(root);
			Stage inspectMenuStage = new Stage();
			inspectMenuStage.setTitle("Inspecting Message: " + m.getMessage());
			inspectMenuStage.initModality(Modality.APPLICATION_MODAL);
			inspectMenuStage.initOwner(primaryStage);
			inspectMenuStage.setScene(scene);
			inspectMenuStage.show();
			//sends a request for the trust score of the originator of the message
			NetworkUtilities.sendWalletRequest(m.getPublicKey());
			messageInspectController.populateInspector(m);
		} catch (IOException e) {
			Output.out(true, "Error loading inspect screen: " + e);
		}

	}

	/**
	 * when the application window is closed
	 * @param event the close window event
	 */
	private void closeWindowEvent(WindowEvent event) {
		
		Output.out("closing client...");
		clientThread.close();
		if(config.getClientType().equals(ClientType.RSU)){
			//saves the state of the RSU client
			FileHandler.saveClientState();
			FileHandler.saveWallets();
		}
	}

	/**
	 * Gets the vehicle client controller
	 * @return the vehicle client controller
	 */
	public static VehicleClientController getVehicleClientController() {
		return vehicleClientController;
	}

	/**
	 * gets the client configuration
	 * @return the client configuration
	 */
	public static ClientConfiguration getClientConfiguration() {
		return config;
	}

	/**
	 * gets whether or not a client is connected to this client
	 * @param string the public key of the client to check
	 * @return true if the client is currently connected, false otherwise
	 */
	public static boolean isClientConnected(String string) {
		return connectedClients.containsKey(string);
	}

	/**
	 * adds a vehicle client to the list of connected vehicle clients
	 * @param c the connectedclient to add
	 */
	public static synchronized void addVehicleClient(ConnectedClient c) {
		if (!isClientConnected(c.getPublicKey())) {
			connectedClients.put(c.getPublicKey(), c);
			connectedVehicleAddresses.add(c.getAddress());
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (config.getClientType().equals(ClientType.VEHICLE)) {
						getVehicleClientController().addConnectedClient(c);
					} else {
						getRSUClientController().addConnectedClient(c);
					}
				}
			});

		}
	}

	/**
	 * adds a vehicle client to the list of connected rsu clients
	 * @param c the connectedclient to add
	 */
	public static synchronized void addRSUClient(ConnectedClient c) {
		if (!isClientConnected(c.getPublicKey())) {
			connectedClients.put(c.getPublicKey(), c);
			connectedRSUAddresses.add(c.getAddress());
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (config.getClientType().equals(ClientType.VEHICLE)) {
						getVehicleClientController().addConnectedClient(c);
					} else {
						getRSUClientController().addConnectedClient(c);
					}
				}
			});

		}
	}

	/**
	 * adds a message to the list of messages on the client UI
	 * @param m the message to add to the list
	 */
	public static synchronized void addMessage(VehicleMessage m) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				getVehicleClientController().addMessage(m);
			}
		});

	}
	
	/**
	 * sets the trust score of the inspected wallet node
	 * @param score the amount to set the trust score to
	 */
	public static synchronized void setTrustScore(float score) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				messageInspectController.setScore(score+" ["+(score<32?"Malicious":"Trusted")+"]");
			}
		});

	}

	/**
	 * gets the running client thread
	 * @return the client thread
	 */
	public static ClientThread getClientThread() {
		return clientThread;
	}

	/**
	 * gets the connected vehicle addresses
	 * @return a collection of the collected vehicle addresses
	 */
	public synchronized static Collection<SocketAddress> getConnectedVehicleAddresses() {
		return connectedVehicleAddresses;
	}

	/**
	 * gets the connected RSU addresses
	 * @return a collection of the collected RSU addresses
	 */
	public synchronized static Collection<SocketAddress> getConnectedRSUAddresses() {
		return connectedRSUAddresses;
	}

	/**
	 * gets the current UNIX timestamp of this client
	 * @return the unix timestamp of this client
	 */
	public static long getTimestamp() {
		return System.currentTimeMillis();
	}

	/**
	 * adds a vehicle transaction to the mempool
	 * @param t the transaction to add to the mempool
	 */
	public synchronized static void addToMempool(VehicleTransaction t) {
		mempool.put(t.getTransactionID(), t);
		if(mempool.size() >= MEM_MAX){// and this client is not within the last n/2 block publishers
			Output.out("mem_max size has been met, creating a block from mempool");
			ArrayList<VehicleTransaction> firstN = new ArrayList<VehicleTransaction>();
			for(VehicleTransaction v:mempool.values()){
				if(firstN.size()<MEM_MAX){
					firstN.add(v);
				}
			}
			Block b = new Block(firstN.toArray(new VehicleTransaction[0]));
			addBlock(b);
			NetworkUtilities.sendBlock(b);
		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				rsuClientController.setTransactions(mempool.values());
			}
		});
	}
	
	/**
	 * attempts to add a block to the blockchain, first checking its validity and then adding
	 * @param b the unverified block to check and then add to the blockchain
	 */
	public synchronized static void addBlock(Block b){
		if(Verify.verifyBlock(b)){
			FileHandler.saveBlock(b);
			NetworkUtilities.sendBlock(b);
			mostRecentBlock = b.getBlockHash();
			for(VehicleTransaction t: b.getTransactions()){
				mempool.remove(t.getTransactionID());
				processTransaction(t);
			}
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					rsuClientController.setTransactions(mempool.values());
				}
			});
		}
	}

	/**
	 * processes a transaction
	 * @param t the transaction to process
	 */
	public synchronized static void processTransaction(VehicleTransaction t){
		String messageOriginatorID = t.getMessageOriginatorID();
		String messageVerifierID = t.getMessageVerifierID();
		float verifierScore= 50;
		float originatorScore = 50;
		if(wallets.containsKey(messageOriginatorID)){
			originatorScore = wallets.get(messageOriginatorID).getScore();
		}else{
			originatorScore= 50;
		}
		if(wallets.containsKey(messageVerifierID)){
			verifierScore = wallets.get(messageVerifierID).getScore();
		}else{
			verifierScore= 50;
		}
		
		Wallet originatorWallet = new Wallet(messageOriginatorID);
		Wallet verifierWallet = new Wallet(messageOriginatorID);
		verifierScore-=0.1f;
		verifierWallet.setScore(verifierScore);
		
		if(t.getAmount()){
			//agreement transaction
			originatorWallet.setScore(originatorScore+(verifierScore/100f));
		}else{
			//disagreement transaction
			originatorWallet.setScore(originatorScore-(verifierScore/100f));
		}
		wallets.put(messageOriginatorID, originatorWallet);
		wallets.put(messageVerifierID, verifierWallet);
	}
	/**
	 * checks if a transaction is in the mempool
	 * @param t the transaction to check
	 * @return true if the transaction is in the mempool, false otherwise
	 */
	public synchronized static boolean isInMempool(VehicleTransaction t) {
		return mempool.containsKey(t.getTransactionID());
	}

	/**
	 * checks gets the mempool
	 * @return a collection representing all of the transactions stored within the mempool
	 */
	public synchronized static Collection<VehicleTransaction> getMempool() {
		return mempool.values();
	}

	/**
	 * gets a connected client
	 * @param s the public key of the client to get
	 * @return the connectedclient object associated with the public key provided
	 */
	public synchronized static ConnectedClient getConnectedClient(String s) {
		return connectedClients.get(s);
	}

	/**
	 * gets the RSU client controller
	 * @return the RSU client controller
	 */
	public static RSUClientController getRSUClientController() {
		return rsuClientController;
	}

	/**
	 * gets the most recent blocks block hash
	 * @return the most recent blocks block hash
	 */
	public static String getMostRecentBlock() {
		return mostRecentBlock;
	}
	/**
	 * gets every wallet stored on the system
	 * @return a collection of wallets stored in the system.
	 */
	public static Collection<Wallet> getWallets(){
		return wallets.values();
	}
	/**
	 * gets a wallet from the owners public key
	 * @param id the public key of the wallet owner 
	 * @return the wallet object belonging to the id provided
	 */
	public static Wallet getWallet(String id){
		if(wallets.get(id)!=null){
			return wallets.get(id);
		}
		return new Wallet(id);
	}

	/**
	 * sets the most recent block hash
	 * @param mostRecentBlock the hash of the most recent block
	 */
	public static void setMostRecentBlock(String mostRecentBlock) {
		Client.mostRecentBlock = mostRecentBlock;
	}
}
