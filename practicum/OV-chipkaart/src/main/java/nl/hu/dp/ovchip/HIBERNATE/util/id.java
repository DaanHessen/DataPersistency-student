package nl.hu.dp.ovchip.HIBERNATE.util;

import org.hibernate.Session;

public class id {

    public static Long getNextId(Session session, String entityName, String idFieldName) {
        String hql = "SELECT MAX(e." + idFieldName + ") FROM " + entityName + " e";
        Long maxId = (Long) session.createQuery(hql).uniqueResult();
        return (maxId == null) ? 1L : maxId + 1;
    }
}

