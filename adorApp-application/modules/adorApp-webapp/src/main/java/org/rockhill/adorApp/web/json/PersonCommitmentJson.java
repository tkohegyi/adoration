package org.rockhill.adorApp.web.json;

import org.rockhill.adorApp.database.tables.Link;

import java.util.ArrayList;

public class PersonCommitmentJson {
    public ArrayList<Link> linkedHours;
    public ArrayList<Link> others;

    public PersonCommitmentJson() {
        linkedHours = new ArrayList<>();
        others = new ArrayList<>();
    }

}
