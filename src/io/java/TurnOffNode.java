
package io.java;


/**
 * @author jchaves
 * @since
 */
public class TurnOffNode {

    private Integer nodeIndex;
    private Float initialTime;
    private Float endTime;

    /**
     * @since
     */
    TurnOffNode() {
        this.nodeIndex = 0;
        this.initialTime = 0f;
        this.endTime = 0f;
    }

    /**
     * @param nodeIndex
     * @param initialTime
     * @param endTime
     * @since
     */
    TurnOffNode(Integer nodeIndex, Float initialTime, Float endTime) {
        this.nodeIndex = nodeIndex;
        this.initialTime = initialTime;
        this.endTime = endTime;
    }

    /**
     * @return o valor da propriedade nodeIndex
     */
    public Integer getNodeIndex() {
        return this.nodeIndex;
    }

    /**
     * @param nodeIndex
     *            o novo valor de nodeIndex
     */
    public void setNodeIndex(Integer nodeIndex) {
        this.nodeIndex = nodeIndex;
    }

    /**
     * @return o valor da propriedade initialTime
     * @since
     */
    public Float getInitialTime() {
        return this.initialTime;
    }

    /**
     * @param initialTime
     *            o novo valor de initialTime
     */
    public void setInitialTime(Float initialTime) {
        this.initialTime = initialTime;
    }

    /**
     * @return o valor da propriedade endTime
     */
    public Float getEndTime() {
        return this.endTime;
    }

    /**
     * @param endTime
     *            o novo valor de endTime
     */
    public void setEndTime(Float endTime) {
        this.endTime = endTime;
    }

}
