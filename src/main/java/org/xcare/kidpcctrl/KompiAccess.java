package org.xcare.kidpcctrl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlRootElement;

//@formatter:off
@NamedQueries({ 
 @NamedQuery(name = KompiAccess.FIND_BY_USER, query = "SELECT k FROM KompiAccess k WHERE k.username = :username"),
 @NamedQuery(name = KompiAccess.FIND_ALL, query = "SELECT k FROM KompiAccess k")
})
//@formatter:on
@XmlRootElement
@Entity
public class KompiAccess {

    public static final String FIND_BY_USER = "KompiAccess.findByUser";
    public static final String FIND_ALL = "KompiAccess.findAll";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Long version;

    @Column(unique = true)
    private String username;

    private Date lastAccess;

    private Date dailyCreditGrantedTimestamp;

    private Date doNotTrackUntil;

    private int dailyCredit;

    private int creditBalanceInSeconds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
    }

    public Date getDailyCreditGrantedTimestamp() {
        return dailyCreditGrantedTimestamp;
    }

    public void setDailyCreditGrantedTimestamp(Date dailyCreditGrantedTimestamp) {
        this.dailyCreditGrantedTimestamp = dailyCreditGrantedTimestamp;
    }

    public int getDailyCredit() {
        return dailyCredit;
    }

    public void setDailyCredit(int dailyCredit) {
        this.dailyCredit = dailyCredit;
    }

    public int getCreditBalanceInSeconds() {
        return creditBalanceInSeconds;
    }

    public void setCreditBalanceInSeconds(int creditBalanceInSeconds) {
        this.creditBalanceInSeconds = creditBalanceInSeconds;
    }

    public Date getDoNotTrackUntil() {
        return doNotTrackUntil;
    }

    public void setDoNotTrackUntil(Date doNotTrackUntil) {
        this.doNotTrackUntil = doNotTrackUntil;
    }

}
