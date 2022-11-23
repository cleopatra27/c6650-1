package org.example;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Entity
@Table(name = "SkierData")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "SkierData.findAll", query = "SELECT s FROM SkierData s"),
        @NamedQuery(name = "SkierData.findById", query = "SELECT s FROM SkierData s WHERE s.id = :id")})
public class SkierData implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "skierID")
    private Integer skierID;
    @Column(name = "resortID")
    private Integer resortID;
    @Column(name = "seasonID")
    private Integer seasonID;
    @Column(name = "dayID")
    private Integer dayID;
    @Column(name = "time")
    private Integer time;
    @Column(name = "liftID")
    private Integer liftID;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSkierID() {
        return skierID;
    }

    public void setSkierID(Integer skierID) {
        this.skierID = skierID;
    }

    public Integer getResortID() {
        return resortID;
    }

    public void setResortID(Integer resortID) {
        this.resortID = resortID;
    }

    public Integer getSeasonID() {
        return seasonID;
    }

    public void setSeasonID(Integer seasonID) {
        this.seasonID = seasonID;
    }

    public Integer getDayID() {
        return dayID;
    }

    public void setDayID(Integer dayID) {
        this.dayID = dayID;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getLiftID() {
        return liftID;
    }

    public void setLiftID(Integer liftID) {
        this.liftID = liftID;
    }
}
