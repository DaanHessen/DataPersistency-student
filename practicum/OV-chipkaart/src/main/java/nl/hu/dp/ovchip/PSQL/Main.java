package nl.hu.dp.ovchip.PSQL;

import nl.hu.dp.ovchip.PSQL.data.AdresDAO;
import nl.hu.dp.ovchip.PSQL.data.OVChipkaartDAO;
import nl.hu.dp.ovchip.PSQL.data.ProductDAO;
import nl.hu.dp.ovchip.PSQL.data.ReizigerDAO;
import nl.hu.dp.ovchip.PSQL.domain.Adres;
import nl.hu.dp.ovchip.PSQL.domain.OVChipkaart;
import nl.hu.dp.ovchip.PSQL.domain.Product;
import nl.hu.dp.ovchip.PSQL.domain.Reiziger;
import nl.hu.dp.ovchip.PSQL.service.AdresDAOPsql;
import nl.hu.dp.ovchip.PSQL.service.OVChipkaartDAOPsql;
import nl.hu.dp.ovchip.PSQL.service.ProductDAOPsql;
import nl.hu.dp.ovchip.PSQL.service.ReizigerDAOPsql;
import nl.hu.dp.ovchip.PSQL.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/***
 * De code werkt nu een stuk beter dan hiervoor. Ook heb ik de code netter gemaakt eb gewerkt aan consistentie.
 * Ook heb ik sommige (herbruikbare) code in aparte helper methodes gezet.
 *
 * De main klasse test de werking van de DAOPsql klassen.
 ***/

public class Main {

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            AdresDAO adresDAO = new AdresDAOPsql(connection);
            ReizigerDAO reizigerDAO = new ReizigerDAOPsql(connection, adresDAO);
            OVChipkaartDAO ovChipkaartDAO = new OVChipkaartDAOPsql(connection, reizigerDAO);
            ProductDAO productDAO = new ProductDAOPsql(connection, ovChipkaartDAO);

            Reiziger reiziger = null;
            Adres adres = null;
            OVChipkaart ovChipkaart = null;
            Product product = null;

            try {
                try {
                    reiziger = new Reiziger();
                    reiziger.setVoorletters("A");
                    reiziger.setTussenvoegsel("van");
                    reiziger.setAchternaam("Test");
                    reiziger.setGeboortedatum(Date.valueOf("2001-01-01"));
                    reizigerDAO.save(reiziger);
                    System.out.println("Reiziger saved: " + reiziger);
                } catch (Exception e) {
                    connection.rollback();
                    System.out.println("saveReizigerTest: Fout tijdens het opslaan van Reiziger, rollback uitgevoerd.");
                    e.printStackTrace();
                }

                if (reiziger != null) {
                    try {
                        adres = new Adres();
                        adres.setPostcode("4321AB");
                        adres.setHuisnummer("23");
                        adres.setStraat("Teststraat");
                        adres.setWoonplaats("Teststad");
                        adres.setReiziger(reiziger);
                        adresDAO.save(adres);
                        System.out.println("Adres saved: " + adres);
                    } catch (Exception e) {
                        connection.rollback();
                        System.out.println("saveAdresTest: Fout tijdens het opslaan van Adres, rollback uitgevoerd.");
                        e.printStackTrace();
                    }
                }

                if (reiziger != null) {
                    try {
                        ovChipkaart = new OVChipkaart();
                        ovChipkaart.setGeldigTot(Date.valueOf("2026-12-31"));
                        ovChipkaart.setKlasse(2);
                        ovChipkaart.setSaldo(50.0);
                        ovChipkaart.setReiziger(reiziger);
                        ovChipkaartDAO.save(ovChipkaart);
                        System.out.println("OVChipkaart saved: " + ovChipkaart);
                    } catch (Exception e) {
                        connection.rollback();
                        System.out.println("saveOVChipkaartTest: Fout tijdens het opslaan van OVChipkaart, rollback uitgevoerd.");
                        e.printStackTrace();
                    }
                }

                if (ovChipkaart != null) {
                    try {
                        product = new Product();
                        product.setNaam("NS Subscription");
                        product.setBeschrijving("Monthly Subscription for Public Transport");
                        product.setPrijs(99.99);
                        productDAO.save(product);
                        System.out.println("Product saved: " + product);

                        ovChipkaart.addProduct(product);
                        ovChipkaartDAO.update(ovChipkaart);
                        System.out.println("OVChipkaart updated with product: " + ovChipkaart);
                    } catch (Exception e) {
                        connection.rollback();
                        System.out.println("saveProductTest: Fout tijdens het opslaan van Product of het bijwerken van OVChipkaart, rollback uitgevoerd.");
                        e.printStackTrace();
                    }
                }

                if (reiziger != null) {
                    try {
                        Reiziger retrievedReiziger = reizigerDAO.findById(reiziger.getId());
                        System.out.println("Retrieved Reiziger: " + retrievedReiziger);

                        Adres retrievedAdres = adresDAO.findById(adres.getId());
                        System.out.println("Retrieved Adres: " + retrievedAdres);

                        OVChipkaart retrievedOvChipkaart = ovChipkaartDAO.findByKaartNummer(ovChipkaart.getKaart_nummer());
                        System.out.println("Retrieved OVChipkaart: " + retrievedOvChipkaart);

                        Product retrievedProduct = productDAO.findById(product.getProductNummer());
                        System.out.println("Retrieved Product: " + retrievedProduct);
                    } catch (Exception e) {
                        connection.rollback();
                        System.out.println("retrieveEntitiesTest: Fout tijdens het ophalen van gegevens, rollback uitgevoerd.");
                        e.printStackTrace();
                    }
                }

                if (reiziger != null) {
                    try {
                        List<OVChipkaart> ovChipkaarten = ovChipkaartDAO.findByReiziger(reiziger);

                        for (OVChipkaart oc : ovChipkaarten) {
                            ovChipkaartDAO.delete(oc);
                            System.out.println("OVChipkaart deleted: " + oc);
                        }

                        reizigerDAO.delete(reiziger);
                        System.out.println("Reiziger deleted: " + reiziger);
                    } catch (Exception e) {
                        connection.rollback();
                        System.out.println("deleteEntitiesTest: Fout tijdens het verwijderen van gegevens, rollback uitgevoerd.");
                        e.printStackTrace();
                    }
                }

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
