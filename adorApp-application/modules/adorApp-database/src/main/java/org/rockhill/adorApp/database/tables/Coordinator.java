package org.rockhill.adorApp.database.tables;

import org.rockhill.adorApp.database.business.helper.enums.CoordinatorTypes;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dbo.coordinator")
public class Coordinator {

    private Long id;
    private Integer coordinatorType;
    private Long personId;

    public Coordinator() {
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

    @Column(name = "coordinatorType", nullable = false)
    public Integer getCoordinatorType() {
        return coordinatorType;
    }

    public void setCoordinatorType(Integer coordinatorType) {
        CoordinatorTypes.getTypeFromId(coordinatorType);
        this.coordinatorType = coordinatorType;
    }

    @Column(name = "personId", nullable = true)
    public Long getPersonId() {
        return personId;
    }
    public void setPersonId(Long personId) {
        this.personId = personId;
    }

}
