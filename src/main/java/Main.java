
package main.java;


import io.java.EntranceParser;
import io.java.SimulationParams;
import io.java.TclGeneratorSimulationData;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
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
    private static boolean debug = false;

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {
        // avoid putting commas in trace analysis process
        Locale.setDefault(new Locale("en", "US"));

        final Logger log = Logger.getLogger(Main.class.getName());

        EntranceParser xmlParser = new EntranceParser(paramFileRadical);
        List<SimulationParams> simulationProfiles = xmlParser.parseXML();

        // File for the convergence report
        FileWriter convergenceReport = new FileWriter("convergenceReport.txt");
        // Plot Data File
        FileWriter plotDataFile = new FileWriter("plotData.txt");
        int profileIndex = 0;
        for (SimulationParams simulationProfile : simulationProfiles) {
            simulationProfile.setIterations(0);
            String strLog =
                    "\n===========================================================================\n"
                            + "Executing simulation profile " + profileIndex + "\n"
                            + "===========================================================================\n";
            log.info(strLog);
            convergenceReport.write(strLog);

            // putting flow data inside simulation params
            MakeFlows.make(simulationProfile);

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
                        SimulationParams.getTimeOffset());

                for (int i = 0; i < numberOfScripts; i++) {
                    log.info("\n===========================================================================\n"
                            + "Executing wireless simulation " + i + "\n"
                            + "===========================================================================\n");
                    final SimulationExecutor wirelessShell = new SimulationExecutor();
                    wirelessShell.executeCommand("ns " + simulationProfile.getWirelessFileDiscriminator() + i + ".tcl");
                    wirelessShell.executeCommand("killall ns");
                }

                WirelessFileProcess wirelessTrFile = new WirelessFileProcess(simulationProfile, numberOfScripts);
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
                        new SimulationIterator(debug, wiredSimulationData, wirelessSimulationData, simulationProfile,
                                convergenceReport);
                simulationProfile = simuIterator.generateNewSimulationParams();
                convergenceReport.flush();
            }
            String profileData =
                    "Profile: " + profileIndex + "\n" + "Cluster Size:   "
                            + simulationProfile.getNumberOfNodesInCluster() + "\n" + "Cluster Number: "
                            + simulationProfile.getNumberOfClusters() + "\n"
                            + "Internal Traffic External Traffic WiredBandWitdth AppThroughput MeanDelay" + "\n"
                            + simulationProfile.getInternalTraffic() + " " + simulationProfile.getExternalTraffic()
                            + " " + simulationProfile.getWiredBandwidth() + " " + simulationProfile.getAppThroughput()
                            + " " + simulationProfile.getConvergedMeanDelay() + "\n" + "\n";
            plotDataFile.write(profileData);
            plotDataFile.flush();
            profileIndex++;
        }
        convergenceReport.close();
        plotDataFile.close();
    }
}
