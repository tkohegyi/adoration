package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.BusinessWithLink;
import org.rockhill.adorApp.database.business.BusinessWithTranslator;
import org.rockhill.adorApp.database.business.helper.enums.TranslatorDayNames;
import org.rockhill.adorApp.database.tables.Link;
import org.rockhill.adorApp.database.tables.Translator;
import org.rockhill.adorApp.web.json.CoverageInformationJson;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class CoverageProvider {

    private final Logger logger = LoggerFactory.getLogger(CoverageProvider.class);


    @Autowired
    BusinessWithTranslator businessWithTranslator;
    @Autowired
    BusinessWithLink businessWithLink;

    public CoverageInformationJson getCoverageInfo(CurrentUserInformationJson currentUserInformationJson) {
        CoverageInformationJson coverageInformationJson = new CoverageInformationJson();

        //fill the day names first
        List<Translator> translatorList = businessWithTranslator.getTranslatorList(currentUserInformationJson.languageCode);
        coverageInformationJson.dayNames = new HashMap<>();
        for (TranslatorDayNames dayName : TranslatorDayNames.values()) {
            String textId = dayName.toString();
            String value = businessWithTranslator.getTranslatorValue(currentUserInformationJson.languageCode, textId, textId);
            coverageInformationJson.dayNames.put(textId.toLowerCase(), value);
        }

        //fill the hour coverage information
        List<Link> linkList = businessWithLink.getLinkList();
        coverageInformationJson.hours = new HashMap<>();
        //ensure that we have initial info about all the hours
        for (int i = 0; i < 168; i++) {
            coverageInformationJson.hours.put(Integer.valueOf(i), 0);
        }
        for (Link link : linkList) {
            Integer hourId = link.getHourId();
            if (coverageInformationJson.hours.containsKey(hourId)) {
                //we already have this in the map
                coverageInformationJson.hours.put(hourId, coverageInformationJson.hours.get(hourId) + 1);
            } else {
                //we don't have this in our map, data error !
                logger.warn("Unexpected row in Link table, with Id:" + link.getId());
            }
        }

        return coverageInformationJson;
    }

}
