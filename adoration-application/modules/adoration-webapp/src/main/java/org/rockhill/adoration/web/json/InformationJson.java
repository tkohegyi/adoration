package org.rockhill.adoration.web.json;

import org.rockhill.adoration.database.tables.Link;
import org.rockhill.adoration.helper.JsonField;

import java.util.List;
import java.util.Map;

/**
 * Json structure that is used to provide information for a registered adorator.
 */
public class InformationJson {
    @JsonField
    public String error; // filled only in case of error
    @JsonField
    public String name; //name of the adorator
    @JsonField
    public String status;   //status of the adorator
    @JsonField
    public String id;   //id of the adorator
    @JsonField
    public List<Link> linkList; //committed hours of the adorator
    @JsonField
    public List<CoordinatorJson> leadership; //main coordinators
    @JsonField
    public List<Link> currentHourList; //adorators in actual hour
    @JsonField
    public List<Link> futureHourList; //adorators in next hour
    @JsonField
    public List<PersonJson> relatedPersonList; //info about ppl
    @JsonField
    public Map<Integer, String> dayNames; //dayId - text pairs
    @JsonField
    public Integer hourInDayNow;
    @JsonField
    public Integer hourInDayNext;
}
