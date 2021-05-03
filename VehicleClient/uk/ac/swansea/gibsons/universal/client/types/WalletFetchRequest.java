package uk.ac.swansea.gibsons.universal.client.types;

import java.io.Serializable;

/**
 * the class that is used in order to fetch a wallet from an RSU
 * @author 975359
 *
 */
public class WalletFetchRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String WALLET_ID;
	/**
	 * the constructor for a wallet fetch request
	 * @param walletID the ID of the wallet to fetch
	 */
	public WalletFetchRequest(String walletID){
		this.WALLET_ID = walletID;
	}
	/**
	 * gets the wallet ID
	 * @return the wallet ID
	 */
	public String getWalletID(){
		return WALLET_ID;
	}
}
