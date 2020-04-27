package org.rockhill.adorApp.web.json;

import java.util.Map;

public class CoverageInformationJson {
    public Map<String,String> dayNames; //textId - text pairs
    public Map<Integer,Integer> hours; //hourId - number of adorators
    public Map<Integer,Integer> onlineHours; //hourId - number of online adorators
}
