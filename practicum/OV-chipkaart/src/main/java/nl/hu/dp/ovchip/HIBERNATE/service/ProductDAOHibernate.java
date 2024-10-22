package nl.hu.dp.ovchip.HIBERNATE.service;

import nl.hu.dp.ovchip.PSQL.data.OVChipkaartDAO;
import nl.hu.dp.ovchip.PSQL.data.ProductDAO;
import nl.hu.dp.ovchip.PSQL.domain.OVChipkaart;
import nl.hu.dp.ovchip.PSQL.domain.Product;
import nl.hu.dp.ovchip.PSQL.domain.Reiziger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOHibernate implements ProductDAO {
    private Connection connection;
    private OVChipkaartDAO ovChipkaartDAO;

    public ProductDAOHibernate(Connection connection, OVChipkaartDAO ovChipkaartDAO) {
        this.connection = connection;
        this.ovChipkaartDAO = ovChipkaartDAO;
    }

    @Override
    public boolean save(Product product) throws SQLException {
        PreparedStatement statement = null;

        try {
            long newProductNummer = generateNewId("product", "product_nummer");
            product.setProductNummer(newProductNummer);

            String sql = "INSERT INTO product (product_nummer, naam, beschrijving, prijs) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, product.getProductNummer());
            statement.setString(2, product.getNaam());
            statement.setString(3, product.getBeschrijving());
            statement.setDouble(4, product.getPrijs());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Creating product failed, no rows affected.");
            }

            for (OVChipkaart ovChipkaart : product.getOVChipkaarten()) {
                saveAssociatedOVChipkaarten(product, ovChipkaart);
            }

            return true;
        } finally {
            if (statement != null) statement.close();
        }
    }

    @Override
    public boolean update(Product product) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "UPDATE product SET naam = ?, beschrijving = ?, prijs = ? WHERE product_nummer = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, product.getNaam());
            statement.setString(2, product.getBeschrijving());
            statement.setDouble(3, product.getPrijs());
            statement.setLong(4, product.getProductNummer());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Updating product failed, no rows affected.");
            }

            deleteAssociatedOVChipkaarten(product);
            for (OVChipkaart ovChipkaart : product.getOVChipkaarten()) {
                saveAssociatedOVChipkaarten(product, ovChipkaart);
            }

            return true;
        } finally {
            if (statement != null) statement.close();
        }
    }

    @Override
    public boolean delete(Product product) throws SQLException {
        PreparedStatement statement = null;

        try {
            deleteAssociatedOVChipkaarten(product);

            String sql = "DELETE FROM product WHERE product_nummer = ?";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, product.getProductNummer());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Deleting product failed, no rows affected.");
            }

            return true;
        } finally {
            if (statement != null) statement.close();
        }
    }

    @Override
    public Product findById(Long productNummer) throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        Product product = null;

        try {
            String sql = "SELECT * FROM product WHERE product_nummer = ?";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, productNummer);
            rs = statement.executeQuery();

            if (rs.next()) {
                product = resultSetToProduct(rs);

                // Fetch associated OVChipkaarten
                List<OVChipkaart> ovChipkaarten = findOVChipkaartenByProduct(product);
                product.setOVChipkaarten(ovChipkaarten);
            }
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
        }

        return product;
    }

    @Override
    public List<Product> findByOVChipkaart(OVChipkaart ovChipkaart) throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        List<Product> producten = new ArrayList<>();
        String sql = "SELECT p.* FROM product p" +
                " JOIN ov_chipkaart_product ocp ON p.product_nummer = ocp.product_nummer" +
                " WHERE ocp.kaart_nummer = ?";

        try {
            statement = connection.prepareStatement(sql);
            statement.setLong(1, ovChipkaart.getKaart_nummer());
            rs = statement.executeQuery();

            while (rs.next()) {
                Product product = resultSetToProduct(rs);
                producten.add(product);
            }
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
        }

        return producten;
    }

    @Override
    public List<Product> findAll() throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        List<Product> producten = new ArrayList<>();

        try {
            String sql = "SELECT * FROM product";
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();

            while (rs.next()) {
                Product product = resultSetToProduct(rs);
                producten.add(product);
            }
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
        }

        return producten;
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

    private Product resultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductNummer(rs.getLong("product_nummer"));
        product.setNaam(rs.getString("naam"));
        product.setBeschrijving(rs.getString("beschrijving"));
        product.setPrijs(rs.getDouble("prijs"));
        return product;
    }

    private void saveAssociatedOVChipkaarten(Product product, OVChipkaart ovChipkaart) throws SQLException {
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

    private void deleteAssociatedOVChipkaarten(Product product) throws SQLException {
        PreparedStatement statement = null;

        try {
            String sql = "DELETE FROM ov_chipkaart_product WHERE product_nummer = ?";
            statement = connection.prepareStatement(sql);
            statement.setLong(1, product.getProductNummer());
            statement.executeUpdate();
        } finally {
            if (statement != null) statement.close();
        }
    }

    private List<OVChipkaart> findOVChipkaartenByProduct(Product product) throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;
        List<OVChipkaart> ovChipkaarten = new ArrayList<>();
        String sql = "SELECT oc.* FROM ov_chipkaart oc" +
                " JOIN ov_chipkaart_product ocp ON oc.kaart_nummer = ocp.kaart_nummer" +
                " WHERE ocp.product_nummer = ?";

        try {
            statement = connection.prepareStatement(sql);
            statement.setLong(1, product.getProductNummer());
            rs = statement.executeQuery();

            while (rs.next()) {
                OVChipkaart ovChipkaart = new OVChipkaart();
                ovChipkaart.setKaart_nummer(rs.getLong("kaart_nummer"));
                ovChipkaart.setGeldigTot(rs.getDate("geldig_tot"));
                ovChipkaart.setKlasse(rs.getInt("klasse"));
                ovChipkaart.setSaldo(rs.getDouble("saldo"));

                Reiziger reiziger = ovChipkaartDAO.findByKaartNummer(ovChipkaart.getKaart_nummer()).getReiziger();
                ovChipkaart.setReiziger(reiziger);
                ovChipkaarten.add(ovChipkaart);
            }
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
        }

        return ovChipkaarten;
    }
}
