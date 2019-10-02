package org.rockhill.adorApp.web.json;

import com.google.api.client.util.ArrayMap;

import java.util.Map;

public class LoginUrlInformationJson {
    public Map<String,String> loginUrls; //field ID - url text pairs

    public LoginUrlInformationJson() {
        loginUrls = new ArrayMap<>();
    }

}
