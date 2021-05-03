package uk.ac.swansea.gibsons.universal.client.types;

import java.io.Serializable;
import java.util.Base64;

import uk.ac.swansea.gibsons.vehicle.client.Client;
import uk.ac.swansea.gibsons.vehicle.client.network.VehicleMessage;
import uk.ac.swansea.gibsons.vehicle.client.utils.ECDSA;
import uk.ac.swansea.gibsons.vehicle.client.utils.Hash;

/**
 * The class that represents a vehicle transaction (when one vehicle rates another)
 * @author 975359
 *
 */
public class VehicleTransaction implements Serializable{

	private static final long serialVersionUID = 1L;
	private final boolean TRANSACTION_AMOUNT;
	private final String MESSAGE_HASH;
	private final String MESSAGE_ORIGINATOR_ID;
	private final String MESSAGE_VERIFIER_ID;
	private final String MESSAGE_ORIGINATOR_LICENSE;
	private final String MESSAGE_VERIFIER_LICENSE;
	private final String MESSAGE_ORIGINATOR_SIGNATURE;
	private final String MESSAGE_VERIFIER_SIGNATURE;
	
	/**
	 * a constructor for a vehicle transaction
	 * @param amount whether or not the transaction is a positive reaction or a negative one
	 * @param m the message that the transaction is refering to
	 */
	public VehicleTransaction(boolean amount,VehicleMessage m){
		TRANSACTION_AMOUNT = amount;
		MESSAGE_HASH = Hash.getHashString(m.getMessage()+m.getTimestamp());
		MESSAGE_ORIGINATOR_ID = m.getPublicKey();
		MESSAGE_VERIFIER_ID = Client.getClientConfiguration().getPublicKey();
		MESSAGE_ORIGINATOR_LICENSE = m.getLicense();
		MESSAGE_VERIFIER_LICENSE = Client.getClientConfiguration().getLicense();
		MESSAGE_ORIGINATOR_SIGNATURE = m.getSigniture();
		MESSAGE_VERIFIER_SIGNATURE = Base64.getEncoder().encodeToString(ECDSA.sign(Client.getClientConfiguration().getPrivateKey(), getTransactionID()));
	}

	/**
	 * another constructor for a vehicle transaction
	 * @param amount whether or not the transaction is a positive reaction or a negative one
	 * @param messageHash the hash of the message that the transaction is refering to
	 * @param messageOriginatorID the message originators public ID
	 * @param messageOriginatorLicense the message originators license
	 * @param messageOriginatorSignature the message originators signature
	 * @param messageVerifierID the message verifiers public ID
	 * @param messageVerifierLicense the message verifiers public authority license
	 * @param messageVerifierSignature the message verifiers signature
	 * @param transactionID the transaction ID
	 */
	public VehicleTransaction(boolean amount, String messageHash, String messageOriginatorID, String messageOriginatorLicense, String messageOriginatorSignature,
			String messageVerifierID, String messageVerifierLicense, String messageVerifierSignature, String transactionID) {
		TRANSACTION_AMOUNT = amount;
		MESSAGE_HASH = messageHash;
		MESSAGE_ORIGINATOR_ID = messageOriginatorID;
		MESSAGE_VERIFIER_ID = messageVerifierID;
		MESSAGE_ORIGINATOR_LICENSE = messageOriginatorLicense;
		MESSAGE_VERIFIER_LICENSE =messageVerifierLicense;
		MESSAGE_ORIGINATOR_SIGNATURE = messageOriginatorSignature;
		MESSAGE_VERIFIER_SIGNATURE= messageVerifierSignature;
		
	}

	/**
	 * gets whether or not the transaction is a positive one
	 * @return true if the transaction is a positive reaction, otherwise false
	 */
	public boolean isPositiveTransaction() {
		return TRANSACTION_AMOUNT;
	}

	/**
	 * gets the message originators ID
	 * @return the message originators ID as a string
	 */
	public String getMessageOriginatorID() {
		return MESSAGE_ORIGINATOR_ID;
	}

	/**
	 * the message verifiers ID
	 * @return the message verifiers public key (ID)
	 */
	public String getMessageVerifierID() {
		return MESSAGE_VERIFIER_ID;
	}

	/**
	 * the message verifiers Originator License
	 * @return the message originators License
	 */
	public String getMessageOriginatorLicense() {
		return MESSAGE_ORIGINATOR_LICENSE;
	}

	/**
	 * the message originators signature
	 * @return the message originators signature
	 */
	public String getMessageOriginatorSignature() {
		return MESSAGE_ORIGINATOR_SIGNATURE;
	}

	/**
	 * gets the message verifiers license
	 * @return the message verifiers license
	 */
	public String getMessageVerifierLicense() {
		return MESSAGE_VERIFIER_LICENSE;
	}

	/**
	 * gets the message verifiers signature
	 * @return the message verifiers signature
	 */
	public String getMessageVerifierSignature() {
		return MESSAGE_VERIFIER_SIGNATURE;
	}
	/**
	 * gets the hash (ID) of the message that the transaction is referring to
	 * @return the message hash
	 */
	public String getMessageHash(){
		return MESSAGE_HASH;
	}
	/**
	 * gets the transaction ID
	 * @return the transaction ID
	 */
	public String getTransactionID(){
		return Hash.getHashString(MESSAGE_HASH+MESSAGE_VERIFIER_ID+TRANSACTION_AMOUNT);
	}
	/**
	 * gets a string representation of the transaction
	 */
	public String toString(){
		return getTransactionID();
	}

	/**
	 * gets the amount (positive or negative) that this transaction represents
	 * @return whether or not the transaction is a positive or negative reaction
	 */
	public boolean getAmount() {
		return TRANSACTION_AMOUNT;
	}
}
