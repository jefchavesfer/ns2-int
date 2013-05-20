package main.java;

import java.io.IOException;
import java.util.List;

public class WirelessNodeSimulationProcessor {
	
	public static void createSimulationFiles(List<TclGeneratorSimulationData> data, String wFileDiscriminator) throws IOException{
		
		for(int i = 0; i < data.size(); i ++ ){
			TclGeneratorSimulationData simulationPart = data.get(i);
			simulationPart.setFileRadical(wFileDiscriminator + i);
			ScriptGeneratorDSR scriptGenerator = new ScriptGeneratorDSR(simulationPart);
			scriptGenerator.generateScript();
		}	
		
	}
}
