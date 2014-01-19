
package main.java;


import io.java.TclGeneratorSimulationData;

import java.io.IOException;
import java.util.List;

import scriptGen.java.ScriptGeneratorDSR;


/**
 * This class generate scripts for all simulation data stored in the {@link TclGeneratorSimulationData}
 * 
 * @author jchaves
 */
public class WirelessNodeSimulationProcessor {

    /**
     * @param data
     * @param wFileDiscriminator
     * @param wirelessProtocol
     * @throws IOException
     */
    public static void createSimulationFiles(List<TclGeneratorSimulationData> data, String wFileDiscriminator,
            String wirelessProtocol) throws IOException {

        for (int i = 0; i < data.size(); i++) {
            TclGeneratorSimulationData simulationPart = data.get(i);
            simulationPart.setFileRadical(wFileDiscriminator + i);
            simulationPart.setWirelessProtocol(wirelessProtocol);
            ScriptGeneratorDSR scriptGenerator = new ScriptGeneratorDSR(simulationPart);
            scriptGenerator.generateScript();
        }

    }
}
