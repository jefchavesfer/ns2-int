
package tree.java;


/**
 * @author jchaves
 */
public class LinkInfo {

    private int sourceCluster;
    private int sourceNodeX;
    private int sourceNodeY;
    private int destinationCluster;
    private int destinationNodeX;
    private int destinationNodeY;

    /**
     * @param sourceCluster
     * @param destinationCluster
     * @param sourceNodeX
     * @param destinationNodeX
     * @param sourceNodeY
     * @param destinationNodeY
     */
    public LinkInfo(int sourceCluster, int destinationCluster, int sourceNodeX, int destinationNodeX, int sourceNodeY,
            int destinationNodeY) {
        this.sourceCluster = sourceCluster;
        this.sourceNodeX = sourceNodeX;
        this.sourceNodeY = sourceNodeY;
        this.destinationCluster = destinationCluster;
        this.destinationNodeX = destinationNodeX;
        this.destinationNodeY = destinationNodeY;
    }

    /**
     * @return o valor da propriedade sourceCluster
     */
    public int getSourceCluster() {
        return this.sourceCluster;
    }

    /**
     * @param sourceCluster
     *            o novo valor de sourceCluster
     */
    public void setSourceCluster(int sourceCluster) {
        this.sourceCluster = sourceCluster;
    }

    /**
     * @return o valor da propriedade sourceNodeX
     */
    public int getSourceNodeX() {
        return this.sourceNodeX;
    }

    /**
     * @param sourceNodeX
     *            o novo valor de sourceNodeX
     */
    public void setSourceNodeX(int sourceNodeX) {
        this.sourceNodeX = sourceNodeX;
    }

    /**
     * @return o valor da propriedade sourceNodeY
     */
    public int getSourceNodeY() {
        return this.sourceNodeY;
    }

    /**
     * @param sourceNodeY
     *            o novo valor de sourceNodeY
     */
    public void setSourceNodeY(int sourceNodeY) {
        this.sourceNodeY = sourceNodeY;
    }

    /**
     * @return o valor da propriedade destinationCluster
     */
    public int getDestinationCluster() {
        return this.destinationCluster;
    }

    /**
     * @param destinationCluster
     *            o novo valor de destinationCluster
     */
    public void setDestinationCluster(int destinationCluster) {
        this.destinationCluster = destinationCluster;
    }

    /**
     * @return o valor da propriedade destinationNodeX
     */
    public int getDestinationNodeX() {
        return this.destinationNodeX;
    }

    /**
     * @param destinationNodeX
     *            o novo valor de destinationNodeX
     */
    public void setDestinationNodeX(int destinationNodeX) {
        this.destinationNodeX = destinationNodeX;
    }

    /**
     * @return o valor da propriedade destinationNodeY
     */
    public int getDestinationNodeY() {
        return this.destinationNodeY;
    }

    /**
     * @param destinationNodeY
     */
    public void setDestinationNodeY(int destinationNodeY) {
        this.destinationNodeY = destinationNodeY;
    }
}
