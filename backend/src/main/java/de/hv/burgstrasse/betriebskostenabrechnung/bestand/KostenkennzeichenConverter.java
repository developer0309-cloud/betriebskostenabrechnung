package de.hv.burgstrasse.betriebskostenabrechnung.bestand;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class KostenkennzeichenConverter implements AttributeConverter<Kostenkennzeichen, String> {
    @Override
    public String convertToDatabaseColumn(Kostenkennzeichen kennzeichen) {
        return kennzeichen == null ? null : kennzeichen.bezeichnung();
    }

    @Override
    public Kostenkennzeichen convertToEntityAttribute(String bezeichnung) {
        return Kostenkennzeichen.von(bezeichnung).orElse(null);
    }
}
