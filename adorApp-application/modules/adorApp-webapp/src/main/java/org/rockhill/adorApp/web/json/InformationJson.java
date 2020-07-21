package org.rockhill.adorApp.web.json;

import org.rockhill.adorApp.database.tables.Link;

import java.util.List;
import java.util.Map;

public class InformationJson {
    public String error; // filled only in case of error
    public String name;
    public String status;
    public String id;
    public List<Link> linkList;
    public List<PersonJson> leadership;
    public List<Link> currentHourList;
    public List<Link> futureHourList;
    public List<PersonJson> relatedPersonList;
    public Map<Integer,String> dayNames; //dayId - text pairs
}
