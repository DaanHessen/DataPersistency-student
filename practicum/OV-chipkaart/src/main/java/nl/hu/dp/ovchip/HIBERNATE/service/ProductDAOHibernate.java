package nl.hu.dp.ovchip.HIBERNATE.service;

import nl.hu.dp.ovchip.HIBERNATE.data.ProductDAO;
import nl.hu.dp.ovchip.HIBERNATE.domain.OVChipkaart;
import nl.hu.dp.ovchip.HIBERNATE.domain.Product;
import nl.hu.dp.ovchip.HIBERNATE.util.id;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProductDAOHibernate implements ProductDAO {
    private static final Logger logger = LoggerFactory.getLogger(ProductDAOHibernate.class);
    private final SessionFactory sessionFactory;

    public ProductDAOHibernate(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public boolean save(Product product) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            if (product.getProductNummer() == null) {
                Long nextId = id.getNextId(session, "Product", "productNummer");
                product.setProductNummer(nextId);
            }

            session.save(product);
            transaction.commit();
            logger.info("[log] Product saved: {}", product);
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("[error] Error saving Product: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Product product) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(product);
            transaction.commit();
            logger.info("[log] Product updated: {}", product);
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("[error] Error updating Product: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Product product) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(product);
            transaction.commit();
            logger.info("[log] Product deleted: {}", product);
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("[error] Error deleting Product: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Product findById(Long productNummer) {
        try (Session session = sessionFactory.openSession()) {
            Product product = session.get(Product.class, productNummer);
            logger.info("[log] Product found by ID {}: {}", productNummer, product);
            return product;
        } catch (Exception e) {
            logger.error("[error] Error finding Product by ID {}: {}", productNummer, e.getMessage());
            return null;
        }
    }

    @Override
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart) {
        try (Session session = sessionFactory.openSession()) {
            List<Product> producten = session.createQuery(
                            "select distinct p from Product p join p.ovChipkaarten ovc where ovc = :ovc", Product.class)
                    .setParameter("ovc", ovChipkaart)
                    .list();
            logger.info("[log] Products found by OVChipkaart {}: {}", ovChipkaart.getKaart_nummer(), producten);
            return producten;
        } catch (Exception e) {
            logger.error("[error] Error finding Products by OVChipkaart {}: {}", ovChipkaart.getKaart_nummer(), e.getMessage());
            return null;
        }
    }

    @Override
    public List<Product> findAll() {
        try (Session session = sessionFactory.openSession()) {
            List<Product> producten = session.createQuery("from Product", Product.class).list();
            logger.info("[log] All Products retrieved");
            return producten;
        } catch (Exception e) {
            logger.error("[error] Error retrieving all Products: {}", e.getMessage());
            return null;
        }
    }
}
