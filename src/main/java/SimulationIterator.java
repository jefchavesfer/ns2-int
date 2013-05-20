package main.java;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tree.java.DataNode;

public class SimulationIterator {
	
	private List<TclGeneratorSimulationData> wiredSimulationData;
	private List<TclGeneratorSimulationData> wirelessSimulationData;
	private SimulationParams simulationParams;
	private Logger log;
	
	public SimulationIterator(
			List<TclGeneratorSimulationData> wiredSimulationData,
			List<TclGeneratorSimulationData> wirelessSimulationData,
			SimulationParams previousParams) {
		super();
		this.wiredSimulationData = wiredSimulationData;
		this.wirelessSimulationData = wirelessSimulationData;
		this.simulationParams = previousParams;
		this.log = Logger.getLogger(Main.class.getName()); 
	}
	
	private Float fabs(Float val){
		if (val < 0) return -val;
		else return val;
	}
	
	public SimulationParams generateNewSimulationParams()
	{
		
		Iterator<TclGeneratorSimulationData> iteratorWireless = this.wirelessSimulationData.iterator();
		Iterator<TclGeneratorSimulationData> iteratorWired = this.wiredSimulationData.iterator();
		
		Float wiredSimulationDelay = 0f;
		Float wirelessSimulationDelay = 0f;
		Float wiredSimulationDeliveryRate = 0f;
		Float wirelessSimulationDeliveryRate = 0f;
		
		while( iteratorWireless.hasNext() && iteratorWired.hasNext() ){
			TclGeneratorSimulationData wirelessSimulation = iteratorWireless.next();
			TclGeneratorSimulationData wiredSimulation = iteratorWired.next();

			//deciding how throughput data is equal
			Map<String, DataNode> wiredThroughputData = wiredSimulation.getThroughputData();
			Float wiredDeliveryRate = wiredSimulation.getDeliveryRate();
			Float wiredMeanDelay = wiredSimulation.getMeanDelay();
			
			Map<String, DataNode> wirelessThroughputData = wirelessSimulation.getThroughputData();
			Float wirelessDeliveryRate = wirelessSimulation.getDeliveryRate();
			Float wirelessMeanDelay = wirelessSimulation.getMeanDelay();  
			//for now it will be like this
			//wirelessMeanDelay = wirelessMeanDelay/2;
			
			wiredSimulationDelay += wiredMeanDelay;
			wirelessSimulationDelay += wirelessMeanDelay;
			wirelessSimulationDeliveryRate += wirelessDeliveryRate;
			wiredSimulationDeliveryRate += wiredDeliveryRate;
		}
		
		wirelessSimulationDelay = wirelessSimulationDelay / (wirelessSimulationData.size() - 1);
		wiredSimulationDelay = wiredSimulationDelay / (wiredSimulationData.size() - 1);
		
		wirelessSimulationDeliveryRate = wirelessSimulationDeliveryRate / (wirelessSimulationData.size() - 1);
		wiredSimulationDeliveryRate = wiredSimulationDeliveryRate / (wiredSimulationData.size() - 1);
		
		Float errRelDelivery = (wirelessSimulationDeliveryRate - wiredSimulationDeliveryRate)/wiredSimulationDeliveryRate;
		Float errRelDelay = (wirelessSimulationDelay - wiredSimulationDelay) / wiredSimulationDelay;
		
		log.info("\n===========================================================================" +
				 "\nSimulation Iterator Report "												 +
				 "\n===========================================================================");
		
		SimulationParams newSimulationParams = this.simulationParams.clone();
		if( fabs(errRelDelivery) > this.simulationParams.getMaxRelDifDeliveryRate()){
				log.info("STATE: trying to balance devivery rates");
				newSimulationParams.setAppThroughput(this.simulationParams.getAppThroughput()*(1 + errRelDelivery));
		}
		else
		{
			log.info("STATE: trying to balance mean delays");
			//here we try to lower the channel width in order to balance the mean delay
			if( fabs(errRelDelay) > this.simulationParams.getMaxRelDifMeanDelay()){
				Float wiredBandwidth = Float.valueOf(this.simulationParams.getWiredBandwidth().substring(0, this.simulationParams.getWiredBandwidth().length() - 2));
				newSimulationParams.setWiredBandwidth(wiredBandwidth * (1 + errRelDelay) + "Mb");
			}
			else
			{
				//WIN
				log.info("STATE: FOUND balanced parameters");
				newSimulationParams.setConverged(true);
			}
		}
		log.info("Relative error for delivery rate " + errRelDelivery );
		log.info("New wired bandwidth " + newSimulationParams.getAppThroughput());
		log.info("Relative error for mean delay    " + errRelDelay );
		log.info("New wired bandwidth " + newSimulationParams.getWiredBandwidth());
		return newSimulationParams;
	}

	public SimulationParams getNewSimulationParams(){
		return this.simulationParams;
	}

	public List<TclGeneratorSimulationData> getWiredSimulationData() {
		return wiredSimulationData;
	}

	public void setWiredSimulationData(
			List<TclGeneratorSimulationData> wiredSimulationData) {
		this.wiredSimulationData = wiredSimulationData;
	}

	public List<TclGeneratorSimulationData> getWirelessSimulationData() {
		return wirelessSimulationData;
	}

	public void setWirelessSimulationData(
			List<TclGeneratorSimulationData> wirelessSimulationData) {
		this.wirelessSimulationData = wirelessSimulationData;
	}	
}
