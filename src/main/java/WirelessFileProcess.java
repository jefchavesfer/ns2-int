package main.java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import tree.java.FlowNode;
import tree.java.DataNode;

class WirelessFileProcess {
	
	private List<TclGeneratorSimulationData> tclGenSimulationData;
	private Integer  nn;
	private Integer numberOfScripts;
	private Integer i;
	private String fileDiscriminator;
	private Float timeInterval;
	private Logger log;
	
	
	public WirelessFileProcess(String fileDiscriminator, Integer numberOfScripts, Integer nn) {
		this.tclGenSimulationData = new ArrayList<TclGeneratorSimulationData>();
		this.nn = nn;
		this.numberOfScripts = numberOfScripts;
		this.i = 0;
		this.fileDiscriminator = fileDiscriminator;
		this.log = Logger.getLogger(WirelessFileProcess.class.getName()); 
	}
	
	public void processFile() throws IOException {
		while(this.i < this.numberOfScripts){	
			String filename = this.fileDiscriminator + i + ".tr" ;
			System.out.println(filename);
			FileReader trFile = new FileReader(filename);
			String trLine;
			boolean first = true;
			TclGeneratorSimulationData tclSimulationData = new TclGeneratorSimulationData();
			String time = null;
			do {
				trLine = trFile.readLine();
				if (trLine != null) {
					String[] columns = trLine.split(" ");
					String packetStatus = columns[0];
					time = columns[1];
					
					if( "s".equals(packetStatus) || "r".equals(packetStatus) || "D".equals(packetStatus)){
						if(first){
							tclSimulationData.setT0(Float.valueOf(time));
							first = false;
						}
						tclSimulationData = this.organizeExperimentData(columns, tclSimulationData);
					} 
				}
			} while (trLine != null);
			if(!first){ //there's at least one parseable line
				tclSimulationData.setTf(Float.valueOf(time));
				this.timeInterval = tclSimulationData.getTf() - tclSimulationData.getT0();	
			}
			this.processExperimentData(tclSimulationData);
			tclSimulationData.setNn(this.nn);
			this.tclGenSimulationData.add(tclSimulationData);
			this.i++;
		}
	}

	private TclGeneratorSimulationData organizeExperimentData(String[] columns, TclGeneratorSimulationData tclSimulationData) {

		String packetStatus = columns[0];
		String sPacketNode = columns[2];
		String packetLayer = columns[3];
		String packetNumber = columns[6];
		String packetType = columns[7];
		String packetTime = columns[1];

		// belongs to the wireless domain
		Map<String, FlowNode> flowData = tclSimulationData.getFlowData();
		Map<String, DataNode> queueData = tclSimulationData.getQueueData();
		Map<String, DataNode> droppedData = tclSimulationData.getDroppedData();
		
		if (( "s".equals(packetStatus) || "r".equals(packetStatus) ) && "cbr".equals(packetType) && "AGT".equals(packetLayer)) {
			FlowNode flowNode = flowData.get(packetNumber);
			if (flowNode == null) {
				flowNode = new FlowNode();
				flowData.put(packetNumber, flowNode);
			} 
			String key =packetStatus + sPacketNode;
			if(flowNode.getTimeData().get(key) != null)
			{
				throw new RuntimeException("Fluxo de Pacotes Incoerente");
			}
			flowNode.getTimeData().put(key, Float.valueOf(packetTime));
		}
		if ("D".equals(packetStatus)) {
			DataNode dropData = droppedData.get(packetNumber);
			if (dropData != null){
				System.out.println("pacote " + packetNumber + " foi dropado de novo");
			} else{
				dropData = new DataNode();
				droppedData.put(packetNumber, dropData);
			}
			droppedData.get(packetNumber).incrNodeData();
		}
		return tclSimulationData;
	}
	
	private void processExperimentData(TclGeneratorSimulationData tclSimulationData) {
		Map<String, DataNode> throughputData = tclSimulationData.getThroughputData();
		Integer sentPackets = 0;
		Integer receivedPackets = 0; 
		Float timeSum  = 0f;
		Integer sucessfullDeliveries = 0;
		for( Entry<String, FlowNode> packetFlow : tclSimulationData.getFlowData().entrySet() ){
			FlowNode packetData = packetFlow.getValue();
			Map<String, Float> timeData = packetData.getTimeData();
			//System.out.println(timeData.toString());
			Iterator<Entry<String, Float>> iterator = timeData.entrySet().iterator();
			boolean first = true;
			
			String beginFlow= null;
			String endFlow=null;
			Entry<String, Float> timeEntry = null;
			while(iterator.hasNext()){
				//end time
				timeEntry = iterator.next();
				String key = timeEntry.getKey();
				//deliveryRate
				if( key.contains("s") ){
					if(first){
						Map<String, DataNode> droppedData = tclSimulationData.getDroppedData();
						if(!droppedData.containsKey(packetFlow.getKey())){
							//queue fail, transmission fail, strange fails
							DataNode nodeData = new DataNode();
							nodeData.incrNodeData();
							droppedData.put(packetFlow.getKey(), nodeData);
							System.out.println("packet " + packetFlow.getKey() + " shown suspicious transmission failure");
						}
						endFlow="dropped";
						//it is a dropped packet flow
					}
					sentPackets++;
				} else if (key.contains("r")){
					if( first ){
						endFlow = key;
						packetData.setEndTime(timeEntry.getValue());
						sucessfullDeliveries ++;
					}
					receivedPackets++;
				}
				first = false;
			}
			if (timeEntry != null){
				beginFlow = timeEntry.getKey();
				if( beginFlow.contains("r") ){
						throw new RuntimeException("Insane flow, it begins receiving packet");
				} else {
					packetData.setInitialTime(timeEntry.getValue());
					if( packetData.getEndTime() != null ){
						packetData.calculateTimeInterval();
						timeSum += packetData.getTimeInterval();
					}
				}
			}
			String flowString = beginFlow + endFlow;
			DataNode flowData = throughputData.get(flowString);
			if( flowData == null ){
				flowData = new DataNode();
				throughputData.put(flowString, flowData);
			}
			flowData.incrNodeData();
		}
		
		this.log.info("\n===========================================================================" +
					  "\nSimulation " + tclSimulationData.getT0() + " " + tclSimulationData.getTf()   +
					  "\n===========================================================================");
		
		//calculating mean flow throughput 
		for(Map.Entry<String, DataNode> entryFlowData : throughputData.entrySet()){
			DataNode flowData = entryFlowData.getValue();
			flowData.setFlowRate(1/(flowData.getNodeData()/this.timeInterval));			
			this.log.info("flowRate " + entryFlowData.getKey() +  ": " + flowData.getFlowRate());
		}
		//calculating delivery rate
		if(sentPackets != 0){
			tclSimulationData.setDeliveryRate(receivedPackets.floatValue() / sentPackets.floatValue());
		} else {
			tclSimulationData.setDeliveryRate(0f);
		}
		this.log.info("deliveryRate: " + tclSimulationData.getDeliveryRate());
		
		//calculating mean delay
		if(sucessfullDeliveries != 0){
			tclSimulationData.setMeanDelay(timeSum/sucessfullDeliveries);
		} else {
			tclSimulationData.setMeanDelay(0f);
		}
		this.log.info("meanDelay: " + tclSimulationData.getMeanDelay());
		
		//tclSimulationData.setThroughputData(throughputData);
		//this.tclGenSimulationData.add(tclSimulationData);
		this.log.info(throughputData.toString());
	}

	public List<TclGeneratorSimulationData> getTclGenSimulationData() {
		return tclGenSimulationData;
	}

	public void setTclGenSimulationData(
			List<TclGeneratorSimulationData> tclGenSimulationData) {
		this.tclGenSimulationData = tclGenSimulationData;
	}
}