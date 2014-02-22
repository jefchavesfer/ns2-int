
package tree.java;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class SimFlowData {

    FlowData flowData;
    FlowData wirelessFlowData;

    public SimFlowData() {
        this.flowData = new FlowData();
        this.wirelessFlowData = new FlowData();
    }

    public FlowData getFlowData() {
        return this.flowData;
    }

    public void setFlowData(FlowData flowData) {
        this.flowData = flowData;
    }

    public FlowData getWirelessFlowData() {
        return this.wirelessFlowData;
    }

    public void setWirelessFlowData(FlowData wirelessFlowData) {
        this.wirelessFlowData = wirelessFlowData;
    }
}
