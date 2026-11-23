package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mieter")
public class Mieter extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToMany
    @JoinTable(
            name = "mieter_person",
            joinColumns = @JoinColumn(name = "mieter_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    public List<NatuerlichePerson> personen = new ArrayList<>();
}
