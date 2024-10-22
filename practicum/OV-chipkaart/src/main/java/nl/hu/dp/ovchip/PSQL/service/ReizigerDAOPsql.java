package nl.hu.dp.ovchip.PSQL.service;

import nl.hu.dp.ovchip.PSQL.data.AdresDAO;
import nl.hu.dp.ovchip.PSQL.data.ReizigerDAO;
import nl.hu.dp.ovchip.PSQL.domain.Adres;
import nl.hu.dp.ovchip.PSQL.domain.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO {
    private final Connection connection;
    private final AdresDAO adresDAO;

    public ReizigerDAOPsql(Connection connection, AdresDAO adresDAO) {
        this.connection = connection;
        this.adresDAO = adresDAO;
    }

    @Override
    public boolean save(Reiziger reiziger) throws SQLException {
        PreparedStatement statement = null;

        try {
            long newId = generateNewId("reiziger", "reiziger_id");
            reiziger.setId(newId);

            String sql = "INSERT INTO reiziger (reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, reiziger.getId());
            statement.setString(2, reiziger.getVoorletters());
            statement.setString(3, reiziger.getTussenvoegsel());
            statement.setString(4, reiziger.getAchternaam());
            statement.setDate(5, reiziger.getGeboortedatum());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Creating reiziger failed, no rows affected.");
            }

            if (reiziger.getAdres() != null) {
                reiziger.getAdres().setReiziger(reiziger);
                adresDAO.save(reiziger.getAdres());
            }

            return true;
        } finally {
            if (statement != null) statement.close();
        }
    }

    @Override
    public boolean update(Reiziger reiziger) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "UPDATE reiziger SET voorletters = ?, tussenvoegsel = ?, achternaam = ?, geboortedatum = ? WHERE reiziger_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, reiziger.getVoorletters());
            statement.setString(2, reiziger.getTussenvoegsel());
            statement.setString(3, reiziger.getAchternaam());
            statement.setDate(4, reiziger.getGeboortedatum());
            statement.setLong(5, reiziger.getId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Updating reiziger failed, no rows affected.");
            }

            Adres adres = adresDAO.findByReiziger(reiziger);
            if (reiziger.getAdres() != null) {
                if (adres != null) {
                    adresDAO.update(reiziger.getAdres());
                } else {
                    adresDAO.save(reiziger.getAdres());
                }
            } else if (adres != null) {
                adresDAO.delete(adres);
            }

            return true;
        } finally {
            if (statement != null) statement.close();
        }
    }

    @Override
    public boolean delete(Reiziger reiziger) throws SQLException {
        PreparedStatement statement = null;

        try {
            Adres adres = adresDAO.findByReiziger(reiziger);
            if (adres != null) {
                adresDAO.delete(adres);
            }

            String sql = "DELETE FROM reiziger WHERE reiziger_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, reiziger.getId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Deleting reiziger failed, no rows affected.");
            }

            return true;
        } finally {
            if (statement != null) statement.close();
        }
    }

    @Override
    public Reiziger findById(long id) throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        Reiziger reiziger = null;

        try {
            String sql = "SELECT * FROM reiziger WHERE reiziger_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            rs = statement.executeQuery();

            if (rs.next()) {
                reiziger = resultSetToReiziger(rs);
                Adres adres = adresDAO.findByReiziger(reiziger);
                reiziger.setAdres(adres);
            }
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
        }

        return reiziger;
    }

    @Override
    public List<Reiziger> findByGeboortedatum(Date geboortedatum) throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        List<Reiziger> reizigers = new ArrayList<>();

        try {
            String sql = "SELECT * FROM reiziger WHERE geboortedatum = ?";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, geboortedatum);
            rs = statement.executeQuery();

            while (rs.next()) {
                Reiziger reiziger = resultSetToReiziger(rs);
                Adres adres = adresDAO.findByReiziger(reiziger);
                reiziger.setAdres(adres);
                reizigers.add(reiziger);
            }
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
        }

        return reizigers;
    }

    @Override
    public List<Reiziger> findAll() throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        List<Reiziger> reizigers = new ArrayList<>();

        try {
            String sql = "SELECT * FROM reiziger";
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();

            while (rs.next()) {
                Reiziger reiziger = resultSetToReiziger(rs);
                Adres adres = adresDAO.findByReiziger(reiziger);
                reiziger.setAdres(adres);
                reizigers.add(reiziger);
            }
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
        }

        return reizigers;
    }

    private long generateNewId(String table, String column) throws SQLException {
        String sql = "SELECT MAX(" + column + ") FROM " + table;
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1) + 1;
            } else {
                return 1;
            }
        }
    }

    private Reiziger resultSetToReiziger(ResultSet rs) throws SQLException {
        Reiziger reiziger = new Reiziger();
        reiziger.setId(rs.getLong("reiziger_id"));
        reiziger.setVoorletters(rs.getString("voorletters"));
        reiziger.setTussenvoegsel(rs.getString("tussenvoegsel"));
        reiziger.setAchternaam(rs.getString("achternaam"));
        reiziger.setGeboortedatum(rs.getDate("geboortedatum"));
        return reiziger;
    }
}
