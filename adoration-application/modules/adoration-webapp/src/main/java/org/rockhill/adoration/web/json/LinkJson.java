package org.rockhill.adoration.web.json;

import org.rockhill.adoration.database.tables.Link;
import org.rockhill.adoration.helper.JsonField;

import java.util.List;
import java.util.Map;

/**
 * Json structure to hold all information that is necessary for presenting an hour-adorator link.
 */
public class LinkJson {
    @JsonField
    public List<Link> linkList; //committed hours
    @JsonField
    public List<PersonJson> relatedPersonList; //info about ppl
    @JsonField
    public Map<Integer, String> dayNames; //dayId - text pairs
}
