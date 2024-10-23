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

/***
 * Let op, veel logging is rood, wat lijkt op veel errors, maar dit is dus logging.
 ***/
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        boolean testReizigerDAOTest = false;
        boolean testAdresDAOTest = false;
        boolean testOVChipkaartDAOTest = false;
        boolean testProductDAOTest = false;

        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

        ReizigerDAO reizigerDAO = new ReizigerDAOHibernate(sessionFactory);
        AdresDAO adresDAO = new AdresDAOHibernate(sessionFactory);
        OVChipkaartDAO ovChipkaartDAO = new OVChipkaartDAOHibernate(sessionFactory);
        ProductDAO productDAO = new ProductDAOHibernate(sessionFactory);

        try {
            try {
                testReizigerDAO(reizigerDAO);
                testReizigerDAOTest = true;
            } catch (SQLException e) {
                logger.error("testReizigerDAOTest: Fout tijdens het testen van ReizigerDAO.", e);
            }

            try {
                testAdresDAO(adresDAO, reizigerDAO);
                testAdresDAOTest = true;
            } catch (SQLException e) {
                logger.error("testAdresDAOTest: Fout tijdens het testen van AdresDAO.", e);
            }

            try {
                testOVChipkaartDAO(ovChipkaartDAO, reizigerDAO, productDAO);
                testOVChipkaartDAOTest = true;
            } catch (SQLException e) {
                logger.error("testOVChipkaartDAOTest: Fout tijdens het testen van OVChipkaartDAO.", e);
            }

            try {
                testProductDAO(productDAO, ovChipkaartDAO, reizigerDAO);
                testProductDAOTest = true;
            } catch (SQLException e) {
                logger.error("testProductDAOTest: Fout tijdens het testen van ProductDAO.", e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            sessionFactory.close();
        }

        printTestResults(testReizigerDAOTest, testAdresDAOTest, testOVChipkaartDAOTest, testProductDAOTest);
    }

    private static void printTestResults(boolean... tests) {
        String[] testNames = {
                "testReizigerDAOTest",
                "testAdresDAOTest",
                "testOVChipkaartDAOTest",
                "testProductDAOTest"
        };

        int successfulTests = 0;
        for (int i = 0; i < tests.length; i++) {
            if (tests[i]) {
                System.out.println(testNames[i] + " -- succeeded");
                successfulTests++;
            } else {
                System.out.println(testNames[i] + " -- failed");
            }
        }

        System.out.println(successfulTests + " tests out of " + tests.length + " ran successfully");
    }

    /**
     * Tests voor ReizigerDAOHibernate klasse.
     * Maakt een nieuwe reiziger aan, slaat deze op, haalt de reiziger op, update de reiziger (achternaam wordt van Dijk),
     * haalt alle reizigers op, en verwijdert de reiziger.
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
     * Tests voor AdresDAOHibernate klasse.
     * Maakt een nieuwe reiziger en adres aan, koppelt ze aan elkaar, slaat de reiziger op, haalt het adres op, update
     * het adres (woonplaats wordt Utrecht), haalt alle adressen op, en verwijdert het adres en reiziger.
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
     * Tests voor OVChipkaartDAOHibernate klasse.
     * Maakt een nieuw reiziger, OVChipkaart, en product aan, koppelt het product aan de OVChipkaart, updatet de OVChipkaart
     * (saldo wordt 75.0), haalt alle OVChipkaarten op, en verwijdert de OVChipkaart, product, en teiziger.
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
     * Tests voor ProductDAOHibernate klasse.
     * Maakt een reiziger, OVChipkaart en product aan, koppelt de OVChipkaart aan het product, slaat het product op,
     * haalt het product op, update het product (prijs wordt 35.0), haalt alle producten op, en verwijdert het produc.
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