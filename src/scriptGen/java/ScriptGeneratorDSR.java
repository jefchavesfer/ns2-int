
package scriptGen.java;


import io.java.TclGeneratorSimulationData;
import io.java.TurnOffNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import tree.java.DataNode;


/**
 * @author jchaves
 */
public class ScriptGeneratorDSR {

    private static String br = "\r\n";
    private FileWriter scriptFile;
    private TclGeneratorSimulationData data;
    private int totalOfNodes;
    private Integer timeOffset;
    private Integer distNodes = 170;
    private Integer distCloseNodes = 10;
    private Map<Integer, List<String>> sourceSinkAdjacentNodesByWirelessNode;
    private Map<Integer, Integer> adjacentNodeAbsoluteCountInitialValueByWirelessNode;
    private Float deltaFlow;

    /**
     * @param data
     * @param timeOffset
     * @throws IOException
     */
    public ScriptGeneratorDSR(TclGeneratorSimulationData data, Integer timeOffset) throws IOException {
        // all flows are unique, therefore the source-destination pair is unique for each flow
        this.data = data;
        this.timeOffset = timeOffset;
        this.totalOfNodes = this.data.getNc() + (this.data.getThroughputData().size() * 2);
        this.scriptFile = new FileWriter(this.data.getFileRadical() + ".tcl");
        this.deltaFlow = (this.timeOffset * 0.8f) / this.data.getThroughputData().size();
    }

    private String writeNodePositionChange(String time, String strNodeIndex, String strXOffset, String strYOffset) {
        return "              $ns_ at " + time + " \"$node_(" + strNodeIndex + ") setdest " + strXOffset + " "
                + strYOffset + " $farSpeed\"" + br;
    }

    private void writeHeader() throws IOException {
        // @formatter:off
        this.scriptFile.write(br + "# Initializing" + br 
                                 + "set ns_		[new Simulator]" + br + br
                                 + "#topology parameters" + br 
                                 + "set opt(chan)           Channel/WirelessChannel       ;#Channel Type" + br 
                                 + "set opt(prop)           Propagation/TwoRayGround      ;# radio-propagation model" + br
                                 + "set opt(netif)          Phy/WirelessPhy               ;# network interface type" + br
                                 + "set opt(mac)            Mac/802_11                    ;# MAC type" + br
                                 + "set opt(ifq)            CMUPriQueue" + br
                                 + "set opt(ll)             LL                            ;# link layer type" + br
                                 + "set opt(ant)            Antenna/OmniAntenna           ;# antenna model" + br
                                 + "set opt(ifqlen)         " + this.data.getNQueue() + "    ;# max packet in ifq" + br
                                 + "set opt(nc)             " + this.data.getNc() + "     ;# number of mobilenodes" + br
                                 + "set opt(nn)             " + this.totalOfNodes + "     ;# totalOfNodes" + br
                                 + "set rad [expr int( sqrt( $opt(nc) ) ) ]" + br                                  
                                 + "set farAwayDistConstant 10" + br
                                 + "set farAwaySpeedConstant  1000" + br
                                 + "set initial_x     1" + br 
                                 + "set initial_y     1" + br
                                 + "set pos_x         $initial_x" + br 
                                 + "set pos_y         $initial_y" + br + br
                                 + "set PI  [expr atan(1) * 4] "+ br    
                                 + "#Area" + br 
                                 + "set distNodes               " + this.distNodes + br
                                 + "set closeAdjacentNodes      " + this.distCloseNodes + br
                                 + "set opt(x)		[expr $distNodes * $rad + $initial_x]" + br 
                                 + "set opt(y)		[expr $distNodes * $rad + $initial_y]" + br
                                 + "set opt(stop) " + ( this.data.getTf() + this.timeOffset ) + "	         ;# simulation time" + br 
                                 + "set opt(tr)   " + this.data.getFileRadical() + "     ;# trace file" + br + br
                                 + "#protocol" + br 
                                 + "set opt(rp) " + this.data.getWirelessProtocol() + br + br 
                                 + "set tracefd     [open $opt(tr).tr w]" + br 
                                 + "$ns_ trace-all $tracefd" + br + br
                                 + "set namtrace [open $opt(tr).nam w]" + br 
                                 + "$ns_ namtrace-all-wireless $namtrace $opt(x) $opt(y)" + br + br
                                 + "set farXOffset [ expr $opt(nc) * $farAwayDistConstant  * $distNodes + $initial_x]" + br 
                                 + "set farYOffset [ expr $opt(nc) * $farAwayDistConstant  * $distNodes + $initial_y]" + br
                                 + "set farSpeed   [ expr $opt(nc) * $farAwaySpeedConstant ]" + br
                                 + "# set up topography object" + br 
                                 + "set topo       [new Topography]" + br + br
                                 + "$topo load_flatgrid [ expr $opt(x) + $farXOffset + 2 * $closeAdjacentNodes ] [ expr $opt(y) + $farYOffset + 2 * $closeAdjacentNodes ]" + br + br 
                                 + "# Create God" + br
                                 + "set god_ [create-god $opt(nn)]" + br + br 
                                 + "# configure node, please note the change below." + br
                                 + "$ns_ node-config -adhocRouting $opt(rp) \\" + br 
                                 + "		-llType $opt(ll) \\" + br
                                 + "		-macType $opt(mac) \\" + br 
                                 + "		-ifqType $opt(ifq) \\" + br 
                                 + "		-ifqLen $opt(ifqlen) \\" + br
                                 + "		-antType $opt(ant) \\" + br 
                                 + "		-propType $opt(prop) \\" + br 
                                 + "		-phyType $opt(netif) \\" + br
                                 + "		-topoInstance $topo \\" + br 
                                 + "		-agentTrace ON \\" + br 
                                 + "		-routerTrace ON \\" + br
                                 + "		-macTrace ON \\" + br 
                                 + "		-movementTrace ON \\" + br 
                                 + "		-channel [new $opt(chan)]" + br + br
                                 + "# configure offset for distant inactive nodes" + br
                                 + "for {set i 0} {$i < $opt(nn)} {incr i} {" + br 
                                 + "  	set node_($i) [$ns_ node]" + br
                                 + "}" + br );
       // @formatter:on
    }

    private void writeAdjacentNodes() throws IOException {

        // this is needed for knowing how many source and destination nodes are near a wireless node
        this.sourceSinkAdjacentNodesByWirelessNode = new TreeMap<Integer, List<String>>();

        for (Map.Entry<String, DataNode> flow : this.data.getThroughputData().entrySet()) {
            DataNode flowData = flow.getValue();

            String wiredSource = flowData.getSource();
            String wiredDestination = flowData.getDestination();

            String wirelessSource = flowData.getWirelessSourceNode();
            Integer wirelessSourceValue = (Integer.valueOf(wirelessSource) - this.data.getN0()) / this.data.getNn();

            String wirelessDestination = flowData.getWirelessDestinationNode();
            Integer wirelessDestinationValue =
                    (Integer.valueOf(wirelessDestination) - this.data.getN0()) / this.data.getNn();

            String uniqueFlow =
                    wiredSource + " " + wirelessSourceValue + " " + wirelessDestinationValue + " " + wiredDestination;

            List<String> adjacentNodes = this.sourceSinkAdjacentNodesByWirelessNode.get(wirelessSourceValue);
            if (adjacentNodes == null) {
                adjacentNodes = new ArrayList<String>();
                this.sourceSinkAdjacentNodesByWirelessNode.put(wirelessSourceValue, adjacentNodes);
            }

            if (!adjacentNodes.contains(uniqueFlow)) {
                adjacentNodes.add(uniqueFlow);
            } else {
                throw new RuntimeException("the adjacent flow is unique: " + uniqueFlow + " adjacentNodes "
                        + adjacentNodes);
            }

            adjacentNodes = this.sourceSinkAdjacentNodesByWirelessNode.get(wirelessDestinationValue);
            if (adjacentNodes == null) {
                adjacentNodes = new ArrayList<String>();
                this.sourceSinkAdjacentNodesByWirelessNode.put(wirelessDestinationValue, adjacentNodes);
            }

            if (!adjacentNodes.contains(uniqueFlow)) {
                adjacentNodes.add(uniqueFlow);
            } else {
                throw new RuntimeException("the adjacent flow is unique");
            }

            String[] splitFlow = flow.getKey().split(" ");
            if (!splitFlow[0].equals(wiredSource) || !splitFlow[1].equals(wirelessSource)
                    || !splitFlow[2].equals(wirelessDestination) || !splitFlow[3].equals(wiredDestination)) {
                throw new RuntimeException("flow key is different from flow data");
            }
        }

        this.scriptFile.write(br);
        for (int i = 0; i < this.data.getNc(); i++) {
            List<String> adjacentNodesList = this.sourceSinkAdjacentNodesByWirelessNode.get(i);
            Integer adjacentNodesListSize = 0;
            if (adjacentNodesList != null) {
                adjacentNodesListSize = adjacentNodesList.size();
                for (int j = 0; j < adjacentNodesListSize; j++) {
                    this.scriptFile.write("# unique flow " + j + " : " + adjacentNodesList.get(j) + br);
                }
            }
            this.scriptFile.write(br + " set totalAdjacentNodes_(" + i + ") " + adjacentNodesListSize + br);

            this.prepareTurnOnOffFlags(i);
        }
        this.scriptFile.write(br);
    }

    private TurnOffNode searchNodeTurnOffData(int i) {
        for (TurnOffNode turnOffNodeData : this.data.getTurnOffNodes()) {
            if (turnOffNodeData.getNodeIndex() == i) {
                return turnOffNodeData;
            }
        }
        return null;
    }

    /**
     * @param i
     * @throws IOException
     */
    private void prepareTurnOnOffFlags(int i) throws IOException {
        TurnOffNode turnOffNodeData = this.searchNodeTurnOffData(i);

        if ((turnOffNodeData == null)
                || ((this.data.getTf() < turnOffNodeData.getInitialTime()) && (this.data.getTf() < turnOffNodeData
                        .getEndTime()))
                || ((this.data.getT0() > turnOffNodeData.getInitialTime()) && (this.data.getT0() > turnOffNodeData
                        .getEndTime()))) {

            this.scriptFile.write(" # this node follows normally in this slot" + br);
            this.scriptFile.write(" set turnOffFlag_(" + i + ")  0 " + br);
            this.scriptFile.write(" set turnOnFlag_(" + i + ")   0 " + br);
            this.scriptFile.write(" set initialOffTime_(" + i + ") 0 " + br);
            this.scriptFile.write(" set endOffTime_(" + i + ") 0 " + br);

        } else if ((turnOffNodeData.getInitialTime() < this.data.getT0())
                && (turnOffNodeData.getEndTime() >= this.data.getTf())) {

            this.scriptFile.write(" # this node is not being considered in this slot" + br);
            this.scriptFile.write(" set turnOffFlag_(" + i + ")  2 " + br);
            this.scriptFile.write(" set turnOnFlag_(" + i + ")  2 " + br);
            this.scriptFile.write(" set initialOffTime_(" + i + ") 0 " + br);
            this.scriptFile.write(" set endOffTime_(" + i + ") 0 " + br);

        } else {
            Boolean disappearCond =
                    (turnOffNodeData.getInitialTime() >= this.data.getT0())
                            && (turnOffNodeData.getInitialTime() < this.data.getTf());

            Boolean returnCond =
                    (turnOffNodeData.getEndTime() >= this.data.getT0())
                            && (turnOffNodeData.getEndTime() < this.data.getTf());

            // make disappear and reappear in same time slot
            if (disappearCond && returnCond) {
                this.scriptFile.write(" # both events to turn off and on are considered in this slot" + br);
                this.scriptFile.write(" set turnOffFlag_(" + i + ")  1 " + br);
                this.scriptFile.write(" set turnOnFlag_(" + i + ")  1 " + br);

                this.scriptFile.write(" set initialOffTime_(" + i + ") "
                        + (turnOffNodeData.getInitialTime() + this.data.getTimeOffSet()) + br);

                this.scriptFile.write(" set endOffTime_(" + i + ") "
                        + (turnOffNodeData.getEndTime() + this.data.getTimeOffSet()) + br);

            } else if (disappearCond) {
                this.scriptFile.write(" # this node turns off in this slot" + br);
                this.scriptFile.write(" set turnOffFlag_(" + i + ")  1 " + br);
                this.scriptFile.write(" set turnOnFlag_(" + i + ")  0 " + br);

                this.scriptFile.write(" set initialOffTime_(" + i + ") "
                        + (turnOffNodeData.getInitialTime() + this.data.getTimeOffSet()) + br);

                this.scriptFile.write(" set endOffTime_(" + i + ") "
                        + (turnOffNodeData.getEndTime() + this.data.getTimeOffSet()) + br);

            } else if (returnCond) {
                this.scriptFile.write(" # this node turns on in this slot" + br);
                this.scriptFile.write(" set turnOffFlag_(" + i + ")  0 " + br);
                this.scriptFile.write(" set turnOnFlag_(" + i + ")  1 " + br);

                this.scriptFile.write(" set initialOffTime_(" + i + ") "
                        + (turnOffNodeData.getInitialTime() + this.data.getTimeOffSet()) + br);

                this.scriptFile.write(" set endOffTime_(" + i + ") "
                        + (turnOffNodeData.getEndTime() + this.data.getTimeOffSet()) + br);
            }
        }
    }

    private void writeNodePositions() throws IOException {
        // @formatter:off
        this.scriptFile.write(br + "puts \"adjusting node initial position\"" + br 
                                 + "set adjcentNodeAsoluteCount   $opt(nc)" + br
                                 + "for {set i 0} {$i < $opt(nc)} {incr i} {" + br + br
                                 + "    if {$i ==  0} {" + br
                                 + "            set pos_x $initial_x" + br
                                 + "            set pos_y [expr $rad * $distNodes + $initial_y]" + br
                                 + "    } elseif {$i % $rad  == 0 } {" + br
                                 + "            set pos_x $initial_x" + br
                                 + "            set pos_y [expr $pos_y - $distNodes]" + br
                                 + "    } else {" + br
                                 + "            set pos_x [expr $pos_x + $distNodes]" + br
                                 + "    }" + br
                                 + "    puts \"node $i: ($pos_x, $pos_y)\"" + br
                                 + "    if {$turnOffFlag_($i) != 2 && $turnOnFlag_($i) != 1} { " + br 
                                 + "       $node_($i) set X_ $pos_x" + br
                                 + "       $node_($i) set Y_ $pos_y" + br
                                 + "       $node_($i) set Z_ 0"      + br   
                                 + "    } else {" + br
                                 + "       #put inactive nodes far away                   " + br
                                 + "       $node_($i) set X_ [ expr $pos_x + $farXOffset ]" + br
                                 + "       $node_($i) set Y_ [ expr $pos_y + $farYOffset ]" + br
                                 + "       $node_($i) set Z_ 0                            " + br  
                                 + "    }" + br
                                 + "    if {$turnOffFlag_($i) == 1} {" + br
                                 +         this.writeNodePositionChange("$initialOffTime_($i)", "$i", "[ expr $pos_x + $farXOffset ]", "[ expr $pos_y + $farYOffset ]")
                                 + "    } " + br  
                                 + "    if {$turnOnFlag_($i) == 1} {" + br
                                 +         this.writeNodePositionChange("$endOffTime_($i)", "$i", "$pos_x", "$pos_y")
                                 + "    } " + br  
                                 + "    if {$totalAdjacentNodes_($i) > 0} {" + br
                                 + "        puts \"adjusting node $i adjacent source and destination nodes initial positions\"" + br
                                 + "        set angle [ expr 2 * $PI/$totalAdjacentNodes_($i) ]" + br 
                                 + "        for {set j 0} {$j < $totalAdjacentNodes_($i)} {incr j} {" + br 
                                 + "            #clockwise fulfilling" + br  
                                 + "            set pos_a_x [ expr $pos_x - $closeAdjacentNodes * cos($j * $angle) ]" + br
                                 + "            set pos_a_y [ expr $pos_y + $closeAdjacentNodes * sin($j * $angle) ]" + br 
                                 + "            if {$turnOffFlag_($i) != 2 && $turnOnFlag_($i) != 1} { " + br
                                 + "              $node_($adjcentNodeAsoluteCount) set X_ $pos_a_x" + br 
                                 + "              $node_($adjcentNodeAsoluteCount) set Y_ $pos_a_y" + br
                                 + "              $node_($adjcentNodeAsoluteCount) set Z_ 0" + br 
                                 + "            } else {" + br
                                 + "              #put inactive nodes far away                 " + br
                                 + "              $node_($adjcentNodeAsoluteCount) set X_ [ expr $pos_a_x + $farXOffset ]" + br 
                                 + "              $node_($adjcentNodeAsoluteCount) set Y_ [ expr $pos_a_y + $farYOffset ]" + br
                                 + "              $node_($adjcentNodeAsoluteCount) set Z_ 0" + br 
                                 + "            }" + br
                                 + "            if {$turnOffFlag_($i) == 1} {" + br
                                 +                 this.writeNodePositionChange("$initialOffTime_($i)", "$adjacentNodeCount", "[ expr $pos_a_x + $farXOffset ]", "[ expr $pos_a_y + $farYOffset ]")
                                 + "            } " + br
                                 + "            if {$turnOnFlag_($i) == 1} {" + br
                                 +                 this.writeNodePositionChange("$endOffTime_($i)", "$adjacentNodeCount", "$pos_a_x", "$pos_a_y")
                                 + "            } " + br
                                 + "            puts \"node $adjcentNodeAsoluteCount: ($pos_a_x, $pos_a_y)\"" + br
                                 + "            incr adjcentNodeAsoluteCount" + br
                                 + "         }" + br
                                 + "     }" + br
                                 + "}" + br
                                 + "#ns2 node screen size" + br
                                 + "for {set i 0} {$i < $opt(nn)} {incr i} {" + br
                                 + "    $ns_ initial_node_pos $node_($i) 30" + br 
                                 + "}" + br);
        // @formatter:on
    }

    private void writeAgents(Float t0, Float tf, Integer source, Integer destination, Integer flow, Float rate,
            Integer sourceWireless, Integer destinationWireless) throws IOException {
        // @formatter:off 
        this.scriptFile.write(br + "#source node" + br 
                                 + "#creating udp agent" + br
                                 + "#source wireless: " + sourceWireless + " destination Wireless: " + destinationWireless + br
                                 + "puts \"creating udp(" + flow + ")\"" + br + br
                                 + "set udp_(" + flow + ") [new Agent/UDP]" + br
                                 + "$udp_(" + flow + ") set class_ 2" + br + br
                                 + "$ns_ attach-agent $node_(" + source + ") $udp_(" + flow + ")" + br + br
                                 + "puts \"traffic cbr(" + flow + ")\"" + br + br + "set cbr(" + flow + ") [new Application/Traffic/CBR]" + br + br 
                                 + "#cbr parameters" + br + "$cbr(" + flow + ") set packetSize_ " + this.data.getPacketSize() + br
                                 + "$cbr(" + flow + ") set interval_ " + rate + br + br
                                 + "puts \"linking cbr to the udp source\"" + br
                                 + "$cbr(" + flow + ") attach-agent $udp_(" + flow + ")" + br + br
                                 + "puts \"creating sink agent sink(" + flow + ")\"" + br
                                 + "#destination node " + br
                                 + "set sink_(" + flow + ") [new Agent/Null]" + br + br 
                                 + "puts \"setting sink agent to the destintion node- node_(" + destination + ") sink(" + flow + ")\"" + br
                                 + "$ns_ attach-agent $node_(" + destination + ") $sink_(" + flow + ")" + br + br
                                 + "puts \"Connecting udp(" + flow + ") sink(" + flow + ")\"" + br
                                 + "$ns_ connect $udp_(" + flow + ") $sink_(" + flow + ")" + br + br
                                 + "set nrand_c [ expr rand() ]" + br + "puts \"cbr(" + flow + ") iniciando em " + flow + " * " + this.deltaFlow + " \"" + br
                                 + "$ns_ at " + (t0 + (flow * this.deltaFlow)) + " \"$cbr(" + flow + ") start\"" + br
                                 + "$ns_ at " + (tf + this.timeOffset) + " \"$cbr(" + flow + ") stop\"" + br);
       // @formatter:on 
    }

    private void writeSimulation() throws IOException {
        // @formatter:off 
        this.scriptFile.write(br + "#" + br 
                                 + "# finishing simulation" + br + "#" + br
                                 + "for {set i 0} {$i < $opt(nn) } {incr i} {" + br
                                 + "    $ns_ at $opt(stop).01 \"$node_($i) reset\""+ br
                                 + "}" + br
                                 + "# stopping nam animation" + br
                                 + "$ns_ at  $opt(stop).01	\"$ns_ nam-end-wireless $opt(stop)\"" + br
                                 + "$ns_ at $opt(stop).01 \"puts \\\"NS EXITING...\\\" ; $ns_ halt\"" + br
                                 + "proc stop {} {" + br
                                 + "    global ns_ tracefd" + br
                                 + "    $ns_ flush-trace" + br
                                 + "    close $tracefd" + br
                                 + "}" + br
                                 + "puts \"Starting Simulation...\"" + br
                                 + "$ns_ run" + br);
        // @formatter:on 
    }

    /**
     * @throws IOException
     */
    public void generateScript() throws IOException {

        this.writeHeader();

        // specifying the tcl adjacent node constants
        this.writeAdjacentNodes();

        // specifying node coordinates
        this.writeNodePositions();

        // this is needed for knowing the first ajacent node id for each wireless Node
        int absoluteCount = this.data.getNc();

        this.adjacentNodeAbsoluteCountInitialValueByWirelessNode = new TreeMap<Integer, Integer>();
        for (Map.Entry<Integer, List<String>> adjacentNodeEntry : this.sourceSinkAdjacentNodesByWirelessNode.entrySet()) {

            Integer wirelessNode = adjacentNodeEntry.getKey();
            this.adjacentNodeAbsoluteCountInitialValueByWirelessNode.put(wirelessNode, absoluteCount);
            int numberOfAdjacentNodes = adjacentNodeEntry.getValue().size();
            absoluteCount += numberOfAdjacentNodes;
        }

        // sanity test
        if (absoluteCount != this.totalOfNodes) {
            throw new RuntimeException("the absolute count final value must be equal to total of nodes");
        }

        int iFlow = 0;
        for (Map.Entry<String, DataNode> flow : this.data.getThroughputData().entrySet()) {
            DataNode flowData = flow.getValue();
            Float t0 = this.data.getT0();
            Float tf = this.data.getTf();
            Float rate = flow.getValue().getFlowRate();

            Integer source = Integer.valueOf(flowData.getWirelessSourceNode());
            Integer destination = Integer.valueOf(flowData.getWirelessDestinationNode());

            Integer sourceWireless = (source - this.data.getN0()) / this.data.getNn();
            Integer destinationWireless = (destination - this.data.getN0()) / this.data.getNn();

            Integer sourceAdjacentNodeCount =
                    this.adjacentNodeAbsoluteCountInitialValueByWirelessNode.get(sourceWireless);
            Integer destinationAdjacentNodeCount =
                    this.adjacentNodeAbsoluteCountInitialValueByWirelessNode.get(destinationWireless);

            this.writeAgents(t0, tf, sourceAdjacentNodeCount, destinationAdjacentNodeCount, iFlow, rate,
                    sourceWireless, destinationWireless);

            iFlow++;
            // update ajacent node absolute countes
            this.adjacentNodeAbsoluteCountInitialValueByWirelessNode.put(sourceWireless, sourceAdjacentNodeCount + 1);
            this.adjacentNodeAbsoluteCountInitialValueByWirelessNode.put(destinationWireless,
                    destinationAdjacentNodeCount + 1);
        }

        this.writeSimulation();
        this.scriptFile.flush();
        this.scriptFile.close();
    }
}
