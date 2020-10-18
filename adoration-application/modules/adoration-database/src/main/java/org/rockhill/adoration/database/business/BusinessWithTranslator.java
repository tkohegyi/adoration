package org.rockhill.adoration.database.business;

import com.sun.istack.NotNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.rockhill.adoration.database.SessionFactoryHelper;
import org.rockhill.adoration.database.tables.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BusinessWithTranslator {

    private final Logger logger = LoggerFactory.getLogger(BusinessWithTranslator.class);


    /**
     * Gets List of all translated words based on a specified language code.
     *
     * @return with the list.
     */
    public List<Translator> getTranslatorList(@NotNull String languageCode) {
        List<Translator> result = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Translator as T where T.languageCode like :languageCode";
            Query query = session.createQuery(hql);
            query.setParameter("languageCode", languageCode);
            result = (List<Translator>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        return result;
    }

    /**
     * Get translated text value based on language code an text id.
     *
     * @param languageCode is the specific language code
     * @param textId       is the text id to be translated
     * @param defaultValue is the default return value in case the specific languageCode + textId cannot be found
     * @return
     */
    public String getTranslatorValue(@NotNull String languageCode, @NotNull String textId, @NotNull String defaultValue) {
        String result = defaultValue;
        List<Translator> qResult = null;
        SessionFactory sessionFactory = SessionFactoryHelper.getSessionFactory();
        if (sessionFactory != null) {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            String hql = "from Translator as T where T.languageCode like :languageCode and T.textId like :textId";
            Query query = session.createQuery(hql);
            query.setParameter("languageCode", languageCode);
            query.setParameter("textId", textId);
            qResult = (List<Translator>) query.list();
            session.getTransaction().commit();
            session.close();
        }
        if (qResult != null && qResult.size() > 0) {
            result = qResult.get(0).getText();
        } else {
            logger.warn("Unable to find translator text for languageCode/textId pair: " + languageCode + "/" + textId);
        }
        return result;
    }
}
