package nl.hu.dp.ovchip.HIBERNATE.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"kaart_nummer"})
@Entity
@Table(name = "ov_chipkaart")
public class OVChipkaart {

    @Id
    @Column(name = "kaart_nummer")
    private Long kaart_nummer;

    @Column(name = "geldig_tot")
    private Date geldigTot;

    @Column(name = "klasse")
    private int klasse;

    @Column(name = "saldo")
    private double saldo;

    @ManyToOne
    @JoinColumn(name = "reiziger_id")
    private Reiziger reiziger;

    @ManyToMany
    @JoinTable(name = "ov_chipkaart_product",
            joinColumns = @JoinColumn(name = "kaart_nummer"),
            inverseJoinColumns = @JoinColumn(name = "product_nummer"))
    private List<Product> producten = new ArrayList<>();

    public void addProduct(Product product) {
        if (!producten.contains(product)) {
            producten.add(product);
            product.getOvChipkaarten().add(this);
        }
    }

    public void removeProduct(Product product) {
        if (producten.contains(product)) {
            producten.remove(product);
            product.getOvChipkaarten().remove(this);
        }
    }

    @Override
    public String toString() {
        return "OVChipkaart {#" + kaart_nummer + ", geldig tot: " + geldigTot + ", klasse: " + klasse + ", saldo: €" + saldo + "}";
    }
}
