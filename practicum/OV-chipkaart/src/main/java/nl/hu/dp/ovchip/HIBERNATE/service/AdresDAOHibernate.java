package nl.hu.dp.ovchip.HIBERNATE.service;

import nl.hu.dp.ovchip.HIBERNATE.util.id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import nl.hu.dp.ovchip.HIBERNATE.data.AdresDAO;
import nl.hu.dp.ovchip.HIBERNATE.domain.Adres;
import nl.hu.dp.ovchip.HIBERNATE.domain.Reiziger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

@RequiredArgsConstructor
public class AdresDAOHibernate implements AdresDAO {
    private final SessionFactory sessionFactory;
    private static final Logger logger = LoggerFactory.getLogger(AdresDAOHibernate.class);

    @Override
    public boolean save(Adres adres) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            if (adres.getId() == null) {
                Long nextId = id.getNextId(session, "Adres", "id");
                adres.setId(nextId);
            }

            session.save(adres);
            transaction.commit();
            logger.debug("Saved adres: " + adres);
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error saving adres", e);
            return false;
        }
    }

    @Override
    public boolean update(Adres adres) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(adres);
            transaction.commit();
            logger.debug("Updated adres: " + adres);
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error updating adres", e);
            return false;
        }
    }

    @Override
    public boolean delete(Adres adres) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(adres);
            transaction.commit();
            logger.debug("Deleted adres: " + adres);
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error deleting adres", e);
            return false;
        }
    }

    @Override
    public Adres findById(long adres_id) {
        try (Session session = sessionFactory.openSession()) {
            Adres adres = session.get(Adres.class, adres_id);
            logger.debug("Found adres by id: " + adres);
            return adres;
        } catch (Exception e) {
            logger.error("Error finding adres by id", e);
            return null;
        }
    }

    @Override
    public Adres findByReiziger(Reiziger reiziger) {
        try (Session session = sessionFactory.openSession()) {
            Adres adres = session.createQuery("from Adres where reiziger_id = :reiziger_id", Adres.class)
                    .setParameter("reiziger_id", reiziger.getId())
                    .uniqueResult();
            logger.debug("Found adres by reiziger: " + adres);
            return adres;
        } catch (Exception e) {
            logger.error("Error finding adres by reiziger", e);
            return null;
        }
    }

    @Override
    public List<Adres> findAll() {
        try (Session session = sessionFactory.openSession()) {
            List<Adres> adressen = session.createQuery("from Adres", Adres.class).list();
            logger.debug("Found all adressen: " + adressen);
            return adressen;
        } catch (Exception e) {
            logger.error("Error finding all adressen", e);
            return null;
        }
    }
}