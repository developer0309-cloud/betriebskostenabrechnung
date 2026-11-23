package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.time.Clock;

@ApplicationScoped
public class BerlinClock {
    @Produces
    @ApplicationScoped
    public Clock clock() {
        return Clock.system(MietvertragResource.BERLIN);
    }
}
