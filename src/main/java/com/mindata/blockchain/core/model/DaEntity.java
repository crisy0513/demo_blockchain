package com.mindata.blockchain.core.model;

import javax.persistence.*;

/**
 * @author wuweifeng wrote on 2017/10/25.
 */
@Entity
@Table(name = "da",schema = "blockchain_4")
public class DaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String dh;
    private String tm;
    private String yw;

    @Override
    public String toString() {
        return "DaEntity{" +
                "id=" + id +
                ", dh='" + dh + '\'' +
                ", tm='" + tm + '\'' +
                ", yw='" + yw + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDh() {
        return dh;
    }

    public void setDh(String dh) {
        this.dh = dh;
    }

    public String getTm() {
        return tm;
    }

    public void setTm(String tm) {
        this.tm = tm;
    }

    public String getYw() {
        return yw;
    }

    public void setYw(String yw) {
        this.yw = yw;
    }
}
