package scriptGen.java;
import io.java.TclGeneratorSimulationData;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import tree.java.DataNode;

public class ScriptGenerator {
	private static String br = " \r\n";
	private FileWriter scriptFile;
	
	private TclGeneratorSimulationData data;
	
	ScriptGenerator(String fileName, TclGeneratorSimulationData data) throws IOException{
	   	this.data = data;
	   	this.scriptFile = new FileWriter(fileName);			
	}
	
	private void writeHeader() throws IOException{
		this.scriptFile.write(																			  br + 
		"# Inicialization " 																			+ br +
		"set ns_		[new Simulator] " 																+ br + 

		"#Topology parameters"																			+ br +
		"set opt(chan)           Channel/WirelessChannel    	;# Channel Type"  						+ br +
		"set opt(prop)           Propagation/TwoRayGround   	;# radio-propagation mode" 				+ br +
		"set opt(netif)          Phy/WirelessPhy/802_15_4   	;# network interface type"				+ br +
		"set opt(mac)            Mac/802_15_4	           		;# MAC type" 							+ br +
		"set opt(ifq)            Queue/DropTail/PriQueue	   	;# queue approach" 						+ br +
		"set opt(ll)             LL                         	;# link layer type" 					+ br +
		"set opt(ant)            Antenna/OmniAntenna        	;# antenna model" 						+ br +
		"set opt(filters)        GradientFilter    	   			;# options can be one or more of" 		+ br +
		";# TPP/OPP/Gear/Rmst/SourceRoute/Log/TagFilter" 												+ br +
		"set opt(ifqlen)         50                        		;# max packet in ifq" 					+ br +
		"set opt(fim)		     " + this.data.getTf() + "		;# end of the simulation (s)" 			+ br +

		"#protocols"																					+ br +
		"set opt(rp)             Directed_Diffusion"													+ br +
		"set opt(tr)             simulacaoDD      	   			;# trace file"							+ br +

		"#node numbers"																					+ br +
		"set opt(nn)             " + this.data.getNn()													+ br +
		"set rad [expr int( sqrt($opt(nn)) ) ]"															+ br +

		"#Area"																							+ br +
		"set distNodes  		100"																	+ br +
		"set opt(x)		[expr $distNodes * $rad]"														+ br +
		"set opt(y)		[expr $distNodes * $rad]"                                                       + br +

		"#transmitting nodes"                                                               		    + br +
		"set opt(nt)             1"																		+ br +

		"set pos_x         	0"																			+ br +
		"set pos_y        	0"																			+ br +

		"set tracefd     [open $opt(tr).tr w]"															+ br +
		"$ns_ trace-all $tracefd"																		+ br +
		"set namtrace    [open $opt(tr).nam w]"															+ br +	
		"$ns_ namtrace-all-wireless $namtrace $opt(x) $opt(y)"											+ br +

		"# ajustar objeto de topografia"																+ br +
		"set topo       [new Topography]"																+ br +

		"$topo load_flatgrid $opt(x) $opt(y)"															+ br +

		"# criar God"																					+ br +
		"set god_ [create-god $opt(nn)]"																+ br +

		"puts \"adjusting wireless-node configuration\""												+ br +
		"$ns_ node-config -adhocRouting $opt(rp) \\ "													+ br +
		"    -llType $opt(ll) \\ "																		+ br +
		"    -macType $opt(mac) \\ " 																	+ br +
		"    -ifqType $opt(ifq) \\ " 																	+ br +
		"    -ifqLen $opt(ifqlen) \\ "																	+ br +
		"    -antType $opt(ant) \\ " 																	+ br +
		"    -propType $opt(prop) \\ " 																	+ br +
		"    -phyType $opt(netif) \\ " 																	+ br +
		"    -topoInstance $topo \\ " 																	+ br +
		"    -diffusionFilter $opt(filters) \\ "														+ br +
		"    -agentTrace ON \\ "																		+ br + 
		"    -routerTrace ON \\ " 																	    + br +
		"    -macTrace OFF \\ "																			+ br +
		"    -phyTrace OFF \\ "																			+ br +
		"    -channel [new $opt(chan)] "																+ br +

		"#regular mesh"																					+ br +
		"for {set i 0} {$i < $opt(nn)} {incr i} {"														+ br +
		"    set node_($i) [$ns_ node $i]"																+ br +
		"    #if { $i == $k } {"																		+ br +
		"    #	$node_($i) sscs startPANCoord <txBeacon = 1> <beaconOrder = 6> <SuperframeOrder = 2>"   + br +
		"    #} else {"																					+ br +
		"    #	$node_($i) sscs startDevice <isFFD = 1> <assoPermit = 1> <txBeacon =0> <beaconOrder = 6> <SuperframeOrder = 2>" + br +
		"    #}"																						+ br +
		"}"																								+ br +

		"puts \"adjusting node initial position\""														+ br +
		"set rad [expr int( sqrt($opt(nn)) ) ]"															+ br +
		"for {set i 0} {$i < $opt(nn)} {incr i} {"														+ br +

		"	if {$i == 0} {"																				+ br +
		"		set pos_x 0"																			+ br +
		"		set pos_y [expr $rad * $distNodes]"														+ br +
		"	} elseif {$i % $rad  == 0 }"																+ br +
		"		set pos_x 0"																			+ br +
		"		set pos_y [expr $pos_y - $distNodes]"													+ br +
		"	} else {"																					+ br +
		"		set pos_x [expr $pos_x + $distNodes]"													+ br +
		"	}"																							+ br +
		"	puts \"n� $i: ($pos_x, $pos_y)\""															+ br +
		"	$node_($i) set X_ $pos_x"																	+ br +
		"	$node_($i) set Y_ $pos_y"																	+ br +
		"	$node_($i) set Z_ 0"																		+ br +
		"}"																								+ br +

		
		"source $opt(ip)"																				+ br +	

		"#nam node size"																				+ br +
		"for {set i 0} {$i < $opt(nn)} {incr i} {"														+ br +
		"    $ns_ initial_node_pos $node_($i) 30"                                                       + br +
		"}");
		
	}
	private void writeAgents(Float t0, Integer source, Integer destination, Integer flow ) throws IOException{
		this.scriptFile.write(
//		"#0 a 19 sao os emissores"																		+ br +
//		"#os nos sao escolhidos sem repeticao"															+ br +
//		"for {set i 0} {$i < $opt(nt)} {incr i} {"														+ br +
//		"    set l [expr $i + 1]"																		+ br +
//		"    set repetido_ 1"																			+ br +
//		"    #escolhe um no ainda nao escolhido"														+ br +
//		"    while {$repetido_ == 1} {"																	+ br +
//		"		set repetido_ 0"																		+ br +
//		"		set nrand [ expr (int(rand()*65535) % $opt(nn)) ] ;#chosse random node"					+ br +
//
//		"		#vefifying if the node was already used as destination"									+ br +
//		"		for {set j 0} {$j < $l} {incr j} {"														+ br +
//
//		"	    	if {$a($j) == $nrand} {"															+ br +
//		"				set repetido_ 1"																+ br +
//		"	    	}"																					+ br +
//		"		}"																						+ br +
//		"		#for"																					+ br +
//		"	}"																							+ br +
//		"	#while"																						+ br +

		"    #source node"																				+ br +
		"    puts \"flow " + flow + " chosen node " + source + "\""										+ br +
		"    puts \"creating source agent ddf_(" + flow + ")\""											+ br +
		"    set ddf_(" + flow + ") [new Application/DiffApp/PingSender/TPP]"							+ br +
		"    puts \"binding source agent - node_("+ source + ") ddf_(" + flow + ")\""					+ br +
		"    $ns_ attach-diffapp $node_(" + source + ") $ddf_(" + flow + ")"							+ br +
																										  br +
		"   puts \"ddf_(" + flow + ") publishing in " + t0 + "s\""										+ br +
		"   $ns_ at " + t0 + "\"$ddf_(" + flow + ") publish\""											+ br +
																										  br +
		"	puts \"creating sink agent sink(" + flow + ")\""											+ br +
		"	set sink_(0) [new Application/DiffApp/PingReceiver/TPP]"									+ br +
		"	puts \"colocando o agente de destino - node_(" + destination + ") sink(" + flow + ")\""		+ br +
		"	$ns_ attach-diffapp $node_(" + destination + ") $sink_(" + flow + ")"						+ br +
		"	puts \"sink_(" + flow + ") subscrevendo em " + t0 + "s\""									+ br +
		"	$ns_ at " + t0 + " \"$sink_(" + flow + ") subscribe\""										+ br		
		);																				
		
	}
	private void writeSimulation() throws IOException{
		this.scriptFile.write(																					  br + 
		"#"																								+ br +
		"# Terminando a simulacao"																		+ br +
		"#"																								+ br +
		"for {set i 0} {$i < $opt(nn) } {incr i} {"														+ br +
		"    $ns_ at $opt(fim).01 \"$node_($i) reset\""													+ br +
		"}"																								+ br +

		"# parando anima��o no nam"																		+ br +
		"$ns_ at  $opt(fim).01	\"$ns_ nam-end-wireless $opt(fim)\""									+ br +
		"$ns_ at $opt(fim).01 \"puts \"NS EXITING...\" ; $ns_ halt"										+ br +

		"proc stop {} {"																				+ br +
		"    global ns_ tracefd"																		+ br +
		"    $ns_ flush-trace"																			+ br +
		"    close $tracefd"																			+ br +
		"}"																								+ br +

		"puts \"Starting Simulation...\"" 																+ br +
		"$ns_ run"
		);

	}
	
	public void generateScript() throws IOException{
		this.writeHeader();
		int iFlow = 0;
		for( Map.Entry<String, DataNode> flow : this.data.getThroughputData().entrySet()){
			String[] split = flow.getKey().split(" ");
			Float t0 = this.data.getT0();
			Integer source = Integer.valueOf(split[0]);
			Integer destination = Integer.valueOf(split[1]);
			this.writeAgents(t0, source, destination, iFlow);
			iFlow ++;
		}
		this.writeSimulation();
		this.scriptFile.flush();
		this.scriptFile.close();
	}
}
