
package main.java;


import io.java.SimulationApproach;
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

    private static Integer maxIterations = 40;
    private static String decFormat = "#.#####";
    private static Float decPrecision = 0.00001f;
    private static Float decEqPrecision = decPrecision * 10;
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

        if (this.wirelessSimulationData.size() != this.wiredSimulationData.size()) {
            throw new RuntimeException("Discrepancies between wired and wireless simulation sizes");
        }

        Float wiredSimulationDelay = 0f;
        Float wirelessSimulationDelay = 0f;
        Float wiredSimulationDeliveryRate = 0f;
        Float wirelessSimulationDeliveryRate = 0f;
        Integer numberOfNotEmptySimulations = 0;

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

            if (wirelessSimulation.getThroughputData().size() != wirelessSimulation.getThroughputData().size()) {
                throw new RuntimeException("Discrepancies between wired and wireless throughput data");
            }

            // gaps among samples are not needed in calculations
            if (!wirelessSimulation.getThroughputData().isEmpty()) {
                wiredSimulationDelay += wiredMeanDelay;
                wirelessSimulationDelay += wirelessMeanDelay;
                wirelessSimulationDeliveryRate += wirelessDeliveryRate;
                wiredSimulationDeliveryRate += wiredDeliveryRate;
                numberOfNotEmptySimulations++;
            } else {
                System.out.println("gap");
            }
        }

        String strLog = null;
        strLog =
                "\n==========================================================================="
                        + "\nSimulation Iterator Report "
                        + "\n===========================================================================\n";
        this.log.info(strLog);
        this.convergenceReport.write(strLog);
        SimulationParams newSimulationParams = this.simulationParams.clone();

        if (newSimulationParams.getIterations() > (SimulationIterator.maxIterations - 1)) {
            strLog = "max number of iterations achieved";
            newSimulationParams.setConverged(true);
            this.log.info(strLog);
            this.convergenceReport.write(strLog);
        }

        if (numberOfNotEmptySimulations > 0) {
            wirelessSimulationDelay = wirelessSimulationDelay / numberOfNotEmptySimulations;
            wiredSimulationDelay = wiredSimulationDelay / numberOfNotEmptySimulations;

            wirelessSimulationDeliveryRate = wirelessSimulationDeliveryRate / numberOfNotEmptySimulations;
            wiredSimulationDeliveryRate = wiredSimulationDeliveryRate / numberOfNotEmptySimulations;

            Float errRelDelivery =
                    (wirelessSimulationDeliveryRate - wiredSimulationDeliveryRate) / wiredSimulationDeliveryRate;
            Float errRelDelay = (wirelessSimulationDelay - wiredSimulationDelay) / wiredSimulationDelay;
            Float correctionFactorDelay = null;
            Float correctionFactorDelivery = null;

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

                correctionFactorDelivery =
                        this.generateParamsToNewAppThroughputIteration(newSimulationParams, errRelDelivery);
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

                    if (this.simulationParams.getSimulationApproach() == SimulationApproach.ONLY_TROUGHPUT) {
                        // change ONLY appThroughput for the two variables
                        correctionFactorDelay =
                                this.generateParamsToNewAppThroughputIteration(newSimulationParams, errRelDelay);
                    } else {
                        // change wiredBandwidth AND appThroughput
                        correctionFactorDelay =
                                this.generateParamsToNewWiredBandwidthIteration(newSimulationParams, errRelDelay);
                    }
                } else {
                    // WIN
                    strLog = "STATE: FOUND balanced parameters\n";
                    this.log.info(strLog);
                    this.convergenceReport.write(strLog);
                    newSimulationParams.setConvergedMeanDelay(wirelessSimulationDelay);
                    newSimulationParams.setConverged(true);
                }
            }
            strLog =
                    "Relative error for delivery rate " + errRelDelivery + " correctionFactor "
                            + correctionFactorDelivery + "\n" + "New app throughput "
                            + newSimulationParams.getAppThroughput() + "\n" + "Relative error for mean delay    "
                            + errRelDelay + " correction Factor " + correctionFactorDelay + "\n"
                            + "New wired bandwidth " + newSimulationParams.getWiredBandwidth() + "\n" + "tamWired "
                            + this.wiredSimulationData.size() + " tamWireless " + this.wirelessSimulationData.size();
        } else {
            strLog = "There is no simulation data";
            // it is impossible iterate nothing
            newSimulationParams.setConverged(true);
        }
        this.log.info(strLog);
        this.convergenceReport.write(strLog);
        return newSimulationParams;
    }

    /**
     * @param newSimulationParams
     * @param errRelDelivery
     * @return correctionFactorDelivery
     * @throws IOException
     */
    private Float generateParamsToNewAppThroughputIteration(SimulationParams newSimulationParams, Float errRelDelivery)
            throws IOException {
        Float correctionFactorDelivery;
        correctionFactorDelivery = errRelDelivery;
        Float newAppThroughput = null;
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
                        Float.valueOf(this.simulationParams.getAppThroughputHistory().get(lastAppThroughputIndex - 2));
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
        DecimalFormat df = new DecimalFormat(decFormat);
        if (newAppThroughput == null) {
            newSimulationParams.setDeliveryRateError(correctionFactorDelivery);
            newAppThroughput = appThroughput * (1 + correctionFactorDelivery);
            if (this.equalsFloat(newAppThroughput, appThroughput, decEqPrecision)) {
                newAppThroughput = appThroughput + correctionFactorDelivery;
            }
            if (newAppThroughput < 0) {
                newAppThroughput = decPrecision;
                Integer listSize = this.simulationParams.getAppThroughputHistory().size();
                if (listSize > 1) {
                    if ((this.simulationParams.getAppThroughputHistory().get(listSize - 1) == decPrecision)
                            && (this.simulationParams.getAppThroughputHistory().get(listSize - 1) == this.simulationParams
                                    .getAppThroughputHistory().get(listSize - 2))) {
                        String strLog = "There is NO smaller value for throughput";
                        this.log.info(strLog);
                        this.convergenceReport.write(strLog);
                        newSimulationParams.setConverged(true);
                    }
                }
            }
        }
        String strNewAppThroughput = df.format(newAppThroughput);
        newSimulationParams.setAppThroughput(Float.valueOf(strNewAppThroughput));
        return correctionFactorDelivery;
    }

    /**
     * @param newSimulationParams
     * @param errRelDelay
     * @return correctionFactorDelay
     * @throws IOException
     */
    private Float generateParamsToNewWiredBandwidthIteration(SimulationParams newSimulationParams, Float errRelDelay)
            throws IOException {
        Float correctionFactorDelay = errRelDelay;
        Float newWiredBandwidth = null;
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
        DecimalFormat df = new DecimalFormat(decFormat);
        if (newWiredBandwidth == null) {
            newSimulationParams.setMeanDelayError(correctionFactorDelay);
            newWiredBandwidth = (wiredBandwidth * (1 - correctionFactorDelay));
            if (this.equalsFloat(wiredBandwidth, newWiredBandwidth, decEqPrecision)) {
                newWiredBandwidth = wiredBandwidth - correctionFactorDelay;
            }
            if (newWiredBandwidth < 0) {
                newWiredBandwidth = decPrecision;
                Integer listSize = this.simulationParams.getWiredBandwidthHistory().size();
                if (listSize > 1) {
                    if ((this.simulationParams.getWiredBandwidthHistory().get(listSize - 1) == (decPrecision + "Mb"))
                            && (this.simulationParams.getWiredBandwidthHistory().get(listSize - 1) == this.simulationParams
                                    .getWiredBandwidthHistory().get(listSize - 2))) {
                        String strLog = "There is NO smaller value for wiredBandwidth";
                        this.log.info(strLog);
                        this.convergenceReport.write(strLog);
                        newSimulationParams.setConverged(true);
                    }
                }
            }
        }
        String strNewWiredBandWidth = df.format(newWiredBandwidth);
        newSimulationParams.setWiredBandwidth(strNewWiredBandWidth + "Mb");
        return correctionFactorDelay;
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
