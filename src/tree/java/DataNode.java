
package tree.java;


public class DataNode {

    String source;
    String destination;
    String wirelessSourceNode;
    String wirelessDestinationNode;
    Integer nodeData;
    Float flowRate;
    Float packetDelay;

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public DataNode() {
        this.nodeData = 0;
    }

    public Integer getNodeData() {
        return this.nodeData;
    }

    public void setNodeData(Integer nodeData) {
        this.nodeData = nodeData;
    }

    public Integer incrNodeData() {
        return this.nodeData++;
    }

    public void decrNodeData() {
        this.nodeData--;
    }

    @Override
    public String toString() {
        return this.nodeData.toString();
    }

    public Float getFlowRate() {
        return this.flowRate;
    }

    public void setFlowRate(Float flowRate) {
        this.flowRate = flowRate;
    }

    public Float getPacketDelay() {
        return this.packetDelay;
    }

    public void setPacketDelay(Float packetDelay) {
        this.packetDelay = packetDelay;
    }

    public String getWirelessSourceNode() {
        return this.wirelessSourceNode;
    }

    public void setWirelessSourceNode(String wirelessSourceNode) {
        this.wirelessSourceNode = wirelessSourceNode;
    }

    public String getWirelessDestinationNode() {
        return this.wirelessDestinationNode;
    }

    public void setWirelessDestinationNode(String wirelessDestinationNode) {
        this.wirelessDestinationNode = wirelessDestinationNode;
    }
}
