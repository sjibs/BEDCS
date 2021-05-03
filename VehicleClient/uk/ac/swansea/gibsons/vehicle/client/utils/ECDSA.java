package uk.ac.swansea.gibsons.vehicle.client.utils;


import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
/**
 * A utility class that runs the various functions required to perform ECDSA
 * @author 975359
 */
public class ECDSA {
/**
 * verifies text that has been signed using ECDSA	
 * @param publicKeyString the public key of the signer
 * @param signature the signature that was supposedly produced when ECDSA was performed
 * @param plaintext the text that was supposedly signed
 * @return true if the signature is valid, false otherwise
 */
	public static boolean verify(String publicKeyString, String signature,String plaintext){
		try{
			Signature sig = Signature.getInstance("SHA256withECDSA");
			EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));
			KeyFactory keyFactory = KeyFactory.getInstance("EC");
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
			sig.initVerify(publicKey);
			sig.update(plaintext.getBytes("UTF-8"));
			return sig.verify(Base64.getDecoder().decode(signature));
		}catch(NoSuchAlgorithmException | InvalidKeyException | SignatureException | UnsupportedEncodingException | InvalidKeySpecException e){
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Signs a plaintext message as a string
	 * @param publicKey the public key of the signer
	 * @param privateKey the private key to sign the message with
	 * @param plaintext the message to sign
	 * @return the signed message
	 */
	public static byte[] sign(PrivateKey privateKey,String plaintext){
		try {
			Signature sig = Signature.getInstance("SHA256withECDSA");
			sig.initSign(privateKey);
			sig.update(plaintext.getBytes("UTF-8"));
			byte[] signature = sig.sign();
			return signature;
		} catch (InvalidKeyException | SignatureException | UnsupportedEncodingException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Signs a plaintext message as a string
	 * @param stringPublicKey the public key of the signer as a string
	 * @param stringPrivateKey the private key, as a string, to sign the message with
	 * @param plaintext the message to sign
	 * @return the signed message
	 */
	public static byte[] sign(String stringPrivateKey,String plaintext){
		try {
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(stringPrivateKey));
			KeyFactory kf = KeyFactory.getInstance("EC");
			PrivateKey privateKey= kf.generatePrivate(privateKeySpec);
			Signature sig = Signature.getInstance("SHA256withECDSA");
			sig.initSign(privateKey);
			sig.update(plaintext.getBytes("UTF-8"));
			byte[] signature = sig.sign();
			return signature;
		} catch (InvalidKeyException | SignatureException | UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			Output.out(true,"a problem occurred when signing message: "+e);
		}
		return null;
	}
}
