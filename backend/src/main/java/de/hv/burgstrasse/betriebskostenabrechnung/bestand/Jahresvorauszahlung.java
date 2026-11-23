package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(
        name = "jahresvorauszahlung",
        uniqueConstraints = @UniqueConstraint(name = "uq_vorauszahlung_jahr", columnNames = {"mietvertrag_id", "jahr"}))
public class Jahresvorauszahlung extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mietvertrag_id", nullable = false)
    public Mietvertrag mietvertrag;

    @Column(nullable = false)
    public Integer jahr;

    @Column(precision = 12, scale = 2, nullable = false)
    public BigDecimal betrag;
}
