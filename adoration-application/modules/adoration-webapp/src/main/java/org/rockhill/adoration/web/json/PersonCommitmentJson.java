package org.rockhill.adoration.web.json;

import org.rockhill.adoration.database.tables.Link;
import org.rockhill.adoration.helper.JsonField;

import java.util.ArrayList;

public class PersonCommitmentJson {
    @JsonField
    public ArrayList<Link> linkedHours;
    @JsonField
    public ArrayList<Link> others;
    @JsonField
    public ArrayList<String> dayNames;

    public PersonCommitmentJson() {
        linkedHours = new ArrayList<>();
        others = new ArrayList<>();
        dayNames = new ArrayList<>();
    }

}
