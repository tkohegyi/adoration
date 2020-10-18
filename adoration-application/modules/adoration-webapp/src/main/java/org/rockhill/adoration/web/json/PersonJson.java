package org.rockhill.adoration.web.json;

import org.rockhill.adoration.database.tables.Person;

public class PersonJson {
    private static final String UNKNOWN_DATA = "???";
    public Long id;
    public String name;
    public String email;
    public String mobile;
    public String coordinatorComment;
    public String visibleComment;

    /**
     * Create a data class.
     * If requestor is privileged user, then returns with all fields.
     * If requestor is not privileged, then:
     * - Only ID is available in case DCH is not enabled.
     * - Name is "Anonymous" in case person would like to remain anonymous, and only id and mobile fields are filled.
     * - email/mobile is visible based on visibility flag.
     * - coordinator comment requires privileged user
     * - visible comment is always available
     *
     * @param person
     */
    public PersonJson(Person person, Boolean isPrivilegedUser) {
        this.id = person.getId();
        if (!person.getIsAnonymous() || isPrivilegedUser) {
            this.name = person.getName();
        } else {
            this.name = "Anonymous";
        }
        if ((person.getIsAnonymous() || !person.getEmailVisible()) && !isPrivilegedUser) {
            this.email = UNKNOWN_DATA;
        } else {
            this.email = person.getEmail();
        }
        if (person.getMobileVisible() || isPrivilegedUser) {
            this.mobile = person.getMobile();
        } else {
            this.mobile = UNKNOWN_DATA;
        }
        if (isPrivilegedUser) {
            this.coordinatorComment = person.getCoordinatorComment();
        } else {
            this.coordinatorComment = "";
        }
        this.visibleComment = person.getVisibleComment();
    }
}


