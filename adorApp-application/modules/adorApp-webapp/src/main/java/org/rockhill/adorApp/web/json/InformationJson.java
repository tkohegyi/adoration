package org.rockhill.adorApp.web.json;

import org.rockhill.adorApp.database.tables.Link;

import java.util.List;
import java.util.Map;

public class InformationJson {
    public String error; // filled only in case of error
    public String name; //name of the adorator
    public String status;   //status of the adorator
    public String id;   //id of the adorator
    public List<Link> linkList; //commit hours of the adorator
    public List<CoordinatorJson> leadership; //main coordinators
    public List<Link> currentHourList; //adorators in actual hour
    public List<Link> futureHourList; //adorators in next hour
    public List<PersonJson> relatedPersonList; //info about ppl
    public Map<Integer,String> dayNames; //dayId - text pairs
}
