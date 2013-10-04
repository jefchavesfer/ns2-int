
package main.java;


import io.java.SimulationParams;
import io.java.TclGeneratorSimulationData;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tree.java.DataNode;


/**
 * Implements the decision process taken for stopping or continuing the simulation
 * 
 * @author jchaves
 */
public class SimulationIterator {

    private List<TclGeneratorSimulationData> wiredSimulationData;
    private List<TclGeneratorSimulationData> wirelessSimulationData;
    private SimulationParams simulationParams;
    private Logger log;
    private FileWriter convergenceReport;

    /**
     * @param wiredSimulationData
     * @param wirelessSimulationData
     * @param previousParams
     * @param convergenceReport
     */
    public SimulationIterator(List<TclGeneratorSimulationData> wiredSimulationData,
            List<TclGeneratorSimulationData> wirelessSimulationData, SimulationParams previousParams,
            FileWriter convergenceReport) {
        super();
        this.wiredSimulationData = wiredSimulationData;
        this.wirelessSimulationData = wirelessSimulationData;
        this.simulationParams = previousParams;
        this.log = Logger.getLogger(Main.class.getName());
        this.convergenceReport = convergenceReport;
    }

    private Float fabs(Float val) {
        if (val < 0) {
            return -val;
        }
        return val;
    }

    /**
     * @return simulation params for the next simulation
     * @throws IOException
     */
    public SimulationParams generateNewSimulationParams() throws IOException {

        Iterator<TclGeneratorSimulationData> iteratorWireless = this.wirelessSimulationData.iterator();
        Iterator<TclGeneratorSimulationData> iteratorWired = this.wiredSimulationData.iterator();

        Float wiredSimulationDelay = 0f;
        Float wirelessSimulationDelay = 0f;
        Float wiredSimulationDeliveryRate = 0f;
        Float wirelessSimulationDeliveryRate = 0f;

        while (iteratorWireless.hasNext() && iteratorWired.hasNext()) {
            TclGeneratorSimulationData wirelessSimulation = iteratorWireless.next();
            TclGeneratorSimulationData wiredSimulation = iteratorWired.next();

            // deciding how throughput data is equal
            Map<String, DataNode> wiredThroughputData = wiredSimulation.getThroughputData();
            Float wiredDeliveryRate = wiredSimulation.getDeliveryRate();
            Float wiredMeanDelay = wiredSimulation.getMeanDelay();

            Map<String, DataNode> wirelessThroughputData = wirelessSimulation.getThroughputData();
            Float wirelessDeliveryRate = wirelessSimulation.getDeliveryRate();
            Float wirelessMeanDelay = wirelessSimulation.getMeanDelay();
            // for now it will be like this
            // wirelessMeanDelay = wirelessMeanDelay/2;

            wiredSimulationDelay += wiredMeanDelay;
            wirelessSimulationDelay += wirelessMeanDelay;
            wirelessSimulationDeliveryRate += wirelessDeliveryRate;
            wiredSimulationDeliveryRate += wiredDeliveryRate;
        }

        wirelessSimulationDelay = wirelessSimulationDelay / (this.wirelessSimulationData.size() - 1);
        wiredSimulationDelay = wiredSimulationDelay / (this.wiredSimulationData.size() - 1);

        wirelessSimulationDeliveryRate = wirelessSimulationDeliveryRate / (this.wirelessSimulationData.size() - 1);
        wiredSimulationDeliveryRate = wiredSimulationDeliveryRate / (this.wiredSimulationData.size() - 1);

        Float errRelDelivery =
                (wirelessSimulationDeliveryRate - wiredSimulationDeliveryRate) / wiredSimulationDeliveryRate;
        Float correctionFactorDelivery = errRelDelivery;
        Float errRelDelay = (wirelessSimulationDelay - wiredSimulationDelay) / wiredSimulationDelay;
        Float correctionFactorDelay = errRelDelay;

        String strLog = null;
        strLog =
                "\n==========================================================================="
                        + "\nSimulation Iterator Report "
                        + "\n===========================================================================\n";
        this.log.info(strLog);
        this.convergenceReport.write(strLog);

        SimulationParams newSimulationParams = this.simulationParams.clone();
        if (this.fabs(errRelDelivery) > this.simulationParams.getMaxRelDifDeliveryRate()) {
            strLog = "STATE: trying to balance devivery rates\n";
            this.log.info(strLog);
            this.convergenceReport.write(strLog);
            if (this.simulationParams.getDeliveryRateError().size() > 0) {
                Float prevErrRelDelivery = this.simulationParams.getDeliveryRateError().get(0);
                Float relErrorTest = (errRelDelivery - prevErrRelDelivery) / prevErrRelDelivery;
                if (this.fabs(relErrorTest) < (2 * this.simulationParams.getMaxRelDifDeliveryRate())) {
                    if (correctionFactorDelivery > 0) {
                        correctionFactorDelivery = 2 * this.simulationParams.getMaxRelDifDeliveryRate();
                    } else {
                        correctionFactorDelivery = -2 * this.simulationParams.getMaxRelDifDeliveryRate();
                    }
                }
            }
            newSimulationParams.setDeliveryRateError(correctionFactorDelivery);
            Float newThroughput = this.simulationParams.getAppThroughput() * (1 + correctionFactorDelivery);
            newSimulationParams.setAppThroughput(newThroughput);
        } else {
            strLog = "STATE: trying to balance mean delays\n";
            this.log.info(strLog);
            this.convergenceReport.write(strLog);
            // here we try to lower the channel width in order to balance the mean delay
            if (this.fabs(errRelDelay) > this.simulationParams.getMaxRelDifMeanDelay()) {
                Float wiredBandwidth =
                        Float.valueOf(this.simulationParams.getWiredBandwidth().substring(0,
                                this.simulationParams.getWiredBandwidth().length() - 2));
                if (this.simulationParams.getMeanDelayError().size() > 0) {
                    Float prevMeanDelayError = this.simulationParams.getMeanDelayError().get(0);
                    Float relErrorTest = (errRelDelay - prevMeanDelayError) / prevMeanDelayError;
                    if (this.fabs(relErrorTest) < (2 * this.simulationParams.getMaxRelDifMeanDelay())) {
                        if (errRelDelay > 0) {
                            correctionFactorDelay = 2 * this.simulationParams.getMaxRelDifMeanDelay();
                        } else {
                            correctionFactorDelay = -2 * this.simulationParams.getMaxRelDifMeanDelay();
                        }
                    }
                }
                newSimulationParams.setMeanDelayError(correctionFactorDelay);
                Float newWiredBandwidth = (wiredBandwidth * (1 - correctionFactorDelay));
                if (newWiredBandwidth < 0) {
                    newWiredBandwidth = 0.0001f;
                }
                newSimulationParams.setWiredBandwidth(newWiredBandwidth + "Mb");
            } else {
                // WIN
                strLog = "STATE: FOUND balanced parameters";
                this.log.info(strLog);
                this.convergenceReport.write(strLog);
                newSimulationParams.setConverged(true);
            }
        }
        strLog =
                "Relative error for delivery rate " + errRelDelivery + " correctionFactor " + correctionFactorDelivery
                        + "\n" + "New app throughput " + newSimulationParams.getAppThroughput() + "\n"
                        + "Relative error for mean delay    " + errRelDelay + " correction Factor "
                        + correctionFactorDelay + "\n" + "New wired bandwidth "
                        + newSimulationParams.getWiredBandwidth() + "\n";
        this.log.info(strLog);
        this.convergenceReport.write(strLog);
        return newSimulationParams;
    }

    /**
     * @return simulationParams
     */
    public SimulationParams getNewSimulationParams() {
        return this.simulationParams;
    }

    /**
     * @return list of {@link TclGeneratorSimulationData} from the wired simulation
     */
    public List<TclGeneratorSimulationData> getWiredSimulationData() {
        return this.wiredSimulationData;
    }

    /**
     * @param wiredSimulationData
     */
    public void setWiredSimulationData(List<TclGeneratorSimulationData> wiredSimulationData) {
        this.wiredSimulationData = wiredSimulationData;
    }

    /**
     * @return list of {@link TclGeneratorSimulationData} from the wireless simulation
     */
    public List<TclGeneratorSimulationData> getWirelessSimulationData() {
        return this.wirelessSimulationData;
    }

    /**
     * @param wirelessSimulationData
     */
    public void setWirelessSimulationData(List<TclGeneratorSimulationData> wirelessSimulationData) {
        this.wirelessSimulationData = wirelessSimulationData;
    }
}
