package org.rockhill.adoration.web.json;

import org.rockhill.adoration.helper.JsonField;

public class MessageToCoordinatorJson {
    @JsonField
    public String info;
    @JsonField
    public String text;
    @JsonField
    public String captcha;
}

