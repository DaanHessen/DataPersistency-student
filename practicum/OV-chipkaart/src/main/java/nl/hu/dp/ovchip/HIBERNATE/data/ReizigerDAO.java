package nl.hu.dp.ovchip.HIBERNATE.data;

import nl.hu.dp.ovchip.HIBERNATE.domain.Reiziger;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface ReizigerDAO {
    boolean save(Reiziger reiziger) throws SQLException;
    boolean update(Reiziger reiziger) throws SQLException;
    boolean delete(Reiziger reiziger) throws SQLException;
    Reiziger findById(long reiziger_id) throws SQLException;
    List<Reiziger> findByGeboortedatum(Date geboortedatum) throws SQLException;
    List<Reiziger> findAll() throws SQLException;
}
