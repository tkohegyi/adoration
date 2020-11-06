package org.rockhill.adoration.web.controller;

import org.rockhill.adoration.web.controller.helper.ControllerBase;

public class MockControllerBase extends ControllerBase {
        public String mockGetJsonString(String id, Object object) {
            return getJsonString(id, object);
        }

}
