package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mietvertrag")
public class Mietvertrag extends PanacheEntityBase {
    @Id
    @Column(length = 160)
    public String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mieteinheit_id")
    public Mieteinheit mieteinheit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mieter_id")
    public Mieter mieter;

    public LocalDate vertragsbeginn;
    public LocalDate vertragsende;

    @OneToMany(mappedBy = "mietvertrag", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("jahr")
    public List<Jahresvorauszahlung> vorauszahlungen = new ArrayList<>();

    @Column(name = "erstellt_am", nullable = false)
    public LocalDate erstelltAm;
}
