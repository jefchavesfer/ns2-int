/*
 * Copyright (c) 1999-2009 Touch Tecnologia e Informatica Ltda.
 * Gomes de Carvalho, 1666, 3o. Andar, Vila Olimpia, Sao Paulo, SP, Brasil.
 * Todos os direitos reservados.
 * 
 * Este software e confidencial e de propriedade da Touch Tecnologia e
 * Informatica Ltda. (Informacao Confidencial). As informacoes contidas neste
 * arquivo nao podem ser publicadas, e seu uso esta limitado de acordo com os
 * termos do contrato de licenca.
 */

package scriptGen.java;


import io.java.NodeData;
import io.java.SimulationParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;


/**
 * Make random traffic flow patterns
 * 
 * @author jchaves
 */

public class MakeFlows {

    private static Logger log;
    private static int bigN = 65535;
    private static Map<NodeData, NodeData> internalFlowMap;
    private static Map<NodeData, NodeData> externalFlowMap;
    private static Set<NodeData> eligibleNodes;
    private static Integer xn;
    private static Integer xc;
    private static Integer centerClusterIndex;
    private static Integer desirableWiredHopNumber;
    private static Integer desirableWirelessHopNumber;
    private static SimulationParams params;

    /**
     * @param simulationParams
     */
    public static void make(SimulationParams simulationParams) {
        xn = (int) Math.sqrt(simulationParams.getNumberOfNodesInCluster());
        xc = (int) Math.sqrt(simulationParams.getNumberOfClusters());
        centerClusterIndex = ((xn + 1) / 2) - 1;
        internalFlowMap = new HashMap<NodeData, NodeData>();
        externalFlowMap = new HashMap<NodeData, NodeData>();
        simulationParams.setInternalFlowMap(internalFlowMap);
        simulationParams.setExternalFlowMap(externalFlowMap);
        log = Logger.getLogger(SimulationParams.class.getName());
        desirableWiredHopNumber = simulationParams.getDesirableInternalHopNumber();
        desirableWirelessHopNumber = simulationParams.getDesirableExternalHopNumber();
        params = simulationParams;
        generateFlows();
    }

    private static void generateEligibleNodeSet() {
        eligibleNodes = new HashSet<NodeData>();
        for (int c = 0; c < params.getNumberOfClusters(); c++) {
            for (int i = 0; i < xn; i++) {
                for (int j = 0; j < xn; j++) {
                    if ((i == centerClusterIndex) && (j == centerClusterIndex)) {
                        continue;
                    }
                    eligibleNodes.add(new NodeData(c, i, j));
                }
            }
        }
    }

    private static int[] clusterToPosition(int clusterNum) {
        int xPos = clusterNum / xc;
        int yPos = clusterNum % xc;
        return new int[] { xPos, yPos };
    }

    private static int abs(int x) {
        return (x < 0) ? -x : x;
    }

    private static int calculateNumberOfWirelessHops(int sourceX, int sourceY, int destinationX, int destinationY) {
        int absX = abs(sourceX - destinationX);
        int absY = abs(sourceY - destinationY);
        int smaller = (absX < absY) ? absX : absY;
        int bigger = (absX > absY) ? absX : absY;
        int diagonalHopNumber = smaller;
        int horizontalOrVerticalHopNumber = bigger - diagonalHopNumber;
        return horizontalOrVerticalHopNumber + diagonalHopNumber;
    }

    private static int calculateNumberOfWiredHops(int sourceX, int sourceY, int destinationX, int destinationY) {
        int absX = abs(sourceX - destinationX);
        int absY = abs(sourceY - destinationY);
        return absX + absY;
    }

    private static boolean isEligibleNodesFromSameCluster() {
        if (eligibleNodes.size() <= 1) {
            return false;
        }
        List<NodeData> remainingNodesList = new ArrayList<NodeData>(eligibleNodes);
        Iterator<NodeData> itNode = remainingNodesList.iterator();
        Integer testCluster = itNode.next().getCluster();
        while (itNode.hasNext()) {
            if (itNode.next().getCluster() != testCluster) {
                return false;
            }
        }
        return true;
    }

    private static int getAvaliableWiredHopNumberClosestToDesirable(int presentCluster) {
        List<NodeData> remainingNodesList = new ArrayList<NodeData>(eligibleNodes);
        // difference between desirable and possible hops. The difference must be minimal.
        // The beggining value is equal to cluster number, because any difference is lessen than that
        int minimalDiff = -1;
        int closestDesirableValue = 0;
        for (int i = 0; i < remainingNodesList.size(); i++) {
            NodeData testNode = remainingNodesList.get(i);
            if (testNode.getCluster() != presentCluster) {
                continue;
            }
            for (int j = i + 1; j < remainingNodesList.size(); j++) {
                NodeData remainingNode = remainingNodesList.get(j);
                if (remainingNode.getCluster() != presentCluster) {
                    continue;
                }
                int hops =
                        calculateNumberOfWiredHops(remainingNode.getX(), remainingNode.getY(), testNode.getX(),
                                testNode.getY());
                int testDiff = abs(hops - desirableWirelessHopNumber);
                if (testDiff == 0) {
                    // there is at least one pair that is desirable
                    return hops;
                } else if ((testDiff < minimalDiff) || (minimalDiff < 0)) {
                    // in case of no desirable pair the function will return the closest hop number to the desirable
                    minimalDiff = testDiff;
                    closestDesirableValue = hops;
                }
            }
        }
        return closestDesirableValue;
    }

    private static int getAvaliableWirelessHopNumberClosestToDesirable() {
        List<NodeData> remainingNodesList = new ArrayList<NodeData>(eligibleNodes);
        // difference between desirable and possible hops. The difference must be minimal.
        // The beggining value is equal to cluster number, because any difference is lessen than that
        int minimalDiff = -1;
        int closestDesirableValue = 0;

        for (int i = 0; i < remainingNodesList.size(); i++) {
            NodeData testNode = remainingNodesList.get(i);
            for (int j = i + 1; j < remainingNodesList.size(); j++) {
                NodeData remainingNode = remainingNodesList.get(j);
                if (remainingNode.getCluster() == testNode.getCluster()) {
                    continue;
                }
                int[] destinationCoord = clusterToPosition(remainingNode.getCluster());
                int[] sourceCoord = clusterToPosition(testNode.getCluster());
                int hops =
                        calculateNumberOfWirelessHops(sourceCoord[0], sourceCoord[1], destinationCoord[0],
                                destinationCoord[1]);
                int testDiff = abs(hops - desirableWirelessHopNumber);
                if (testDiff == 0) {
                    // there is at least one pair that is desirable
                    return hops;
                } else if ((testDiff < minimalDiff) || (minimalDiff < 0)) {
                    // in case of no desirable pair the function will return the closest hop number to the desirable
                    minimalDiff = testDiff;
                    closestDesirableValue = hops;
                }
            }
        }
        return closestDesirableValue;
    }

    private static void breakRandomExternalFlow() {
        // sometimes depending on the random values sequence
        // the remaining eligibleNodes are from the same cluster,
        // so it is not possible compose an external flow.
        // In this case, this funcion is called for breaking a random
        // external flow in order to become possible
        // continuing the flow building process

        Integer nFlows = externalFlowMap.size();
        Integer randomFlow = ((int) (Math.random() * bigN) % nFlows);

        Set<NodeData> keySet = externalFlowMap.keySet();
        List<NodeData> sourceList = new ArrayList<NodeData>(keySet);
        NodeData sourceNode = sourceList.get(randomFlow);
        NodeData destinationNode = externalFlowMap.get(sourceNode);
        eligibleNodes.add(sourceNode);
        eligibleNodes.add(destinationNode);
        externalFlowMap.remove(sourceNode);
    }

    private static void generateFlows() {
        Integer nInternalFlow = ((int) (params.getInternalTraffic() * params.getNumberOfNodesInCluster())) / 2;
        Integer nExternalFlow = ((int) (params.getExternalTraffic() * params.getNumberOfNodesInCluster())) / 2;
        Integer minimalNodes = (nInternalFlow + nExternalFlow) * 2;

        if (minimalNodes > (params.getNumberOfNodesInCluster() - 1)) {
            // it does not count the center node
            throw new RuntimeException("It is impossible to build this network minimalNodes > "
                    + params.getNumberOfNodesInCluster());

        }

        generateEligibleNodeSet();

        for (int i = 0; i < params.getNumberOfClusters(); i++) {

            desirableWiredHopNumber = params.getDesirableInternalHopNumber();

            for (int iInternalFlow = 0; iInternalFlow < nInternalFlow; iInternalFlow++) {
                log.info("cluster " + i + " flow " + iInternalFlow);
                boolean tryagain = true;
                while (tryagain == true) {

                    // verifying if is possible keep up with desirable wired hop number
                    desirableWiredHopNumber = getAvaliableWiredHopNumberClosestToDesirable(i);
                    if (desirableWiredHopNumber < 1) {
                        throw new RuntimeException("Wired: There must be at least one hop");
                    }

                    Integer nrand_sx = ((int) (Math.random() * bigN) % xn);
                    Integer nrand_sy = ((int) (Math.random() * bigN) % xn);
                    NodeData candidateSourceNode = new NodeData(i, nrand_sx, nrand_sy);
                    if ((nrand_sx == xn) && (nrand_sy == xn)) {
                        // it can't be the center node
                        continue;
                    }
                    if (!eligibleNodes.contains(candidateSourceNode)) {
                        continue;
                    }

                    // while (tryagain == true) {
                    Integer nrand_dx = ((int) (Math.random() * bigN) % xn);
                    Integer nrand_dy = ((int) (Math.random() * bigN) % xn);
                    NodeData candidateDestinationNode = new NodeData(i, nrand_dx, nrand_dy);

                    if (!eligibleNodes.contains(candidateDestinationNode)) {
                        continue;
                    }
                    if (calculateNumberOfWiredHops(candidateSourceNode.getX(), candidateSourceNode.getY(),
                            candidateDestinationNode.getX(), candidateDestinationNode.getY()) != desirableWiredHopNumber) {
                        continue;
                    }

                    eligibleNodes.remove(candidateSourceNode);
                    eligibleNodes.remove(candidateDestinationNode);
                    internalFlowMap.put(candidateSourceNode, candidateDestinationNode);
                    tryagain = false;
                    // }
                }
            }
        }

        for (int i = 0; i < params.getNumberOfClusters(); i++) {

            for (int iExternalFlow = 0; iExternalFlow < nExternalFlow; iExternalFlow++) {
                log.info("cluster " + i + " external flow " + iExternalFlow);
                boolean tryagain = true;
                while (tryagain == true) {

                    // verifying if is possible keep up with desirable wireless hop number
                    desirableWirelessHopNumber = getAvaliableWirelessHopNumberClosestToDesirable();
                    if (desirableWirelessHopNumber < 1) {
                        throw new RuntimeException("Wireless: There must be at least one hop");
                    }

                    Integer nrand_sc = ((int) (Math.random() * bigN) % params.getNumberOfClusters());
                    Integer nrand_sx = ((int) (Math.random() * bigN) % xn);
                    Integer nrand_sy = ((int) (Math.random() * bigN) % xn);
                    NodeData candidateSourceNode = new NodeData(nrand_sc, nrand_sx, nrand_sy);
                    if (!eligibleNodes.contains(candidateSourceNode)) {
                        continue;
                    }

                    // while (tryagain == true) {
                    Integer nrand_dc = ((int) (Math.random() * bigN) % params.getNumberOfClusters());
                    if (nrand_dc == nrand_sc) {
                        continue;
                    }
                    Integer nrand_dx = ((int) (Math.random() * bigN) % xn);
                    Integer nrand_dy = ((int) (Math.random() * bigN) % xn);
                    NodeData candidateDestinationNode = new NodeData(nrand_dc, nrand_dx, nrand_dy);

                    if (!eligibleNodes.contains(candidateDestinationNode)) {
                        continue;
                    }
                    int[] candidateSourceNodePosition = clusterToPosition(candidateSourceNode.getCluster());
                    int[] candidateDestinationNodePosition = clusterToPosition(candidateDestinationNode.getCluster());
                    if (calculateNumberOfWirelessHops(candidateSourceNodePosition[0], candidateSourceNodePosition[1],
                            candidateDestinationNodePosition[0], candidateDestinationNodePosition[1]) != desirableWirelessHopNumber) {
                        continue;
                    }
                    eligibleNodes.remove(candidateSourceNode);
                    eligibleNodes.remove(candidateDestinationNode);
                    externalFlowMap.put(candidateSourceNode, candidateDestinationNode);
                    tryagain = false;
                    // }
                }
                // verifying if there is at least an external valid flow
                while (isEligibleNodesFromSameCluster()) {
                    desirableWirelessHopNumber = 1; // minimal only for unbreaking
                    breakRandomExternalFlow();
                    iExternalFlow--;
                }
            }
        }
    }
}
