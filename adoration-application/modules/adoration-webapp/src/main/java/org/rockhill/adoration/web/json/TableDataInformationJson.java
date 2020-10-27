package org.rockhill.adoration.web.json;

import org.rockhill.adoration.helper.JsonField;

public class TableDataInformationJson {
    @JsonField
    public Object data;

    public TableDataInformationJson(Object data) {
        this.data = data;
    }
}
