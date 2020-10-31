package org.rockhill.adoration.web.json;

import org.rockhill.adoration.helper.JsonField;

/**
 * Json structure that is used when a message has been sent to the administrator.
 */
public class MessageToCoordinatorJson {
    @JsonField
    public String info;
    @JsonField
    public String text;
    @JsonField
    public String captcha;
}

