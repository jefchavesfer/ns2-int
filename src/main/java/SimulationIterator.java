
package main.java;


import io.java.SimulationParams;
import io.java.TclGeneratorSimulationData;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
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

    private boolean equalsFloat(Float v1, Float v2, Float prec) {
        Float diff = this.fabs(v1 - v2);
        if (diff <= prec) {
            return true;
        }
        return false;
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
        Float newWiredBandwidth = null;
        Float newAppThroughput = null;

        if (this.fabs(errRelDelivery) > this.simulationParams.getMaxRelDifDeliveryRate()) {
            strLog = "STATE: trying to balance devivery rates\n";
            this.log.info(strLog);
            this.convergenceReport.write(strLog);

            strLog = "Tamanho erro delivery: " + this.simulationParams.getDeliveryRateError().size() + "\n";
            this.convergenceReport.write(strLog);
            this.log.info(strLog);

            strLog = "Tamanho app throughput: " + this.simulationParams.getAppThroughputHistory().size() + "\n";
            this.convergenceReport.write(strLog);
            this.log.info(strLog);

            Float appThroughput = this.simulationParams.getAppThroughput();
            if (correctionFactorDelivery >= 1.0f) {
                correctionFactorDelivery = 0.8f;
            } else if (correctionFactorDelivery <= -1.0f) {
                correctionFactorDelivery = -0.8f;
            } else if (this.simulationParams.getDeliveryRateError().size() > 0) {
                Integer lastErrorIndex = this.simulationParams.getDeliveryRateError().size() - 1;
                Float lastError = this.simulationParams.getDeliveryRateError().get(lastErrorIndex);
                if ((lastError * errRelDelivery) < 0) {
                    Integer lastAppThroughputIndex = this.simulationParams.getAppThroughputHistory().size();
                    Float previousAppThroughput =
                            Float.valueOf(this.simulationParams.getAppThroughputHistory().get(
                                    lastAppThroughputIndex - 2));
                    // errors with different signals
                    newAppThroughput = (previousAppThroughput + appThroughput) / 2;
                    // clean history and error history
                    newSimulationParams.getAppThroughputHistory().clear();
                    newSimulationParams.getDeliveryRateError().clear();
                } else {
                    // Float relErrorTest = (errRelDelivery - lastError) / lastError;
                    if (this.fabs(errRelDelivery) < (2 * this.simulationParams.getMaxRelDifDeliveryRate())) {
                        if (correctionFactorDelivery > 0) {
                            correctionFactorDelivery = 2 * this.simulationParams.getMaxRelDifDeliveryRate();
                        } else {
                            correctionFactorDelivery = -2 * this.simulationParams.getMaxRelDifDeliveryRate();
                        }
                    }
                }
            }
            DecimalFormat df = new DecimalFormat("#.###");
            if (newAppThroughput == null) {
                newSimulationParams.setDeliveryRateError(correctionFactorDelivery);
                newAppThroughput = appThroughput * (1 + correctionFactorDelivery);
                if (this.equalsFloat(newAppThroughput, appThroughput, 0.001f)) {
                    newAppThroughput = appThroughput + correctionFactorDelay;
                }
                if (newAppThroughput < 0) {
                    newAppThroughput = 0.0001f;
                }
            }
            String strNewAppThroughput = df.format(newAppThroughput);
            newSimulationParams.setAppThroughput(Float.valueOf(strNewAppThroughput));
        } else {
            // here we try to lower the channel width in order to balance the mean delay
            if (this.fabs(errRelDelay) > this.simulationParams.getMaxRelDifMeanDelay()) {
                strLog = "STATE: trying to balance mean delays\n";
                this.log.info(strLog);
                this.convergenceReport.write(strLog);

                strLog = "Tamanho erro delay: " + this.simulationParams.getMeanDelayError().size() + "\n";
                this.convergenceReport.write(strLog);
                this.log.info(strLog);
                strLog = "Tamanho banda wired: " + this.simulationParams.getWiredBandwidthHistory().size() + "\n";
                this.convergenceReport.write(strLog);
                this.log.info(strLog);

                Float wiredBandwidth =
                        Float.valueOf(this.simulationParams.getWiredBandwidth().substring(0,
                                this.simulationParams.getWiredBandwidth().length() - 2));
                if (correctionFactorDelay >= 1.0f) {
                    // correction factor is too big
                    correctionFactorDelay = 0.8f;
                } else if (correctionFactorDelay <= -1.0f) {
                    // correction factor is too small
                    correctionFactorDelay = -0.8f;
                } else if (this.simulationParams.getMeanDelayError().size() > 0) {
                    // has history data
                    Integer lastErrorIndex = this.simulationParams.getMeanDelayError().size() - 1;
                    Float lastError = this.simulationParams.getMeanDelayError().get(lastErrorIndex);
                    if ((lastError * errRelDelay) < 0) {
                        Integer lastWiredBandWidthIndex = this.simulationParams.getWiredBandwidthHistory().size();
                        Float previousWiredBandwidth =
                                Float.valueOf(this.simulationParams
                                        .getWiredBandwidthHistory()
                                        .get(lastWiredBandWidthIndex - 2)
                                        .substring(
                                                0,
                                                this.simulationParams.getWiredBandwidthHistory()
                                                        .get(lastWiredBandWidthIndex - 2).length() - 2));
                        // errors with different signals
                        newWiredBandwidth = (previousWiredBandwidth + wiredBandwidth) / 2;
                        // clean history and error history
                        newSimulationParams.getWiredBandwidthHistory().clear();
                        newSimulationParams.getMeanDelayError().clear();
                    } else {
                        // Float relErrorTest = (errRelDelay - lastError) / lastError;
                        // the error difference is too small
                        if (this.fabs(errRelDelay) < (2 * this.simulationParams.getMaxRelDifMeanDelay())) {
                            if (errRelDelay > 0) {
                                correctionFactorDelay = 2 * this.simulationParams.getMaxRelDifMeanDelay();
                            } else {
                                correctionFactorDelay = -2 * this.simulationParams.getMaxRelDifMeanDelay();
                            }
                        }
                    }
                }
                DecimalFormat df = new DecimalFormat("#.###");
                if (newWiredBandwidth == null) {
                    newSimulationParams.setMeanDelayError(correctionFactorDelay);
                    newWiredBandwidth = (wiredBandwidth * (1 - correctionFactorDelay));
                    if (this.equalsFloat(wiredBandwidth, newWiredBandwidth, 0.001f)) {
                        newWiredBandwidth = wiredBandwidth - correctionFactorDelay;
                    }
                    if (newWiredBandwidth < 0) {
                        newWiredBandwidth = 0.0001f;
                    }
                }
                String strNewWiredBandWidth = df.format(newWiredBandwidth);
                newSimulationParams.setWiredBandwidth(strNewWiredBandWidth + "Mb");
            } else {
                // WIN
                strLog = "STATE: FOUND balanced parameters\n";
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
