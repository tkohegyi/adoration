package org.rockhill.adoration.database.tables;

import org.rockhill.adoration.database.business.helper.enums.AdorationMethodTypes;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dbo.link")
public class Link {

    private Long id;
    private Long personId;
    private Integer hourId;
    private Integer type;
    private Integer priority;
    private String adminComment;
    private String publicComment;

    public Link() {
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

    @Column(name = "personId", nullable = false)
    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    @Column(name = "hourId", nullable = false)
    public Integer getHourId() {
        return hourId;
    }

    public void setHourId(Integer hourId) {
        this.hourId = hourId;
    }

    @Column(name = "type", nullable = false)
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        AdorationMethodTypes.getTypeFromId(type); //this will fail if the type is incorrect
        this.type = type;
    }

    @Column(name = "priority", nullable = false)
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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

    @Column(name = "publicComment", nullable = true)
    public String getPublicComment() {
        if (publicComment != null) {
            return publicComment;
        } else {
            return "";
        }
    }

    public void setPublicComment(String publicComment) {
        this.publicComment = publicComment;
    }
}
