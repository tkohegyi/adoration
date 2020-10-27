package org.rockhill.adoration.web.json;

import org.rockhill.adoration.database.business.helper.enums.TranslatorDayNames;
import org.rockhill.adoration.helper.JsonField;

public class RegisterAdoratorJson {
    @JsonField
    public String name;
    @JsonField
    public String comment;
    @JsonField
    public Integer dayId;
    @JsonField
    public Integer hourId;
    @JsonField
    public String email;
    @JsonField
    public String coordinate;
    @JsonField
    public Integer method;
    @JsonField
    public String dhc;
    @JsonField
    public String dhcSignedDate;
    @JsonField
    public String mobile;
    @JsonField
    public Long personId;
    @JsonField
    public Long socialId;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Név/Name: ").append(name).append(",\n");
        sb.append("Megjegyzés/Comment: " + comment + ",\n");
        sb.append("Nap/Day: " + TranslatorDayNames.getTranslatedString(dayId) + ",\n");
        sb.append("Óra/Hour: " + hourId + ",\n");
        sb.append("Email: " + email + ",\n");
        sb.append("Segítség szervezésben: " + coordinate + ",\n");
        sb.append("Adorálás módja: " + method + " (1: kápolnában hetente; 2: online hetente; 3: ad-hoc),\n");
        sb.append("Adatkezelési hozzájárulás: " + dhc + ",\n");
        sb.append("Adatkezelési hozzájárulás dátuma: " + dhcSignedDate + ",\n");
        sb.append("Telefonszám: " + mobile + ",\n");
        if (personId == null) {
            sb.append("A személy nincs beazonosítva.\n");
        } else {
            sb.append("A személy azonosítója: " + personId + ".\n");
        }
        if (socialId == null) {
            sb.append("A személy nincs belépve.\n");
        } else {
            sb.append("A személy belépett, Social azonosítója: " + socialId + ".\n");
        }
        return sb.toString();
    }
}
