
package io.java;


import java.util.HashMap;
import java.util.Map;

import tree.java.FlowNode;
import tree.java.DataNode;
import tree.java.TreeDataInfo;


/**
 * @author jchaves
 */
public class TclGeneratorSimulationData {

    private String fileRadical;
    private Map<Integer, TreeDataInfo> packetData;
    private Map<String, FlowNode> flowData;
    private Map<String, DataNode> queueData;
    private Map<String, DataNode> droppedData;
    private Map<String, DataNode> throughputData;
    private Integer nn;
    private Integer nc;
    private Integer nQueue;
    private Integer packetSize;
    private Float t0;
    private Float tf;
    private Float deliveryRate;
    private Float meanDelay;

    /**
	 */
    public TclGeneratorSimulationData() {
        this.packetData = new HashMap<Integer, TreeDataInfo>();
        this.flowData = new HashMap<String, FlowNode>();
        this.queueData = new HashMap<String, DataNode>();
        this.droppedData = new HashMap<String, DataNode>();
        this.throughputData = new HashMap<String, DataNode>();
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

    public Map<String, FlowNode> getFlowData() {
        return this.flowData;
    }

    public void setFlowData(Map<String, FlowNode> flowData) {
        this.flowData = flowData;
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
}
