package uk.ac.swansea.gibsons.universal.client.types;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

import uk.ac.swansea.gibsons.vehicle.client.Client;
import uk.ac.swansea.gibsons.vehicle.client.utils.ECDSA;
import uk.ac.swansea.gibsons.vehicle.client.utils.Hash;

/**
 * The class that represents a block object
 * @author 975359
 *
 */
public class Block implements Serializable{
	private static final long serialVersionUID = 1L;
	private final String BLOCK_PROPAGATOR_ID;
	private final String BLOCK_PROPAGATOR_LICENSE;
	private final String BLOCK_PROPAGATOR_SIGNATURE;
	private final String BLOCK_HASH;//created from the merkle root of the transactions+
	private final String PREV_BLOCK;
	private final VehicleTransaction[] TRANSACTIONS;
	/**
	 * The constructor for a block
	 * @param transactions an array of all of the transactions to include within the block
	 */
	public Block(VehicleTransaction[] transactions){
		//order all transactions by Transaction ID
		List<VehicleTransaction> tList = Arrays.asList(transactions);
		tList.sort(new VehicleTransactionComparator());
		this.TRANSACTIONS =   tList.toArray(new VehicleTransaction[0]);
		this.BLOCK_PROPAGATOR_LICENSE = Client.getClientConfiguration().getLicense();
		this.BLOCK_PROPAGATOR_ID = Client.getClientConfiguration().getPublicKey();
		this.PREV_BLOCK = Client.getMostRecentBlock()==null?"null":Client.getMostRecentBlock();
		this.BLOCK_HASH = getBlockHash();
		this.BLOCK_PROPAGATOR_SIGNATURE = Base64.getEncoder().encodeToString(ECDSA.sign(Client.getClientConfiguration().getPrivateKey(), BLOCK_HASH));

	}
	/**
	 * gets the blocks hash value
	 * @return gets the value of the block hash
	 */
	public String getBlockHash(){
		//if there isn't already a block hash, generate a new one
		if(BLOCK_HASH != null){
			return BLOCK_HASH;
		}
		String cummulativeHash = this.getPropagatorID()+this.getPreviousBlock();
		System.out.println("1: " + cummulativeHash);
		for (int i = 0; i < TRANSACTIONS.length; i++) {
			cummulativeHash = Hash.getHashString(
					cummulativeHash + TRANSACTIONS[i]
							.getTransactionID());
			System.out.println("    "+i+":"+TRANSACTIONS[i].getTransactionID());
		}
		return cummulativeHash;
	}
	/**
	 * gets the propagator license
	 * @return the license of the block propagator
	 */
	public String getPropagatorLicense(){
		return BLOCK_PROPAGATOR_LICENSE;
	}
	/**
	 * gets the ID of the propagator node
	 * @return the public key (ID) of the block propagator
	 */
	public String getPropagatorID(){
		return BLOCK_PROPAGATOR_ID;
	}
	/**
	 * gets the signature of the block propagator
	 * @return the signature of the block propagator
	 */
	public String getPropagatorSignature(){
		return BLOCK_PROPAGATOR_SIGNATURE;
	}
	/**
	 * gets the hash of the previous block in the chain
	 * @return the hash as a string, of the previous block in the chain
	 */
	public String getPreviousBlock(){
		return PREV_BLOCK;
	}
	/**
	 * gets an array of all of the transactions that have been carried out within the block
	 * @return the array of all transactions that have bene carried out within the block
	 */
	public VehicleTransaction[] getTransactions() {
		return TRANSACTIONS;
		
	}
}
/**
 * A comparator class needed to perform timsort on the vehicle transactions
 * @author 975359
 */
 class VehicleTransactionComparator implements Comparator<VehicleTransaction> {
    @Override
    public int compare(VehicleTransaction t1, VehicleTransaction t2) {
    	return t1.getTransactionID().compareTo(t2.getTransactionID());
    }
 }
