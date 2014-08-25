
package main.java;


import io.java.SimulationApproach;
import io.java.SimulationParams;
import io.java.TclGeneratorSimulationData;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;


/**
 * Implements the decision process taken for stopping or continuing the simulation iterative process
 * 
 * @author jchaves
 */
public class SimulationIterator {

    private static Integer maxIterations = 40;
    private static String decFormat = "#.#####";
    private static Float decPrecision = 0.00001f;
    private static Float decEqPrecision = decPrecision * 10;
    private Map<String, Integer> masterPacketCountList;
    private List<TclGeneratorSimulationData> wiredSimulationData;
    private List<TclGeneratorSimulationData> wirelessSimulationData;
    private SimulationParams simulationParams;
    private Logger log;
    private FileWriter convergenceReport;
    private boolean debug;

    /**
     * @param debug
     * @param wiredSimulationData
     * @param wirelessSimulationData
     * @param previousParams
     * @param convergenceReport
     */
    public SimulationIterator(boolean debug, List<TclGeneratorSimulationData> wiredSimulationData,
            List<TclGeneratorSimulationData> wirelessSimulationData, SimulationParams previousParams,
            FileWriter convergenceReport) {
        this.wiredSimulationData = wiredSimulationData;
        this.wirelessSimulationData = wirelessSimulationData;
        this.simulationParams = previousParams;
        this.log = Logger.getLogger(Main.class.getName());
        this.convergenceReport = convergenceReport;
        this.debug = debug;
        this.masterPacketCountList = new HashMap<String, Integer>();
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

    private <K, V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K, V> map) {

        List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());

        Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {

            @Override
            public int compare(Entry<K, V> e1, Entry<K, V> e2) {
                return e2.getValue().compareTo(e1.getValue());
            }
        });

        return sortedEntries;
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

            Float wiredDeliveryRate = wiredSimulation.getDeliveryRate();
            Float wiredMeanDelay = wiredSimulation.getMeanDelay();

            Float wirelessDeliveryRate = wirelessSimulation.getDeliveryRate();
            Float wirelessMeanDelay = wirelessSimulation.getMeanDelay();

            // for now it will be like this
            // wirelessMeanDelay = wirelessMeanDelay/2;

            if (wirelessSimulation.getThroughputData().size() != wirelessSimulation.getThroughputData().size()) {
                throw new RuntimeException("Discrepancies between wired and wireless throughput data");
            }

            // packet through node master count
            for (Map.Entry<String, Integer> nodeCount : wirelessSimulation.getNodePacketCountData().entrySet()) {
                Integer packetCount = this.masterPacketCountList.get(nodeCount.getKey());
                if (packetCount == null) {
                    this.masterPacketCountList.put(nodeCount.getKey(), nodeCount.getValue());
                } else {
                    this.masterPacketCountList.put(nodeCount.getKey(), packetCount + nodeCount.getValue());
                }
            }

            // gaps among samples are not needed in calculations
            if (!wirelessSimulation.getThroughputData().isEmpty()) {
                wiredSimulationDelay += wiredMeanDelay;
                wirelessSimulationDelay += wirelessMeanDelay;
                wirelessSimulationDeliveryRate += wirelessDeliveryRate;
                wiredSimulationDeliveryRate += wiredDeliveryRate;
                numberOfNotEmptySimulations++;
            } else {
                if (this.debug) {
                    System.out.println("gap");
                }
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

                    if (this.simulationParams.getSimulationApproach() == SimulationApproach.ONLY_THROUGHPUT) {
                        // change meanDelay AND appThroughput
                        correctionFactorDelay =
                                this.generateParamsToNewLinkDelayIteration(newSimulationParams, errRelDelay);
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
                            + "New wired bandwidth " + newSimulationParams.getWiredBandwidth() + "\n"
                            + "New link delay " + newSimulationParams.getLinkDelay() + "\n" + "tamWired "
                            + this.wiredSimulationData.size() + " tamWireless " + this.wirelessSimulationData.size()
                            + "\n";

            strLog = strLog + "\npacket count " + this.masterPacketCountList.toString() + "\n";

        } else {
            strLog = "There is no simulation data\n";
            // it is impossible iterate nothing
            newSimulationParams.setConverged(true);
        }

        if (newSimulationParams.getIterations() > (SimulationIterator.maxIterations - 1)) {
            strLog = "max number of iterations achieved\n";
            newSimulationParams.setConvergedMeanDelay(wirelessSimulationDelay);
            newSimulationParams.setConverged(true);
            this.log.info(strLog);
            this.convergenceReport.write(strLog);
        }

        newSimulationParams.setIterations(newSimulationParams.getIterations() + 1);
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
        if (this.fabs(correctionFactorDelivery) > (4 * this.simulationParams.getMaxRelDifDeliveryRate())) {
            // correction factor is too big or small
            if (correctionFactorDelivery > 0) {
                correctionFactorDelivery = 4 * this.simulationParams.getMaxRelDifDeliveryRate();
            } else {
                correctionFactorDelivery = -4 * this.simulationParams.getMaxRelDifDeliveryRate();
            }
        } else if (this.simulationParams.getDeliveryRateError().size() > 0) {
            Integer lastErrorIndex = this.simulationParams.getDeliveryRateError().size() - 1;
            Float lastError = this.simulationParams.getDeliveryRateError().get(lastErrorIndex);
            Integer lastAppThroughputIndex = this.simulationParams.getAppThroughputHistory().size() - 1;
            if (((lastError * correctionFactorDelivery) < 0) && (lastAppThroughputIndex > 0)) {
                Float previousAppThroughput =
                        Float.valueOf(this.simulationParams.getAppThroughputHistory().get(lastAppThroughputIndex - 1));
                // errors with different signals
                newAppThroughput = (previousAppThroughput + appThroughput) / 2;
                // clean history and error history
                newSimulationParams.getAppThroughputHistory().clear();
                newSimulationParams.getDeliveryRateError().clear();
            } else {
                // Float relErrorTest = (errRelDelivery - lastError) / lastError;
                if (this.fabs(correctionFactorDelivery) < (2 * this.simulationParams.getMaxRelDifDeliveryRate())) {
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
                        String strLog = "There is NO smaller value for throughput\n";
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

    private Float generateParamsToNewLinkDelayIteration(SimulationParams newSimulationParams, Float errRelDelay)
            throws IOException {
        Float correctionFactorLinkDelay;
        correctionFactorLinkDelay = errRelDelay;
        Float newLinkDelay = null;
        Float linkDelay =
                Float.valueOf(this.simulationParams.getLinkDelay().substring(0,
                        this.simulationParams.getLinkDelay().length() - 2));
        if (this.fabs(correctionFactorLinkDelay) > (4 * this.simulationParams.getMaxRelDifMeanDelay())) {
            // correction factor is too big or small
            if (correctionFactorLinkDelay > 0) {
                correctionFactorLinkDelay = 4 * this.simulationParams.getMaxRelDifMeanDelay();
            } else {
                correctionFactorLinkDelay = -4 * this.simulationParams.getMaxRelDifMeanDelay();
            }
        } else if (this.simulationParams.getMeanDelayError().size() > 0) {
            Integer lastErrorIndex = this.simulationParams.getMeanDelayError().size() - 1;
            Float lastError = this.simulationParams.getMeanDelayError().get(lastErrorIndex);
            Integer lastLinkDelayIndex = this.simulationParams.getLinkDelayHistory().size() - 1;
            if (((lastError * correctionFactorLinkDelay) < 0) && (lastLinkDelayIndex > 0)) {

                Float previousLinkDelay =
                        Float.valueOf(this.simulationParams
                                .getLinkDelayHistory()
                                .get(lastLinkDelayIndex - 1)
                                .substring(
                                        0,
                                        this.simulationParams.getLinkDelayHistory().get(lastLinkDelayIndex - 1)
                                                .length() - 2));
                // errors with different signals
                newLinkDelay = (previousLinkDelay + linkDelay) / 2;
                // clean history and error history
                newSimulationParams.getLinkDelayHistory().clear();
                newSimulationParams.getMeanDelayError().clear();
            } else {
                if (this.fabs(correctionFactorLinkDelay) < (2 * this.simulationParams.getMaxRelDifMeanDelay())) {
                    if (correctionFactorLinkDelay > 0) {
                        correctionFactorLinkDelay = 2 * this.simulationParams.getMaxRelDifMeanDelay();
                    } else {
                        correctionFactorLinkDelay = -2 * this.simulationParams.getMaxRelDifMeanDelay();
                    }
                }
            }
        }
        DecimalFormat df = new DecimalFormat(decFormat);
        if (newLinkDelay == null) {
            newSimulationParams.setDeliveryRateError(correctionFactorLinkDelay);
            newLinkDelay = linkDelay * (1 + correctionFactorLinkDelay);
            if (this.equalsFloat(newLinkDelay, linkDelay, decEqPrecision)) {
                newLinkDelay = linkDelay + correctionFactorLinkDelay;
            }
            if (newLinkDelay < 0) {
                newLinkDelay = decPrecision;
                Integer listSize = this.simulationParams.getLinkDelayHistory().size();
                if (listSize > 1) {
                    if ((this.simulationParams.getLinkDelayHistory().get(listSize - 1) == (decPrecision + "ms"))
                            && (this.simulationParams.getLinkDelayHistory().get(listSize - 1) == this.simulationParams
                                    .getLinkDelayHistory().get(listSize - 2))) {
                        String strLog = "There is NO smaller value for linkDelay\n";
                        // TODO: implement the case to low throughput if wired simulation is slower than wireless
                        this.log.info(strLog);
                        this.convergenceReport.write(strLog);
                        newSimulationParams.setConverged(true);
                    }
                }
            }
        }
        String newStrLinkDelay = df.format(newLinkDelay);
        newSimulationParams.setLinkDelay(newStrLinkDelay + "ms");
        return correctionFactorLinkDelay;
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
        if (this.fabs(correctionFactorDelay) > (4 * this.simulationParams.getMaxRelDifMeanDelay())) {
            // correction factor is too big or small
            if (correctionFactorDelay > 0) {
                correctionFactorDelay = 4 * this.simulationParams.getMaxRelDifMeanDelay();
            } else {
                correctionFactorDelay = -4 * this.simulationParams.getMaxRelDifMeanDelay();
            }
        } else if (this.simulationParams.getMeanDelayError().size() > 0) {
            // has history data
            Integer lastErrorIndex = this.simulationParams.getMeanDelayError().size() - 1;
            Float lastError = this.simulationParams.getMeanDelayError().get(lastErrorIndex);
            Integer lastWiredBandWidthIndex = this.simulationParams.getWiredBandwidthHistory().size() - 1;
            if (((lastError * correctionFactorDelay) < 0) && (lastWiredBandWidthIndex > 0)) {
                Float previousWiredBandwidth =
                        Float.valueOf(this.simulationParams
                                .getWiredBandwidthHistory()
                                .get(lastWiredBandWidthIndex - 1)
                                .substring(
                                        0,
                                        this.simulationParams.getWiredBandwidthHistory()
                                                .get(lastWiredBandWidthIndex - 1).length() - 2));
                // errors with different signals
                newWiredBandwidth = (previousWiredBandwidth + wiredBandwidth) / 2;
                // clean history and error history
                newSimulationParams.getWiredBandwidthHistory().clear();
                newSimulationParams.getMeanDelayError().clear();
            } else {
                // the error difference is too small
                if (this.fabs(correctionFactorDelay) < (2 * this.simulationParams.getMaxRelDifMeanDelay())) {
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
                        String strLog = "There is NO smaller value for wiredBandwidth\n";
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
