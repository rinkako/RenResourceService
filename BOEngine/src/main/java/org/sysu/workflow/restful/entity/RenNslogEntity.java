/*
 * Project Ren @ 2018
 * Rinkako, Ariana, Gordan. SYSU SDCS.
 */
package org.sysu.workflow.restful.entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Author: Rinkako
 * Date  : 2018/2/1
 * Usage :
 */
@Entity
@Table(name = "ren_nslog", schema = "renboengine", catalog = "")
public class RenNslogEntity {
    private String logid;
    private String label;
    private String level;
    private String message;
    private Timestamp timestamp;
    private String rtid;

    @Id
    @Column(name = "logid", nullable = false, length = 64)
    public String getLogid() {
        return logid;
    }

    public void setLogid(String logid) {
        this.logid = logid;
    }

    @Basic
    @Column(name = "label", nullable = true, length = 64)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Basic
    @Column(name = "level", nullable = true, length = 16)
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Basic
    @Column(name = "message", nullable = true, length = -1)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Basic
    @Column(name = "timestamp", nullable = true)
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Basic
    @Column(name = "rtid", nullable = true, length = 64)
    public String getRtid() {
        return rtid;
    }

    public void setRtid(String rtid) {
        this.rtid = rtid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RenNslogEntity that = (RenNslogEntity) o;

        if (logid != null ? !logid.equals(that.logid) : that.logid != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (level != null ? !level.equals(that.level) : that.level != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        if (rtid != null ? !rtid.equals(that.rtid) : that.rtid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = logid != null ? logid.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (rtid != null ? rtid.hashCode() : 0);
        return result;
    }
}