package org.rockhill.adoration.database.tables;

import org.rockhill.adoration.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adoration.database.exception.DatabaseHandlingException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Entity
@Table(name = "dbo.person")
public class Person {

    private Long id;
    private String name;
    private Integer adorationStatus;
    private Boolean isAnonymous;
    private String mobile;
    private Boolean mobileVisible;
    private String email; //single e-mail
    private Boolean emailVisible;
    private String adminComment;
    private Boolean dhcSigned;
    private String dhcSignedDate;
    private String coordinatorComment;
    private String visibleComment;
    private String languageCode;

    public Person() {
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

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if ((name == null) || (name.length() == 0)) {
            throw new DatabaseHandlingException("Person name tried to set to null/zero length.");
        }
        this.name = name;
    }

    @Column(name = "adorationStatus", nullable = false)
    public Integer getAdorationStatus() {
        return adorationStatus;
    }

    public void setAdorationStatus(Integer adorationStatus) {
        AdoratorStatusTypes.getTypeFromId(adorationStatus);
        this.adorationStatus = adorationStatus;
    }

    @Column(name = "isAnonymous", nullable = false)
    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    @Column(name = "mobile", nullable = true)
    public String getMobile() {
        if (mobile != null) {
            return mobile;
        } else {
            return "";
        }
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(name = "mobileVisible", nullable = false)
    public Boolean getMobileVisible() {
        return mobileVisible;
    }

    public void setMobileVisible(Boolean mobileVisible) {
        this.mobileVisible = mobileVisible;
    }

    @Column(name = "email", nullable = true)
    public String getEmail() {
        if (email != null) {
            return email;
        } else {
            return "";
        }
    }

    public void setEmail(String email) {
        if (email != null) {
            this.email = email.toLowerCase();
        } else {
            this.email = null;
        }
    }

    @Column(name = "emailVisible", nullable = false)
    public Boolean getEmailVisible() {
        return emailVisible;
    }

    public void setEmailVisible(Boolean emailVisible) {
        this.emailVisible = emailVisible;
    }

    @Column(name = "adminComment", nullable = true)
    public String getAdminComment() {
        if (adminComment != null) {
            return adminComment;
        } else {
            return "";
        }
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }

    @Column(name = "dhcSigned", nullable = false)
    public Boolean getDhcSigned() {
        return dhcSigned;
    }

    public void setDhcSigned(Boolean dhcSigned) {
        this.dhcSigned = dhcSigned;
    }

    @Column(name = "dhcSignedDate", nullable = true)
    //this also might be null
    public String getDhcSignedDate() {
        if (dhcSignedDate != null) {
            return dhcSignedDate;
        } else {
            return "";
        }
    }

    public void setDhcSignedDate(String dhcSignedDate) {
        if ((dhcSignedDate != null) && (dhcSignedDate.length() > 0)){
            try {
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                df1.parse(dhcSignedDate);
            } catch (ParseException e) {
                throw new DatabaseHandlingException("DhcSignedDate date format is unacceptable, it must be YYYY-MM-DD");
            }
        }
        this.dhcSignedDate = dhcSignedDate;
    }

    @Column(name = "coordinatorComment", nullable = true)
    public String getCoordinatorComment() {
        if (coordinatorComment != null) {
            return coordinatorComment;
        } else {
            return "";
        }
    }

    public void setCoordinatorComment(String coordinatorComment) {
        this.coordinatorComment = coordinatorComment;
    }

    @Column(name = "visibleComment", nullable = true)
    public String getVisibleComment() {
        if (visibleComment != null) {
            return visibleComment;
        } else {
            return "";
        }
    }

    public void setVisibleComment(String visibleComment) {
        this.visibleComment = visibleComment;
    }

    @Column(name = "languageCode", nullable = false)
    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
}
