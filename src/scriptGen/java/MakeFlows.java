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
    private static Integer nn;
    private static Integer nc;
    private static Integer x;
    private static Integer centerClusterIndex;
    private static Float internalPerc;
    private static Float externalPerc;

    /**
     * @param simulationParams
     */
    public static void make(SimulationParams simulationParams) {
        nn = simulationParams.getNumberOfNodesInCluster();
        nc = simulationParams.getNumberOfClusters();
        x = (int) Math.sqrt(simulationParams.getNumberOfNodesInCluster());
        centerClusterIndex = ((x + 1) / 2) - 1;
        internalFlowMap = new HashMap<NodeData, NodeData>();
        externalFlowMap = new HashMap<NodeData, NodeData>();
        simulationParams.setInternalFlowMap(internalFlowMap);
        simulationParams.setExternalFlowMap(externalFlowMap);
        log = Logger.getLogger(SimulationParams.class.getName());
        internalPerc = simulationParams.getInternalTraffic();
        externalPerc = simulationParams.getExternalTraffic();
        generateFlows();
    }

    private static void generateEligibleNodeSet() {
        eligibleNodes = new HashSet<NodeData>();
        for (int c = 0; c < nc; c++) {
            for (int i = 0; i < x; i++) {
                for (int j = 0; j < x; j++) {
                    if ((i == centerClusterIndex) && (j == centerClusterIndex)) {
                        continue;
                    }
                    eligibleNodes.add(new NodeData(c, i, j));
                }
            }
        }
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
        Integer nInternalFlow = ((int) (internalPerc * nn)) / 2;
        Integer nExternalFlow = ((int) (externalPerc * nn)) / 2;
        Integer minimalNodes = (nInternalFlow + nExternalFlow) * 2;

        if (minimalNodes > (nn - 1)) {
            // it does not count the center node
            throw new RuntimeException("It is impossible to build this network minimalNodes > " + nn);

        }

        generateEligibleNodeSet();

        for (int i = 0; i < nc; i++) {
            for (int iInternalFlow = 0; iInternalFlow < nInternalFlow; iInternalFlow++) {
                log.info("cluster " + i + " flow " + iInternalFlow);
                boolean tryagain = true;
                while (tryagain == true) {
                    Integer nrand_sx = ((int) (Math.random() * bigN) % x);
                    Integer nrand_sy = ((int) (Math.random() * bigN) % x);
                    NodeData candidateSourceNode = new NodeData(i, nrand_sx, nrand_sy);
                    if ((nrand_sx == x) && (nrand_sy == x)) {
                        // it can't be the center node
                        continue;
                    }
                    if (!eligibleNodes.contains(candidateSourceNode)) {
                        continue;
                    }
                    eligibleNodes.remove(candidateSourceNode);
                    while (tryagain == true) {
                        Integer nrand_dx = ((int) (Math.random() * bigN) % x);
                        Integer nrand_dy = ((int) (Math.random() * bigN) % x);
                        NodeData candidateDestinationNode = new NodeData(i, nrand_dx, nrand_dy);
                        if (!eligibleNodes.contains(candidateDestinationNode)) {
                            continue;
                        }
                        eligibleNodes.remove(candidateDestinationNode);
                        internalFlowMap.put(candidateSourceNode, candidateDestinationNode);
                        tryagain = false;
                    }
                }
            }
        }

        for (int i = 0; i < nc; i++) {
            for (int iExternalFlow = 0; iExternalFlow < nExternalFlow; iExternalFlow++) {
                log.info("cluster " + i + " external flow " + iExternalFlow);
                boolean tryagain = true;
                while (tryagain == true) {
                    Integer nrand_sc = ((int) (Math.random() * bigN) % nc);
                    Integer nrand_sx = ((int) (Math.random() * bigN) % x);
                    Integer nrand_sy = ((int) (Math.random() * bigN) % x);
                    NodeData candidateSourceNode = new NodeData(nrand_sc, nrand_sx, nrand_sy);
                    if (!eligibleNodes.contains(candidateSourceNode)) {
                        continue;
                    }
                    eligibleNodes.remove(candidateSourceNode);
                    while (tryagain == true) {
                        Integer nrand_dc = ((int) (Math.random() * bigN) % nc);
                        if (nrand_dc == nrand_sc) {
                            continue;
                        }
                        Integer nrand_dx = ((int) (Math.random() * bigN) % x);
                        Integer nrand_dy = ((int) (Math.random() * bigN) % x);
                        NodeData candidateDestinationNode = new NodeData(nrand_dc, nrand_dx, nrand_dy);
                        if (!eligibleNodes.contains(candidateDestinationNode)) {
                            continue;
                        }
                        eligibleNodes.remove(candidateDestinationNode);
                        externalFlowMap.put(candidateSourceNode, candidateDestinationNode);
                        tryagain = false;
                    }
                }
                // verifying if there is at least an external valid flow
                if (isEligibleNodesFromSameCluster()) {
                    breakRandomExternalFlow();
                    iExternalFlow--;
                }
            }
        }
    }
}
