package nl.hu.dp.ovchip.PSQL.domain;

import lombok.*;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"kaart_nummer"})
public class OVChipkaart {
    private long kaart_nummer;
    private Date geldigTot;
    private int klasse;
    private double saldo;
    private Reiziger reiziger;
    private List<Product> producten = new ArrayList<>();

    public OVChipkaart(long kaartnummer, Date geldigTot, int klasse, double saldo, long reizigerId) {
        this.kaart_nummer = kaartnummer;
        this.geldigTot = geldigTot;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger = reiziger;
    }

    public void addProduct(Product product) {
        if (!producten.contains(product)) {
            producten.add(product);
            product.addOVChipkaart(this);
        }
    }

    public void removeProduct(Product product) {
        if (producten.contains(product)) {
            producten.remove(product);
            product.removeOVChipkaart(this);
        }
    }

    @Override
    public String toString() {
        return "OVChipkaart {#" + kaart_nummer + ", geldig tot: " + geldigTot + ", klasse: " + klasse + ", saldo: €" + saldo + "}";
    }
}

