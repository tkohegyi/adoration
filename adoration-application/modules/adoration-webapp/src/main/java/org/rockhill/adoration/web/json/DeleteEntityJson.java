package org.rockhill.adoration.web.json;

import org.rockhill.adoration.helper.JsonField;

/**
 * Json structure to be used to delete an entity.
 * Since every database record has its own unique id, specifying the id is enough.
 */
public class DeleteEntityJson {
    @JsonField
    public String entityId;
}
