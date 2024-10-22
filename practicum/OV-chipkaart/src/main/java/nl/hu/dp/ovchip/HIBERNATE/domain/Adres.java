package nl.hu.dp.ovchip.HIBERNATE.domain;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "adres")
public class Adres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "adres_id")
    private Long id;

    @Column (name = "postcode")
    private String postcode;

    @Column (name = "huisnummer")
    private String huisnummer;

    @Column (name = "straat")
    private String straat;

    @Column (name = "woonplaats")
    private String woonplaats;

    @OneToOne
    @JoinColumn(name = "reiziger_id")
    private Reiziger reiziger;

    @Override
    public String toString() {
        return "Adres {#" + id + " " + postcode + " " + huisnummer + " " + straat + " " + woonplaats + "}";
    }
}
