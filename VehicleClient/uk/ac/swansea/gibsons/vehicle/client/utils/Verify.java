package uk.ac.swansea.gibsons.vehicle.client.utils;


import uk.ac.swansea.gibsons.universal.client.types.Block;
import uk.ac.swansea.gibsons.universal.client.types.ClientInformationPacket;
import uk.ac.swansea.gibsons.universal.client.types.VehicleMessagePacket;
import uk.ac.swansea.gibsons.universal.client.types.VehicleTransaction;
import uk.ac.swansea.gibsons.vehicle.client.Client;
/**
 * A utility class used for verifying blocks, transactions, clients, ect.
 * @author 975359
 *
 */
public class Verify {
	/**
	 * verifies a clients join request
	 * @param p the client information packet to verify
	 * @return true if the client join request is valid, false otherwise
	 */
	public static boolean verifyClientJoinRequest(
			ClientInformationPacket p) {
		Output.out(p.getType() + p.getPublicKey());
		return ECDSA.verify(Client.TA_PUBLIC_KEY,
				p.getTASignature(),
				p.getType() + p.getPublicKey());
	}

	/**
	 * verifies a vehicle message
	 * @param p the vehicle message packet to verify
	 * @return true if the vehicle message is valid
	 */
	public static boolean verifyVehicleClientMessage(
			VehicleMessagePacket p) {
		return ECDSA.verify(p.getPublicKey(),
				p.getSignature(), Hash.getHashString(
						p.getMessage() + p.getTimestamp()));
	}

	/**
	 * Verifies the vehicle transaction as a legitimate transaction
	 * 
	 * @param t
	 *            The transaction object to verify
	 * @return a boolean; true if the transaction is valid, false otherwise
	 */
	public static boolean verifyVehicleTransaction(
			VehicleTransaction t) {
		// If the message originator has a valid license and is a registered
		// vehicle
		if (ECDSA.verify(Client.TA_PUBLIC_KEY,
				t.getMessageOriginatorLicense(),
				"VEHICLE" + t.getMessageOriginatorID())) {
			// if the message verifier has a valid license and is a registered
			// vehicle
			if (ECDSA.verify(Client.TA_PUBLIC_KEY,
					t.getMessageVerifierLicense(),
					"VEHICLE" + t.getMessageVerifierID())) {
				// if the message originator has signed the message with their
				// private key
				if (ECDSA.verify(t.getMessageOriginatorID(),
						t.getMessageOriginatorSignature(),
						t.getMessageHash())) {
					// if the message verifier has signed the transaction with
					// their private key
					if (ECDSA.verify(
							t.getMessageVerifierID(),
							t.getMessageVerifierSignature(),
							t.getTransactionID())) {
						// if the message verifier is not trying to verify their
						// own message
						if (!t.getMessageOriginatorID()
								.equals(t
										.getMessageVerifierID())) {
							// if the transaction is not a repeat of a
							// historical transaction
							if (!FileHandler
									.isTransactionRepeated(t
											.getTransactionID())) {
								return true;
							} else {
								Output.out(true,
										"Transaction has already been carried out");
							}
						} else {
							Output.out(true,
									"Message Verifier is Message Originator");
						}
					} else {
						Output.out(true,
								"Message Verifier's Signature is invalid");
					}
				} else {
					Output.out(true,
							"Message Originator's Signature is invalid");
				}
			} else {
				Output.out(true,
						"Message Verifier has invalid license.");
			}
		} else {
			Output.out(true,
					"Message Originator has invalid license.");
		}
		return false;
	}

	/**
	 * Verifies the authenticity of the a block
	 * 
	 * @param b the block to verify
	 * @return true if the block is valid, otherwise false
	 */
	public static boolean verifyBlock(Block b) {
		Output.out("Verifying block: " + b.getBlockHash());
		// generates the block hash for the block b
		String cummulativeHash = b.getPropagatorID()
				+ b.getPreviousBlock();
		System.out.println("2: " + cummulativeHash);
		VehicleTransaction[] transactions = b
				.getTransactions();
		for (int i = 0; i < transactions.length; i++) {
			cummulativeHash = Hash.getHashString(
					cummulativeHash + transactions[i]
							.getTransactionID());
			System.out.println("    "+i+":"+transactions[i].getTransactionID());
		}
		// checks that the generated block hash is the same as the block hash
		// stored in the block
		System.out.println(cummulativeHash);
		System.out.println(b.getBlockHash());
		if (cummulativeHash.equals(b.getBlockHash())) {
			b.getPropagatorSignature();
			// checks that the correct number of transactions are stored within
			// the block
			if (b.getTransactions().length == Client.MEM_MAX) {
				// checks that the block author is allowed to publish a block at
				// this time
				if (!FileHandler.isAuthorOfLastBlocks(
						b.getPreviousBlock(),
						b.getPropagatorID(),
						(Client.getConnectedRSUAddresses()
								.size() / 2))) {
					// checks that the block author is a valid RSU and has a
					// valid license from the TA
					if (ECDSA.verify(Client.TA_PUBLIC_KEY,
							b.getPropagatorLicense(),
							"RSU" + b.getPropagatorID())) {
						// checks that the block author is signed the block with
						// their private key
						if (ECDSA.verify(
								b.getPropagatorID(),
								b.getPropagatorSignature(),
								b.getBlockHash())) {
							// loops through every transaction to see if they
							// are valid
							boolean passedTest = true;
							for (VehicleTransaction t : b
									.getTransactions()) {
								if (!verifyVehicleTransaction(
										t)) {
									passedTest = false;
								}
							}
							// if every transaction is valid
							if (passedTest) {
								Output.out(
										"Block has been"
								+" verified");
								return true;
							} else {
								Output.out(true,
										"Not all transactions"
								+" could be validated");
							}

						} else {
							Output.out(true,
									"Block signature could"
								+" not be validated.");
						}
					} else {
						Output.out(true,
								"Block propagator doesn't"
								+" have a correct license.");
					}
				} else {
					Output.out(true,
							"Block propagator has published"
					+" a block too recently.");
				}

			} else {
				Output.out(true,
						"Incorrect number of transactions in Block.");
			}
		} else {
			Output.out(true,
					"Block hash doesn't match block contents");
		}

		return false;
	}

	public static int getLastPublished(
			String RSUpublicKey) {
		return 0;
	}

}
