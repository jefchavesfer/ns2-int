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

package io.java;


/**
 * Stores all simulation flow pattern data
 * 
 * @author jchaves
 */
public class NodeData {

    private Integer cluster;
    private Integer x;
    private Integer y;

    /**
     * @param cluster
     * @param x
     * @param y
     * @since
     */
    public NodeData(Integer cluster, Integer x, Integer y) {
        super();
        this.cluster = cluster;
        this.x = x;
        this.y = y;
    }

    /**
     * @param cluster
     * @param x
     * @param y
     */
    public void setNode(Integer cluster, Integer x, Integer y) {
        this.x = x;
        this.y = y;
        this.cluster = cluster;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.cluster == null) ? 0 : this.cluster.hashCode());
        result = (prime * result) + ((this.x == null) ? 0 : this.x.hashCode());
        result = (prime * result) + ((this.y == null) ? 0 : this.y.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        NodeData other = (NodeData) obj;
        if (this.cluster == null) {
            if (other.cluster != null) {
                return false;
            }
        } else if (!this.cluster.equals(other.cluster)) {
            return false;
        }
        if (this.x == null) {
            if (other.x != null) {
                return false;
            }
        } else if (!this.x.equals(other.x)) {
            return false;
        }
        if (this.y == null) {
            if (other.y != null) {
                return false;
            }
        } else if (!this.y.equals(other.y)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "" + this.cluster + this.x + this.y;
    }

    /**
     * @return cluster
     */
    public Integer getCluster() {
        return this.cluster;
    }

    /**
     * @return x
     */
    public Integer getX() {
        return this.x;
    }

    /**
     * @return y
     */
    public Integer getY() {
        return this.y;
    }
}
