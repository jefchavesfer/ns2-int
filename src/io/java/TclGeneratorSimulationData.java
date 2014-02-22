
package io.java;


import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import tree.java.DataNode;
import tree.java.SimFlowData;
import tree.java.TreeDataInfo;


/**
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
    private Map<Integer, TreeDataInfo> packetData;
    private Map<String, SimFlowData> simFlowMap;
    private Map<String, DataNode> queueData;
    private Map<String, DataNode> droppedData;
    private Map<String, DataNode> throughputData;
    private Float deliveryRate;
    private Float meanDelay;
    private Integer sentPackages;
    private Integer receivedPackages;
    private Integer successfulDeliveries;
    private Float sucessfulDeliveryTimeSum;

    /**	 */
    public TclGeneratorSimulationData() {
        this.packetData = new HashMap<Integer, TreeDataInfo>();
        this.simFlowMap = new HashMap<String, SimFlowData>();
        this.queueData = new HashMap<String, DataNode>();
        this.droppedData = new HashMap<String, DataNode>();
        this.throughputData = new HashMap<String, DataNode>();
        this.sentPackages = 0;
        this.receivedPackages = 0;
        this.successfulDeliveries = 0;
        this.sucessfulDeliveryTimeSum = 0f;
    }

    /**
     * @return packetData
     */
    public Map<Integer, TreeDataInfo> getPacketData() {
        return this.packetData;
    }

    public void setPacketData(Map<Integer, TreeDataInfo> packetData) {
        this.packetData = packetData;
    }

    public Map<String, SimFlowData> getSimFlowMap() {
        return this.simFlowMap;
    }

    public void setSimFlowMap(Map<String, SimFlowData> simFlowMap) {
        this.simFlowMap = simFlowMap;
    }

    public Map<String, DataNode> getQueueData() {
        return this.queueData;
    }

    public void setQueueData(Map<String, DataNode> queueData) {
        this.queueData = queueData;
    }

    public Map<String, DataNode> getDroppedData() {
        return this.droppedData;
    }

    public void setDroppedData(Map<String, DataNode> droppedData) {
        this.droppedData = droppedData;
    }

    public Map<String, DataNode> getThroughputData() {
        return this.throughputData;
    }

    public void setThroughputData(Map<String, DataNode> throughputData) {
        this.throughputData = throughputData;
    }

    public Integer getNn() {
        return this.nn;
    }

    public void setNn(Integer nn) {
        this.nn = nn;
    }

    public Integer getNc() {
        return this.nc;
    }

    public void setNc(Integer nc) {
        this.nc = nc;
    }

    public Float getT0() {
        return this.t0;
    }

    public void setT0(Float t0) {
        this.t0 = t0;
    }

    public Float getTf() {
        return this.tf;
    }

    public void setTf(Float tf) {
        this.tf = tf;
    }

    public Integer getN0() {
        return this.n0;
    }

    public void setN0(Integer n0) {
        this.n0 = n0;
    }

    public Float getDeliveryRate() {
        return this.deliveryRate;
    }

    public void setDeliveryRate(Float deliveryRate) {
        this.deliveryRate = deliveryRate;
    }

    public Float getMeanDelay() {
        return this.meanDelay;
    }

    public void setMeanDelay(Float meanDelay) {
        this.meanDelay = meanDelay;
    }

    public String getFileRadical() {
        return this.fileRadical;
    }

    public void setFileRadical(String fileRadical) {
        this.fileRadical = fileRadical;
    }

    public Integer getNQueue() {
        return this.nQueue;
    }

    public void setNQueue(Integer nQueue) {
        this.nQueue = nQueue;
    }

    public Integer getPacketSize() {
        return this.packetSize;
    }

    public void setPacketSize(Integer packetSize) {
        this.packetSize = packetSize;
    }

    public String getWirelessProtocol() {
        return this.wirelessProtocol;
    }

    public void setWirelessProtocol(String wirelessProtocol) {
        this.wirelessProtocol = wirelessProtocol;
    }

    public Integer getSentPackages() {
        return this.sentPackages;
    }

    public void incrSentPackages() {
        this.sentPackages += 1;
    }

    public void setSentPackages(Integer sentPackages) {
        this.sentPackages = sentPackages;
    }

    public Integer getReceivedPackages() {
        return this.receivedPackages;
    }

    public void incrReceivedPackages() {
        this.receivedPackages += 1;
    }

    public void setReceivedPackages(Integer receivedPackages) {
        this.receivedPackages = receivedPackages;
    }

    public Integer getSuccessfulDeliveries() {
        return this.successfulDeliveries;
    }

    public void setSuccessfulDeliveries(Integer successfulDeliveries) {
        this.successfulDeliveries = successfulDeliveries;
    }

    public void incrSuccessFulDeliveries() {
        this.successfulDeliveries += 1;
    }

    public Integer getnQueue() {
        return this.nQueue;
    }

    public void setnQueue(Integer nQueue) {
        this.nQueue = nQueue;
    }

    public Float getSucessfulDeliveryTimeSum() {
        return this.sucessfulDeliveryTimeSum;
    }

    public void setSucessfulDeliveryTimeSum(Float sucessfulDeliveryTimeSum) {
        this.sucessfulDeliveryTimeSum = sucessfulDeliveryTimeSum;
    }

    public void incrSuccessfulDeliveryTimeSum(Float initialTime, Float endTime) {
        this.sucessfulDeliveryTimeSum += (endTime - initialTime);
    }

    public void executeMetricsCalculations(Logger log) {
        // calculating delivery rate
        Float sentPacketsFloat = this.sentPackages.floatValue();
        if (sentPacketsFloat != 0f) {
            this.deliveryRate = this.receivedPackages.floatValue() / sentPacketsFloat;
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
