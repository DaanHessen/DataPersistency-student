package nl.hu.dp.ovchip.HIBERNATE.data;

import nl.hu.dp.ovchip.HIBERNATE.domain.OVChipkaart;
import nl.hu.dp.ovchip.HIBERNATE.domain.Product;
import nl.hu.dp.ovchip.HIBERNATE.domain.Reiziger;

import java.sql.SQLException;
import java.util.List;

public interface OVChipkaartDAO {
    boolean save(OVChipkaart ovChipkaart) throws SQLException;
    boolean update(OVChipkaart ovChipkaart) throws SQLException;
    boolean delete(OVChipkaart ovChipkaart) throws SQLException;
    OVChipkaart findByKaartNummer(long kaartnummer) throws SQLException;
    List<OVChipkaart> findAll() throws SQLException;
    List<OVChipkaart> findByReiziger(Reiziger reiziger) throws SQLException;
    List<OVChipkaart> findByProduct(Product product) throws SQLException; // Added method
}