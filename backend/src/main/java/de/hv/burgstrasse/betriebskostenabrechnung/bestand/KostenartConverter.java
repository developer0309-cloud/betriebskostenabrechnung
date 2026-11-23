package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class KostenartConverter implements AttributeConverter<Kostenart, String> {
    @Override
    public String convertToDatabaseColumn(Kostenart kostenart) {
        return kostenart == null ? null : kostenart.bezeichnung();
    }

    @Override
    public Kostenart convertToEntityAttribute(String bezeichnung) {
        return Kostenart.von(bezeichnung).orElse(null);
    }
}
