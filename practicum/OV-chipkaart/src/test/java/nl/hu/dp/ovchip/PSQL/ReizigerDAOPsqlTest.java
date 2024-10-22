package nl.hu.dp.ovchip.PSQL;

import nl.hu.dp.util.DatabaseConnection;
import nl.hu.dp.ovchip.PSQL.data.AdresDAO;
import nl.hu.dp.ovchip.PSQL.data.ReizigerDAO;
import nl.hu.dp.ovchip.PSQL.domain.Adres;
import nl.hu.dp.ovchip.PSQL.domain.Reiziger;
import nl.hu.dp.ovchip.PSQL.service.AdresDAOPsql;
import nl.hu.dp.ovchip.PSQL.service.ReizigerDAOPsql;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;
import java.util.List;

import static org.junit.Assert.*;

public class ReizigerDAOPsqlTest {

    private static Connection connection;
    private ReizigerDAO reizigerDAO;
    private AdresDAO adresDAO;

    @BeforeClass
    public static void setUpOnce() throws SQLException {
        connection = DatabaseConnection.getConnection();
    }

    @AfterClass
    public static void tearDownOnce() throws SQLException {
        if (connection != null) connection.close();
    }

    @Before
    public void setUp() throws SQLException {
        connection.setAutoCommit(false);
        adresDAO = new AdresDAOPsql(connection);
        reizigerDAO = new ReizigerDAOPsql(connection, adresDAO);
    }

    @After
    public void tearDown() throws SQLException {
        connection.rollback();
        connection.setAutoCommit(true);
    }

    @Test
    public void testSaveAndFindById() throws SQLException {
        // Create a new Reiziger
        Reiziger reiziger = new Reiziger("T", "van", "Test", Date.valueOf("2000-01-01"));
        assertTrue(reizigerDAO.save(reiziger));

        // Retrieve the Reiziger by ID
        Reiziger retrievedReiziger = reizigerDAO.findById(reiziger.getId());
        assertNotNull(retrievedReiziger);
        assertEquals("Test", retrievedReiziger.getAchternaam());

        // No need to delete; transaction will be rolled back
    }

    @Test
    public void testUpdate() throws SQLException {
        // Create and save a new Reiziger
        Reiziger reiziger = new Reiziger("U", null, "UpdateTest", Date.valueOf("1995-05-05"));
        assertTrue(reizigerDAO.save(reiziger));

        // Update the Reiziger
        reiziger.setAchternaam("Updated");
        assertTrue(reizigerDAO.update(reiziger));

        // Retrieve and verify the update
        Reiziger updatedReiziger = reizigerDAO.findById(reiziger.getId());
        assertEquals("Updated", updatedReiziger.getAchternaam());
    }

    @Test
    public void testFindAll() throws SQLException {
        // Get initial count
        List<Reiziger> initialReizigers = reizigerDAO.findAll();

        // Add a new Reiziger
        Reiziger reiziger = new Reiziger("A", null, "AllTest", Date.valueOf("1990-01-01"));
        assertTrue(reizigerDAO.save(reiziger));

        // Get updated list
        List<Reiziger> updatedReizigers = reizigerDAO.findAll();
        assertEquals(initialReizigers.size() + 1, updatedReizigers.size());
    }

    @Test
    public void testFindByGeboortedatum() throws SQLException {
        // Create Reizigers with the same geboortedatum
        Date date = Date.valueOf("1980-01-01");
        Reiziger r1 = new Reiziger("F", null, "FindTest1", date);
        Reiziger r2 = new Reiziger("F", null, "FindTest2", date);
        assertTrue(reizigerDAO.save(r1));
        assertTrue(reizigerDAO.save(r2));

        // Retrieve by geboortedatum
        List<Reiziger> reizigers = reizigerDAO.findByGeboortedatum(date);
        assertTrue(reizigers.size() >= 2);
    }

    @Test
    public void testAdresAssociation() throws SQLException {
        // Create a Reiziger and an Adres
        Reiziger reiziger = new Reiziger("B", null, "AdresTest", Date.valueOf("1985-05-05"));
        Adres adres = new Adres("1234AB", "12", "Teststraat", "Teststad", reiziger);
        reiziger.setAdres(adres);

        // Save Reiziger and Adres
        assertTrue(reizigerDAO.save(reiziger));

        // Retrieve Reiziger and check Adres
        Reiziger retrievedReiziger = reizigerDAO.findById(reiziger.getId());
        assertNotNull(retrievedReiziger.getAdres());
        assertEquals("Teststraat", retrievedReiziger.getAdres().getStraat());
    }
}
