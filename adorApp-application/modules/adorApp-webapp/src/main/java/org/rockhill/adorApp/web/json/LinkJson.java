package org.rockhill.adorApp.web.json;

import org.rockhill.adorApp.database.tables.Link;

import java.util.List;
import java.util.Map;

public class LinkJson {
    public List<Link> linkList; //committed hours
    public List<PersonJson> relatedPersonList; //info about ppl
    public Map<Integer,String> dayNames; //dayId - text pairs
}
