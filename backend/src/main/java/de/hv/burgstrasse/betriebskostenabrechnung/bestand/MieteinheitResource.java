package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;

@Path("/api/mieteinheiten")
@RolesAllowed("eigentuemer")
@Produces(MediaType.APPLICATION_JSON)
public class MieteinheitResource {
    @Inject
    CriteriaBuilderFactory criteriaBuilderFactory;

    @Inject
    EntityManager entityManager;

    @GET
    public List<Ansicht> list() {
        CriteriaBuilder<Mieteinheit> query = criteriaBuilderFactory.create(entityManager, Mieteinheit.class);
        return query.orderByAsc("gebaeude.kuerzel").orderByAsc("id").getResultList().stream()
                .map(Ansicht::von)
                .toList();
    }

    public record Ansicht(
            Long id,
            String kuerzel,
            String gebaeude,
            String bezeichnung,
            BigDecimal wohnflaeche,
            int rauchwarnmelder) {
        static Ansicht von(Mieteinheit einheit) {
            return new Ansicht(
                    einheit.id,
                    einheit.gebaeude.kuerzel,
                    einheit.gebaeude.name,
                    einheit.bezeichnung,
                    einheit.wohnflaeche,
                    einheit.rauchwarnmelder);
        }
    }
}
