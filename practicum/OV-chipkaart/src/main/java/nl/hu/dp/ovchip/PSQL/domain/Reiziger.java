package nl.hu.dp.ovchip.PSQL.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Reiziger {
    private Long id;
    private String voorletters;
    private String tussenvoegsel;
    private String achternaam;
    private Date geboortedatum;
    private Adres adres;
    private List<OVChipkaart> OVChipkaarten = new ArrayList<>();

    public Reiziger(String voorletters, String tussenvoegsel, String achternaam, Date geboortedatum) {
        this.voorletters = voorletters;
        this.tussenvoegsel = tussenvoegsel;
        this.achternaam = achternaam;
        this.geboortedatum = geboortedatum;
    }

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
