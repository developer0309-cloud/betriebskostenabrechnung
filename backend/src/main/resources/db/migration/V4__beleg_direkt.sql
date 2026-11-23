ALTER TABLE beleg ADD COLUMN kennzeichen VARCHAR(16) DEFAULT 'indirekt' NOT NULL;
ALTER TABLE beleg ADD COLUMN mieter_id BIGINT;
ALTER TABLE beleg ADD CONSTRAINT fk_beleg_mieter FOREIGN KEY (mieter_id) REFERENCES mieter (id);

ALTER TABLE beleg DROP CONSTRAINT ck_beleg_kostenart;
ALTER TABLE beleg ADD CONSTRAINT ck_beleg_kostenart CHECK (kostenart IN (
    'Grundsteuer',
    'Wartung Rauchwarnmelder',
    'Allgemeinstrom',
    'Hausreinigung',
    'Gebäudeversicherung',
    'Glasversicherung',
    'Strassenreinigung',
    'Entwässerung',
    'Müllentsorgung',
    'Direkte Kosten'
));

ALTER TABLE beleg ADD CONSTRAINT ck_beleg_kennzeichen CHECK (kennzeichen IN ('direkt', 'indirekt'));
ALTER TABLE beleg ADD CONSTRAINT ck_beleg_zuordnung CHECK (
    (kennzeichen = 'direkt' AND kostenart = 'Direkte Kosten' AND mieter_id IS NOT NULL)
    OR (kennzeichen = 'indirekt' AND kostenart <> 'Direkte Kosten' AND mieter_id IS NULL)
);

ALTER TABLE beleg ALTER COLUMN kennzeichen DROP DEFAULT;
