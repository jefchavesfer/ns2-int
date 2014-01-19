
package model.java;


import io.java.FileReader;
import io.java.SimulationParams;
import io.java.TclGeneratorSimulationData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import tree.java.DataNode;
import tree.java.FlowNode;


public class FileProcess {

    private List<TclGeneratorSimulationData> tclGenSimulationData;
    private Float timeInterval = 5.0f;
    private Integer nn;
    private Integer nc;
    private Integer nQueue;
    private Integer packetSize;
    private String fileRadical;
    private Logger log;

    public FileProcess(SimulationParams simulationParams) {
        this.tclGenSimulationData = new ArrayList<TclGeneratorSimulationData>();
        this.timeInterval = simulationParams.getTimeInterval();
        this.nn = simulationParams.getNumberOfNodesInCluster();
        this.nc = simulationParams.getNumberOfNodesInCluster();
        this.nQueue = simulationParams.getWiredQueueSize();
        this.packetSize = simulationParams.getPacketSize();
        this.fileRadical = simulationParams.getWiredFileDiscriminator();
        this.log = Logger.getLogger(SimulationParams.class.getName());
    }

    public void processFile() {
        Integer n0 = this.nn / 2;
        FileReader trFile = new FileReader(this.fileRadical + ".tr");
        String trLine;
        Float maxTime = 0f;
        maxTime += this.timeInterval;
        TclGeneratorSimulationData tclSimulationData = new TclGeneratorSimulationData();
        do {
            trLine = trFile.readLine();
            if (trLine != null) {
                String[] columns = trLine.split(" ");
                Float timeInstant = Float.valueOf(columns[1]);
                if (timeInstant >= maxTime) {
                    do { // generate files even for packet intervals bigger than sampling
                        this.generateTclSimulationData(maxTime, tclSimulationData);
                        tclSimulationData = new TclGeneratorSimulationData();
                        maxTime += this.timeInterval;
                        if (maxTime < timeInstant) {
                            System.out.println("lacou");
                        }
                    } while (maxTime < timeInstant);
                }
                tclSimulationData = this.organizeExperimentData(columns, tclSimulationData, n0);
            }
        } while (trLine != null);
        trFile.close();
        this.generateTclSimulationData(maxTime, tclSimulationData);
    }

    private void generateTclSimulationData(Float maxTime, TclGeneratorSimulationData tclSimulationData) {

        tclSimulationData.setNn(this.nn);
        tclSimulationData.setNc(this.nc);
        tclSimulationData.setNQueue(this.nQueue);
        tclSimulationData.setPacketSize(this.packetSize);
        tclSimulationData.setTf(maxTime);
        tclSimulationData.setT0(maxTime - this.timeInterval);

        this.processExperimentData(tclSimulationData);
    }

    private TclGeneratorSimulationData organizeExperimentData(String[] columns,
            TclGeneratorSimulationData tclSimulationData, Integer n0) {

        String packetStatus = columns[0];
        Integer sPacketNode = Integer.valueOf(columns[2]);
        Integer dPacketNode = Integer.valueOf(columns[3]);
        String packetNumber = columns[11];
        String packetTime = columns[1];
        String flowString = columns[2] + " " + columns[3];

        if ((((sPacketNode - n0) % this.nn) == 0) && (((dPacketNode - n0) % this.nn) == 0)) {
            // belongs to the wireless domain
            Map<String, FlowNode> flowData = tclSimulationData.getFlowData();
            Map<String, DataNode> queueData = tclSimulationData.getQueueData();
            Map<String, DataNode> droppedData = tclSimulationData.getDroppedData();

            if ("r".equals(packetStatus)) {
                FlowNode flowNode = flowData.get(packetNumber);
                if (flowNode == null) {
                    flowNode = new FlowNode();
                    flowData.put(packetNumber, flowNode);
                } else {
                    // sanity verification
                    if (!flowNode.getNodeChain().contains(sPacketNode)) {
                        throw new RuntimeException("Packet " + packetNumber + " flow is broken.");
                    }
                }
                // we have to store the destination packet to complete the flow
                String key = packetStatus + dPacketNode;
                if (flowNode.getTimeData().get(key) != null) {
                    throw new RuntimeException("Fluxo de Pacotes Incoerente");
                }
                flowNode.getTimeData().put(key, Float.valueOf(packetTime));
                flowNode.getNodeChain().add(sPacketNode);
                flowNode.getNodeChain().add(dPacketNode);
            } else if ("+".equals(packetStatus)) {
                DataNode queueNode = queueData.get(flowString);
                if (queueNode == null) {
                    queueNode = new DataNode();
                    queueData.put(flowString, queueNode);
                }
                queueNode.incrNodeData();
            } else if ("-".equals(packetStatus)) {
                DataNode queueNode = queueData.get(flowString);
                if (queueNode == null) {
                    queueNode = new DataNode();
                    queueData.put(flowString, queueNode);
                }
                queueNode.decrNodeData();

                // sanity verification
                if (queueNode.getNodeData() < 0) {
                    // throw new RuntimeException("queue flow " + flowString
                    // + " incoherent.");
                    System.out.println("queue flow " + flowString + " incoherent.");
                }
                FlowNode flowNode = flowData.get(packetNumber);
                if (flowNode == null) {
                    flowNode = new FlowNode();
                    flowData.put(packetNumber, flowNode);
                }
                String key = packetStatus + sPacketNode;
                if (flowNode.getTimeData().get(key) != null) {
                    throw new RuntimeException("Fluxo de Pacotes Incoerente");
                }
                flowNode.getTimeData().put(key, Float.valueOf(packetTime));
                flowNode.getNodeChain().add(sPacketNode);
                flowNode.getNodeChain().add(dPacketNode);
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
        }
        return tclSimulationData;
    }

    private void processExperimentData(TclGeneratorSimulationData tclSimulationData) {
        // Map<String, QueueNode> throughputData = new HashMap<String, QueueNode>();
        // for( Entry<Integer, FlowNode> packetFlow : tclSimulationData.getFlowData().entrySet() ){
        // FlowNode packetData = packetFlow.getValue();
        // Map<String, Float> timeData = packetData.getTimeData();
        // Iterator<Entry<String, Float>> iterator = timeData.entrySet().iterator();
        // boolean first = true;
        //
        //
        // System.out.println(timeData.toString());
        // List<Integer> nodeChain = packetFlow.getValue().getNodeChain();
        // System.out.println(nodeChain.toString());
        // Object[] flow = nodeChain.toArray();
        // Integer src = (Integer) flow[0];
        // Integer dest = (Integer) flow[nodeChain.size() - 1];
        // String flowString = src + " " + dest;
        // QueueNode flowData = throughputData.get(flowString);
        // if( flowData == null ){
        // flowData = new QueueNode();
        // throughputData.put(flowString, flowData);
        // }
        // flowData.incrNodeData();
        // }
        // //calculating mean flow throughput
        // for(Map.Entry<String, QueueNode> entryFlowData : throughputData.entrySet()){
        // QueueNode flowData = entryFlowData.getValue();
        // flowData.setFlowRate(1/(flowData.getNodeData()/this.timeInterval));
        // }
        //
        // tclSimulationData.setThroughputData(throughputData);
        // this.tclGenSimulationData.add(tclSimulationData);
        // System.out.println(throughputData.toString());

        Map<String, DataNode> throughputData = new HashMap<String, DataNode>();
        Integer sentPackets = 0;
        Integer receivedPackets = 0;
        Integer sucessfullDeliveries = 0;
        Float timeSum = 0f;
        for (Entry<String, FlowNode> packetFlow : tclSimulationData.getFlowData().entrySet()) {
            // List<Integer> nodeChain = packetFlow.getValue().getNodeChain();

            FlowNode packetData = packetFlow.getValue();
            Map<String, Float> timeData = packetData.getTimeData();
            // System.out.println(timeData.toString());
            Iterator<Entry<String, Float>> iterator = timeData.entrySet().iterator();
            boolean first = true;
            boolean dropped = false;
            String beginFlow = null;
            String endFlow = null;
            Entry<String, Float> timeEntry = null;
            while (iterator.hasNext()) {
                // end time
                timeEntry = iterator.next();
                String key = timeEntry.getKey();

                if (dropped) {
                    throw new RuntimeException("FUUU");
                }
                if (first) {
                    beginFlow = key;
                    packetData.setInitialTime(timeEntry.getValue());
                }

                // deliveryRate
                if (key.contains("-")) {
                    sentPackets++;
                } else if (key.contains("r")) {
                    receivedPackets++;
                } else if (key.contains("d")) {
                    dropped = true;
                }
                first = false;
            }
            if (timeEntry != null) {
                endFlow = timeEntry.getKey();
                packetData.setEndTime(timeEntry.getValue());
                if (endFlow.contains("r")) {
                    sucessfullDeliveries++;
                    packetData.calculateTimeInterval();
                    timeSum += packetData.getTimeInterval();
                }
            }
            String flowString = beginFlow + " " + endFlow;

            DataNode flowData = throughputData.get(flowString);
            if (flowData == null) {
                flowData = new DataNode();
                throughputData.put(flowString, flowData);
            }
            flowData.incrNodeData();
        }

        this.log.info("\n===========================================================================" + "\nSimulation "
                + tclSimulationData.getT0() + " " + tclSimulationData.getTf()
                + "\n===========================================================================");

        // calculating mean flow throughput
        for (Map.Entry<String, DataNode> entryFlowData : throughputData.entrySet()) {
            DataNode flowData = entryFlowData.getValue();
            if (flowData.getNodeData() != 0) {
                flowData.setFlowRate(1 / (flowData.getNodeData() / this.timeInterval));
            } else {
                flowData.setFlowRate(0f);
            }
            this.log.info("flowRate " + entryFlowData.getKey() + ": " + flowData.getFlowRate());
        }

        // calculating delivery rate
        Float sentPacketsFloat = sentPackets.floatValue();
        if (sentPacketsFloat != 0f) {
            tclSimulationData.setDeliveryRate(receivedPackets.floatValue() / sentPacketsFloat);
        } else {
            tclSimulationData.setDeliveryRate(0f);
        }

        this.log.info("deliveryRate: " + tclSimulationData.getDeliveryRate());

        // calculating mean delay
        if (sucessfullDeliveries != 0f) {
            tclSimulationData.setMeanDelay(timeSum / sucessfullDeliveries);
        } else {
            tclSimulationData.setMeanDelay(0f);
        }
        this.log.info("meanDelay: " + tclSimulationData.getMeanDelay());

        tclSimulationData.setThroughputData(throughputData);
        this.tclGenSimulationData.add(tclSimulationData);
        this.log.info(throughputData.toString());

        // TODO: falta filas e dropped
    }

    public List<TclGeneratorSimulationData> getTclGenSimulationData() {
        return this.tclGenSimulationData;
    }

    public void setTclGenSimulationData(List<TclGeneratorSimulationData> tclGenSimulationData) {
        this.tclGenSimulationData = tclGenSimulationData;
    }
}
