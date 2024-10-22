package nl.hu.dp.ovchip.HIBERNATE.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"productNummer"})
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "product_nummer")
    private Long productNummer;

    @Column (name = "naam")
    private String naam;

    @Column (name = "beschrijving")
    private String beschrijving;

    @Column (name = "prijs")
    private double prijs;

    @ManyToMany(mappedBy = "producten")
    private List<OVChipkaart> OVChipkaarten = new ArrayList<>();

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
