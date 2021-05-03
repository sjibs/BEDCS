package uk.ac.swansea.gibsons.universal.client.types;

import java.io.Serializable;

/**
 * The class that represents a vehicle's wallet
 * @author 97359
 *
 */
public class Wallet implements Serializable{

	private static final long serialVersionUID = 1L;
	String vehicleID;
	float score = 50;
	/**
	 * a constructor for a wallet
	 * @param vehicleID the ID of the wallet
	 */
	public Wallet(String vehicleID) {
		this.vehicleID = vehicleID;
	}
	/**
	 * sets the score of the wallet
	 * @param score the score of the wallet
	 */
	public void setScore(float score) {
		this.score = score;
		if (score > 99) {
			this.score = 99;
		}
		if (score < 0) {
			this.score = 0;
		}
	}
	/**
	 * gets the score of the wallet
	 * @return the score of the wallet
	 */
	public float getScore(){
		return score;
	}
	/**
	 * gets the ID of the wallet
	 * @return the id of the wallet
	 */
	public String getID(){
		return vehicleID;
	}
}
