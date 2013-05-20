package tree.java;


public class DataNode {
	Integer  nodeData;
	Float flowRate;
	Float packetDelay;
	
	public DataNode(){
		nodeData = 0;
	}
	
	public Integer getNodeData() {
		return nodeData;
	}
	
	public void setNodeData(Integer nodeData) {
		this.nodeData = nodeData;
	}
	
	public Integer incrNodeData() {
		return nodeData++;
	}
	
	public void decrNodeData() {
		this.nodeData--;
	}
	
	public String toString(){
		return nodeData.toString();
	}

	public Float getFlowRate() {
		return flowRate;
	}

	public void setFlowRate(Float flowRate) {
		this.flowRate = flowRate;
	}

	public Float getPacketDelay() {
		return packetDelay;
	}

	public void setPacketDelay(Float packetDelay) {
		this.packetDelay = packetDelay;
	}
} 
