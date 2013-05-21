package io.java;

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
	
	public Float getAppThroughput() {
		return appThroughput;
	}

	public void setAppThroughput(Float appThroughput) {
		this.appThroughput = appThroughput;
	}

	public String getWiredBandwidth() {
		return wiredBandwidth;
	}

	public void setWiredBandwidth(String wiredBandwidth) {
		this.wiredBandwidth = wiredBandwidth;
	}

	public Integer getPacketSize() {
		return packetSize;
	}

	public void setPacketSize(Integer packetSize) {
		this.packetSize = packetSize;
	}

	public Float getInternalTraffic() {
		return internalTraffic;
	}

	public void setInternalTraffic(Float internalTraffic) {
		this.internalTraffic = internalTraffic;
	}

	public Float getExternalTraffic() {
		return externalTraffic;
	}

	public void setExternalTraffic(Float externalTraffic) {
		this.externalTraffic = externalTraffic;
	}

	public String getLinkDelay() {
		return linkDelay;
	}

	public void setLinkDelay(String linkDelay) {
		this.linkDelay = linkDelay;
	}

	public Float getDissimilarityCoefficient() {
		return dissimilarityCoefficient;
	}

	public void setDissimilarityCoefficient(Float dissimilarityCoefficient) {
		this.dissimilarityCoefficient = dissimilarityCoefficient;
	}

	public Float getMaxRelDifDeliveryRate() {
		return maxRelDifDeliveryRate;
	}

	public void setMaxRelDifDeliveryRate(Float maxRelDifDeliveryRate) {
		this.maxRelDifDeliveryRate = maxRelDifDeliveryRate;
	}

	public Float getMaxRelDifMeanDelay() {
		return maxRelDifMeanDelay;
	}

	public void setMaxRelDifMeanDelay(Float maxRelDifMeanDelay) {
		this.maxRelDifMeanDelay = maxRelDifMeanDelay;
	}
	
	public Boolean isConverged() {
		return converged;
	}

	public void setConverged(Boolean converged) {
		this.converged = converged;
	}

	
	public Float getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(Float timeInterval) {
		this.timeInterval = timeInterval;
	}
	
	public String getWiredFileDiscriminator() {
		return wiredFileDiscriminator;
	}

	public void setWiredFileDiscriminator(String wiredFileDiscriminator) {
		this.wiredFileDiscriminator = wiredFileDiscriminator;
	}

	public String getWirelessFileDiscriminator() {
		return wirelessFileDiscriminator;
	}

	public void setWirelessFileDiscriminator(String wirelessFileDiscriminator) {
		this.wirelessFileDiscriminator = wirelessFileDiscriminator;
	}
	
	public Integer getInitialTime() {
		return initialTime;
	}

	public void setInitialTime(Integer initialTime) {
		this.initialTime = initialTime;
	}

	public Integer getEndTime() {
		return endTime;
	}

	public void setEndTime(Integer endTime) {
		this.endTime = endTime;
	}

	public Integer getNumberOfNodesInCluster() {
		return numberOfNodesInCluster;
	}

	public void setNumberOfNodesInCluster(Integer numberOfNodesInCluster) {
		this.numberOfNodesInCluster = numberOfNodesInCluster;
	}

	public Integer getNumberOfClusters() {
		return numberOfClusters;
	}

	public void setNumberOfClusters(Integer numberOfClusters) {
		this.numberOfClusters = numberOfClusters;
	}

	public Integer getWirelessQueueSize() {
		return wirelessQueueSize;
	}

	public void setWirelessQueueSize(Integer wirelessQueueSize) {
		this.wirelessQueueSize = wirelessQueueSize;
	}
	
	public Integer getWiredQueueSize() {
		return wiredQueueSize;
	}

	public void setWiredQueueSize(Integer wiredQueueSize) {
		this.wiredQueueSize = wiredQueueSize;
	}

	public SimulationParams clone(){
		
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

		return cloned;
	}
}
