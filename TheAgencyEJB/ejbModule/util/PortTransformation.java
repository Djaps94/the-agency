package util;

public class PortTransformation {

	
	public static String transform(String destination, int offset){
		String[] parts = destination.split(":");
		int port = Integer.parseInt(parts[1]) - 3000 + offset;
		return parts[0]+":"+port;
	}
}
