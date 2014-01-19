package scriptGen.java;
import io.java.TclGeneratorSimulationData;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import tree.java.DataNode;

/**
 * @author jchaves
 */
public class ScriptGeneratorDSR {
	private static String br = "\r\n";
	private FileWriter scriptFile;
	private TclGeneratorSimulationData data;
	
	/**
	 * @param data
	 * @throws IOException
	 */
	public ScriptGeneratorDSR(TclGeneratorSimulationData data) throws IOException{
	    this.data = data;
	    this.scriptFile = new FileWriter(this.data.getFileRadical() + ".tcl");
	}
	
	private void writeHeader() throws IOException{
		this.scriptFile.write(																					  br +
				"# Initializing"																		+ br +
				"set ns_		[new Simulator]"														+ br +
																										  br +
				"#topology parameters"																+ br +
				"set opt(chan)           Channel/WirelessChannel       ;#Channel Type"					+ br +
				"set opt(prop)           Propagation/TwoRayGround      ;# radio-propagation model"  	+ br +
				"set opt(netif)          Phy/WirelessPhy               ;# network interface type"		+ br +
				"set opt(mac)            Mac/802_11                    ;# MAC type"						+ br +
				"set opt(ifq)            CMUPriQueue"													+ br +
				"set opt(ll)             LL                            ;# link layer type"				+ br +
				"set opt(ant)            Antenna/OmniAntenna           ;# antenna model"				+ br +
				"set opt(ifqlen)         " + this.data.getNQueue() + " ;# max packet in ifq"			+ br +
				"set opt(nn)             " + this.data.getNc() + "     ;# number of mobilenodes"		+ br +
				"set rad [expr int( sqrt($opt(nn)) ) ]"													+ br +
																										  br +
				"#Area"																					+ br +
				"set distNodes  		100"															+ br +
				"set opt(x)		[expr $distNodes * $rad]"												+ br +
				"set opt(y)		[expr $distNodes * $rad]"                                               + br +
				"set opt(stop) " + this.data.getTf() + "	         ;# simulation time"				+ br +
				"set opt(tr)   " + this.data.getFileRadical() + "    ;# trace file"	                    + br +
				"#protocol"                 															+ br +
				"set opt(rp) "  + this.data.getWirelessProtocol()                                 						+ br +
																										  br +
 				"set pos_x         0"																	+ br +
				"set pos_y         0"																	+ br +
																										  br +
				"set tracefd     [open $opt(tr).tr w]"													+ br +
				"$ns_ trace-all $tracefd"																+ br +
																										  br +
				"set namtrace [open $opt(tr).nam w]"													+ br +
				"$ns_ namtrace-all-wireless $namtrace $opt(x) $opt(y)"									+ br +
																										  br +
				"# set up topography object"															+ br +
				"set topo       [new Topography]"														+ br +
																										  br +
				"$topo load_flatgrid $opt(x) $opt(y)"													+ br +
																										  br +
				"# Create God"																			+ br +
				"set god_ [create-god $opt(nn)]"														+ br +
																										  br +
				"# configure node, please note the change below."										+ br +
				"$ns_ node-config -adhocRouting $opt(rp) \\"											+ br +
				"		-llType $opt(ll) \\"															+ br +
				"		-macType $opt(mac) \\"															+ br +			
				"		-ifqType $opt(ifq) \\"															+ br +
				"		-ifqLen $opt(ifqlen) \\"														+ br +
				"		-antType $opt(ant) \\"															+ br +
				"		-propType $opt(prop) \\"														+ br +
				"		-phyType $opt(netif) \\"														+ br +
				"		-topoInstance $topo \\"															+ br +
				"		-agentTrace ON \\"																+ br +
				"		-routerTrace ON \\"																+ br +
				"		-macTrace ON \\"																+ br +
				"		-movementTrace ON \\"															+ br +
				"		-channel [new $opt(chan)]"														+ br +
																										  br +
				"for {set i 0} {$i < $opt(nn)} {incr i} {"												+ br +
				"  	set node_($i) [$ns_ node]" 															+ br +
				"}"																						+ br +
																										  br +
			    "puts \"adjusting node initial position\""												+ br +
			    "set rad [expr int( sqrt($opt(nn)) ) ]"													+ br +
			    "for {set i 0} {$i < $opt(nn)} {incr i} {"												+ br +
			    																						  br +
			    "	if {$i == 0} {"																		+ br +
			    "		set pos_x 0"																	+ br +
			    "		set pos_y [expr $rad * $distNodes]"												+ br +
			    "	} elseif {$i % $rad  == 0 } {"														+ br +
			    "		set pos_x 0"																	+ br +
			    "		set pos_y [expr $pos_y - $distNodes]"											+ br +
			    "	} else {"																			+ br +
			    "		set pos_x [expr $pos_x + $distNodes]"											+ br +
			    "	}"																					+ br +
			    "	puts \"node $i: ($pos_x, $pos_y)\""													+ br +
			    "	$node_($i) set X_ $pos_x"															+ br +
			   	"	$node_($i) set Y_ $pos_y"															+ br +
			   	"	$node_($i) set Z_ 0"																+ br +
				"}"																						+ br +
																										  br +
				"#ns2 node screen size"															+ br +
				"for {set i 0} {$i < $opt(nn)} {incr i} {"												+ br +
				"	$ns_ initial_node_pos $node_($i) 30"												+ br +
				"}"																						+ br
		);
	}
	private void writeAgents(Float t0, Float tf, Integer source, Integer destination, Integer flow, Float rate ) throws IOException{
		this.scriptFile.write(																					  br +
				"#source node"  																		+ br +
				"#creating udp agent"																	+ br +
				"puts \"creating udp(" + flow + ")\""													+ br +
																										  br +
				"set udp_(" + flow + ") [new Agent/UDP]" 												+ br +
				"$udp_(" + flow + ") set class_ 2"														+ br +
																										  br +
				"$ns_ attach-agent $node_(" + source + ") $udp_(" + flow + ")"							+ br +
																										  br +
				"puts \"traffic cbr(" + flow + ")\""													+ br +
																										  br +
				"set cbr(" + flow + ") [new Application/Traffic/CBR]" 									+ br +
																										  br +
				"#cbr parameters"																		+ br +
				"$cbr(" + flow + ") set packetSize_ " + this.data.getPacketSize()       				+ br +
				"$cbr(" + flow + ") set interval_ " + rate												+ br +
				   																						  br +
				"puts \"linking cbr to the udp source\"" 												+ br +
				"$cbr(" + flow + ") attach-agent $udp_(" + flow + ")"									+ br +
																										  br +
				"puts \"creating sink agent sink(" + flow + ")\""										+ br +
				"set sink_(" + flow + ") [new Agent/Null]"												+ br +
																										  br +				
				"puts \"setting sink agent to the destintion node- node_("+ destination +") sink("+ flow +")\"" + br +
				"$ns_ attach-agent $node_("+ destination +") $sink_("+ flow +")"						+ br +
																										  br +	
				"puts \"Connecting udp("+ flow +") sink("+ flow +")\""									+ br +
				"$ns_ connect $udp_("+ flow +") $sink_("+ flow +")"										+ br +
																										  br +
				"set nrand_c [ expr rand() ]"															+ br +
				"puts \"cbr("+ flow +") iniciando em " + t0 + " \""									 	+ br +
				"$ns_ at " + t0 + " \"$cbr("+ flow +") start\""											+ br +
				"$ns_ at " + tf + " \"$cbr("+ flow +") stop\""											+ br );
	}
	
	private void writeSimulation() throws IOException{
		this.scriptFile.write(																					  br + 
		"#"																								+ br +
		"# finishing simulation"																		+ br +
		"#"																								+ br +
		"for {set i 0} {$i < $opt(nn) } {incr i} {"														+ br +
		"    $ns_ at $opt(stop).01 \"$node_($i) reset\""												+ br +
		"}"																								+ br +

		"# stopping nam animation"																		+ br +
		"$ns_ at  $opt(stop).01	\"$ns_ nam-end-wireless $opt(stop)\""									+ br +
		"$ns_ at $opt(stop).01 \"puts \\\"NS EXITING...\\\" ; $ns_ halt\""								+ br +

		"proc stop {} {"																				+ br +
		"    global ns_ tracefd"																		+ br +
		"    $ns_ flush-trace"																			+ br +
		"    close $tracefd"																			+ br +
		"}"																								+ br +

		"puts \"Starting Simulation...\"" 																+ br +
		"$ns_ run"
		);
	}
	
	/**
	 * @throws IOException
	 */
	public void generateScript() throws IOException{
		this.writeHeader();
		int iFlow = 0;
		
		for( Map.Entry<String, DataNode> flow : this.data.getThroughputData().entrySet()){
			String[] split = flow.getKey().split(" ");
			Float t0 = this.data.getT0();
			Float tf = this.data.getTf();
			Float rate = flow.getValue().getFlowRate();
			Integer source = Integer.valueOf(split[0].substring(1));
			Integer destination = Integer.valueOf(split[1].substring(1));
			Integer sourceWireless = (source - this.data.getNn() / 2 ) / this.data.getNn();
			Integer destinationWireless = (destination - this.data.getNn() / 2 ) / this.data.getNn();
			this.writeAgents(t0, tf, sourceWireless, destinationWireless, iFlow, rate);
			iFlow ++;
		}
		
		this.writeSimulation();
		this.scriptFile.flush();
		this.scriptFile.close();
	}
}
