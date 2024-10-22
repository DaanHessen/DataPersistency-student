package nl.hu.dp.ovchip.PSQL.application;

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
import nl.hu.dp.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            AdresDAO adresDAO = new AdresDAOPsql(connection);
            ReizigerDAO reizigerDAO = new ReizigerDAOPsql(connection, adresDAO);
            OVChipkaartDAO ovChipkaartDAO = new OVChipkaartDAOPsql(connection, reizigerDAO);
            ProductDAO productDAO = new ProductDAOPsql(connection, ovChipkaartDAO);

            try {
                // Test ReizigerDAO
                Reiziger reiziger = new Reiziger();
                reiziger.setVoorletters("A");
                reiziger.setTussenvoegsel("van");
                reiziger.setAchternaam("Test");
                reiziger.setGeboortedatum(Date.valueOf("2001-01-01"));
                reizigerDAO.save(reiziger);
                System.out.println("Reiziger saved: " + reiziger);

                // Test AdresDAO
                Adres adres = new Adres();
                adres.setPostcode("4321AB");
                adres.setHuisnummer("23");
                adres.setStraat("Teststraat");
                adres.setWoonplaats("Teststad");
                adres.setReiziger(reiziger);
                adresDAO.save(adres);
                System.out.println("Adres saved: " + adres);

                // Test OVChipkaartDAO
                OVChipkaart ovChipkaart = new OVChipkaart();
                ovChipkaart.setGeldigTot(Date.valueOf("2026-12-31"));
                ovChipkaart.setKlasse(2);
                ovChipkaart.setSaldo(50.0);
                ovChipkaart.setReiziger(reiziger);
                ovChipkaartDAO.save(ovChipkaart);
                System.out.println("OVChipkaart saved: " + ovChipkaart);

                // Test ProductDAO
                Product product = new Product();
                product.setNaam("NS Subscription");
                product.setBeschrijving("Monthly Subscription for Public Transport");
                product.setPrijs(99.99);
                productDAO.save(product);
                System.out.println("Product saved: " + product);

                ovChipkaart.addProduct(product);
                ovChipkaartDAO.update(ovChipkaart);
                System.out.println("OVChipkaart updated with product: " + ovChipkaart);

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
