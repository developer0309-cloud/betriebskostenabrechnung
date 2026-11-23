package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "mieteinheit")
public class Mieteinheit extends PanacheEntityBase {
    @Id
    public Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "gebaeude_id")
    public Gebaeude gebaeude;

    @Column(length = 64, nullable = false)
    public String bezeichnung;

    @Column(precision = 10, scale = 2, nullable = false)
    public BigDecimal wohnflaeche;
    public int rauchwarnmelder;
}
