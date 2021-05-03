package uk.ac.swansea.gibsons.vehicle.client.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import uk.ac.swansea.gibsons.universal.client.types.ClientInformationPacket.ClientType;
import uk.ac.swansea.gibsons.universal.client.types.Block;
import uk.ac.swansea.gibsons.universal.client.types.VehicleTransaction;
import uk.ac.swansea.gibsons.universal.client.types.Wallet;
import uk.ac.swansea.gibsons.vehicle.client.Client;

/**
 * The class that hosts the file handling methods
 * @author 975359
 *
 */
public class FileHandler {
	public static URL loadResourceURL(String filePath) {
		URL url = FileHandler.class.getResource(filePath);
		if (url == null) {
			url = FileHandler.class.getResource("/" + filePath);
		}
		return url;
	}

	private final static String CONFIG_FILE_NAME = "config.cfg";

	/**
	 * loads the configuration file as a Configuration object
	 * 
	 * @param args
	 *            the program arguments to use
	 * @return the Configuration of the program
	 */
	public static ClientConfiguration loadConfiguration(String[] args) {
		ClientConfiguration config = null;
		File configFile = new File(CONFIG_FILE_NAME);
		if (!configFile.exists()) {
			// creates a new configuration file from the arguments (if possible)
			Output.out("no config file found, creating a new config file.");
			if (args.length < 4) {
				Output.out(true, "not enough arguments create a new config file.");
			} else {
				try {
					configFile.createNewFile();
					FileWriter fw = new FileWriter(configFile);
					fw.write("#Configuration file for A Client\ntype=VEHICLE\nversion=1.0\nname=" + args[0]
							+ "\npublickey=" + args[1] + "\nprivatekey=" + args[2] + "\nlicense=" + args[3] + "\nport="
							+ "40021");
					fw.close();
					config = new ClientConfiguration(ClientType.VEHICLE, "1.0", args[0], args[1], args[2], args[3],
							40021);
				} catch (IOException e) {
					Output.out(true, "an error occurred when creating a new config file: " + e);
				}
			}
		} else {
			// loads the properties of the config file into a config object
			try {
				InputStream inputStream = new FileInputStream(configFile);
				Properties properties = new Properties();
				if (inputStream != null) {
					properties.load(inputStream);
				}
				config = new ClientConfiguration(
						properties.getProperty("type").equals("VEHICLE") ? ClientType.VEHICLE : ClientType.RSU,
						properties.getProperty("version"), properties.getProperty("name"),
						properties.getProperty("publickey"), properties.getProperty("privatekey"),
						properties.getProperty("license"), Integer.parseInt(properties.getProperty("port")));

				inputStream.close();
			} catch (FileNotFoundException e) {
				Output.out(true, "couldn't find the config file: " + e);
			} catch (IOException e) {
				Output.out(true, "an error occurred when reading the config file: " + e);
			}
		}
		return config;
	}


	public final static String CLIENT_STATE_NAME = "client_state.csv";

	/**
	 * saves the client state (the most recent block and all transactions contained within mempool)
	 */
	public static void saveClientState() {
		File f = new File(CLIENT_STATE_NAME);
		try {
			FileWriter fw = new FileWriter(f);
			fw.write(Client.getMostRecentBlock()+"\n");
			for (VehicleTransaction t : Client.getMempool()) {
				fw.write(t.getAmount() + "," + t.getMessageHash() + "," + t.getMessageOriginatorID() + ","
						+ t.getMessageOriginatorLicense() + "," + t.getMessageOriginatorSignature() + ","
						+ t.getMessageVerifierID() + "," + t.getMessageVerifierLicense() + ","
						+ t.getMessageVerifierSignature() + "," + t.getTransactionID() + "\n");
			}
			fw.close();
		} catch (IOException e) {
			Output.out(true, "An error occurred when saving the mempool: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * loads the client state that was previously saved
	 */
	public static void loadMempool() {
		File f = new File(CLIENT_STATE_NAME);
		Output.out("loading mempool from file...");
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			if(line != null){
				Client.setMostRecentBlock(line);
			}
			line = br.readLine();
			while (line != null) {
				String[] splitLine = line.split(",");
				Client.addToMempool(
						new VehicleTransaction(Boolean.parseBoolean(splitLine[0]), splitLine[1], splitLine[2],
								splitLine[3], splitLine[4], splitLine[5], splitLine[6], splitLine[7], splitLine[8]));
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			Output.out(true,"an error occurred when loading the mempool: "+e);
		}
	}
	/**
	 * Saves a block
	 * @param b the block to save
	 */
	public static void saveBlock(Block b){
		File directory = new File("Blocks");
		if (!directory.exists()){
			directory.mkdirs();
		}
		//uses hash string because toBase64 can contain break characters
		File f = new File("Blocks/block_"+toHexString(b.getBlockHash().getBytes())+".csv");
		try {
			FileWriter fw = new FileWriter(f);
			fw.write(b.getBlockHash()+","+b.getPreviousBlock()+","+b.getPropagatorID()+","+b.getPropagatorLicense()+","+b.getPropagatorSignature()+"\n");
			VehicleTransaction[] transactions = b.getTransactions();
			for (VehicleTransaction t : transactions) {
				fw.write(t.getAmount() + "," + t.getMessageHash() + "," + t.getMessageOriginatorID() + ","
						+ t.getMessageOriginatorLicense() + "," + t.getMessageOriginatorSignature() + ","
						+ t.getMessageVerifierID() + "," + t.getMessageVerifierLicense() + ","
						+ t.getMessageVerifierSignature() + "," + t.getTransactionID() + "\n");
			}
			fw.close();
		} catch (IOException e) {
			Output.out(true,"Error saving block");
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if the transaction with an ID is located inside a block file
	 * @param transactionID the ID of the transaction to search
	 * @return a boolean; true if the transaction is located within a block, false otherwise
	 */
	public static boolean isTransactionRepeated(String transactionID){
		if(Client.getMostRecentBlock()==null||Client.getMostRecentBlock().equals("null")){
			return false;
		}
		File directory = new File("Blocks");
		File[] blockFiles = directory.listFiles();
		for(File f:blockFiles){
			try {
				List<String> lines = Files.readAllLines(f.toPath());
			    int first = 1;//the start index of the file to search
			    int last = Client.MEM_MAX;//sets the maximum index to the maximum number of transactions per block
			    int mid;//the middle of the binary search
			    boolean finished = false;
			    while(finished == false){
			    	mid =first + ((last-first)/2);
			    	String[] tID = lines.get(mid).split(",");
			    	if(tID[8].compareTo(transactionID)<0){
			    		first = mid+1;
			    	}else if(tID[8].compareTo(transactionID)>0){
			    		last = mid-1;
			    	}else{
			    		return true;
			    	}
			    	if(first>last){
			    		finished = true;
			    	}
			    }
			} catch (IOException e1) {
				Output.out(true,"error reading block files when searching for transaction");
			}
		}
		return false;
	}
	
	/**
	 * checks that a public key hasn't been the propagator of a block for n blocks
	 * @param prevBlock the hash of the previous block
	 * @param license the license to search
	 * @param n the number of blocks to go back
	 * @return true if the public key has propagated a block within the past n block
	 */
	public static boolean isAuthorOfLastBlocks(String prevBlock, String license, int n){

		try {
			while(n>0 && prevBlock != null && prevBlock != "null"){
				File f = new File("Blocks/block_"+toHexString(prevBlock.getBytes())+".csv");
				BufferedReader br = new BufferedReader(new FileReader(f));
				String[] line = br.readLine().split(",");
				if(line[2].equals(license)){
					br.close();
					return true;
				}
				prevBlock=line[1];
				n--;
				br.close();
			}
			
		} catch (IOException e) {
			Output.out(true,"An error occurred when checking the previous block owners");
		}
		return false;
	}
	/**
	 * used because we cannot save files that contain the character "/"
	 * Method taken from:
	 * https://stackoverflow.com/questions/332079/in-java-how-do-i-convert-a-byte-array-to-a-string-of-hex-digits-while-keeping-l/2197650#2197650
	 * @param bytes the bytes to convert to a hex string
	 * @return the hex string converted from the bytes
	 */
	public static String toHexString(byte[] bytes) {
	    char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j*2] = hexArray[v/16];
	        hexChars[j*2 + 1] = hexArray[v%16];
	    }
	    return new String(hexChars);
	}

	private static final String CLIENT_WALLETS_NAME= "wallets.csv";
	/**
	 * saves the wallets to the wallets.csv, called when an RSU client is closed
	 */
	public static void saveWallets() {
		
		File f = new File(CLIENT_WALLETS_NAME);
		try {
			FileWriter fw = new FileWriter(f);
			for (Wallet w : Client.getWallets()) {
				fw.write(w.getID()+","+w.getScore()+"\n");
			}
			fw.close();
		} catch (IOException e) {
			Output.out(true, "An error occurred when saving the mempool: " + e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the wallets from the wallets.csv file
	 * @return the wallets from the wallets.csv file
	 */
	public static HashMap<String,Wallet> loadWallets(){
		File f = new File(CLIENT_WALLETS_NAME);
		HashMap<String,Wallet>  wallets = new HashMap<String,Wallet>();
		Output.out("loading wallets from file...");
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			while (line != null) {
				String[] splitLine = line.split(",");
				Wallet w = new Wallet(splitLine[0]);
				w.setScore(Integer.parseInt(splitLine[1]));
				wallets.put(splitLine[0],w);
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			Output.out(true,"an error occurred when loading the wallets: "+e);
		}
		return wallets;
	}

}
