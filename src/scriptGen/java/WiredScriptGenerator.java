
package scriptGen.java;


import io.java.NodeData;
import io.java.SimulationParams;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;


/**
 * Generates ns2-script for the wired simulation
 * 
 * @author jchaves
 */
public class WiredScriptGenerator {

    static private String br = " \r\n";
    private FileWriter scriptFile;

    private SimulationParams simulationParams;

    /**
     * @param simulationParams
     * @throws IOException
     */
    public WiredScriptGenerator(SimulationParams simulationParams) throws IOException {
        super();
        this.simulationParams = simulationParams;
        this.scriptFile = new FileWriter(this.simulationParams.getWiredFileDiscriminator() + ".tcl");
    }

    private void writeSimulation() throws IOException{
                DecimalFormat df = new DecimalFormat("#.###");
		this.scriptFile.write(																									 											  br +
		        "set nc " + this.simulationParams.getNumberOfClusters() + " ;# number of clusters" + br +
			"set nn " + this.simulationParams.getNumberOfNodesInCluster() + " ;# total number of src/router nodes in each cluster"	+ br +
			"set distNodes 100 ;# distande between cluster nodes" + br + br +
			
			"#Topology calculations" + br +				
			"set x 			[expr int(sqrt($nn))]" + br +
			"set xc	 		[expr int(sqrt($nc))]" + br + br +

			"set initialX 100" + br +
			"set initialY [expr $x * $xc * $distNodes + $initialX]"	+ br +
			"set initialZ 0" + br + br +

			"#area" + br +
			"set val(x)		[expr $initialX + ( $x * $xc + 1 ) * $distNodes ]" + br +
			"set val(y)		[expr $initialY + ( $x ) * $distNodes]"	+ br + br +

			"set clusterinternaltraffic " + this.simulationParams.getInternalTraffic() + " ; #percent of cluster size busy with internal communication" + br + br +

			"set clusterexternaltraffic " + this.simulationParams.getExternalTraffic() + " ; #percent of cluster size busy with external communication" + br + br +

			"set clNodeIntAct [expr int($nn * $clusterinternaltraffic/2)] ;# the number of nodes doing internal communication by cluster" + br + br +
				
			"set clNodeExtAct [expr int($nn * $clusterexternaltraffic/2)] ;# the number of nodes doing external communication by cluster" + br + br +

			"#CBR parameters" + br +																																	
			"set cbrPacketSize " + this.simulationParams.getPacketSize() + br +																						
			"set cbrPacketInterval " + df.format(1.0f/this.simulationParams.getAppThroughput()) + br + br +																					
																			
			"#NoC position (center of the toplogy)"	+ br +
			"set xCluster [expr ($x + 1) / 2 - 1]" + br +
			"set yCluster [expr ($x + 1) / 2 - 1]" + br + br +
				
			"# Create a simulator object" + br +
			"set Nocns [new Simulator]" + br +
			"#$Nocns node-config -addressType hierarchical"	+ br + br +
				
			"#AddrParams set domain_num_ 1           ;# number of domains" + br +
			"#lappend cluster_num $nc                ;# number of clusters in each domain"	+ br +
			"#AddrParams set cluster_num_ $cluster_num" + br +
			"#for {set i 0} {$i < $nc} {incr i} {"	+ br +
			"#	lappend eilastlevel $nn        ;# number of nodes in each cluster" + br +
			"#}" + br +
			"#AddrParams set nodes_num_ $eilastlevel" + br + br +
				
			"#Open simulation exit file" + br +
			"set tracefd     [open " + this.simulationParams.getWiredFileDiscriminator() + ".tr w]"	+ br +
			"#Open the NAM trace file" + br +
			"set namtrace [open " + this.simulationParams.getWiredFileDiscriminator() + ".nam w]" + br +
			"$Nocns trace-all $tracefd" + br +
			"$Nocns namtrace-all $namtrace" + br + br +

			"# set up topography object" + br +
			"#set topo       [new Topography]" + br +
			"#$topo load_flatgrid $val(x) $val(y)" + br + br +
				
			"#Creating god"	+ br +
			"#create-god [expr $nc * $nn]" + br + br +
							
			"set max_bandwidth " + this.simulationParams.getWiredBandwidth() + br +
			"set buffSize "      + this.simulationParams.getWiredQueueSize() + br +
			"set linkDelay "     + this.simulationParams.getLinkDelay() + br + br +
																		
			"#total mesh" + br +
			"for {set cl 0} {$cl < $nc} {incr cl} {" + br +
			"	for {set i 0} {$i < $x} {incr i} {" + br +
			"		for {set j 0} {$j < $x} {incr j} {" + br +
			"			#setting element number" + br +
			"			set nEl     [expr $i * $x + $j]" + br +
			"			set r($cl$i$j) [$Nocns node]" + br +
			"			#set r($cl$i$j) [$Nocns node [lindex 0.$cl.$nEl]]" + br +
			"			$r($cl$i$j) shape square" + br +
			"			puts \"node r($cl$i$j) created. Index: 0.$cl.$nEl\"" + br +
			"		}" + br +
			"	}" + br +
			"}" + br + br +
	
			"#shotcuts" + br +
			"set maxCoord [expr $x * $xc]" + br +
			"for {set i 0} {$i < $nc} {incr i} {" + br +
			"	#for each cluster coordinate calculation" + br +
			"	set xNode [expr int($i % $nc) * $xc + $xCluster]" + br +
			"	set yNode [expr int($i / $nc) * $xc + $yCluster]" + br + br +
				
			"	#right cluster" + br +
			"	set rc [expr $i + 1]" + br + br +

			"	#down cluster"	+ br +
			"	set dc [expr $i + $xc]" + br + br + 

			"	#down-right cluster" + br + 
			"	set drc [expr $i + $xc + 1]" + br + br +

			"	#down-left cluster" + br +
			"	set dlc [expr $i + $xc - 1]" + br + br + 
			
			"	#making connections, if possible" + br +
			"	set maxInLine     [expr $xc * (int($i / $xc) + 1)]" + br +																									
			"	set minInNextLine [expr $maxInLine - 1]" + br +
			"	set maxInNextLine [expr $minInNextLine + $xc + 1]" + br + br +
						
			"	if { $rc < $maxInLine } {" + br +	
			"		$Nocns duplex-link    $r($i$xCluster$yCluster) $r($rc$xCluster$yCluster) $max_bandwidth $linkDelay DropTail" + br +
			"		$Nocns duplex-link-op $r($i$xCluster$yCluster) $r($rc$xCluster$yCluster) orient right" + br +
			"		$Nocns queue-limit    $r($i$xCluster$yCluster) $r($rc$xCluster$yCluster) $buffSize" + br +
			"		$Nocns queue-limit    $r($rc$xCluster$yCluster) $r($i$xCluster$yCluster) $buffSize ;#double direction" + br +
			"		$Nocns duplex-link-op $r($i$xCluster$yCluster) $r($rc$xCluster$yCluster) queuePos 0.5" + br + br +
			"	}" + br +
			"	if { $dc < $nc } {" + br +	
			"		$Nocns duplex-link    $r($i$xCluster$yCluster) $r($dc$xCluster$yCluster) $max_bandwidth $linkDelay DropTail" + br +
			"		$Nocns duplex-link-op $r($i$xCluster$yCluster) $r($dc$xCluster$yCluster) orient down" + br +
			"		$Nocns queue-limit    $r($i$xCluster$yCluster) $r($dc$xCluster$yCluster) $buffSize" + br +
			"		$Nocns queue-limit    $r($dc$xCluster$yCluster) $r($i$xCluster$yCluster) $buffSize ;#double direction" + br +
			"		$Nocns duplex-link-op $r($i$xCluster$yCluster) $r($dc$xCluster$yCluster) queuePos 0.5" + br +
			"	}"																																				+ br +
			"	if { $drc < $nc &&  $drc < $maxInNextLine  } {"																									+ br +
			"		$Nocns duplex-link    $r($i$xCluster$yCluster) $r($drc$xCluster$yCluster) $max_bandwidth $linkDelay DropTail"								+ br +
			"		$Nocns duplex-link-op $r($i$xCluster$yCluster) $r($drc$xCluster$yCluster) orient down-right"												+ br +
			"		$Nocns queue-limit    $r($i$xCluster$yCluster) $r($drc$xCluster$yCluster) $buffSize"														+ br +
			"		$Nocns queue-limit    $r($drc$xCluster$yCluster) $r($i$xCluster$yCluster) $buffSize ;#double +direction"									+ br +
			"		$Nocns duplex-link-op $r($i$xCluster$yCluster) $r($drc$xCluster$yCluster) queuePos 0.5"														+ br +
			"	}"																																				+ br +
			"	if { $dlc > $minInNextLine &&  $dlc < $nc } {"																									+ br +
			"		$Nocns duplex-link    $r($i$xCluster$yCluster) $r($dlc$xCluster$yCluster) $max_bandwidth $linkDelay DropTail"								+ br +
			"		$Nocns duplex-link-op $r($i$xCluster$yCluster) $r($dlc$xCluster$yCluster) orient down-left"													+ br +
			"		$Nocns queue-limit    $r($i$xCluster$yCluster) $r($dlc$xCluster$yCluster) $buffSize"														+ br +
			"		$Nocns queue-limit    $r($dlc$xCluster$yCluster) $r($i$xCluster$yCluster) $buffSize ;#double direction"										+ br +
			"		$Nocns duplex-link-op $r($i$xCluster$yCluster) $r($dlc$xCluster$yCluster) queuePos 0.5"														+ br +
			"	}"																																				+ br +
			"}"																																					+ br +
													
			"#create links between nodes"																														+ br +
			"#mesh with shotcuts topology"																														+ br +
			"set ctrl 0"																																		+ br +
			"#mesh connections"																																	+ br +
			"for {set cl 0} {$cl < $nc} {incr cl} {"																											+ br +
			"	for {set i 0} {$i < $x} {incr i} {"																												+ br +
			"		for {set j 0} {$j < $x} {incr j} {"																											+ br +
			"			#right node"																															+ br +
			"			set rn [expr $j + 1]"																													+ br +
			"			set ctrl [expr $ctrl + 1]"																												+ br +
																
			"			#down node"																																+ br +
			"			set dn [expr $i + 1]"																													+ br +
			"			puts \"rn: $rn , x: $x , cl: $cl, i: $i, j: $j\"" 																						+ br +
			"			if { $rn < $x } {"																														+ br +
			"				puts \"entrei no normal\"" 																											+ br +
			"				#has simple right connection"																										+ br +
			"				puts \"r([expr $cl][expr $i][expr $j]) r([expr $cl][expr $i][expr $rn])\""															+ br +
			"				$Nocns duplex-link $r([expr $cl][expr $i][expr $j]) $r([expr $cl][expr $i][expr $rn]) $max_bandwidth $linkDelay DropTail"			+ br +
			"				$Nocns duplex-link-op $r([expr $cl][expr $i][expr $j ]) $r([expr $cl][expr $i][expr $rn]) orient right"								+ br +

			"				$Nocns queue-limit    $r([expr $cl][expr $i][expr $j ]) $r([expr $cl][expr $i][expr $rn]) $buffSize"								+ br +
																																									
			"				$Nocns queue-limit    $r([expr $cl][expr $i][expr $rn]) $r([expr $cl][expr $i][expr $j ]) $buffSize"								+ br +
			"				$Nocns duplex-link-op $r([expr $cl][expr $i][expr $j ]) $r([expr $cl][expr $i][expr $rn]) queuePos 0.5"								+ br +
			"			} else {"																																+ br +
			"				if { [expr $cl % $xc] != [expr $xc - 1] } {"																						+ br +
			"					#right cluster"																													+ br +
			"					set rc [expr $cl + 1 ]"																											+ br +
			"					puts \"rc: $rc , xc: $xc\""																										+ br +
			"					#has right cluster connection"																									+ br +
			"					$Nocns duplex-link    $r([expr $cl][expr $i][expr $j]) $r([expr $rc][expr $i]0) $max_bandwidth $linkDelay DropTail"				+ br +
			"					$Nocns duplex-link-op $r([expr $cl][expr $i][expr $j]) $r([expr $rc][expr $i]0) orient right"									+ br +

			"					$Nocns queue-limit    $r([expr $cl][expr $i][expr $j]) $r([expr $rc][expr $i]0) $buffSize"										+ br +
			"					$Nocns queue-limit    $r([expr $rc][expr $i]0) $r([expr $cl][expr $i][expr $j]) $buffSize"										+ br +
			"					$Nocns duplex-link-op $r([expr $cl][expr $i][expr $j]) $r([expr $rc][expr $i]0) queuePos 0.5"									+ br +
			"				}"																																	+ br +
			"			}"																																		+ br +
			
			"			if { $dn < $x } {"																														+ br +
			"				#has simple down connection" 																										+ br +
			"				$Nocns duplex-link    $r([expr $cl][expr $i][expr $j ]) $r([expr $cl][expr $dn][expr $j]) $max_bandwidth $linkDelay DropTail"		+ br +
			"				$Nocns duplex-link-op $r([expr $cl][expr $i][expr $j ]) $r([expr $cl][expr $dn][expr $j]) orient down"								+ br +
			"				$Nocns queue-limit    $r([expr $cl][expr $i][expr $j ]) $r([expr $cl][expr $dn][expr $j]) $buffSize"								+ br +
			"				$Nocns queue-limit    $r([expr $cl][expr $dn][expr $j]) $r([expr $cl][expr $i][expr $j]) $buffSize"									+ br +
			"				$Nocns duplex-link-op $r([expr $cl][expr $i][expr $j ]) $r([expr $cl][expr $dn][expr $j]) queuePos 0.5"								+ br +
			"			} else {"																																+ br +
			"				#down cluster"																														+ br +
			"				set dc [expr $cl + $xc]"																											+ br +
								
			"				if { $dc < $nc } {"																													+ br +
			"					#has down cluster connection"																									+ br +
			"					puts \"dc: $dc, nc: $nc, cl: $cl, i: $i, j: $j\""																				+ br +
			"					$Nocns duplex-link    $r([expr $cl][expr $i][expr $j]) $r([expr $dc]0[expr $j]) $max_bandwidth $linkDelay DropTail"				+ br +
			"					$Nocns duplex-link-op $r([expr $cl][expr $i][expr $j]) $r([expr $dc]0[expr $j]) orient down"									+ br +
				
			"					$Nocns queue-limit    $r([expr $cl][expr $i][expr $j]) $r([expr $dc]0[expr $j]) $buffSize"										+ br +
			"					$Nocns queue-limit    $r([expr $dc]0[expr $j]) $r([expr $cl][expr $i][expr $j]) $buffSize"										+ br +
			"					$Nocns duplex-link-op $r([expr $cl][expr $i][expr $j]) $r([expr $dc]0[expr $j]) queuePos 0.5"									+ br +
			"				}"																																	+ br +
			"			}"																																		+ br +
			"		}"																																			+ br +
			"	}"																																				+ br +
			"}" + br);																																					

	                Integer idFlow = 0;
                        this.scriptFile.write( br + "#INTERNAL FLOWS" + br);
                        for ( Map.Entry<NodeData, NodeData> internalFlow : this.simulationParams.getInternalFlowMap().entrySet()){
                            String source = internalFlow.getKey().toString();
		            String destination = internalFlow.getValue().toString();
		            this.scriptFile.write( br + 
		                                "               #Create a UDP agent and attach it to node s(" + idFlow + ")"                                                                                                                                                                           + br +
		                                "               puts \"Creating udp_(" + idFlow + ")\""                                                                                                                                                                                                                        + br +
		                                "               set udp_(" + idFlow + ") [new Agent/UDP]"                                                                                                                                                                                                                      + br +
		                                "               $udp_(" + idFlow + ") set class_ 2"                                                                                                                                                                                                                            + br +

		                                "               puts \"Attaching r(" + source +") udp_(" + idFlow + ")\""                                                                                                                           + br +
		                                "               $Nocns attach-agent $r(" + source +") $udp_(" + idFlow + ")"                                                                                                                        + br +

		                                "               # Create a CBR traffic source and attach it to udp(" + idFlow + ")"                                                                                                                                                          + br +
		                                "               puts \"Creating cbr(" + idFlow + ")\""                                                                                                                                                                                                                         + br +
		                                "               puts \"Attaching cbr(" + idFlow + ") udp(" + idFlow + ")\""                                                                                                                                                                                           + br +
		                                "               set cbr(" + idFlow + ") [new Application/Traffic/CBR]"                                                                                                                                                                                         + br +

		                                "               $cbr(" + idFlow + ") set packetSize_ $cbrPacketSize"                                                                                                                                                                                           + br +
		                                "               $cbr(" + idFlow + ") set interval_ $cbrPacketInterval"                                                                                                                                                                                         + br +

		                                "               $cbr(" + idFlow + ") attach-agent $udp_(" + idFlow + ")"                                                                                                                                                                                                      + br +

		                                "               puts \"Creating sink_(" + idFlow + ")\""                                                                                                                                                                                                                       + br +
		                                "               set sink_(" + idFlow + ") [new Agent/Null]" + br + 
		                                
		                                "               puts \"Attaching r(" + destination + ") sink_(" + idFlow + ")\""                                                                                                                          + br +
		                                "               $Nocns attach-agent $r(" + destination + ") $sink_(" + idFlow + ")"                                                                                                                       + br +

		                                "               puts \"Connecting udp(" + idFlow + ") sink(" + idFlow + ")\""                                                                                                                                                                                         + br +
		                                "               $Nocns connect $udp_(" + idFlow + ") $sink_(" + idFlow + ")"                                                                                                                                                                                          + br +

		                                "               set nrand_c [ expr rand() ]"                                                                                                                                                                                                                            + br +
		                                "               puts \"cbr(" + idFlow + ") starting at $nrand_c + " + this.simulationParams.getInitialTime() +"\""                                                                                                                                                                                         + br +
		                                "               $Nocns at [ expr "+ this.simulationParams.getInitialTime() +" + $nrand_c ] \"$cbr(" + idFlow + ") start\""                                                                                                                                                                        + br +
		                                "               $Nocns at " + this.simulationParams.getEndTime() + ".01 \"$cbr(" + idFlow + ") stop\""    +br     
		                                
		                    );
		                    idFlow++;
		                }
		                
		                this.scriptFile.write( br + "#EXTERNAL FLOWS" + br);
		                for ( Map.Entry<NodeData, NodeData> externalFlow : this.simulationParams.getExternalFlowMap().entrySet()){
                                    String source = externalFlow.getKey().toString();
                                    String destination = externalFlow.getValue().toString();
                                    this.scriptFile.write( br +
                                                "               #Create a UDP agent and attach it to node s(" + idFlow + ")"                                                                                                                                                                           + br +
                                                "               puts \"Creating udp_(" + idFlow + ")\""                                                                                                                                                                                                                        + br +
                                                "               set udp_(" + idFlow + ") [new Agent/UDP]"                                                                                                                                                                                                                      + br +
                                                "               $udp_(" + idFlow + ") set class_ 2"                                                                                                                                                                                                                            + br +

                                                "               puts \"Attaching r(" + source +") udp_(" + idFlow + ")\""                                                                                                                           + br +
                                                "               $Nocns attach-agent $r(" + source +") $udp_(" + idFlow + ")"                                                                                                                        + br +

                                                "               # Create a CBR traffic source and attach it to udp(" + idFlow + ")"                                                                                                                                                          + br +
                                                "               puts \"Creating cbr(" + idFlow + ")\""                                                                                                                                                                                                                         + br +
                                                "               puts \"Attaching cbr(" + idFlow + ") udp(" + idFlow + ")\""                                                                                                                                                                                           + br +
                                                "               set cbr(" + idFlow + ") [new Application/Traffic/CBR]"                                                                                                                                                                                         + br +

                                                "               $cbr(" + idFlow + ") set packetSize_ $cbrPacketSize"                                                                                                                                                                                           + br +
                                                "               $cbr(" + idFlow + ") set interval_ $cbrPacketInterval"                                                                                                                                                                                         + br +

                                                "               $cbr(" + idFlow + ") attach-agent $udp_(" + idFlow + ")"                                                                                                                                                                                                      + br +

                                                "               puts \"Creating sink_(" + idFlow + ")\""                                                                                                                                                                                                                       + br +
                                                "               set sink_(" + idFlow + ") [new Agent/Null]" + br + 
                                                
                                                "               puts \"Attaching r(" + destination + ") sink_(" + idFlow + ")\""                                                                                                                          + br +
                                                "               $Nocns attach-agent $r(" + destination + ") $sink_(" + idFlow + ")"                                                                                                                       + br +

                                                "               puts \"Connecting udp(" + idFlow + ") sink(" + idFlow + ")\""                                                                                                                                                                                         + br +
                                                "               $Nocns connect $udp_(" + idFlow + ") $sink_(" + idFlow + ")"                                                                                                                                                                                          + br +

                                                "               set nrand_c [ expr rand() ]"                                                                                                                                                                                                                            + br +
                                                "               puts \"cbr(" + idFlow + ") starting at $nrand_c + " + this.simulationParams.getInitialTime() +"\""                                                                                                                                                                                         + br +
                                                "               $Nocns at [ expr "+ this.simulationParams.getInitialTime() +" + $nrand_c ] \"$cbr(" + idFlow + ") start\""                                                                                                                                                                        + br +
                                                "               $Nocns at " + this.simulationParams.getEndTime() + ".01 \"$cbr(" + idFlow + ") stop\""      +br   
                                                
                                    );
                                    idFlow++;
                                }
		
		                this.scriptFile.write(
				"########################################################"																							+ br +
				"#"																																					+ br +
				"# Tell nodes when the simulation ends"																												+ br +
				"#"																																					+ br +
				"for {set i 0} {$i < $nc} {incr i} {"																												+ br +
				"	for { set j 0 } { $j < $x } {incr j} {"																											+ br +
				"		for { set k 0 } {$k < $x} {incr k} {"																										+ br +
				"				$Nocns at "+ this.simulationParams.getEndTime() + ".01 \"$r([expr $i][expr $j][expr $k]) reset\""									+ br +
				"		}"																																			+ br +
				"	}"																																				+ br +
				"}"																																					+ br +

				"#$Nocns rtproto Static ;# Static routing strategy"																									+ br +

				"$Nocns at " + this.simulationParams.getEndTime() + ".02 \"stop\""																					+ br +
				"$Nocns at " + this.simulationParams.getEndTime() + ".03 \"puts \\\"NS EXITING...\\\" ; $Nocns halt\""												+ br +

				"proc stop {} {"																																	+ br +
				"   global Nocns tracefd namtrace"																													+ br +
				"   $Nocns flush-trace"																																+ br +
				"   close $tracefd"																																	+ br +
				"   close $namtrace ;# Close the NAM trace file"																									+ br +
				    
				"   # Print some info."																																+ br +
				"   puts \"Run nam...\""																															+ br +
				"   exec nam " + this.simulationParams.getWiredFileDiscriminator() + ".nam "																		+ br +
				"}"																																					+ br +	

				"#run the simulation"																																+ br +
				"puts \"eu vou rodar\""																																+ br +
				"$Nocns run"																																		+ br +
				"puts \"rodei\""																																	+ br
				);
	}

    /**
     * @throws IOException
     */
    public void generateScript() throws IOException {
        this.writeSimulation();
        this.scriptFile.flush();
        this.scriptFile.close();
    }
}
