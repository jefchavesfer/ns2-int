
package io.java;


import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * @author jchaves
 */
public class EntranceParser {

    private String simParFileRadical;
    private SimulatorEntranceXMLHandler handler = new SimulatorEntranceXMLHandler();

    /**
     * @param simParFileRadical
     */
    public EntranceParser(String simParFileRadical) {
        super();
        this.simParFileRadical = simParFileRadical;
    }

    /**
     * @return simulation param list
     */
    public List<SimulationParams> parseXML() {

        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(this.simParFileRadical + ".xml", this.handler);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.handler.getParsedSimulationParameters();
    }
}
