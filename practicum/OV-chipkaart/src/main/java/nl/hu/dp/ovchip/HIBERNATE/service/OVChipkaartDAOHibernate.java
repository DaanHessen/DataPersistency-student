package nl.hu.dp.ovchip.HIBERNATE.service;

import nl.hu.dp.ovchip.HIBERNATE.data.OVChipkaartDAO;
import nl.hu.dp.ovchip.HIBERNATE.domain.OVChipkaart;
import nl.hu.dp.ovchip.HIBERNATE.domain.Product;
import nl.hu.dp.ovchip.HIBERNATE.domain.Reiziger;
import nl.hu.dp.ovchip.HIBERNATE.util.id;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OVChipkaartDAOHibernate implements OVChipkaartDAO {
    private static final Logger logger = LoggerFactory.getLogger(OVChipkaartDAOHibernate.class);
    private final SessionFactory sessionFactory;

    public OVChipkaartDAOHibernate(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public boolean save(OVChipkaart ovChipkaart) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            if (ovChipkaart.getKaart_nummer() == null) {
                Long nextId = id.getNextId(session, "OVChipkaart", "kaart_nummer"); // Correct field name is "kaart_nummer"
                ovChipkaart.setKaart_nummer(nextId);
            }

            session.save(ovChipkaart);
            transaction.commit();
            logger.info("[log] OVChipkaart saved: {}", ovChipkaart);
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("[error] Error saving OVChipkaart: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(OVChipkaart ovChipkaart) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(ovChipkaart);
            transaction.commit();
            logger.info("[log] OVChipkaart updated: {}", ovChipkaart);
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("[error] Error updating OVChipkaart: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(OVChipkaart ovChipkaart) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(ovChipkaart);
            transaction.commit();
            logger.info("[log] OVChipkaart deleted: {}", ovChipkaart);
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("[error] Error deleting OVChipkaart: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public OVChipkaart findByKaartNummer(long kaartnummer) {
        try (Session session = sessionFactory.openSession()) {
            OVChipkaart ovChipkaart = session.get(OVChipkaart.class, kaartnummer);
            logger.info("[log] OVChipkaart found by kaartnummer {}: {}", kaartnummer, ovChipkaart);
            return ovChipkaart;
        } catch (Exception e) {
            logger.error("[error] Error finding OVChipkaart by kaartnummer {}: {}", kaartnummer, e.getMessage());
            return null;
        }
    }

    @Override
    public List<OVChipkaart> findAll() {
        try (Session session = sessionFactory.openSession()) {
            List<OVChipkaart> ovChipkaarten = session.createQuery("from OVChipkaart", OVChipkaart.class).list();
            logger.info("[log] All OVChipkaarten retrieved");
            return ovChipkaarten;
        } catch (Exception e) {
            logger.error("[error] Error retrieving all OVChipkaarten: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) {
        try (Session session = sessionFactory.openSession()) {
            List<OVChipkaart> ovChipkaarten = session.createQuery("from OVChipkaart where reiziger = :reiziger", OVChipkaart.class)
                    .setParameter("reiziger", reiziger)
                    .list();
            logger.info("[log] OVChipkaarten found by Reiziger {}: {}", reiziger.getId(), ovChipkaarten);
            return ovChipkaarten;
        } catch (Exception e) {
            logger.error("[error] Error finding OVChipkaarten by Reiziger {}: {}", reiziger.getId(), e.getMessage());
            return null;
        }
    }

    @Override
    public List<OVChipkaart> findByProduct(Product product) {
        try (Session session = sessionFactory.openSession()) {
            List<OVChipkaart> ovChipkaarten = session.createQuery(
                            "select distinct ovc from OVChipkaart ovc join ovc.producten p where p = :product", OVChipkaart.class)
                    .setParameter("product", product)
                    .list();
            logger.info("[log] OVChipkaarten found by Product {}: {}", product.getProductNummer(), ovChipkaarten);
            return ovChipkaarten;
        } catch (Exception e) {
            logger.error("[error] Error finding OVChipkaarten by Product {}: {}", product.getProductNummer(), e.getMessage());
            return null;
        }
    }
}
