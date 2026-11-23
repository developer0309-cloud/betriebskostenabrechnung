package de.hv.burgstrasse.betriebskostenabrechnung.persistence;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;

@ApplicationScoped
public class BlazeProducer {
    @Inject
    EntityManagerFactory entityManagerFactory;

    @Produces
    @ApplicationScoped
    public CriteriaBuilderFactory criteriaBuilderFactory() {
        return Criteria.getDefault().createCriteriaBuilderFactory(entityManagerFactory);
    }
}
