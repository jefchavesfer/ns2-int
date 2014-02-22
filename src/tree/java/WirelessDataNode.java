
package tree.java;


/**
 * @author jchaves
 */
public class WirelessDataNode extends DataNode {

    String wirelessSource;
    String wirelessDestination;
    Integer nodeData;
    Float flowRate;
    Float packetDelay;

    public String getWirelessSource() {
        return this.wirelessSource;
    }

    public void setWirelessSource(String wirelessSource) {
        this.wirelessSource = wirelessSource;
    }

    public String getWirelessDestination() {
        return this.wirelessDestination;
    }

    public void setWirelessDestination(String wirelessDestination) {
        this.wirelessDestination = wirelessDestination;
    }

    public WirelessDataNode() {
        this.nodeData = 0;
    }

    @Override
    public Integer getNodeData() {
        return this.nodeData;
    }

    @Override
    public void setNodeData(Integer nodeData) {
        this.nodeData = nodeData;
    }

    @Override
    public Integer incrNodeData() {
        return this.nodeData++;
    }

    @Override
    public void decrNodeData() {
        this.nodeData--;
    }

    @Override
    public String toString() {
        return this.nodeData.toString();
    }

    @Override
    public Float getFlowRate() {
        return this.flowRate;
    }

    @Override
    public void setFlowRate(Float flowRate) {
        this.flowRate = flowRate;
    }

    @Override
    public Float getPacketDelay() {
        return this.packetDelay;
    }

    @Override
    public void setPacketDelay(Float packetDelay) {
        this.packetDelay = packetDelay;
    }
}
