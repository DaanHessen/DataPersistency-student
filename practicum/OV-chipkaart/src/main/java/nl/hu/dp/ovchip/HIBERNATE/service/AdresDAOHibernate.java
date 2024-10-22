package nl.hu.dp.ovchip.HIBERNATE.service;

import nl.hu.dp.ovchip.PSQL.data.AdresDAO;
import nl.hu.dp.ovchip.PSQL.domain.Adres;
import nl.hu.dp.ovchip.PSQL.domain.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdresDAOHibernate implements AdresDAO {
    private final Connection connection;

    public AdresDAOHibernate(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean save(Adres adres) throws SQLException {
        PreparedStatement statement = null;

        try {
            long newId = generateNewId("adres", "adres_id");
            adres.setId(newId);

            String sql = "INSERT INTO adres (adres_id, postcode, huisnummer, straat, woonplaats, reiziger_id) VALUES (?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, adres.getId());
            statement.setString(2, adres.getPostcode());
            statement.setString(3, adres.getHuisnummer());
            statement.setString(4, adres.getStraat());
            statement.setString(5, adres.getWoonplaats());
            statement.setLong(6, adres.getReiziger().getId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Creating adres failed, no rows affected.");
            }

            return true;
        } finally {
            if (statement != null) statement.close();
        }
    }

    @Override
    public boolean update(Adres adres) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "UPDATE adres SET postcode = ?, huisnummer = ?, straat = ?, woonplaats = ?, reiziger_id = ? WHERE adres_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, adres.getPostcode());
            statement.setString(2, adres.getHuisnummer());
            statement.setString(3, adres.getStraat());
            statement.setString(4, adres.getWoonplaats());
            statement.setLong(5, adres.getReiziger().getId());
            statement.setLong(6, adres.getId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Updating adres failed, no rows affected.");
            }

            return true;
        } finally {
            if (statement != null) statement.close();
        }
    }

    @Override
    public boolean delete(Adres adres) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "DELETE FROM adres WHERE adres_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, adres.getId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Deleting adres failed, no rows affected.");
            }

            return true;
        } finally {
            if (statement != null) statement.close();
        }
    }

    @Override
    public Adres findById(long adres_id) throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        Adres adres = null;

        try {
            String sql = "SELECT * FROM adres WHERE adres_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, adres_id);
            rs = statement.executeQuery();

            if (rs.next()) {
                adres = resultSetToAdres(rs);
            }
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
        }

        return adres;
    }

    @Override
    public Adres findByReiziger(Reiziger reiziger) throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        Adres adres = null;

        try {
            String sql = "SELECT * FROM adres WHERE reiziger_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, reiziger.getId());
            rs = statement.executeQuery();

            if (rs.next()) {
                adres = resultSetToAdres(rs);
                adres.setReiziger(reiziger);
            }
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
        }

        return adres;
    }

    @Override
    public List<Adres> findAll() throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        List<Adres> adressen = new ArrayList<>();

        try {
            String sql = "SELECT * FROM adres";
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();

            while (rs.next()) {
                Adres adres = resultSetToAdres(rs);
                adressen.add(adres);
            }
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
        }

        return adressen;
    }

    private long generateNewId(String table, String column) throws SQLException {
        String sql = "SELECT MAX(" + column + ") FROM " + table;
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1) + 1;
            }
        }
        return 1;
    }

    private Adres resultSetToAdres(ResultSet rs) throws SQLException {
        Adres adres = new Adres();
        adres.setId(rs.getLong("adres_id"));
        adres.setPostcode(rs.getString("postcode"));
        adres.setHuisnummer(rs.getString("huisnummer"));
        adres.setStraat(rs.getString("straat"));
        adres.setWoonplaats(rs.getString("woonplaats"));
        // The Reiziger is not set here; it can be set by the calling method if needed
        return adres;
    }
}
