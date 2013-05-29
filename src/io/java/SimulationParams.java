
package io.java;


import java.util.Map;


/**
 * @author jchaves
 */
public class SimulationParams {

    private String wiredFileDiscriminator;
    private String wirelessFileDiscriminator;
    private Integer initialTime;
    private Integer endTime;

    private Integer numberOfNodesInCluster;
    private Integer numberOfClusters;
    private Float internalTraffic;
    private Float externalTraffic;
    private Integer packetSize;
    private String linkDelay;
    private Integer wirelessQueueSize;
    private Integer wiredQueueSize;

    private Float timeInterval;
    private Float appThroughput;
    private String wiredBandwidth;
    private Float dissimilarityCoefficient;
    private Float maxRelDifDeliveryRate;
    private Float maxRelDifMeanDelay;
    private Boolean converged;

    private Map<NodeData, NodeData> internalFlowMap;
    private Map<NodeData, NodeData> externalFlowMap;

    /**
     * @return appThroughput
     */
    public Float getAppThroughput() {
        return this.appThroughput;
    }

    /**
     * @param appThroughput
     */
    public void setAppThroughput(Float appThroughput) {
        this.appThroughput = appThroughput;
    }

    /**
     * @return wiredBandwidth
     */
    public String getWiredBandwidth() {
        return this.wiredBandwidth;
    }

    /**
     * @param wiredBandwidth
     */
    public void setWiredBandwidth(String wiredBandwidth) {
        this.wiredBandwidth = wiredBandwidth;
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
     * @return linkDelay
     */
    public String getLinkDelay() {
        return this.linkDelay;
    }

    /**
     * @param linkDelay
     */
    public void setLinkDelay(String linkDelay) {
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

    @Override
    public SimulationParams clone() {

        SimulationParams cloned = new SimulationParams();

        cloned.setWiredFileDiscriminator(this.wiredFileDiscriminator);
        cloned.setWirelessFileDiscriminator(this.wirelessFileDiscriminator);
        cloned.setInitialTime(this.initialTime);
        cloned.setEndTime(this.endTime);
        cloned.setTimeInterval(this.timeInterval);
        cloned.setNumberOfNodesInCluster(this.numberOfNodesInCluster);
        cloned.setNumberOfClusters(this.numberOfClusters);
        cloned.setInternalTraffic(this.internalTraffic);
        cloned.setExternalTraffic(this.externalTraffic);
        cloned.setPacketSize(this.packetSize);
        cloned.setLinkDelay(this.linkDelay);
        cloned.setWirelessQueueSize(this.wirelessQueueSize);
        cloned.setWiredQueueSize(this.wiredQueueSize);

        cloned.setAppThroughput(this.appThroughput);
        cloned.setWiredBandwidth(this.wiredBandwidth);
        cloned.setDissimilarityCoefficient(this.dissimilarityCoefficient);
        cloned.setMaxRelDifDeliveryRate(this.maxRelDifDeliveryRate);
        cloned.setMaxRelDifMeanDelay(this.maxRelDifMeanDelay);
        cloned.setConverged(this.converged);

        cloned.setInternalFlowMap(this.internalFlowMap);
        cloned.setExternalFlowMap(this.externalFlowMap);

        return cloned;
    }
}
