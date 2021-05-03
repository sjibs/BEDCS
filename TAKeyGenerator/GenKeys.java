import java.security.KeyPair;
import java.util.Base64;

public class GenKeys {
	final static String TAPrivateKey = "MD4CAQAwEAYHKoZIzj0CAQYFK4EEAAoEJzAlAgEBBCBKvJdwt+isAPVR2ZBAsdiEwfCKWiok9TpQYe688fsrlw==";
	final static String TAPublicKey = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAElWHuSs4LTuw+DmeXGNE8EYEsxMpMNqsG9sHGOziioMjLONyT/dqG1LfGk9A/TLRTFQC9hoWJz1GDXYnhYDR+MA==";
	
	public static void main(String[] args){
		String name = "Vehicle3";
		//outputs key information required to create a client node
		KeyPair kp = ECDSA.genKeys();
		System.out.println(name + ":");
		System.out.println("------------------");
		System.out.println("Private Key: " + Base64.getEncoder().encodeToString(kp.getPrivate().getEncoded()));
		System.out.println("Public Key: " + Base64.getEncoder().encodeToString(kp.getPublic().getEncoded()));
		String k = (Base64.getEncoder().encodeToString(kp.getPublic().getEncoded()));
		String j = Base64.getEncoder().encodeToString(ECDSA.sign(TAPrivateKey, "RSU"+k));
		System.out.println("TA License: " + j);
		System.out.println(ECDSA.verify(TAPublicKey, j, "RSU"+k));
	}
}
