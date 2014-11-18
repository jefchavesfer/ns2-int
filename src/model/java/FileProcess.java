
package model.java;


import io.java.FileReader;
import io.java.SimulationParams;
import io.java.TclGeneratorSimulationData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.logging.Logger;

import tree.java.DataNode;
import tree.java.FlowData;
import tree.java.SimFlowData;


/**
 * Process ns-2 wired simulation trace file in order to generate wireless simulation scripts
 * 
 * @author jchaves
 */
public class FileProcess {

    private List<TclGeneratorSimulationData> tclGenSimulationData;
    private SimulationParams simulationParams;
    private Integer n0;
    private Integer numberOfSimulations;
    private Logger log;
    private static String dummyNodeId = "dummy";

    /**
     * @param simulationParams
     */
    public FileProcess(SimulationParams simulationParams) {
        this.simulationParams = simulationParams;
        this.tclGenSimulationData = new ArrayList<TclGeneratorSimulationData>();
        this.log = Logger.getLogger(SimulationParams.class.getName());
        this.numberOfSimulations =
                (int) ((simulationParams.getEndTime() - simulationParams.getInitialTime()) / simulationParams
                        .getTimeInterval());
        Integer x = (int) Math.sqrt(simulationParams.getNumberOfNodesInCluster());
        this.n0 = (((((x + 1) / 2) - 1) * x) + ((x + 1) / 2)) - 1; // works for odd and even x
    }

    /**
     * read wired .tr file and extract line data
     */
    public void processFile() {

        FileReader trFile = new FileReader(this.simulationParams.getWiredFileDiscriminator() + ".tr");
        String trLine;
        TclGeneratorSimulationData tclSimulationData = new TclGeneratorSimulationData();
        // reading all package data and calculating wired and wireless flows.
        do {
            trLine = trFile.readLine();
            if (trLine != null) {
                String[] columns = trLine.split(" ");
                if (Float.valueOf(columns[1]) < (SimulationParams.getTimeOffset() + this.simulationParams
                        .getInitialTime())) {
                    // reject time offset values used for stabilization
                    continue;
                }

                if (columns.length == 12) {
                    tclSimulationData = this.organizeExperimentData(columns, tclSimulationData);
                } else {
                    throw new RuntimeException("Strange simulation line");
                }
            }
        } while (trLine != null);
        trFile.close();

        // sampling and organizing params for wireless tcl generation
        this.generateTclSimulationData(tclSimulationData);
    }

    private Integer timeToSimulationVector(Float time) {
        return (int) ((time - (this.simulationParams.getInitialTime() + SimulationParams.getTimeOffset())) / this.simulationParams
                .getTimeInterval());
    }

    private void setBasicSimulationData(Vector<TclGeneratorSimulationData> simulationData) {
        Float simulationInit = Float.valueOf(this.simulationParams.getInitialTime());
        for (int i = 0; i < this.numberOfSimulations; i++) {
            TclGeneratorSimulationData simulation = new TclGeneratorSimulationData();
            simulation.setNn(this.simulationParams.getNumberOfNodesInCluster());
            simulation.setNc(this.simulationParams.getNumberOfClusters());
            simulation.setN0(this.n0);
            simulation.setNQueue(this.simulationParams.getWirelessQueueSize());
            simulation.setTf(simulationInit + this.simulationParams.getTimeInterval());
            simulation.setT0(simulationInit);
            simulation.setWirelessProtocol(this.simulationParams.getWirelessProtocol());
            simulation.setPacketSize(this.simulationParams.getPacketSize());
            simulation.setWirelessProtocol(this.simulationParams.getWirelessProtocol());
            simulation.setFileRadical(this.simulationParams.getWirelessFileDiscriminator() + i);
            simulation.setTurnOffNodes(this.simulationParams.getTurnOffNodes());
            simulation.setTimeOffSet(SimulationParams.getTimeOffset());
            simulationData.add(i, simulation);
            simulationInit += this.simulationParams.getTimeInterval();
        }
    }

    private void generateTclSimulationData(TclGeneratorSimulationData tclSimulationData) {
        Vector<TclGeneratorSimulationData> wirelessSimulationsVector =
                new Vector<TclGeneratorSimulationData>(this.numberOfSimulations);
        this.setBasicSimulationData(wirelessSimulationsVector);

        Iterator<Entry<String, SimFlowData>> packageIterator = tclSimulationData.getSimFlowMap().entrySet().iterator();

        while (packageIterator.hasNext()) {
            Entry<String, SimFlowData> packageData = packageIterator.next();
            SimFlowData packageFlowData = packageData.getValue();
            FlowData packageWirelessFlowData = packageFlowData.getWirelessFlowData();
            Iterator<Entry<String, Float>> wirelessTimeDataIterator =
                    packageWirelessFlowData.getTimeData().entrySet().iterator();

            boolean first = true;
            boolean dropped = false;
            Integer currentSimulationIndex = null;
            Integer instantSimulationIndex = null;
            String beginFlow = null;
            Entry<String, Float> previousWirelessTimeEntry = null;
            Entry<String, Float> wirelessTimeEntry = null;

            Float wirelessFlowInitialTime = null;
            Float wirelessFlowEndTime = null;
            Float instantWirelessNodeValue = null;
            DataNode flowData = new DataNode();
            while (wirelessTimeDataIterator.hasNext()) {

                // it is a wireless flow package
                // System.out.println(timeData.toString());
                if ((wirelessTimeEntry != null) && wirelessTimeEntry.getKey().contains("-")) {
                    // previous sent package
                    previousWirelessTimeEntry = wirelessTimeEntry;
                }
                wirelessTimeEntry = wirelessTimeDataIterator.next();

                String key = wirelessTimeEntry.getKey();
                String instantWirelessNode = key.substring(1);
                instantWirelessNodeValue = wirelessTimeEntry.getValue();
                instantSimulationIndex = this.timeToSimulationVector(instantWirelessNodeValue);

                if (instantSimulationIndex > (this.numberOfSimulations - 1)) {
                    // over final simulation time, does not neeed to treat this package anymore
                    break;
                }

                TclGeneratorSimulationData wirelessSimulationData =
                        wirelessSimulationsVector.get(instantSimulationIndex);

                if (dropped) {
                    throw new RuntimeException("sanity: how?");
                }

                if (first) {
                    // it is the first package node
                    if (!key.contains("-")) {
                        if (instantSimulationIndex == 0) {
                            // it can be a previous received package from offset time
                            continue;
                        }
                        throw new RuntimeException("first hop is not a enqueuing");
                    }
                    beginFlow = packageFlowData.getFlowData().getSource();
                    flowData.setSource(beginFlow);
                    flowData.setWirelessSourceNode(instantWirelessNode);
                    currentSimulationIndex = this.timeToSimulationVector(instantWirelessNodeValue);
                    wirelessFlowInitialTime = instantWirelessNodeValue;
                    first = false;
                } else if (key.contains("r")) {
                    // treatment for received events

                    if (currentSimulationIndex != instantSimulationIndex) {

                        if (previousWirelessTimeEntry != null) {
                            // at least one sent wireless package

                            // we have a wireless flow that belongs to two or more simulations
                            String previousReceptionKey = previousWirelessTimeEntry.getKey();

                            // sanity test
                            if (!previousReceptionKey.contains("-")) {
                                throw new RuntimeException("current reception key with no sent packet");
                            }

                            // previous flow cycle
                            String previousReceivedWirelessNode = previousReceptionKey.substring(1);
                            if (!flowData.getWirelessSourceNode().equals(previousReceivedWirelessNode)) {
                                // at least one wireless hop

                                String previousFlowString =
                                        flowData.getSource() + " " + flowData.getWirelessSourceNode() + " "
                                                + previousReceivedWirelessNode + " " + FileProcess.dummyNodeId;
                                wirelessFlowEndTime = previousWirelessTimeEntry.getValue();

                                // although the flow is cut by sampling the delivery is successful for THIS simulation
                                wirelessSimulationData.incrSuccessFulDeliveries();

                                // putting the time taken for this sucessful delivery
                                wirelessSimulationData.incrSuccessfulDeliveryTimeSum(wirelessFlowInitialTime,
                                        wirelessFlowEndTime);

                                // incremeting the flow counter
                                Map<String, DataNode> throughputData = wirelessSimulationData.getThroughputData();
                                DataNode existantFlowData = throughputData.get(previousFlowString);
                                if (existantFlowData == null) {
                                    existantFlowData = new DataNode();
                                    throughputData.put(previousFlowString, existantFlowData);
                                    existantFlowData.setSource(flowData.getSource());
                                    existantFlowData.setWirelessSourceNode(flowData.getWirelessSourceNode());
                                    existantFlowData.setDestination(dummyNodeId);
                                    existantFlowData.setWirelessDestinationNode(previousReceivedWirelessNode);
                                }
                                existantFlowData.incrNodeData();

                            } else {
                                this.log.info("simulation cut flow with no hop. Data: "
                                        + packageWirelessFlowData.getTimeData().toString());
                            }
                        }
                        // new packet flow cycle
                        flowData = new DataNode();
                        beginFlow = dummyNodeId;
                        flowData.setSource(beginFlow);
                        flowData.setWirelessSourceNode(instantWirelessNode);
                        wirelessFlowInitialTime = instantWirelessNodeValue;
                        wirelessFlowEndTime = null;
                        currentSimulationIndex = instantSimulationIndex;
                        previousWirelessTimeEntry = null; // in the new simulation there is no previous received package
                    }
                }

                // deliveryRate
                if (key.contains("-")) {
                    wirelessSimulationData.incrSentPackets();
                } else if (key.contains("r")) {
                    wirelessSimulationData.incrReceivedPackets();
                } else if (key.contains("d")) {
                    dropped = true;
                }

            }
            if ((wirelessTimeEntry != null) && (previousWirelessTimeEntry != null) && (first == false)
                    && (instantSimulationIndex != null) && (instantSimulationIndex < this.numberOfSimulations)) {
                // it has wireless flows and at least one received package

                String endFlowKey = wirelessTimeEntry.getKey();
                String endFlowWirelessNode = endFlowKey.substring(1);

                String previousFlowKey = previousWirelessTimeEntry.getKey();
                String previousFlowWirelessNode = previousFlowKey.substring(1);

                // sanity check
                if (endFlowKey.contains("r") && !previousFlowWirelessNode.equals(endFlowWirelessNode)) {
                    String finalDestination = packageFlowData.getFlowData().getDestination();
                    String flowString =
                            flowData.getSource() + " " + flowData.getWirelessSourceNode() + " " + endFlowWirelessNode
                                    + " " + finalDestination;
                    TclGeneratorSimulationData wirelessSimulationData =
                            wirelessSimulationsVector.get(instantSimulationIndex);
                    wirelessFlowEndTime = instantWirelessNodeValue;

                    wirelessSimulationData.incrSuccessFulDeliveries();

                    wirelessSimulationData.incrSuccessfulDeliveryTimeSum(wirelessFlowInitialTime, wirelessFlowEndTime);

                    Map<String, DataNode> throughputData = wirelessSimulationData.getThroughputData();
                    DataNode existantFlowData = throughputData.get(flowString);
                    if (existantFlowData == null) {
                        existantFlowData = new DataNode();
                        throughputData.put(flowString, existantFlowData);
                        existantFlowData.setSource(flowData.getSource());
                        existantFlowData.setWirelessSourceNode(flowData.getWirelessSourceNode());
                        existantFlowData.setDestination(finalDestination);
                        existantFlowData.setWirelessDestinationNode(endFlowWirelessNode);
                    }
                    existantFlowData.incrNodeData();
                }
            }
        }

        this.tclGenSimulationData = new ArrayList<TclGeneratorSimulationData>(wirelessSimulationsVector);

        // calculating each simulation metrics
        for (TclGeneratorSimulationData simulation : this.tclGenSimulationData) {
            this.log.info("\n==========================================================================="
                    + "\nSimulation " + simulation.getT0() + " " + simulation.getTf()
                    + "\n===========================================================================");
            simulation.executeMetricsCalculations(this.log);

            this.log.info(simulation.getThroughputData().toString());

        }
    }

    private TclGeneratorSimulationData organizeExperimentData(String[] columns,
            TclGeneratorSimulationData tclSimulationData) {

        String packetStatus = columns[0];
        Integer sPacketNode = Integer.valueOf(columns[2]);
        Integer dPacketNode = Integer.valueOf(columns[3]);
        String wiredOrigin = columns[8].substring(0, columns[8].indexOf("."));
        String wiredDestination = columns[9].substring(0, columns[9].indexOf("."));
        String packetNumber = columns[11];
        String packetTime = columns[1];

        Map<String, SimFlowData> simFlowMap = tclSimulationData.getSimFlowMap();
        Map<String, DataNode> queueData = tclSimulationData.getQueueData();

        SimFlowData simFlow = simFlowMap.get(packetNumber);
        if (simFlow == null) {
            simFlow = new SimFlowData();
            simFlowMap.put(packetNumber, simFlow);
        } else {
            // sanity verification
            if (packetStatus.equals("r") && !simFlow.getFlowData().getNodeChain().contains(sPacketNode)) {
                throw new RuntimeException("Packet " + packetNumber + " flow is broken.");
            }
        }

        FlowData flowData = simFlow.getFlowData();

        if (flowData.getSource() == null) {
            flowData.setSource(wiredOrigin);
        } else {
            // sanity check
            if (!flowData.getSource().equals(wiredOrigin)) {
                throw new RuntimeException("Same package but with different source");
            }
        }

        if (flowData.getDestination() == null) {
            flowData.setDestination(wiredDestination);
        } else {
            // sanity check
            if (!flowData.getDestination().equals(wiredDestination)) {
                throw new RuntimeException("Same package but with different destination");
            }
        }

        FlowData wirelessFlowData = null;
        if ((((sPacketNode - this.n0) % this.simulationParams.getNumberOfNodesInCluster()) == 0)
                && (((dPacketNode - this.n0) % this.simulationParams.getNumberOfNodesInCluster()) == 0)) {
            // belongs to the wireless domain
            wirelessFlowData = simFlow.getWirelessFlowData();
        }

        if ("r".equals(packetStatus)) {

            // we have to store the destination packet to complete the flow
            String key = packetStatus + dPacketNode;
            if (simFlow.getFlowData().getTimeData().get(key) != null) {
                throw new RuntimeException("Incoherent packet flow");
            }
            flowData.getTimeData().put(key, Float.valueOf(packetTime));
            flowData.getNodeChain().add(sPacketNode);
            flowData.getNodeChain().add(dPacketNode);

            if (wirelessFlowData != null) {
                wirelessFlowData.getTimeData().put(key, Float.valueOf(packetTime));
                wirelessFlowData.getNodeChain().add(sPacketNode);
                wirelessFlowData.getNodeChain().add(dPacketNode);
            }

        } else if ("+".equals(packetStatus)) {
            DataNode queueNode = queueData.get(columns[2]);
            if (queueNode == null) {
                queueNode = new DataNode();
                queueData.put(columns[2], queueNode);
            }
            queueNode.incrNodeData();

            if (queueNode.getNodeData() > this.simulationParams.getWiredQueueSize()) {
                System.out.println("node " + columns[2] + "queue is incoherent - too much packages.");
            }
        } else if ("-".equals(packetStatus)) {
            DataNode queueNode = queueData.get(columns[2]);
            if (queueNode == null) {
                queueNode = new DataNode();
                queueData.put(columns[2], queueNode);
            }
            queueNode.decrNodeData();

            // sanity verification
            if (queueNode.getNodeData() < 0) {
                System.out.println("node " + columns[2] + "queue is incoherent - negative count.");
            }

            String key = packetStatus + sPacketNode;
            if (simFlow.getFlowData().getTimeData().get(key) != null) {
                throw new RuntimeException("Incoherent packet flow");
            }
            flowData.getTimeData().put(key, Float.valueOf(packetTime));
            flowData.getNodeChain().add(sPacketNode);
            flowData.getNodeChain().add(dPacketNode);

            if (wirelessFlowData != null) {
                wirelessFlowData.getTimeData().put(key, Float.valueOf(packetTime));
                wirelessFlowData.getNodeChain().add(sPacketNode);
                wirelessFlowData.getNodeChain().add(dPacketNode);
            }
        } else if ("d".equals(packetStatus)) {
            // DataNode dropData = droppedData.get(sPacketNode);
            // if (dropData == null) {
            // dropData = new DataNode();
            // droppedData.put(flowString, dropData);
            // }
            // droppedData.get(sPacketNode).incrNodeData();
            //
            // FlowNode flowNode = flowData.get(packetNumber);
            // if (flowNode == null) {
            // flowNode = new FlowNode();
            // flowData.put(packetNumber, flowNode);
            // }
            // String key =packetStatus + sPacketNode;
            // if(flowNode.getTimeData().get(key) != null)
            // {
            // throw new RuntimeException("Fluxo de Pacotes Incoerente");
            // }
            // flowNode.getTimeData().put(key, Float.valueOf(packetTime));
            // flowNode.getNodeChain().add(sPacketNode);
            // flowNode.getNodeChain().add(dPacketNode);
        } else {
            throw new RuntimeException("unrecognized state");
        }

        return tclSimulationData;
    }

    /*
     * private void processExperimentData(TclGeneratorSimulationData tclSimulationData) {
     * // Map<String, QueueNode> throughputData = new HashMap<String, QueueNode>();
     * // for( Entry<Integer, FlowNode> packetFlow : tclSimulationData.getFlowData().entrySet() ){
     * // FlowNode packetData = packetFlow.getValue();
     * // Map<String, Float> timeData = packetData.getTimeData();
     * // Iterator<Entry<String, Float>> iterator = timeData.entrySet().iterator();
     * // boolean first = true;
     * //
     * //
     * // System.out.println(timeData.toString());
     * // List<Integer> nodeChain = packetFlow.getValue().getNodeChain();
     * // System.out.println(nodeChain.toString());
     * // Object[] flow = nodeChain.toArray();
     * // Integer src = (Integer) flow[0];
     * // Integer dest = (Integer) flow[nodeChain.size() - 1];
     * // String flowString = src + " " + dest;
     * // QueueNode flowData = throughputData.get(flowString);
     * // if( flowData == null ){
     * // flowData = new QueueNode();
     * // throughputData.put(flowString, flowData);
     * // }
     * // flowData.incrNodeData();
     * // }
     * // //calculating mean flow throughput
     * // for(Map.Entry<String, QueueNode> entryFlowData : throughputData.entrySet()){
     * // QueueNode flowData = entryFlowData.getValue();
     * // flowData.setFlowRate(1/(flowData.getNodeData()/this.timeInterval));
     * // }
     * //
     * // tclSimulationData.setThroughputData(throughputData);
     * // this.tclGenSimulationData.add(tclSimulationData);
     * // System.out.println(throughputData.toString());
     * 
     * Map<String, DataNode> throughputData = new HashMap<String, DataNode>();
     * // Map<String, DataNode> wirelessThroughputData = new HashMap<String, DataNode>();
     * Integer sentPackets = 0;
     * Integer receivedPackets = 0;
     * Integer sucessfullDeliveries = 0;
     * Float timeSum = 0f;
     * for (Entry<String, SimFlowData> packetMapFlow : tclSimulationData.getSimFlowMap().entrySet()) {
     * 
     * SimFlowData packetSimFlow = packetMapFlow.getValue();
     * 
     * if (!packetSimFlow.getWirelessFlowData().getTimeData().isEmpty()) {
     * // it is a wireless flow package
     * 
     * FlowData packetFlowData = packetSimFlow.getFlowData();
     * 
     * Map<String, Float> timeData = packetFlowData.getTimeData();
     * // System.out.println(timeData.toString());
     * Iterator<Entry<String, Float>> timeIterator = timeData.entrySet().iterator();
     * boolean first = true;
     * boolean dropped = false;
     * String beginFlow = null;
     * Entry<String, Float> timeEntry = null;
     * while (timeIterator.hasNext()) {
     * // end time
     * timeEntry = timeIterator.next();
     * String key = timeEntry.getKey();
     * 
     * if (dropped) {
     * throw new RuntimeException("sanity: how?");
     * }
     * if (first) {
     * beginFlow = key.substring(1);
     * packetFlowData.setInitialTime(timeEntry.getValue());
     * 
     * if (!packetFlowData.getSource().equals(beginFlow)) {
     * throw new RuntimeException("first node is not the origin");
     * }
     * }
     * 
     * // deliveryRate
     * if (key.contains("-")) {
     * sentPackets++;
     * } else if (key.contains("r")) {
     * receivedPackets++;
     * } else if (key.contains("d")) {
     * dropped = true;
     * }
     * first = false;
     * }
     * if ((timeEntry != null) && (beginFlow != null)) {
     * String endFlowKey = timeEntry.getKey();
     * String endFlow = endFlowKey.substring(1);
     * packetFlowData.setEndTime(timeEntry.getValue());
     * 
     * // sanity check
     * if (endFlowKey.contains("r") && endFlow.equals(packetFlowData.getDestination())) {
     * sucessfullDeliveries++;
     * packetFlowData.calculateTimeInterval();
     * timeSum += packetFlowData.getTimeInterval();
     * 
     * String flowString = beginFlow + " " + endFlow;
     * 
     * DataNode flowData = throughputData.get(flowString);
     * if (flowData == null) {
     * flowData = new DataNode();
     * throughputData.put(flowString, flowData);
     * }
     * flowData.incrNodeData();
     * }
     * }
     * 
     * // analysing wireless flow data
     * FlowData packetWirelessFlowData = packetSimFlow.getWirelessFlowData();
     * Map<String, Float> wirelessTimeData = packetWirelessFlowData.getTimeData();
     * // System.out.println(timeData.toString());
     * Iterator<Entry<String, Float>> wirelessTimeIterator = wirelessTimeData.entrySet().iterator();
     * first = true;
     * dropped = false;
     * beginFlow = null;
     * Entry<String, Float> wirelessTimeEntry = null;
     * while (wirelessTimeIterator.hasNext()) {
     * // end time
     * wirelessTimeEntry = timeIterator.next();
     * String key = wirelessTimeEntry.getKey();
     * 
     * if (dropped) {
     * throw new RuntimeException("sanity: how?");
     * }
     * if (first) {
     * beginFlow = key.substring(1);
     * packetWirelessFlowData.setInitialTime(wirelessTimeEntry.getValue());
     * packetWirelessFlowData.setSource(beginFlow);
     * }
     * 
     * // deliveryRate
     * if (key.contains("-")) {
     * sentPackets++;
     * } else if (key.contains("r")) {
     * receivedPackets++;
     * } else if (key.contains("d")) {
     * dropped = true;
     * }
     * first = false;
     * }
     * if ((timeEntry != null) && (beginFlow != null)) {
     * String endFlowKey = timeEntry.getKey();
     * String endFlow = endFlowKey.substring(1);
     * packetWirelessFlowData.setEndTime(timeEntry.getValue());
     * 
     * // sanity check
     * if (endFlowKey.contains("r") && !endFlow.equals(beginFlow)) {
     * sucessfullDeliveries++;
     * packetFlowData.calculateTimeInterval();
     * timeSum += packetFlowData.getTimeInterval();
     * 
     * String flowString = beginFlow + " " + endFlow;
     * 
     * DataNode flowData = throughputData.get(flowString);
     * if (flowData == null) {
     * flowData = new DataNode();
     * throughputData.put(flowString, flowData);
     * }
     * flowData.incrNodeData();
     * }
     * }
     * }
     * }
     * 
     * this.log.info("\n===========================================================================" + "\nSimulation "
     * + tclSimulationData.getT0() + " " + tclSimulationData.getTf()
     * + "\n===========================================================================");
     * 
     * // calculating delivery rate
     * Float sentPacketsFloat = sentPackets.floatValue();
     * if (sentPacketsFloat != 0f) {
     * tclSimulationData.setDeliveryRate(receivedPackets.floatValue() / sentPacketsFloat);
     * } else {
     * tclSimulationData.setDeliveryRate(0f);
     * }
     * 
     * this.log.info("deliveryRate: " + tclSimulationData.getDeliveryRate());
     * 
     * // calculating mean flow throughput
     * for (Map.Entry<String, DataNode> entryFlowData : throughputData.entrySet()) {
     * DataNode flowData = entryFlowData.getValue();
     * if (flowData.getNodeData() != 0) {
     * flowData.setFlowRate(1 / (flowData.getNodeData() / this.simulationParams.getTimeInterval() / tclSimulationData
     * .getDeliveryRate()));
     * } else {
     * flowData.setFlowRate(0f);
     * }
     * this.log.info("flowRate " + entryFlowData.getKey() + ": " + flowData.getFlowRate());
     * }
     * 
     * // calculating mean delay
     * if (sucessfullDeliveries != 0f) {
     * tclSimulationData.setMeanDelay(timeSum / sucessfullDeliveries);
     * } else {
     * tclSimulationData.setMeanDelay(0f);
     * }
     * this.log.info("meanDelay: " + tclSimulationData.getMeanDelay());
     * 
     * tclSimulationData.setThroughputData(throughputData);
     * this.tclGenSimulationData.add(tclSimulationData);
     * this.log.info(throughputData.toString());
     * 
     * // TODO: falta filas e dropped
     * }
     */

    public List<TclGeneratorSimulationData> getTclGenSimulationData() {
        return this.tclGenSimulationData;
    }

    public void setTclGenSimulationData(List<TclGeneratorSimulationData> tclGenSimulationData) {
        this.tclGenSimulationData = tclGenSimulationData;
    }
}
