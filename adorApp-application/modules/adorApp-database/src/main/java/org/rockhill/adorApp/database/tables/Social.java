package org.rockhill.adorApp.database.tables;

import org.rockhill.adorApp.database.business.helper.enums.SocialStatusTypes;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dbo.social")
public class Social {

    private Long id;
    private Long personId;
    private Integer socialStatus;
    private String googleEmail;
    private String googleUserName;
    private String googleUserId;
    private String googleUserPicture;
    private String facebookEmail;
    private String facebookUserName;
    private String facebookUserId;
    private String facebookFirstName;
    private String comment;

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

    @Column(name = "socialStatus", nullable = false)
    public Integer getSocialStatus() {
        return socialStatus;
    }

    public void setSocialStatus(Integer socialStatus) {
        SocialStatusTypes.getTypeFromId(socialStatus); //validation
        this.socialStatus = socialStatus;
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

    @Column(name = "facebookEmail", nullable = true)
    public String getFacebookEmail() {
        if (facebookEmail != null) {
            return facebookEmail;
        } else {
            return "";
        }
    }
    public void setFacebookEmail(String facebookEmail) {
        this.facebookEmail = facebookEmail;
    }

    @Column(name = "facebookUserName", nullable = true)
    public String getFacebookUserName() {
        if (facebookUserName != null) {
            return facebookUserName;
        } else {
            return "";
        }
    }
    public void setFacebookUserName(String facebookUserName) {
        this.facebookUserName = facebookUserName;
    }

    @Column(name = "facebookUserId", nullable = true)
    public String getFacebookUserId() {
        if (facebookUserId != null) {
            return facebookUserId;
        } else {
            return "";
        }
    }
    public void setFacebookUserId(String facebookUserId) {
        this.facebookUserId = facebookUserId;
    }

    @Column(name = "facebookFirstName", nullable = true)
    public String getFacebookFirstName() {
        if (facebookFirstName != null) {
            return facebookFirstName;
        } else {
            return "";
        }
    }
    public void setFacebookFirstName(String facebookFirstName) {
        this.facebookFirstName = facebookFirstName;
    }


    @Column(name = "comment", nullable = true)
    public String getComment() {
        if (comment != null) {
            return comment;
        } else {
            return "";
        }
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}
