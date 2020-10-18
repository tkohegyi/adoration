package org.rockhill.adoration.web.json;

import org.rockhill.adoration.database.business.helper.enums.TranslatorDayNames;

public class RegisterAdoratorJson {
    public String name;
    public String comment;
    public Integer dayId;
    public Integer hourId;
    public String email;
    public String coordinate;
    public Integer method;
    public String dhc;
    public String dhcSignedDate;
    public String mobile;
    public Long personId;
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
