
package io.java;


import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import tree.java.DataNode;
import tree.java.SimFlowData;


/**
 * This class acts as an intermediate for analysing simulation results and
 * generating next iteration scripts
 * 
 * @author jchaves
 */
public class TclGeneratorSimulationData {

    private String fileRadical;
    private String wirelessProtocol;
    private Integer nn;
    private Integer nc;
    private Integer nQueue;
    private Integer packetSize;
    private Float t0;
    private Float tf;

    private Integer n0;
    private Map<String, SimFlowData> simFlowMap;
    private Map<String, DataNode> queueData;
    private Map<String, DataNode> droppedData;
    private Map<String, DataNode> throughputData;
    private Map<String, Integer> nodePacketCountData;
    private Float deliveryRate;
    private Float meanDelay;
    private Integer sentPackets;
    private Integer receivedPackets;
    private Integer successfulDeliveries;
    private Float sucessfulDeliveryTimeSum;
    private Vector<TurnOffNode> turnOffNodes;
    private int timeOffSet;

    /**	 */
    public TclGeneratorSimulationData() {
        this.simFlowMap = new HashMap<String, SimFlowData>();
        this.queueData = new HashMap<String, DataNode>();
        this.droppedData = new HashMap<String, DataNode>();
        this.throughputData = new HashMap<String, DataNode>();
        this.nodePacketCountData = new HashMap<String, Integer>();
        this.sentPackets = 0;
        this.receivedPackets = 0;
        this.successfulDeliveries = 0;
        this.sucessfulDeliveryTimeSum = 0f;
        this.turnOffNodes = new Vector<TurnOffNode>();
    }

    /**
     * @return Wired and Wireless simulation processed flow data map
     */
    public Map<String, SimFlowData> getSimFlowMap() {
        return this.simFlowMap;
    }

    /**
     * @param simFlowMap
     *            the flow map (wired source - wired destination)
     */
    public void setSimFlowMap(Map<String, SimFlowData> simFlowMap) {
        this.simFlowMap = simFlowMap;
    }

    /**
     * @return queue data separated by node
     */
    public Map<String, DataNode> getQueueData() {
        return this.queueData;
    }

    /**
     * @param queueData
     */
    public void setQueueData(Map<String, DataNode> queueData) {
        this.queueData = queueData;
    }

    /**
     * @return dropped packets map
     *         Depending on the wireless protocol, one packet can be dropped twice
     */
    public Map<String, DataNode> getDroppedData() {
        return this.droppedData;
    }

    /**
     * @param droppedData
     */
    public void setDroppedData(Map<String, DataNode> droppedData) {
        this.droppedData = droppedData;
    }

    /**
     * @return throughputData separated by traffic flow
     */
    public Map<String, DataNode> getThroughputData() {
        return this.throughputData;
    }

    /**
     * @param throughputData
     */
    public void setThroughputData(Map<String, DataNode> throughputData) {
        this.throughputData = throughputData;
    }

    /**
     * @return number of nodes in cluster
     */
    public Integer getNn() {
        return this.nn;
    }

    /**
     * @param nn
     */
    public void setNn(Integer nn) {
        this.nn = nn;
    }

    /**
     * @return number of clusters
     */
    public Integer getNc() {
        return this.nc;
    }

    /**
     * @param nc
     */
    public void setNc(Integer nc) {
        this.nc = nc;
    }

    /**
     * @return initial simulation time
     */
    public Float getT0() {
        return this.t0;
    }

    /**
     * @param t0
     */
    public void setT0(Float t0) {
        this.t0 = t0;
    }

    /**
     * @return end simulation time
     */
    public Float getTf() {
        return this.tf;
    }

    /**
     * @param tf
     */
    public void setTf(Float tf) {
        this.tf = tf;
    }

    /**
     * @return cluster offset
     *         This variable gives the number of nodes for arriving to the central node
     *         It is related with Nn
     */
    public Integer getN0() {
        return this.n0;
    }

    /**
     * @param n0
     */
    public void setN0(Integer n0) {
        this.n0 = n0;
    }

    /**
     * @return deliveryRate
     */
    public Float getDeliveryRate() {
        return this.deliveryRate;
    }

    /**
     * @param deliveryRate
     */
    public void setDeliveryRate(Float deliveryRate) {
        this.deliveryRate = deliveryRate;
    }

    /**
     * @return meanDelay
     */
    public Float getMeanDelay() {
        return this.meanDelay;
    }

    /**
     * @param meanDelay
     */
    public void setMeanDelay(Float meanDelay) {
        this.meanDelay = meanDelay;
    }

    /**
     * @return fileRadical
     */
    public String getFileRadical() {
        return this.fileRadical;
    }

    /**
     * @param fileRadical
     */
    public void setFileRadical(String fileRadical) {
        this.fileRadical = fileRadical;
    }

    /**
     * @return wireless node queue size
     */
    public Integer getNQueue() {
        return this.nQueue;
    }

    /**
     * @param nQueue
     *            wireless node queue size
     */
    public void setNQueue(Integer nQueue) {
        this.nQueue = nQueue;
    }

    /**
     * @return packetSize
     */
    public Integer getPacketSize() {
        return this.packetSize;
    }

    /**
     * @param packetSize
     */
    public void setPacketSize(Integer packetSize) {
        this.packetSize = packetSize;
    }

    /**
     * @return wirelessProtocol
     */
    public String getWirelessProtocol() {
        return this.wirelessProtocol;
    }

    /**
     * @param wirelessProtocol
     */
    public void setWirelessProtocol(String wirelessProtocol) {
        this.wirelessProtocol = wirelessProtocol;
    }

    /**
     * @return sentPacketes
     */
    public Integer getSentPackets() {
        return this.sentPackets;
    }

    /**
     * this procedure increments sent packet number
     */
    public void incrSentPackets() {
        this.sentPackets += 1;
    }

    /**
     * @param sentPackets
     */
    public void setSentPackets(Integer sentPackets) {
        this.sentPackets = sentPackets;
    }

    /**
     * @return received packet number
     */
    public Integer getReceivedPackets() {
        return this.receivedPackets;
    }

    /**
     * this procedure increments received packet number
     */
    public void incrReceivedPackets() {
        this.receivedPackets += 1;
    }

    /**
     * @param receivedPackets
     */
    public void setReceivedPackets(Integer receivedPackets) {
        this.receivedPackets = receivedPackets;
    }

    /**
     * @return number of packets that arrived successfully to destination
     */
    public Integer getSuccessfulDeliveries() {
        return this.successfulDeliveries;
    }

    /**
     * @param successfulDeliveries
     */
    public void setSuccessfulDeliveries(Integer successfulDeliveries) {
        this.successfulDeliveries = successfulDeliveries;
    }

    /**
     * this procedure increments the number of packets that arrived successfully to destination
     */
    public void incrSuccessFulDeliveries() {
        this.successfulDeliveries += 1;
    }

    /**
     * @return total time taken for all sucessful deliveries
     */
    public Float getSucessfulDeliveryTimeSum() {
        return this.sucessfulDeliveryTimeSum;
    }

    /**
     * @param sucessfulDeliveryTimeSum
     */
    public void setSucessfulDeliveryTimeSum(Float sucessfulDeliveryTimeSum) {
        this.sucessfulDeliveryTimeSum = sucessfulDeliveryTimeSum;
    }

    /**
     * @return nodeCountData
     */
    public Map<String, Integer> getNodePacketCountData() {
        return this.nodePacketCountData;
    }

    /**
     * @param packetKey
     */
    public void incrNodePacketCountData(String packetKey) {
        Integer packetNum = this.nodePacketCountData.get(packetKey);
        if (packetNum == null) {
            this.nodePacketCountData.put(packetKey, 0);
        } else {
            this.nodePacketCountData.put(packetKey, packetNum + 1);
        }
    }

    /**
     * @param initialTime
     * @param endTime
     *            sum to total the time taken for one succesful delivery
     */
    public void incrSuccessfulDeliveryTimeSum(Float initialTime, Float endTime) {
        this.sucessfulDeliveryTimeSum += (endTime - initialTime);
    }

    /**
     * @return turnOffNodes
     */
    public Vector<TurnOffNode> getTurnOffNodes() {
        return this.turnOffNodes;
    }

    /**
     * @param turnOffNodes
     */
    public void setTurnOffNodes(Vector<TurnOffNode> turnOffNodes) {
        this.turnOffNodes = turnOffNodes;
    }

    /**
     * @return timeOffSet
     */
    public int getTimeOffSet() {
        return this.timeOffSet;
    }

    /**
     * @param timeOffSet
     */
    public void setTimeOffSet(int timeOffSet) {
        this.timeOffSet = timeOffSet;
    }

    /**
     * @param log
     */
    public void executeMetricsCalculations(Logger log) {
        // calculating delivery rate
        Float sentPacketsFloat = this.sentPackets.floatValue();
        if (sentPacketsFloat != 0f) {
            this.deliveryRate = this.receivedPackets.floatValue() / sentPacketsFloat;
        } else {
            this.deliveryRate = 0f;
        }

        if (log != null) {
            log.info("deliveryRate: " + this.deliveryRate);
        }
        // calculating mean flow throughput
        Float timeInterval = this.tf - this.t0;

        for (Map.Entry<String, DataNode> entryFlowData : this.throughputData.entrySet()) {
            DataNode flowData = entryFlowData.getValue();
            if ((flowData.getNodeData() != 0) && (timeInterval != 0) && (this.deliveryRate != 0)) {
                flowData.setFlowRate(1 / (flowData.getNodeData() / timeInterval / this.deliveryRate));
            } else {
                flowData.setFlowRate(0f);
            }

            if (log != null) {
                log.info("flowRate " + entryFlowData.getKey() + ": " + flowData.getFlowRate());
            }
        }

        // calculating mean delay
        if (this.successfulDeliveries != 0) {
            this.meanDelay = this.sucessfulDeliveryTimeSum / this.successfulDeliveries;
        } else {
            this.meanDelay = 0f;
        }

        if (log != null) {
            log.info("meanDelay: " + this.meanDelay);
        }
    }
}
