package org.rockhill.adoration.web.json;

import java.util.Map;
import java.util.Set;

public class CoverageInformationJson {
    public Map<String,String> dayNames; //textId - text pairs
    public Map<Integer,Integer> visibleHours; //hourId - number of adorators, only prio 1,2
    public Map<Integer,Set<Long>> allHours; //hourId - id of adorators, regardless of priority
    public Map<Integer,Set<Long>> onlineHours; //hourId - id of online adorators
    public Map<Long,PersonJson> adorators; //personId - adorator info
}
