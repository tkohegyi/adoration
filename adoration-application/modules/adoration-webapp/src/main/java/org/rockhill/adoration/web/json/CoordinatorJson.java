package org.rockhill.adoration.web.json;

import org.rockhill.adoration.helper.JsonField;

/**
 * Json structure to hold Coordinator information.
 */
public class CoordinatorJson {
    @JsonField
    public String id;
    @JsonField
    public String coordinatorType;
    @JsonField
    public String personId;
    @JsonField
    public String personName;
    @JsonField
    public String phone;
    @JsonField
    public String eMail;
    @JsonField
    public String visibleComment;
    @JsonField
    public String coordinatorTypeText;
}

