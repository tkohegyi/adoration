package org.rockhill.adorApp.web.json;

import org.rockhill.adorApp.database.tables.Link;

import java.util.List;

public class InformationJson {
    public String error; // filled only in case of error
    public String name;
    public String status;
    public List<Link> linkList;
    public List<PersonJson> leadership;
    public List<Link> currentHourList;
    public List<Link> futureHourList;
    public List<PersonJson> relatedPersonList;
}
