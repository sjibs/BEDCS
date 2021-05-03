package uk.ac.swansea.gibsons.vehicle.client.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import uk.ac.swansea.gibsons.universal.client.types.ClientInformationPacket.ClientType;
import uk.ac.swansea.gibsons.vehicle.client.Client;
/**
 * A class that outputs a formatted message to the client
 * @author 975359
 *
 */
public class Output {
	private final static String ERROR_PREFIX = " [ERROR]: ";
	private final static String INFO_PREFIX = " [INFO]: ";
	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
	/**
	 * outputs to the console and to the client UI
	 * @param error whether or not this message is an error message
	 * @param s the string to output
	 */
	public static synchronized void out(boolean error, String s) {
		String sFormatted = DATE_FORMAT.format(new Date()) + (error ? ERROR_PREFIX : INFO_PREFIX)
				+ s;
		System.out.println(sFormatted);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try{
					if(Client.getClientConfiguration().getClientType().equals(ClientType.VEHICLE)){
						Client.getVehicleClientController().addOutput(sFormatted);
					}else{
						Client.getRSUClientController().addOutput(sFormatted);
					}
				}catch(NullPointerException ex){
					System.out.println(ERROR_PREFIX+"no client controller found");
				}
			}
		});
	}

	/**
	 * outputs an info message to the console and to the client UI
	 * @param s the string to output
	 */
	public static void out(String s) {
		out(false, s);
	}
}
