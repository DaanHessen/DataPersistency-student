package nl.hu.dp.ovchip.HIBERNATE.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"kaart_nummer"})
@Entity
@Table(name = "ov_chipkaart")
public class OVChipkaart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "kaart_nummer")
    private long kaart_nummer;

    @Column (name = "geldig_tot")
    private Date geldigTot;

    @Column (name = "klasse")
    private int klasse;

    @Column (name = "saldo")
    private double saldo;

    @ManyToOne(cascade = CascadeType.ALL)
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

