
package io.java;


import java.util.Vector;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Simulator Param XML Handler
 * 
 * @author jchaves
 */
public class SimulatorEntranceXMLHandler extends DefaultHandler {

    private Logger log = Logger.getLogger(DefaultHandler.class.getName());
    private Vector<SimulationParams> simulationProfiles;
    private String buffer;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        System.out.println("Start Element :" + qName);

        if (qName.equalsIgnoreCase("simulationSchema")) {
            this.simulationProfiles = new Vector<SimulationParams>();
        }

        if (qName.equalsIgnoreCase("simulationProfile")) {
            this.simulationProfiles.add(new SimulationParams());
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("numberOfNodesInCluster")) {
            this.log.info("numberOfNodesInCluster: " + this.buffer);
            this.simulationProfiles.lastElement().setNumberOfNodesInCluster(Integer.valueOf(this.buffer));
        }

        if (qName.equalsIgnoreCase("numberOfClusters")) {
            this.log.info("numberOfClusters: " + this.buffer);
            this.simulationProfiles.lastElement().setNumberOfClusters(Integer.valueOf(this.buffer));
        }

        if (qName.equalsIgnoreCase("internalTraffic")) {
            this.log.info("internalTraffic: " + this.buffer);
            this.simulationProfiles.lastElement().setInternalTraffic(Float.valueOf(this.buffer));
        }

        if (qName.equalsIgnoreCase("externalTraffic")) {
            this.log.info("externalTraffic: " + this.buffer);
            this.simulationProfiles.lastElement().setExternalTraffic(Float.valueOf(this.buffer));
        }

        if (qName.equalsIgnoreCase("packetSize")) {
            this.log.info("packetSize: " + this.buffer);
            this.simulationProfiles.lastElement().setPacketSize(Integer.valueOf(this.buffer));
        }

        if (qName.equalsIgnoreCase("timeInterval")) {
            this.log.info("timeInterval: " + this.buffer);
            this.simulationProfiles.lastElement().setTimeInterval(Float.valueOf(this.buffer));
        }

        if (qName.equalsIgnoreCase("appThroughput")) {
            this.log.info("appThroughput: " + this.buffer);
            this.simulationProfiles.lastElement().setAppThroughput(Float.valueOf(this.buffer));
        }

        if (qName.equalsIgnoreCase("wiredBandwidth")) {
            this.log.info("wiredBandwidth: " + this.buffer);
            this.simulationProfiles.lastElement().setWiredBandwidth(this.buffer);
        }

        if (qName.equalsIgnoreCase("linkDelay")) {
            this.log.info("linkDelay: " + this.buffer);
            this.simulationProfiles.lastElement().setLinkDelay(this.buffer);
        }

        if (qName.equalsIgnoreCase("wiredQueueSize")) {
            this.log.info("wiredQueueSize: " + this.buffer);
            this.simulationProfiles.lastElement().setWiredQueueSize(Integer.valueOf(this.buffer));
        }

        if (qName.equalsIgnoreCase("wiredFileDiscriminator")) {
            this.log.info("wiredFileDiscriminator: " + this.buffer);
            this.simulationProfiles.lastElement().setWiredFileDiscriminator(this.buffer);
        }

        if (qName.equalsIgnoreCase("initialTime")) {
            this.log.info("initialTime: " + this.buffer);
            this.simulationProfiles.lastElement().setInitialTime(Integer.valueOf(this.buffer));
        }

        if (qName.equalsIgnoreCase("endTime")) {
            this.log.info("endTime: " + this.buffer);
            this.simulationProfiles.lastElement().setEndTime(Integer.valueOf(this.buffer));
        }

        if (qName.equalsIgnoreCase("wirelessQueueSize")) {
            this.log.info("wirelessQueueSize: " + this.buffer);
            this.simulationProfiles.lastElement().setWirelessQueueSize(Integer.valueOf(this.buffer));
        }

        if (qName.equalsIgnoreCase("wirelessFileDiscriminator")) {
            this.log.info("wirelessFileDiscriminator: " + this.buffer);
            this.simulationProfiles.lastElement().setWirelessFileDiscriminator(this.buffer);
        }

        if (qName.equalsIgnoreCase("maxRelDifDeliveryRate")) {
            this.log.info("maxRelDifDeliveryRate: " + this.buffer);
            this.simulationProfiles.lastElement().setMaxRelDifDeliveryRate(Float.valueOf(this.buffer));
        }

        if (qName.equalsIgnoreCase("maxRelDifMeanDelay")) {
            this.log.info("maxRelDifMeanDelay: " + this.buffer);
            this.simulationProfiles.lastElement().setMaxRelDifMeanDelay(Float.valueOf(this.buffer));
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        this.buffer = new String(ch, start, length);
    }

    /**
     * @return list of simulator param data
     */
    public Vector<SimulationParams> getParsedSimulationParameters() {
        return this.simulationProfiles;
    }
}
