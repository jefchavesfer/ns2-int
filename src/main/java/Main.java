
package main.java;


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

    private static Integer numberOfNodesInCluster = 9;
    private static Integer numberOfClusters = 4;
    private static Float internalTraffic = 0.5f;
    private static Float externalTraffic = 0.5f;
    private static Integer packetSize = 512;
    private static Float timeInterval = 5.0f;
    private static Float appThroughput = 0.023f;
    private static String wiredBandwidth = "0.15Mb";
    private static String linkDelay = "0.1ms";

    private static Integer wiredQueueSize = 8;
    private static String wiredFileDiscriminator = "wiredSimulation";
    private static Integer initialTime = 0;
    private static Integer endTime = 200;

    private static Integer wirelessQueueSize = 50;
    private static String wirelessFileDiscriminator = "wirelessSimulation";

    private static Float maxRelDifDeliveryRate = 0.02f;
    private static Float maxRelDifMeanDelay = 0.02f;

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {

        SimulationParams simulationParams = new SimulationParams();
        simulationParams.setTimeInterval(timeInterval);
        simulationParams.setNumberOfNodesInCluster(numberOfNodesInCluster);
        simulationParams.setNumberOfClusters(numberOfClusters);
        simulationParams.setInternalTraffic(internalTraffic);
        simulationParams.setExternalTraffic(externalTraffic);
        simulationParams.setPacketSize(packetSize);
        simulationParams.setTimeInterval(timeInterval);
        simulationParams.setAppThroughput(appThroughput);
        simulationParams.setWiredBandwidth(wiredBandwidth);
        simulationParams.setLinkDelay(linkDelay);

        simulationParams.setWiredQueueSize(wiredQueueSize);
        simulationParams.setWiredFileDiscriminator(wiredFileDiscriminator);
        simulationParams.setInitialTime(initialTime);
        simulationParams.setEndTime(endTime);

        simulationParams.setWirelessQueueSize(wirelessQueueSize);
        simulationParams.setWirelessFileDiscriminator(wirelessFileDiscriminator);

        simulationParams.setMaxRelDifDeliveryRate(maxRelDifDeliveryRate);
        simulationParams.setMaxRelDifMeanDelay(maxRelDifMeanDelay);
        simulationParams.setConverged(Boolean.FALSE);

        final Logger log = Logger.getLogger(Main.class.getName());

        // putting flow data inside simulation params
        MakeFlows.make(simulationParams);

        // File for the convergence report
        FileWriter convergenceReport = new FileWriter("convergenceReport.txt");

        while (!simulationParams.isConverged()) {

            WiredScriptGenerator wiredScript = new WiredScriptGenerator(simulationParams);
            wiredScript.generateScript();

            log.info("\n===========================================================================\n"
                    + "Executing wired simulation " + "\n"
                    + "===========================================================================\n");
            final SimulationExecutor wiredShell = new SimulationExecutor();
            wiredShell.executeCommand("ns " + simulationParams.getWiredFileDiscriminator() + ".tcl");
            wiredShell.executeCommand("killall ns");

            FileProcess wiredTrFile = new FileProcess(simulationParams);
            wiredTrFile.processFile();
            List<TclGeneratorSimulationData> wiredSimulationData = wiredTrFile.getTclGenSimulationData();
            int numberOfScripts = wiredSimulationData.size();
            WirelessNodeSimulationProcessor.createSimulationFiles(wiredSimulationData,
                    simulationParams.getWirelessFileDiscriminator());

            for (int i = 0; i < numberOfScripts; i++) {
                log.info("\n===========================================================================\n"
                        + "Executing wireless simulation " + i + "\n"
                        + "===========================================================================\n");
                final SimulationExecutor wirelessShell = new SimulationExecutor();
                wirelessShell.executeCommand("ns " + simulationParams.getWirelessFileDiscriminator() + i + ".tcl");
                wirelessShell.executeCommand("killall ns");
            }

            WirelessFileProcess wirelessTrFile =
                    new WirelessFileProcess(simulationParams.getWirelessFileDiscriminator(), numberOfScripts,
                            simulationParams.getNumberOfClusters());
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
                    new SimulationIterator(wiredSimulationData, wirelessSimulationData, simulationParams,
                            convergenceReport);
            simulationParams = simuIterator.generateNewSimulationParams();
            convergenceReport.flush();
        }
        convergenceReport.close();
    }
}
