package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import model.AgentType;

public class PortTransformation {

	
	public static String transform(String destination, int offset){
		String[] parts = destination.split(":");
		int port = Integer.parseInt(parts[1]) - 3000 + offset;
		return parts[0]+":"+port;
	}
	
	public static Set<AgentType> agentTypes(String filename){
		Set<AgentType> set = new HashSet<AgentType>();
		InputStream in = PortTransformation.class.getResourceAsStream(filename);
		BufferedReader br  = new BufferedReader(new InputStreamReader(in));
		List<String> lines = br.lines().collect(Collectors.toList());
		for(String line : lines){
			String[] types = line.split(" ");
			set.add(new AgentType(types[1].trim(), types[0].trim()));
		}
		
		return set;
	}
}
