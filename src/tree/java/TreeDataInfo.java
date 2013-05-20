package tree.java;

import java.util.List;
import java.util.ArrayList;

public class TreeDataInfo {

	Integer nOcurrences;
	List<Integer> nodeChain;
	
	public TreeDataInfo(){
		nOcurrences = 0;
		nodeChain = new ArrayList<Integer>();
	}
	
	public Integer getnOcurrences() {
		return nOcurrences;
	}
	public void setnOcurrences(Integer nOcurrences) {
		this.nOcurrences = nOcurrences;
	}
	public List<Integer> getNodeChain() {
		return nodeChain;
	}
	public void setNodeChain(List<Integer> nodeChain) {
		this.nodeChain = nodeChain;
	}
}

