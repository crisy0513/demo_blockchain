package com.mindata.blockchain.core.model;

import com.mindata.blockchain.core.model.base.BaseEntity;

import javax.persistence.*;

/**
 * @author wuweifeng wrote on 2017/10/25.
 */
@Entity
@Table(name = "block",schema = "blockchain_4")
public class BlockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 2550)
    private String svalue;
    private String skey;
    private String shash;

    @Override
    public String toString() {
        return "BlockEntity{" +
                "id=" + id +
                ", svalue='" + svalue + '\'' +
                ", skey='" + skey + '\'' +
                ", shash='" + shash + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSvalue() {
        return svalue;
    }

    public void setSvalue(String svalue) {
        this.svalue = svalue;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public String getShash() {
        return shash;
    }

    public void setShash(String shash) {
        this.shash = shash;
    }
}
