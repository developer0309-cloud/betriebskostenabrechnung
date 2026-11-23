package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "beleg")
public class Beleg extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public LocalDate valutadatum;

    @Column(precision = 12, scale = 2, nullable = false)
    public BigDecimal betrag;

    @Column(length = 500)
    public String beschreibung;

    @Convert(converter = KostenartConverter.class)
    @Column(nullable = false, length = 64)
    public Kostenart kostenart;

    @Convert(converter = KostenkennzeichenConverter.class)
    @Column(nullable = false, length = 16)
    public Kostenkennzeichen kennzeichen;

    @ManyToOne
    @JoinColumn(name = "mieter_id")
    public Mieter mieter;
}
