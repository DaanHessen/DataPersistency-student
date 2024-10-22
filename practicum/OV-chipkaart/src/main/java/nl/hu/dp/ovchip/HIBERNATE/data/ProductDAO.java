package nl.hu.dp.ovchip.HIBERNATE.data;

import nl.hu.dp.ovchip.PSQL.domain.OVChipkaart;
import nl.hu.dp.ovchip.PSQL.domain.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductDAO {
    boolean save(Product product) throws SQLException;
    boolean update(Product product) throws SQLException;
    boolean delete(Product product) throws SQLException;
    Product findById(Long productNummer) throws SQLException;
    List<Product> findByOVChipkaart(OVChipkaart ovChipkaart) throws SQLException;
    List<Product> findAll() throws SQLException;
}