
package tree.java;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class FlowData {

    List<Integer> nodeChain;
    Map<String, Float> timeData;
    String source;
    String destination;
    Float initialTime;
    Float endTime;
    Float timeInterval;

    public FlowData() {
        this.timeData = new LinkedHashMap<String, Float>();
        this.nodeChain = new ArrayList<Integer>();
        this.source = null;
        this.destination = null;
        this.initialTime = null;
        this.endTime = null;
        this.timeInterval = 0f;
    }

    public List<Integer> getNodeChain() {
        return this.nodeChain;
    }

    public void setNodeChain(List<Integer> nodeChain) {
        this.nodeChain = nodeChain;
    }

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

    public Map<String, Float> getTimeData() {
        return this.timeData;
    }

    public void setTimeData(Map<String, Float> timeData) {
        this.timeData = timeData;
    }

    public Float getInitialTime() {
        return this.initialTime;
    }

    public void setInitialTime(Float initialTime) {
        this.initialTime = initialTime;
    }

    public Float getEndTime() {
        return this.endTime;
    }

    public void setEndTime(Float endTime) {
        this.endTime = endTime;
    }

    public Float getTimeInterval() {
        return this.timeInterval;
    }

    public void setTimeInterval(Float timeInterval) {
        this.timeInterval = timeInterval;
    }

    public void calculateTimeInterval() {
        this.timeInterval = this.endTime - this.initialTime;
    }
}
