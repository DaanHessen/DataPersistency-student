package nl.hu.dp.ovchip.HIBERNATE;

import nl.hu.dp.ovchip.HIBERNATE.data.*;
import nl.hu.dp.ovchip.HIBERNATE.domain.*;
import nl.hu.dp.ovchip.HIBERNATE.service.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

        ReizigerDAO reizigerDAO = new ReizigerDAOHibernate(sessionFactory);
        AdresDAO adresDAO = new AdresDAOHibernate(sessionFactory);
        OVChipkaartDAO ovChipkaartDAO = new OVChipkaartDAOHibernate(sessionFactory);
        ProductDAO productDAO = new ProductDAOHibernate(sessionFactory);

        try {
            testReizigerDAO(reizigerDAO);
            testAdresDAO(adresDAO, reizigerDAO);
            testOVChipkaartDAO(ovChipkaartDAO, reizigerDAO, productDAO);
            testProductDAO(productDAO, ovChipkaartDAO, reizigerDAO);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            sessionFactory.close();
        }
    }

    /**
     * Tests the ReizigerDAOHibernate class.
     * It creates a new reiziger, retrieves it, updates it's last name, retrieves all reizigers, and finally deletes
     * te new reiziger.
     */
    private static void testReizigerDAO(ReizigerDAO reizigerDAO) throws SQLException {
        logger.info("---- Testing ReizigerDAO ----");

        Reiziger reiziger = new Reiziger();
        reiziger.setVoorletters("J");
        reiziger.setTussenvoegsel("van");
        reiziger.setAchternaam("Dijk");
        reiziger.setGeboortedatum(Date.valueOf("1990-01-01"));
        reizigerDAO.save(reiziger);

        Reiziger retrievedReiziger = reizigerDAO.findById(reiziger.getId());
        logger.info("Retrieved Reiziger: {}", retrievedReiziger);

        reiziger.setAchternaam("van Dijk");
        reizigerDAO.update(reiziger);

        List<Reiziger> reizigers = reizigerDAO.findAll();
        logger.info("All Reizigers: {}", reizigers);

        reizigerDAO.delete(reiziger);
    }

    /**
     * Tests the AdresDAOHibernate class.
     * It creates a new reiziger and adres, associates the adres with the reiziger, saves the reiziger, retrieves the
     * adres, updates the adres (set woonplaats to Utrecht), retrieves all adressen, and finally deletes the
     * adres and reiziger.
     */
    private static void testAdresDAO(AdresDAO adresDAO, ReizigerDAO reizigerDAO) throws SQLException {
        logger.info("---- Testing AdresDAO ----");

        Reiziger reiziger = new Reiziger();
        reiziger.setVoorletters("A");
        reiziger.setAchternaam("Jansen");
        reiziger.setGeboortedatum(Date.valueOf("1985-05-15"));

        Adres adres = new Adres();
        adres.setPostcode("1234AB");
        adres.setHuisnummer("10");
        adres.setStraat("Hoofdstraat");
        adres.setWoonplaats("Amsterdam");
        adres.setReiziger(reiziger);

        reiziger.setAdres(adres);
        reizigerDAO.save(reiziger);

        Adres retrievedAdres = adresDAO.findById(adres.getId());
        logger.info("Retrieved Adres: {}", retrievedAdres);

        adres.setWoonplaats("Utrecht");
        adresDAO.update(adres);

        List<Adres> adressen = adresDAO.findAll();
        logger.info("All Adressen: {}", adressen);

        adresDAO.delete(adres);
        reizigerDAO.delete(reiziger);
    }

    /**
     * Tests the OVChipkaartDAOHibernate class.
     * It creates a new reiziger, ov chipkaart and product, associates the ov chipkaart with the reiziger and product,
     * saves the ov chipkaart, retrieves the v chipkaart, updates the ov chipkaart (sets saldo to 75.0), retrieves all
     * ov chipkaarten, and finally deletes the ov chipkaart, product, and reizier.
     */
    private static void testOVChipkaartDAO(OVChipkaartDAO ovChipkaartDAO, ReizigerDAO reizigerDAO, ProductDAO productDAO) throws SQLException {
        logger.info("---- Testing OVChipkaartDAO ----");

        Reiziger reiziger = new Reiziger();
        reiziger.setVoorletters("B");
        reiziger.setAchternaam("de Vries");
        reiziger.setGeboortedatum(Date.valueOf("1975-07-20"));
        reizigerDAO.save(reiziger);

        OVChipkaart ovChipkaart = new OVChipkaart();
        ovChipkaart.setGeldigTot(Date.valueOf("2025-12-31"));
        ovChipkaart.setKlasse(2);
        ovChipkaart.setSaldo(50.0);
        ovChipkaart.setReiziger(reiziger);

        Product product = new Product();
        product.setNaam("Dal Voordeel");
        product.setBeschrijving("Korting in de daluren");
        product.setPrijs(50.0);
        productDAO.save(product);

        ovChipkaart.addProduct(product);
        ovChipkaartDAO.save(ovChipkaart);

        OVChipkaart retrievedOVChipkaart = ovChipkaartDAO.findByKaartNummer(ovChipkaart.getKaart_nummer());
        logger.info("Retrieved OVChipkaart: {}", retrievedOVChipkaart);

        ovChipkaart.setSaldo(75.0);
        ovChipkaartDAO.update(ovChipkaart);

        List<OVChipkaart> ovChipkaarten = ovChipkaartDAO.findAll();
        logger.info("All OVChipkaarten: {}", ovChipkaarten);

        ovChipkaartDAO.delete(ovChipkaart);
        productDAO.delete(product);
        reizigerDAO.delete(reiziger);
    }

    /**
     * Tests the ProductDAOHibernate class.
     * It creates a new reiziger, ov chipkaart, and product, associates the product with the ov chipkaart, saves the
     * product, retrieves the product, updates the product (sets prijs to 35.0), retrieves all products, and finally
     * deletes the product, ov chipkaart, and reiziger.
     */
    private static void testProductDAO(ProductDAO productDAO, OVChipkaartDAO ovChipkaartDAO, ReizigerDAO reizigerDAO) throws SQLException {
        logger.info("---- Testing ProductDAO ----");

        Reiziger reiziger = new Reiziger();
        reiziger.setVoorletters("C");
        reiziger.setAchternaam("Janssen");
        reiziger.setGeboortedatum(Date.valueOf("1995-10-10"));
        reizigerDAO.save(reiziger);

        OVChipkaart ovChipkaart = new OVChipkaart();
        ovChipkaart.setGeldigTot(Date.valueOf("2024-06-30"));
        ovChipkaart.setKlasse(1);
        ovChipkaart.setSaldo(100.0);
        ovChipkaart.setReiziger(reiziger);
        ovChipkaartDAO.save(ovChipkaart);

        Product product = new Product();
        product.setNaam("Weekend Vrij");
        product.setBeschrijving("Vrij reizen in het weekend");
        product.setPrijs(30.0);

        product.addOVChipkaart(ovChipkaart);
        productDAO.save(product);

        Product retrievedProduct = productDAO.findById(product.getProductNummer());
        logger.info("Retrieved Product: {}", retrievedProduct);

        product.setPrijs(35.0);
        productDAO.update(product);

        List<Product> producten = productDAO.findAll();
        logger.info("All Products: {}", producten);

        productDAO.delete(product);
        ovChipkaartDAO.delete(ovChipkaart);
        reizigerDAO.delete(reiziger);
    }
}
