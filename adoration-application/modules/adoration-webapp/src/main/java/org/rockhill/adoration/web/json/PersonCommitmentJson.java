package org.rockhill.adoration.web.json;

import org.rockhill.adoration.database.tables.Link;

import java.util.ArrayList;

public class PersonCommitmentJson {
    public ArrayList<Link> linkedHours;
    public ArrayList<Link> others;
    public ArrayList<String> dayNames;

    public PersonCommitmentJson() {
        linkedHours = new ArrayList<>();
        others = new ArrayList<>();
        dayNames = new ArrayList<>();
    }

}
