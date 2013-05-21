package io.java;

import java.util.HashMap;
import java.util.Map;

import tree.java.FlowNode;
import tree.java.DataNode;
import tree.java.TreeDataInfo;

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
	
	public TclGeneratorSimulationData(){
		this.packetData = new HashMap<Integer, TreeDataInfo>();
		this.flowData = new HashMap<String, FlowNode>();
		this.queueData = new HashMap<String, DataNode>();
		this.droppedData = new HashMap<String, DataNode>();
		this.throughputData = new HashMap<String, DataNode>();
	}
	
	public Map<Integer, TreeDataInfo> getPacketData() {
		return packetData;
	}
	
	public void setPacketData(Map<Integer, TreeDataInfo> packetData) {
		this.packetData = packetData;
	}
	
	public Map<String, FlowNode> getFlowData() {
		return flowData;
	}
	
	public void setFlowData(Map<String, FlowNode> flowData) {
		this.flowData = flowData;
	}
	
	public Map<String, DataNode> getQueueData() {
		return queueData;
	}
	
	public void setQueueData(Map<String, DataNode> queueData) {
		this.queueData = queueData;
	}
	
	public Map<String, DataNode> getDroppedData() {
		return droppedData;
	}
	
	public void setDroppedData(Map<String, DataNode> droppedData) {
		this.droppedData = droppedData;
	}
	
	public Map<String, DataNode> getThroughputData() {
		return throughputData;
	}
	
	public void setThroughputData(Map<String, DataNode> throughputData) {
		this.throughputData = throughputData;
	}

	public Integer getNn() {
		return nn;
	}

	public void setNn(Integer nn) {
		this.nn = nn;
	}
	
	public Integer getNc() {
		return nc;
	}

	public void setNc(Integer nc) {
		this.nc = nc;
	}

	public Float getT0() {
		return t0;
	}

	public void setT0(Float t0) {
		this.t0 = t0;
	}

	public Float getTf() {
		return tf;
	}

	public void setTf(Float tf) {
		this.tf = tf;
	}

	public Float getDeliveryRate() {
		return deliveryRate;
	}

	public void setDeliveryRate(Float deliveryRate) {
		this.deliveryRate = deliveryRate;
	}

	public Float getMeanDelay() {
		return meanDelay;
	}

	public void setMeanDelay(Float meanDelay) {
		this.meanDelay = meanDelay;
	}

	public String getFileRadical() {
		return fileRadical;
	}

	public void setFileRadical(String fileRadical) {
		this.fileRadical = fileRadical;
	}

	public Integer getNQueue() {
		return nQueue;
	}

	public void setNQueue(Integer nQueue) {
		this.nQueue = nQueue;
	}

	public Integer getPacketSize() {
		return packetSize;
	}

	public void setPacketSize(Integer packetSize) {
		this.packetSize = packetSize;
	}
}
