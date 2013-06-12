
package main.java;


import io.java.EntranceParser;
import io.java.SimulationParams;
import io.java.TclGeneratorSimulationData;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import model.java.FileProcess;
import model.java.SimulationExecutor;
import model.java.WirelessFileProcess;
import scriptGen.java.MakeFlows;
import scriptGen.java.WiredScriptGenerator;


/**
 * @author jchaves
 */
class Main {

    private static String paramFileRadical = "parameters";

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {

        final Logger log = Logger.getLogger(Main.class.getName());

        EntranceParser xmlParser = new EntranceParser(paramFileRadical);
        List<SimulationParams> simulationProfiles = xmlParser.parseXML();

        for (SimulationParams simulationProfile : simulationProfiles) {

            // putting flow data inside simulation params
            MakeFlows.make(simulationProfile);

            // File for the convergence report
            FileWriter convergenceReport = new FileWriter("convergenceReport.txt");

            while (!simulationProfile.isConverged()) {

                WiredScriptGenerator wiredScript = new WiredScriptGenerator(simulationProfile);
                wiredScript.generateScript();

                log.info("\n===========================================================================\n"
                        + "Executing wired simulation " + "\n"
                        + "===========================================================================\n");
                final SimulationExecutor wiredShell = new SimulationExecutor();
                wiredShell.executeCommand("ns " + simulationProfile.getWiredFileDiscriminator() + ".tcl");
                wiredShell.executeCommand("killall ns");

                FileProcess wiredTrFile = new FileProcess(simulationProfile);
                wiredTrFile.processFile();
                List<TclGeneratorSimulationData> wiredSimulationData = wiredTrFile.getTclGenSimulationData();
                int numberOfScripts = wiredSimulationData.size();
                WirelessNodeSimulationProcessor.createSimulationFiles(wiredSimulationData,
                        simulationProfile.getWirelessFileDiscriminator());

                for (int i = 0; i < numberOfScripts; i++) {
                    log.info("\n===========================================================================\n"
                            + "Executing wireless simulation " + i + "\n"
                            + "===========================================================================\n");
                    final SimulationExecutor wirelessShell = new SimulationExecutor();
                    wirelessShell.executeCommand("ns " + simulationProfile.getWirelessFileDiscriminator() + i + ".tcl");
                    wirelessShell.executeCommand("killall ns");
                }

                WirelessFileProcess wirelessTrFile =
                        new WirelessFileProcess(simulationProfile.getWirelessFileDiscriminator(), numberOfScripts,
                                simulationProfile.getNumberOfClusters());
                wirelessTrFile.processFile();
                List<TclGeneratorSimulationData> wirelessSimulationData = wirelessTrFile.getTclGenSimulationData();

                for (int i = 0; i < wiredSimulationData.size(); i++) {
                    TclGeneratorSimulationData wiredData = wiredSimulationData.get(i);
                    TclGeneratorSimulationData wirelessData = wirelessSimulationData.get(i);

                    log.info("\n===========================================================================\n"
                            + "Comparisons part " + i + "\n"
                            + "===========================================================================\n");

                    log.info("delivery Date -> wired: " + wiredData.getDeliveryRate() + "; wireless: "
                            + wirelessData.getDeliveryRate());
                    log.info("mean Delay    -> wired: " + wiredData.getMeanDelay() + "; wireless "
                            + wirelessData.getMeanDelay());
                    log.info("flow Data");
                    log.info("wired: " + wiredData.getThroughputData().toString());
                    log.info("wireless: " + wirelessData.getThroughputData().toString());

                }
                SimulationIterator simuIterator =
                        new SimulationIterator(wiredSimulationData, wirelessSimulationData, simulationProfile,
                                convergenceReport);
                simulationProfile = simuIterator.generateNewSimulationParams();
                convergenceReport.flush();
            }
            convergenceReport.close();
        }
    }
}
