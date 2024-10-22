package nl.hu.dp.ovchip.PSQL.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.hu.dp.ovchip.PSQL.domain.OVChipkaart;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"productNummer"})
public class Product {
    private Long productNummer;
    private String naam;
    private String beschrijving;
    private double prijs;
    private List<OVChipkaart> OVChipkaarten = new ArrayList<>();

    public Product(Long productNummer, String naam, String beschrijving, double prijs) {
        this.productNummer = productNummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
    }

    public void addOVChipkaart(OVChipkaart ovChipkaart) {
        if (!OVChipkaarten.contains(ovChipkaart)) {
            OVChipkaarten.add(ovChipkaart);
            ovChipkaart.addProduct(this);
        }
    }

    public void removeOVChipkaart(OVChipkaart ovChipkaart) {
        if (OVChipkaarten.contains(ovChipkaart)) {
            OVChipkaarten.remove(ovChipkaart);
            ovChipkaart.removeProduct(this);
        }
    }

    @Override
    public String toString() {
        return "Product {#" + productNummer + ", " + naam + ", €" + prijs + "}";
    }
}
