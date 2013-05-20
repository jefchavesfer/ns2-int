package tree.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FlowNode {
	List<Integer> nodeChain;
	Map<String, Float> nodeData;
	Map<String, Float> timeData;
	Float initialTime; 
	Float endTime;
	Float timeInterval;
	
	public FlowNode(){
		this.nodeChain = new ArrayList<Integer>();
		this.nodeData = new TreeMap<String, Float>();
		this.timeData = new TreeMap<String, Float>();
		this.initialTime = null;
		this.endTime = null;
		this.timeInterval = 0f;
	}
		
	public List<Integer> getNodeChain() {
		return nodeChain;
	}
	public void setNodeChain(List<Integer> nodeChain) {
		this.nodeChain = nodeChain;
	}
	
	public Map<String, Float> getNodeData() {
		return nodeData;
	}
	public void setNodeData(Map<String, Float> nodeData) {
		this.nodeData = nodeData;
	}

	public Map<String, Float> getTimeData() {
		return timeData;
	}

	public void setTimeData(Map<String, Float> timeData) {
		this.timeData = timeData;
	}

	public Float getInitialTime() {
		return initialTime;
	}

	public void setInitialTime(Float initialTime) {
		this.initialTime = initialTime;
	}

	public Float getEndTime() {
		return endTime;
	}

	public void setEndTime(Float endTime) {
		this.endTime = endTime;
	}

	public Float getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(Float timeInterval) {
		this.timeInterval = timeInterval;
	}
	
	public void calculateTimeInterval(){
		this.timeInterval = this.endTime - this.initialTime;
	}
} 
