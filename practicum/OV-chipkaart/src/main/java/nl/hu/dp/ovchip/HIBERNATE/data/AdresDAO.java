package nl.hu.dp.ovchip.HIBERNATE.data;

import nl.hu.dp.ovchip.HIBERNATE.domain.Adres;
import nl.hu.dp.ovchip.HIBERNATE.domain.Reiziger;

import java.sql.SQLException;
import java.util.List;

public interface AdresDAO {
    boolean save(Adres adres) throws SQLException;
    boolean update(Adres adres) throws SQLException;
    boolean delete(Adres adres) throws SQLException;
    Adres findById(long adres_id) throws SQLException;
    Adres findByReiziger(Reiziger reiziger) throws SQLException;
    List<Adres> findAll() throws SQLException;
}