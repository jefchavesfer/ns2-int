
package io.java;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * Store all simulation Params
 * 
 * @author jchaves
 */
public class SimulationParams {

    private static int historySize = 2;
    private static int timeOffset = 10;

    private String wiredFileDiscriminator;
    private String wirelessFileDiscriminator;
    private String wirelessProtocol;
    private Integer initialTime;
    private Integer endTime;
    private Integer iterations;

    private Integer numberOfNodesInCluster;
    private Integer numberOfClusters;
    private Float internalTraffic;
    private Float externalTraffic;
    private Integer packetSize;
    private Integer wirelessQueueSize;
    private Integer wiredQueueSize;
    private Integer desirableExternalHopNumber;
    private Integer desirableInternalHopNumber;

    private Float timeInterval;
    private List<Float> appThroughput = new ArrayList<Float>();
    private List<Float> deliveryRateError = new ArrayList<Float>();
    private List<String> wiredBandwidth = new ArrayList<String>();
    private List<String> linkDelay = new ArrayList<String>();
    private List<Float> meanDelayError = new ArrayList<Float>();
    private Vector<TurnOffNode> turnOffNodes = new Vector<TurnOffNode>();
    private Float dissimilarityCoefficient;
    private Float maxRelDifDeliveryRate;
    private Float maxRelDifMeanDelay;
    private Float convergedMeanDelay;
    private SimulationApproach simulationApproach;
    private Boolean converged = Boolean.FALSE;

    private Map<NodeData, NodeData> internalFlowMap;
    private Map<NodeData, NodeData> externalFlowMap;

    /**
     * @return appThroughput
     */
    public Float getAppThroughput() {
        if (this.appThroughput.size() == 0) {
            return null;
        }
        return this.appThroughput.get(this.appThroughput.size() - 1);
    }

    /**
     * @return all wired appThroughput stocked
     */
    public List<Float> getAppThroughputHistory() {
        return this.appThroughput;
    }

    /**
     * @param appThroughput
     */
    public void setAppThroughput(Float appThroughput) {
        this.appThroughput.add(appThroughput);
        if (this.appThroughput.size() > SimulationParams.historySize) {
            this.appThroughput.remove(0);
        }
    }

    /**
     * @param appThroughput
     */
    public void setAppThroughputHistory(List<Float> appThroughput) {
        this.appThroughput = appThroughput;
    }

    /**
     * @return all wired appThroughput stocked
     */
    public List<Float> getDeliveryRateError() {
        return this.deliveryRateError;
    }

    /**
     * @param deliveryRateError
     */
    public void setDeliveryRateError(Float deliveryRateError) {
        this.deliveryRateError.add(deliveryRateError);
        if (this.deliveryRateError.size() > (SimulationParams.historySize - 1)) {
            this.deliveryRateError.remove(0);
        }
    }

    /**
     * @param deliveryRateError
     */
    public void setDeliveryRateError(List<Float> deliveryRateError) {
        this.deliveryRateError = deliveryRateError;
    }

    /**
     * @return all wired bandwitdth stocked
     */
    public List<String> getWiredBandwidthHistory() {
        return this.wiredBandwidth;
    }

    /**
     * @return wiredBandwidth
     */
    public String getWiredBandwidth() {
        if (this.wiredBandwidth.size() == 0) {
            return null;
        }
        return this.wiredBandwidth.get(this.wiredBandwidth.size() - 1);
    }

    /**
     * @param wiredBandwidth
     */
    public void setWiredBandwidth(String wiredBandwidth) {
        this.wiredBandwidth.add(wiredBandwidth);
        if (this.wiredBandwidth.size() > SimulationParams.historySize) {
            this.wiredBandwidth.remove(0);
        }
    }

    /**
     * @param wiredBandwidth
     */
    public void setWiredBandwidthHistory(List<String> wiredBandwidth) {
        this.wiredBandwidth = wiredBandwidth;
    }

    /**
     * @return all wired appThroughput stocked
     */
    public List<Float> getMeanDelayError() {
        return this.meanDelayError;
    }

    /**
     * @param meanDelayError
     */
    public void setMeanDelayError(Float meanDelayError) {
        this.meanDelayError.add(meanDelayError);
        if (this.meanDelayError.size() > (SimulationParams.historySize - 1)) {
            this.meanDelayError.remove(0);
        }
    }

    /**
     * @param meanDelayError
     */
    public void setMeanDelayError(List<Float> meanDelayError) {
        this.meanDelayError = meanDelayError;
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
     * @return internalTraffic
     */
    public Float getInternalTraffic() {
        return this.internalTraffic;
    }

    /**
     * @param internalTraffic
     */
    public void setInternalTraffic(Float internalTraffic) {
        this.internalTraffic = internalTraffic;
    }

    /**
     * @return externalTraffic
     */
    public Float getExternalTraffic() {
        return this.externalTraffic;
    }

    /**
     * @param externalTraffic
     */
    public void setExternalTraffic(Float externalTraffic) {
        this.externalTraffic = externalTraffic;
    }

    /**
     * @return desirableExternalHopNumber
     */
    public Integer getDesirableExternalHopNumber() {
        return this.desirableExternalHopNumber;
    }

    /**
     * @param desirableExternalHopNumber
     */
    public void setDesirableExternalHopNumber(Integer desirableExternalHopNumber) {
        this.desirableExternalHopNumber = desirableExternalHopNumber;
    }

    /**
     * @return desirableInternalHopNumber
     */
    public Integer getDesirableInternalHopNumber() {
        return this.desirableInternalHopNumber;
    }

    /**
     * @param desirableInternalHopNumber
     */
    public void setDesirableInternalHopNumber(Integer desirableInternalHopNumber) {
        this.desirableInternalHopNumber = desirableInternalHopNumber;
    }

    /**
     * @return linkDelay
     */
    public String getLinkDelay() {
        if (this.linkDelay.size() == 0) {
            return null;
        }
        return this.linkDelay.get(this.linkDelay.size() - 1);
    }

    /**
     * @param linkDelay
     */
    public void setLinkDelay(String linkDelay) {
        this.linkDelay.add(linkDelay);
        if (this.linkDelay.size() > SimulationParams.historySize) {
            this.linkDelay.remove(0);
        }
    }

    /**
     * @return all wired bandwitdth stocked
     */
    public List<String> getLinkDelayHistory() {
        return this.linkDelay;
    }

    /**
     * @param linkDelay
     */
    public void setLinkDelayHistory(List<String> linkDelay) {
        this.linkDelay = linkDelay;
    }

    /**
     * @return dissimilarityCoefficient
     */
    public Float getDissimilarityCoefficient() {
        return this.dissimilarityCoefficient;
    }

    /**
     * @param dissimilarityCoefficient
     */
    public void setDissimilarityCoefficient(Float dissimilarityCoefficient) {
        this.dissimilarityCoefficient = dissimilarityCoefficient;
    }

    /**
     * @return maxRelDifDeliveryRate
     */
    public Float getMaxRelDifDeliveryRate() {
        return this.maxRelDifDeliveryRate;
    }

    /**
     * @param maxRelDifDeliveryRate
     */
    public void setMaxRelDifDeliveryRate(Float maxRelDifDeliveryRate) {
        this.maxRelDifDeliveryRate = maxRelDifDeliveryRate;
    }

    /**
     * @return maxRelDifMeanDelay
     */
    public Float getMaxRelDifMeanDelay() {
        return this.maxRelDifMeanDelay;
    }

    /**
     * @param maxRelDifMeanDelay
     */
    public void setMaxRelDifMeanDelay(Float maxRelDifMeanDelay) {
        this.maxRelDifMeanDelay = maxRelDifMeanDelay;
    }

    /**
     * @return SimulationApproach
     */
    public SimulationApproach getSimulationApproach() {
        return this.simulationApproach;
    }

    /**
     * @param simulationApproach
     */
    public void setSimulationApproach(SimulationApproach simulationApproach) {
        this.simulationApproach = simulationApproach;
    }

    /**
     * @return convergedMeanDelay
     */
    public Float getConvergedMeanDelay() {
        return this.convergedMeanDelay;
    }

    /**
     * @param convergedMeanDelay
     */
    public void setConvergedMeanDelay(Float convergedMeanDelay) {
        this.convergedMeanDelay = convergedMeanDelay;
    }

    /**
     * @return converged
     */
    public Boolean isConverged() {
        return this.converged;
    }

    /**
     * @param converged
     */
    public void setConverged(Boolean converged) {
        this.converged = converged;
    }

    /**
     * @return timeInterval
     */
    public Float getTimeInterval() {
        return this.timeInterval;
    }

    /**
     * @param timeInterval
     */
    public void setTimeInterval(Float timeInterval) {
        this.timeInterval = timeInterval;
    }

    /**
     * @return wiredFileDiscriminator
     */
    public String getWiredFileDiscriminator() {
        return this.wiredFileDiscriminator;
    }

    /**
     * @param wiredFileDiscriminator
     */
    public void setWiredFileDiscriminator(String wiredFileDiscriminator) {
        this.wiredFileDiscriminator = wiredFileDiscriminator;
    }

    /**
     * @return wirelessFileDiscriminator
     */
    public String getWirelessFileDiscriminator() {
        return this.wirelessFileDiscriminator;
    }

    /**
     * @param wirelessFileDiscriminator
     */
    public void setWirelessFileDiscriminator(String wirelessFileDiscriminator) {
        this.wirelessFileDiscriminator = wirelessFileDiscriminator;
    }

    /**
     * @return initialTime
     */
    public Integer getInitialTime() {
        return this.initialTime;
    }

    /**
     * @param initialTime
     */
    public void setInitialTime(Integer initialTime) {
        this.initialTime = initialTime;
    }

    /**
     * @return endTime
     */
    public Integer getEndTime() {
        return this.endTime;
    }

    /**
     * @param endTime
     */
    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    /**
     * @return timeOffset
     */
    public static int getTimeOffset() {
        return timeOffset;
    }

    /**
     * @return numberOfNodesInCluster
     */
    public Integer getNumberOfNodesInCluster() {
        return this.numberOfNodesInCluster;
    }

    /**
     * @param numberOfNodesInCluster
     */
    public void setNumberOfNodesInCluster(Integer numberOfNodesInCluster) {
        this.numberOfNodesInCluster = numberOfNodesInCluster;
    }

    /**
     * @return numberOfClusters
     */
    public Integer getNumberOfClusters() {
        return this.numberOfClusters;
    }

    /**
     * @param numberOfClusters
     */
    public void setNumberOfClusters(Integer numberOfClusters) {
        this.numberOfClusters = numberOfClusters;
    }

    /**
     * @return wirelessQueueSize
     */
    public Integer getWirelessQueueSize() {
        return this.wirelessQueueSize;
    }

    /**
     * @param wirelessQueueSize
     */
    public void setWirelessQueueSize(Integer wirelessQueueSize) {
        this.wirelessQueueSize = wirelessQueueSize;
    }

    /**
     * @return wiredQueueSize
     */
    public Integer getWiredQueueSize() {
        return this.wiredQueueSize;
    }

    /**
     * @param wiredQueueSize
     */
    public void setWiredQueueSize(Integer wiredQueueSize) {
        this.wiredQueueSize = wiredQueueSize;
    }

    /**
     * @return internalFlowMap
     */
    public Map<NodeData, NodeData> getInternalFlowMap() {
        return this.internalFlowMap;
    }

    /**
     * @param internalFlowMap
     */
    public void setInternalFlowMap(Map<NodeData, NodeData> internalFlowMap) {
        this.internalFlowMap = internalFlowMap;
    }

    /**
     * @return externalFlowMap
     */
    public Map<NodeData, NodeData> getExternalFlowMap() {
        return this.externalFlowMap;
    }

    /**
     * @param externalFlowMap
     */
    public void setExternalFlowMap(Map<NodeData, NodeData> externalFlowMap) {
        this.externalFlowMap = externalFlowMap;
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
     * @return maxIterations
     */
    public Integer getIterations() {
        return this.iterations;
    }

    /**
     * @param iterations
     */
    public void setIterations(Integer iterations) {
        this.iterations = iterations;
    }

    /**
     * @param turnOffNode
     *            adds one node id to be turn off
     */
    public void addTurnOffNode(TurnOffNode turnOffNode) {
        this.turnOffNodes.add(turnOffNode);
    }

    /**
     * @return o valor da propriedade turnOffNodes
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
     * Clear all historyData
     */
    public void clearHistory() {
        this.appThroughput.clear();
        this.wiredBandwidth.clear();
        this.meanDelayError.clear();
        this.deliveryRateError.clear();
    }

    @Override
    public SimulationParams clone() {

        SimulationParams cloned = new SimulationParams();

        cloned.setWiredFileDiscriminator(this.wiredFileDiscriminator);
        cloned.setWirelessFileDiscriminator(this.wirelessFileDiscriminator);
        cloned.setWirelessProtocol(this.wirelessProtocol);
        cloned.setInitialTime(this.initialTime);
        cloned.setEndTime(this.endTime);
        cloned.setTimeInterval(this.timeInterval);
        cloned.setNumberOfNodesInCluster(this.numberOfNodesInCluster);
        cloned.setNumberOfClusters(this.numberOfClusters);
        cloned.setInternalTraffic(this.internalTraffic);
        cloned.setExternalTraffic(this.externalTraffic);
        cloned.setDesirableInternalHopNumber(this.desirableInternalHopNumber);
        cloned.setDesirableExternalHopNumber(this.desirableExternalHopNumber);
        cloned.setPacketSize(this.packetSize);
        cloned.setWirelessQueueSize(this.wirelessQueueSize);
        cloned.setWiredQueueSize(this.wiredQueueSize);
        cloned.setIterations(this.iterations);

        cloned.setAppThroughputHistory(this.appThroughput);
        cloned.setWiredBandwidthHistory(this.wiredBandwidth);
        cloned.setLinkDelayHistory(this.linkDelay);
        cloned.setDissimilarityCoefficient(this.dissimilarityCoefficient);
        cloned.setMaxRelDifDeliveryRate(this.maxRelDifDeliveryRate);
        cloned.setMaxRelDifMeanDelay(this.maxRelDifMeanDelay);
        cloned.setConverged(this.converged);

        cloned.setInternalFlowMap(this.internalFlowMap);
        cloned.setExternalFlowMap(this.externalFlowMap);

        cloned.setTurnOffNodes(this.turnOffNodes);

        cloned.setMeanDelayError(this.meanDelayError);
        cloned.setDeliveryRateError(this.deliveryRateError);
        cloned.setSimulationApproach(this.simulationApproach);
        return cloned;
    }
}
