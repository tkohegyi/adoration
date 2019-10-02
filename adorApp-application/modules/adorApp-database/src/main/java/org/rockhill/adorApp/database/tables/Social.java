package org.rockhill.adorApp.database.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "adoration.dbo.social")
public class Social {

    private Long id;
    private Long personId;
    private String googleEmail;
    private String googleUserName;
    private String googleUserId;
    private String googleUserPicture;

    public Social() {
        // this form used by Hibernate
    }

    @Column(name = "id", nullable = false)
    @Id
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "personId", nullable = true)
    public Long getPersonId() {
        return personId;
    }
    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    @Column(name = "googleEmail", nullable = true)
    public String getGoogleEmail() {
        if (googleEmail != null) {
            return googleEmail;
        } else {
            return "";
        }
    }
    public void setGoogleEmail(String googleEmail) {
        this.googleEmail = googleEmail;
    }

    @Column(name = "googleUserName", nullable = true)
    public String getGoogleUserName() {
        if (googleUserName != null) {
            return googleUserName;
        } else {
            return "";
        }
    }
    public void setGoogleUserName(String googleUserName) {
        this.googleUserName = googleUserName;
    }

    @Column(name = "googleUserId", nullable = true)
    public String getGoogleUserId() {
        if (googleUserId != null) {
            return googleUserId;
        } else {
            return "";
        }
    }
    public void setGoogleUserId(String googleUserId) {
        this.googleUserId = googleUserId;
    }

    @Column(name = "googleUserPicture", nullable = true)
    public String getGoogleUserPicture() {
        if (googleUserPicture != null) {
            return googleUserPicture;
        } else {
            return "";
        }
    }
    public void setGoogleUserPicture(String googleUserPicture) {
        this.googleUserPicture = googleUserPicture;
    }
}
