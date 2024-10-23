package nl.hu.dp.ovchip.HIBERNATE.service;

import lombok.RequiredArgsConstructor;
import nl.hu.dp.ovchip.HIBERNATE.data.ReizigerDAO;
import nl.hu.dp.ovchip.HIBERNATE.domain.OVChipkaart;
import nl.hu.dp.ovchip.HIBERNATE.domain.Product;
import nl.hu.dp.ovchip.HIBERNATE.domain.Reiziger;
import nl.hu.dp.ovchip.HIBERNATE.util.id;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.List;

@RequiredArgsConstructor
public class ReizigerDAOHibernate implements ReizigerDAO {
    private final SessionFactory sessionFactory;
    private static final Logger logger = LoggerFactory.getLogger(ReizigerDAOHibernate.class);

    @Override
    public boolean save(Reiziger reiziger) {
        Transaction transaction = null;
        Session session = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            if (reiziger.getId() == null) {
                Long nextId = id.getNextId(session, "Reiziger", "id");
                reiziger.setId(nextId);
            }

            if (reiziger.getAdres() != null && reiziger.getAdres().getId() == null) {
                Long nextId = id.getNextId(session, "Adres", "id");
                reiziger.getAdres().setId(nextId);
            }

            if (reiziger.getOvChipkaarten() != null) {
                for (OVChipkaart ovChipkaart : reiziger.getOvChipkaarten()) {
                    if (ovChipkaart.getKaart_nummer() == null) {
                        Long nextId = id.getNextId(session, "OVChipkaart", "kaart_nummer");
                        ovChipkaart.setKaart_nummer(nextId);
                    }

                    if (ovChipkaart.getProducten() != null) {
                        for (Product product : ovChipkaart.getProducten()) {
                            if (product.getProductNummer() == null) {
                                Long nextId = id.getNextId(session, "Product", "productNummer");
                                product.setProductNummer(nextId);
                            }
                        }
                    }
                }
            }

            session.save(reiziger);
            transaction.commit();
            logger.debug("Saved reiziger: " + reiziger);
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("Error saving reiziger", e);
            return false;
        } finally {
            if (session != null) session.close();
        }
    }

    @Override
    public boolean update(Reiziger reiziger) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(reiziger);
            transaction.commit();
            logger.info("[log] Reiziger updated: {}", reiziger);
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("[error] Error updating Reiziger: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Reiziger reiziger) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(reiziger);
            transaction.commit();
            logger.info("[log] Reiziger deleted: {}", reiziger);
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("[error] Error deleting Reiziger: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Reiziger findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            Reiziger reiziger = session.get(Reiziger.class, id);
            logger.info("[log] Reiziger found by ID {}: {}", id, reiziger);
            return reiziger;
        } catch (Exception e) {
            logger.error("[error] Error finding Reiziger by ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    @Override
    public List<Reiziger> findByGeboortedatum(Date geboortedatum) {
        try (Session session = sessionFactory.openSession()) {
            List<Reiziger> reizigers = session.createQuery("from Reiziger where geboortedatum = :geboortedatum", Reiziger.class)
                    .setParameter("geboortedatum", geboortedatum)
                    .list();
            logger.info("[log] Reizigers found by geboortedatum {}: {}", geboortedatum, reizigers);
            return reizigers;
        } catch (Exception e) {
            logger.error("[error] Error finding Reizigers by geboortedatum {}: {}", geboortedatum, e.getMessage());
            return null;
        }
    }

    @Override
    public List<Reiziger> findAll() {
        try (Session session = sessionFactory.openSession()) {
            List<Reiziger> reizigers = session.createQuery("from Reiziger", Reiziger.class).list();
            logger.info("[log] All Reizigers retrieved");
            return reizigers;
        } catch (Exception e) {
            logger.error("[error] Error retrieving all Reizigers: {}", e.getMessage());
            return null;
        }
    }
}
