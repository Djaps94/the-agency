package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import model.AgentType;

public class AgencyUtil {
		
	public static Set<AgentType> agentTypes(String filename){
		Set<AgentType> set = new HashSet<AgentType>();
		InputStream in 	  	= AgencyUtil.class.getResourceAsStream(filename);
		BufferedReader br  = new BufferedReader(new InputStreamReader(in));
		List<String> lines = br.lines().collect(Collectors.toList());
		for(String line : lines){
			String[] types = line.split(" ");
			set.add(new AgentType(types[1].trim(), types[0].trim()));
		}
		
		return set;
	}
}
