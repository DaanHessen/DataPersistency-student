package nl.hu.dp.ovchip.PSQL.domain;

import lombok.*;

@Data
@NoArgsConstructor
public class Adres {
    private Long id;
    private String postcode;
    private String huisnummer;
    private String straat;
    private String woonplaats;
    private Reiziger reiziger;

    public Adres(String postcode, String huisnummer, String straat, String woonplaats, Reiziger reiziger) {
        this.postcode = postcode;
        this.huisnummer = huisnummer;
        this.straat = straat;
        this.woonplaats = woonplaats;
        this.reiziger = reiziger;
    }

    @Override
    public String toString() {
        return "Adres {#" + id + " " + postcode + " " + huisnummer + " " + straat + " " + woonplaats + "}";
    }
}
