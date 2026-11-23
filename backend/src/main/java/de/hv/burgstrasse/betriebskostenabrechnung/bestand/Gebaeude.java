package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "gebaeude")
public class Gebaeude extends PanacheEntityBase {
    @Id
    public Long id;
    @Column(length = 2, nullable = false)
    public String kuerzel;
    public String name;
}
