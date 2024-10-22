package nl.hu.dp.ovchip.HIBERNATE.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Entity
@Table(name = "reiziger")
public class Reiziger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "reiziger_id")
    private Long id;

    @Column (name = "voorletters")
    private String voorletters;

    @Column (name = "tussenvoegsel")
    private String tussenvoegsel;

    @Column (name = "achternaam")
    private String achternaam;

    @Column (name = "geboortedatum")
    private Date geboortedatum;

    @OneToOne(mappedBy = "reiziger", cascade = CascadeType.ALL)
    private Adres adres;

    @OneToMany(mappedBy = "reiziger", cascade = CascadeType.ALL)
    private List<OVChipkaart> OVChipkaarten = new ArrayList<>();

    public void addOVChipkaart(OVChipkaart ovChipkaart) {
        if (!OVChipkaarten.contains(ovChipkaart)) {
            OVChipkaarten.add(ovChipkaart);
            ovChipkaart.setReiziger(this);
        }
    }

    public void removeOVChipkaart(OVChipkaart ovChipkaart) {
        if (OVChipkaarten.contains(ovChipkaart)) {
            OVChipkaarten.remove(ovChipkaart);
            ovChipkaart.setReiziger(null);
        }
    }

    @Override
    public String toString() {
        return "Reiziger {#" + id + " " + voorletters + " " +
                (tussenvoegsel != null ? tussenvoegsel + " " : "") +
                achternaam + ", geb. " + geboortedatum + "}";
    }
}
