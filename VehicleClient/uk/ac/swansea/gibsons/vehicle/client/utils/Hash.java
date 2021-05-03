package uk.ac.swansea.gibsons.vehicle.client.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
/**
 * A class that is used to hash strings
 * @author 975359
 *
 */
public class Hash {
	/**
	 * gets the hash of a sha256 string as an array of bytes
	 * @param s the string to hash
	 * @return the hash of the string as a byte array
	 */
	public static byte[] getHash(String s){
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			try {
				return digest.digest(s.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				Output.out(true,"an error occurred when calculating a hash value: "+e);
				return null;
			}
		} catch (NoSuchAlgorithmException e) {
			Output.out(true,"an error occurred when calculating a hash value: "+e);
		}
		return null;
	}
	/**
	 * converts a string to a sha256 hash stored as a string
	 * @param s the string to hash
	 * @return the string value of the hash created from hashing the string
	 */
	public static String getHashString(String s) {
		return Base64.getEncoder().encodeToString(getHash(s));
	}
}
