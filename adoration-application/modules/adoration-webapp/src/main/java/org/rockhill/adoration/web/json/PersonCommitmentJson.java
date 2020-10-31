package org.rockhill.adoration.web.json;

import org.rockhill.adoration.database.tables.Link;
import org.rockhill.adoration.helper.JsonField;

import java.util.ArrayList;
import java.util.List;

/**
 * Json structure to be used to contain information about committed hours of a specific person.
 */
public class PersonCommitmentJson {
    @JsonField
    public List<Link> linkedHours;
    @JsonField
    public List<Link> others;
    @JsonField
    public List<String> dayNames;

    /**
     * Constructor of the structure with empty arrays.
     */
    public PersonCommitmentJson() {
        linkedHours = new ArrayList<>();
        others = new ArrayList<>();
        dayNames = new ArrayList<>();
    }

}
