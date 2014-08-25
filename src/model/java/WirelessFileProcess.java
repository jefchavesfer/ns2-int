
package model.java;


import io.java.FileReader;
import io.java.SimulationParams;
import io.java.TclGeneratorSimulationData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import tree.java.DataNode;
import tree.java.FlowData;
import tree.java.SimFlowData;


/**
 * @author jchaves
 */
public class WirelessFileProcess {

    private List<TclGeneratorSimulationData> tclGenSimulationData;
    private SimulationParams simulationParams;
    private Integer numberOfScripts;
    private Integer i;
    private Float timeInterval;
    private Logger log;

    /**
     * @param simulationParams
     * @param numberOfScripts
     */
    public WirelessFileProcess(SimulationParams simulationParams, Integer numberOfScripts) {
        this.simulationParams = simulationParams;
        this.tclGenSimulationData = new ArrayList<TclGeneratorSimulationData>();
        this.numberOfScripts = numberOfScripts;
        this.i = 0;
        this.log = Logger.getLogger(WirelessFileProcess.class.getName());
    }

    /**
     * 
     */
    public void processFile() {
        while (this.i < this.numberOfScripts) {
            String filename = this.simulationParams.getWirelessFileDiscriminator() + this.i + ".tr";
            System.out.println(filename);
            FileReader trFile = new FileReader(filename);
            String trLine;
            boolean first = true;
            TclGeneratorSimulationData tclSimulationData = new TclGeneratorSimulationData();
            String time = null;
            do {
                trLine = trFile.readLine();
                if (trLine != null) {
                    String[] columns = trLine.split(" ");

                    if (columns.length < 8) {
                        // smaller lines that are outside agent package pattern are ignored
                        continue;
                    }

                    String packetStatus = columns[0];
                    time = columns[1];
                    if (Float.valueOf(time) < (this.simulationParams.getInitialTime() + SimulationParams
                            .getTimeOffset())) {
                        // reject packages before simulation became stable
                        continue;
                    }
                    if ("s".equals(packetStatus) || "r".equals(packetStatus) || "D".equals(packetStatus)) {
                        if (first) {
                            tclSimulationData.setT0(Float.valueOf(time) - SimulationParams.getTimeOffset());
                            first = false;
                        }
                        tclSimulationData = this.organizeExperimentData(columns, tclSimulationData);
                    }
                }

            } while (trLine != null);
            trFile.close();
            if (!first) { // there's at least one parseable line
                tclSimulationData.setTf(Float.valueOf(time) - SimulationParams.getTimeOffset());
                this.timeInterval = tclSimulationData.getTf() - tclSimulationData.getT0();
            }
            this.processExperimentData(tclSimulationData);
            tclSimulationData.setNn(this.simulationParams.getNumberOfClusters());
            this.tclGenSimulationData.add(tclSimulationData);
            this.i++;
        }
    }

    private TclGeneratorSimulationData organizeExperimentData(String[] columns,
            TclGeneratorSimulationData tclSimulationData) {

        String packetStatus = columns[0];
        String sPacketNode = columns[2];
        String packetLayer = columns[3];
        String packetNumber = columns[6];
        String packetType = columns[7];
        String packetTime = columns[1];

        // belongs to the wireless domain
        Map<String, SimFlowData> simFlowData = tclSimulationData.getSimFlowMap();
        Map<String, DataNode> queueData = tclSimulationData.getQueueData();
        Map<String, DataNode> droppedData = tclSimulationData.getDroppedData();

        if (("s".equals(packetStatus) || "r".equals(packetStatus)) && "cbr".equals(packetType)
                && "AGT".equals(packetLayer)) {
            SimFlowData simFlow = simFlowData.get(packetNumber);
            if (simFlow == null) {
                simFlow = new SimFlowData();
                simFlowData.put(packetNumber, simFlow);
            }
            String key = packetStatus + sPacketNode;
            Map<String, Float> wirelessTimeData = simFlow.getWirelessFlowData().getTimeData();
            if (wirelessTimeData.get(key) != null) {
                throw new RuntimeException("Fluxo de Pacotes Incoerente");
            }
            wirelessTimeData.put(key, Float.valueOf(packetTime));
            // increment packet sum that passed through a given node
            tclSimulationData.incrNodePacketCountData(sPacketNode);
        }
        if ("D".equals(packetStatus)) {
            DataNode dropData = droppedData.get(packetNumber);
            if (dropData != null) {
                System.out.println("pacote " + packetNumber + " foi dropado de novo");
            } else {
                dropData = new DataNode();
                droppedData.put(packetNumber, dropData);
            }
            droppedData.get(packetNumber).incrNodeData();
        }
        return tclSimulationData;
    }

    private void processExperimentData(TclGeneratorSimulationData tclSimulationData) {
        Map<String, DataNode> throughputData = tclSimulationData.getThroughputData();
        Integer sentPackets = 0;
        Integer receivedPackets = 0;
        Float timeSum = 0f;
        Integer sucessfullDeliveries = 0;
        for (Entry<String, SimFlowData> packetFlow : tclSimulationData.getSimFlowMap().entrySet()) {
            SimFlowData packetData = packetFlow.getValue();
            FlowData wirelessPackageFlowData = packetData.getWirelessFlowData();
            Map<String, Float> timeData = wirelessPackageFlowData.getTimeData();
            // System.out.println(timeData.toString());
            Iterator<Entry<String, Float>> iterator = timeData.entrySet().iterator();
            boolean first = true;

            String beginFlow = null;
            String endFlow = null;
            Entry<String, Float> timeEntry = null;

            while (iterator.hasNext()) {
                // initial time
                timeEntry = iterator.next();
                String key = timeEntry.getKey();
                // deliveryRate
                if (first) {
                    if (key.contains("r")) {
                        // perhaps it is a received package from offset time interval
                        continue;
                    }
                    wirelessPackageFlowData.setInitialTime(timeEntry.getValue());
                    beginFlow = key;
                }
                if (key.contains("s")) {
                    sentPackets++;
                } else if (key.contains("r")) {
                    receivedPackets++;
                }
                first = false;
            }
            if ((timeEntry != null) && (first == false)) {
                endFlow = timeEntry.getKey();
                if (endFlow.contains("s")) {
                    Map<String, DataNode> droppedData = tclSimulationData.getDroppedData();
                    if (!droppedData.containsKey(packetFlow.getKey())) {
                        // queue fail, transmission fail, strange fails
                        DataNode nodeData = new DataNode();
                        nodeData.incrNodeData();
                        droppedData.put(packetFlow.getKey(), nodeData);
                        System.out.println("packet " + packetFlow.getKey() + " shown suspicious transmission failure");
                    }
                    endFlow = "dropped";
                } else if (endFlow.contains("r")) {
                    wirelessPackageFlowData.setEndTime(timeEntry.getValue());
                    sucessfullDeliveries++;
                } else {
                    throw new RuntimeException("Wireless status not known");
                }
            }
            if (wirelessPackageFlowData.getEndTime() != null) {
                wirelessPackageFlowData.calculateTimeInterval();
                timeSum += wirelessPackageFlowData.getTimeInterval();
            }
            if ((beginFlow != null) && (endFlow != null)) {
                String flowString = beginFlow + endFlow;
                DataNode flowData = throughputData.get(flowString);
                if (flowData == null) {
                    flowData = new DataNode();
                    throughputData.put(flowString, flowData);
                }
                flowData.incrNodeData();
            }
        }

        this.log.info("\n===========================================================================" + "\nSimulation "
                + tclSimulationData.getT0() + " " + tclSimulationData.getTf()
                + "\n===========================================================================");

        // calculating mean flow throughput
        for (Map.Entry<String, DataNode> entryFlowData : throughputData.entrySet()) {
            DataNode flowData = entryFlowData.getValue();
            flowData.setFlowRate(1 / (flowData.getNodeData() / this.timeInterval));
            this.log.info("flowRate " + entryFlowData.getKey() + ": " + flowData.getFlowRate());
        }
        // calculating delivery rate
        if (sentPackets != 0) {
            tclSimulationData.setDeliveryRate(receivedPackets.floatValue() / sentPackets.floatValue());
        } else {
            tclSimulationData.setDeliveryRate(0f);
        }
        this.log.info("deliveryRate: " + tclSimulationData.getDeliveryRate());

        // calculating mean delay
        if (sucessfullDeliveries != 0) {
            tclSimulationData.setMeanDelay(timeSum / sucessfullDeliveries);
        } else {
            tclSimulationData.setMeanDelay(0f);
        }
        this.log.info("meanDelay: " + tclSimulationData.getMeanDelay());

        // tclSimulationData.setThroughputData(throughputData);
        // this.tclGenSimulationData.add(tclSimulationData);
        this.log.info(throughputData.toString());
    }

    /**
     * @return List<TclGeneratorSimulationData>
     */
    public List<TclGeneratorSimulationData> getTclGenSimulationData() {
        return this.tclGenSimulationData;
    }

    /**
     * @param tclGenSimulationData
     */
    public void setTclGenSimulationData(List<TclGeneratorSimulationData> tclGenSimulationData) {
        this.tclGenSimulationData = tclGenSimulationData;
    }
}
