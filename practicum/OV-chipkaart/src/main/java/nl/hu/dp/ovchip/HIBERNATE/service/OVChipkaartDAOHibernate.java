package nl.hu.dp.ovchip.HIBERNATE.service;

import nl.hu.dp.ovchip.PSQL.data.OVChipkaartDAO;
import nl.hu.dp.ovchip.PSQL.data.ReizigerDAO;
import nl.hu.dp.ovchip.PSQL.domain.OVChipkaart;
import nl.hu.dp.ovchip.PSQL.domain.Product;
import nl.hu.dp.ovchip.PSQL.domain.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOHibernate implements OVChipkaartDAO {
    private Connection connection;
    private ReizigerDAO reizigerDAO;

    public OVChipkaartDAOHibernate(Connection connection, ReizigerDAO reizigerDAO) {
        this.connection = connection;
        this.reizigerDAO = reizigerDAO;
    }

    @Override
    public boolean save(OVChipkaart ovChipkaart) throws SQLException {
        PreparedStatement statement = null;

        try {
            long newKaartNummer = generateNewId("ov_chipkaart", "kaart_nummer");
            ovChipkaart.setKaart_nummer(newKaartNummer);

            String sql = "INSERT INTO ov_chipkaart (kaart_nummer, geldig_tot, klasse, saldo, reiziger_id) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, ovChipkaart.getKaart_nummer());
            statement.setDate(2, ovChipkaart.getGeldigTot());
            statement.setInt(3, ovChipkaart.getKlasse());
            statement.setDouble(4, ovChipkaart.getSaldo());
            statement.setLong(5, ovChipkaart.getReiziger().getId());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Creating ov_chipkaart failed, no rows affected.");
            }

            for (Product product : ovChipkaart.getProducten()) {
                saveAssociatedProducts(ovChipkaart, product);
            }

            return true;
        } finally {
            if (statement != null) statement.close();
        }
    }

    @Override
    public boolean update(OVChipkaart ovChipkaart) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "UPDATE ov_chipkaart SET geldig_tot = ?, klasse = ?, saldo = ?, reiziger_id = ? WHERE kaart_nummer = ?";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, ovChipkaart.getGeldigTot());
            statement.setInt(2, ovChipkaart.getKlasse());
            statement.setDouble(3, ovChipkaart.getSaldo());
            statement.setLong(4, ovChipkaart.getReiziger().getId());
            statement.setLong(5, ovChipkaart.getKaart_nummer());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Updating OVChipkaart failed, no rows affected.");
            }

            deleteAssociatedProducts(ovChipkaart);
            for (Product product : ovChipkaart.getProducten()) {
                saveAssociatedProducts(ovChipkaart, product);
            }

            return true;
        } finally {
            if (statement != null) statement.close();
        }
    }

    @Override
    public boolean delete(OVChipkaart ovChipkaart) throws SQLException {
        PreparedStatement statement = null;

        try {
            deleteAssociatedProducts(ovChipkaart);

            String sql = "DELETE FROM ov_chipkaart WHERE kaart_nummer = ?";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, ovChipkaart.getKaart_nummer());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Deleting OVChipkaart failed, no rows affected.");
            }

            return true;
        } finally {
            if (statement != null) statement.close();
        }
    }

    @Override
    public OVChipkaart findByKaartNummer(long kaartnummer) throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        OVChipkaart ovChipkaart = null;

        try {
            String sql = "SELECT * FROM ov_chipkaart WHERE kaart_nummer = ?";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, kaartnummer);
            rs = statement.executeQuery();

            if (rs.next()) {
                ovChipkaart = resultSetToOVChipkaart(rs);

                List<Product> producten = findProductsByOVChipkaart(ovChipkaart);
                ovChipkaart.setProducten(producten);
            }
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
        }

        return ovChipkaart;
    }

    @Override
    public List<OVChipkaart> findAll() throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();

        try {
            String sql = "SELECT * FROM ov_chipkaart";
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();

            while (rs.next()) {
                OVChipkaart ovChipkaart = resultSetToOVChipkaart(rs);

                List<Product> producten = findProductsByOVChipkaart(ovChipkaart);
                ovChipkaart.setProducten(producten);

                ovChipkaarten.add(ovChipkaart);
            }
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
        }

        return ovChipkaarten;
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();

        try {
            String sql = "SELECT * FROM ov_chipkaart WHERE reiziger_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, reiziger.getId());
            rs = statement.executeQuery();

            while (rs.next()) {
                OVChipkaart ovChipkaart = resultSetToOVChipkaart(rs);
                ovChipkaart.setReiziger(reiziger);

                List<Product> producten = findProductsByOVChipkaart(ovChipkaart);
                ovChipkaart.setProducten(producten);

                ovChipkaarten.add(ovChipkaart);
            }
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
        }

        return ovChipkaarten;
    }

    @Override
    public List<OVChipkaart> findByProduct(Product product) throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();
        String sql = "SELECT oc.* FROM ov_chipkaart oc " +
                "JOIN ov_chipkaart_product ocp ON oc.kaart_nummer = ocp.kaart_nummer " +
                "WHERE ocp.product_nummer = ?";

        try {
            statement = connection.prepareStatement(sql);
            statement.setLong(1, product.getProductNummer());
            rs = statement.executeQuery();

            while (rs.next()) {
                OVChipkaart ovChipkaart = resultSetToOVChipkaart(rs);

                List<Product> producten = findProductsByOVChipkaart(ovChipkaart);
                ovChipkaart.setProducten(producten);

                ovChipkaarten.add(ovChipkaart);
            }
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
        }

        return ovChipkaarten;
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

    private OVChipkaart resultSetToOVChipkaart(ResultSet rs) throws SQLException {
        OVChipkaart ovChipkaart = new OVChipkaart();
        ovChipkaart.setKaart_nummer(rs.getLong("kaart_nummer"));
        ovChipkaart.setGeldigTot(rs.getDate("geldig_tot"));
        ovChipkaart.setKlasse(rs.getInt("klasse"));
        ovChipkaart.setSaldo(rs.getDouble("saldo"));

        Reiziger reiziger = reizigerDAO.findById(rs.getLong("reiziger_id"));
        ovChipkaart.setReiziger(reiziger);

        return ovChipkaart;
    }

    private void saveAssociatedProducts(OVChipkaart ovChipkaart, Product product) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "INSERT INTO ov_chipkaart_product (kaart_nummer, product_nummer) VALUES (?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, ovChipkaart.getKaart_nummer());
            statement.setLong(2, product.getProductNummer());
            statement.executeUpdate();
        } finally {
            if (statement != null) statement.close();
        }
    }

    private void deleteAssociatedProducts(OVChipkaart ovChipkaart) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "DELETE FROM ov_chipkaart_product WHERE kaart_nummer = ?";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, ovChipkaart.getKaart_nummer());
            statement.executeUpdate();
        } finally {
            if (statement != null) statement.close();
        }
    }

    private List<Product> findProductsByOVChipkaart(OVChipkaart ovChipkaart) throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        List<Product> producten = new ArrayList<>();
        String sql = "SELECT p.* FROM product p " +
                "JOIN ov_chipkaart_product ocp ON p.product_nummer = ocp.product_nummer " +
                "WHERE ocp.kaart_nummer = ?";

        try {
            statement = connection.prepareStatement(sql);
            statement.setLong(1, ovChipkaart.getKaart_nummer());
            rs = statement.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setProductNummer(rs.getLong("product_nummer"));
                product.setNaam(rs.getString("naam"));
                product.setBeschrijving(rs.getString("beschrijving"));
                product.setPrijs(rs.getDouble("prijs"));

                producten.add(product);
            }
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
        }

        return producten;
    }
}
