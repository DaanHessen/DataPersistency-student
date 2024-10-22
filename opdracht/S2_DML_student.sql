-- ------------------------------------------------------------------------
-- Data & Persistency
-- Opdracht S2: Data Manipulation Language
--
-- (c) 2020 Hogeschool Utrecht
-- Tijmen Muller (tijmen.muller@hu.main.nl)
-- André Donk (andre.donk@hu.main.nl)
--
--
-- Opdracht: schrijf SQL-queries om onderstaande resultaten op te vragen,
-- aan te maken, verwijderen of aan te passen in de database van de
-- bedrijfscasus.
--
-- Codeer je uitwerking onder de regel 'DROP VIEW ...' (bij een SELECT)
-- of boven de regel 'ON CONFLICT DO NOTHING;' (bij een INSERT)
-- Je kunt deze eigen query selecteren en los uitvoeren, en wijzigen tot
-- je tevreden bent.
--
-- Vervolgens kun je je uitwerkingen testen door de testregels
-- (met [TEST] erachter) te activeren (haal hiervoor de commentaartekens
-- weg) en vervolgens het hele bestand uit te voeren. Hiervoor moet je de
-- testsuite in de database hebben geladen (bedrijf_postgresql_test.sql).
-- NB: niet alle opdrachten hebben testregels.
--
-- Lever je werk pas in op Canvas als alle tests slagen. Draai daarna
-- alle wijzigingen in de database terug met de queries helemaal onderaan.
-- ------------------------------------------------------------------------


-- S2.1. Vier-daagse cursussen
--
-- Geef code en omschrijving van alle cursussen die precies vier dagen duren.
-- DROP VIEW IF EXISTS s2_1; CREATE OR REPLACE VIEW s2_1 AS                                                     -- [TEST]

    SELECT code, omschrijving FROM cursussen WHERE lengte = 4;

-- S2.2. Medewerkersoverzicht
--
-- Geef alle informatie van alle medewerkers, gesorteerd op functie,
-- en per functie op leeftijd (van jong naar oud).
-- DROP VIEW IF EXISTS s2_2; CREATE OR REPLACE VIEW s2_2 AS                                                     -- [TEST]

    SELECT * FROM medewerkers ORDER BY functie, gbdatum DESC;

-- S2.3. Door het land
--
-- Welke cursussen zijn in Utrecht en/of in Maastricht uitgevoerd? Geef
-- code en begindatum.
-- DROP VIEW IF EXISTS s2_3; CREATE OR REPLACE VIEW s2_3 AS                                             -- [TEST]

    SELECT c.code, u.begindatum
    FROM cursussen c
    JOIN uitvoeringen u ON c.code = u.cursus
    WHERE LOWER(u.locatie) = 'utrecht' OR LOWER(u.locatie) = 'maastricht';

-- S2.4. Namen
--
-- Geef de naam en voorletters van alle medewerkers, behalve van R. Jansen.
-- DROP VIEW IF EXISTS s2_4; CREATE OR REPLACE VIEW s2_4 AS                                                     -- [TEST]

    SELECT naam, voorl FROM medewerkers WHERE NOT (naam = 'JANSEN' AND voorl = 'R');

-- S2.5. Nieuwe SQL-cursus
--
-- Er wordt een nieuwe uitvoering gepland voor cursus S02, en wel op de
-- komende 2 maart. De cursus wordt gegeven in Leerdam door Nick Smit.
-- Voeg deze gegevens toe.

    INSERT INTO uitvoeringen (cursus, locatie, begindatum, docent)
    VALUES (
            'S02',
            'LEERDAM',
            '2024-03-02',
            (SELECT mnr FROM medewerkers WHERE naam = 'SMIT' AND voorl = 'N')
           )
    ON CONFLICT DO NOTHING;                                                                                         -- [TEST]


-- S2.6. Stagiairs
--
-- Neem één van je collega-studenten aan als stagiair ('STAGIAIR') en
-- voer zijn of haar gegevens in. Kies een personeelnummer boven de 8000.

-- S2.6. Stagiairs

INSERT INTO medewerkers (mnr, naam, voorl, functie, chef, gbdatum, maandsal, comm, afd)
VALUES (
           8001,
           'HESSEN',
           'D',
           'STAGIAIR',
           (SELECT mnr FROM medewerkers WHERE functie = 'MANAGER' AND afd = 30 LIMIT 1),
           '2002-04-09',
           800,
           NULL,
           (SELECT anr FROM afdelingen WHERE naam = 'VERKOOP')
       )
ON CONFLICT DO NOTHING;



-- S2.7. Nieuwe schaal
--
-- We breiden het salarissysteem uit naar zes schalen. Voer een extra schaal in voor mensen die
-- tussen de 3001 en 4000 euro verdienen. Zij krijgen een toelage van 500 euro.

    INSERT INTO schalen (snr, ondergrens, bovengrens, toelage)
    VALUES (
            6,
            3001,
            4000,
            500
           )
    ON CONFLICT DO NOTHING;

-- S2.8. Nieuwe cursus
--
-- Er wordt een nieuwe 6-daagse cursus 'Data & Persistency' in het programma opgenomen.
-- Voeg deze cursus met code 'D&P' toe, maak twee uitvoeringen in Leerdam en schrijf drie
-- mensen in.

    -- voegt nieuwe cursus 'Data & Persistency' toe
    INSERT INTO cursussen (code, omschrijving, type, lengte)
    VALUES ('D&P', 'Data & Persistency', 'ALG', 6)
    ON CONFLICT DO NOTHING;

    -- voegt twee uitvoeringen van de cursus 'Data & Persistency' toe
    INSERT INTO uitvoeringen (cursus, locatie, begindatum)
    VALUES ('D&P', 'LEERDAM', '2024-09-25')
    ON CONFLICT DO NOTHING;

    INSERT INTO uitvoeringen (cursus, locatie, begindatum)
    VALUES ('D&P', 'LEERDAM', '2024-10-15')
    ON CONFLICT DO NOTHING;

    -- schrijft drie mensen in voor de cursus 'Data & Persistency'
INSERT INTO inschrijvingen (cursus, begindatum, cursist)
VALUES ('D&P', '2024-09-25', 7369)  -- 'SMIT', 'N'
ON CONFLICT DO NOTHING;

INSERT INTO inschrijvingen (cursus, begindatum, cursist)
VALUES ('D&P', '2024-09-25', 7521)  -- 'DE WAARD', 'TF'
ON CONFLICT DO NOTHING;

INSERT INTO inschrijvingen (cursus, begindatum, cursist)
VALUES ('D&P', '2024-09-25', 7566)  -- 'JANSEN', 'JM'
ON CONFLICT DO NOTHING;

-- S2.9. Salarisverhoging
--
-- De medewerkers van de afdeling VERKOOP krijgen een salarisverhoging
-- van 5.5%, behalve de manager van de afdeling, deze krijgt namelijk meer: 7%.
-- Voer deze verhogingen door.

    UPDATE medewerkers
    SET maandsal = maandsal * 1.055
    WHERE afd = (SELECT anr FROM afdelingen where naam = 'VERKOOP') AND functie != 'MANAGER';

    UPDATE medewerkers
    SET maandsal = maandsal * 1.07
    WHERE afd = (SELECT anr FROM afdelingen where naam = 'VERKOOP') AND functie = 'MANAGER';

-- S2.10. Concurrent
--
-- Martens heeft als verkoper succes en wordt door de concurrent
-- weggekocht. Verwijder zijn gegevens.

-- Zijn collega Alders heeft ook plannen om te vertrekken. Verwijder ook zijn gegevens.
-- Waarom lukt dit (niet)?

    DELETE FROM medewerkers WHERE naam = 'MARTENS' AND voorl = 'P';
    DELETE FROM medewerkers WHERE naam = 'ALDERS' AND voorl = 'J';

--  dit lukt niet omdat een foreign key contraint op de tabel medewerkers ervoor zorgt dat de gegevens van een medewerker
--  niet verwijderd kunnen worden als deze nog ergens voorkomt in een andere tabel


-- S2.11. Nieuwe afdeling
--
-- Je wordt hoofd van de nieuwe afdeling 'FINANCIEN' te Leerdam,
-- onder de hoede van De Koning. Kies een personeelnummer boven de 8000.
-- Zorg voor de juiste invoer van deze gegevens.

-- voegt nieuwe afdeling 'FINANCIN' toe
INSERT INTO afdelingen (anr, naam, locatie, hoofd)
VALUES (
           50,
           'FINANCIEN',
           'LEERDAM',
           NULL
       )
ON CONFLICT DO NOTHING;

-- voegt nieuwe medewerker 'KONING' toe als hoofd van de nieuwe afd
INSERT INTO medewerkers (mnr, naam, voorl, functie, chef, gbdatum, maandsal, comm, afd)
VALUES (
           8002,
           'KONING',
           'D',
           'MANAGER',
           7839,
           '2000-01-01',
           3000,
           500,
           50
       )
ON CONFLICT DO NOTHING;

UPDATE afdelingen
SET hoofd = 8002
WHERE anr = 50;


-- -------------------------[ HU TESTRAAMWERK ]--------------------------------
-- Met onderstaande query kun je je code testen. Zie bovenaan dit bestand
-- voor uitleg.

SELECT * FROM test_select('S2.1') AS resultaat
UNION
SELECT 'S2.2 wordt niet getest: geen test mogelijk.' AS resultaat
UNION
SELECT * FROM test_select('S2.3') AS resultaat
UNION
SELECT * FROM test_select('S2.4') AS resultaat
UNION
SELECT * FROM test_exists('S2.5', 1) AS resultaat
UNION
SELECT * FROM test_exists('S2.6', 1) AS resultaat
UNION
SELECT * FROM test_exists('S2.7', 6) AS resultaat
ORDER BY resultaat;


-- Draai alle wijzigingen terug om conflicten in komende opdrachten te voorkomen.
UPDATE medewerkers SET afd = NULL WHERE mnr < 7369 OR mnr > 7934;
UPDATE afdelingen SET hoofd = NULL WHERE anr > 40;
DELETE FROM afdelingen WHERE anr > 40;
DELETE FROM medewerkers WHERE mnr < 7369 OR mnr > 7934;
DELETE FROM inschrijvingen WHERE cursus = 'D&P';
DELETE FROM uitvoeringen WHERE cursus = 'D&P';
DELETE FROM cursussen WHERE code = 'D&P';
DELETE FROM uitvoeringen WHERE locatie = 'LEERDAM';
INSERT INTO medewerkers (mnr, naam, voorl, functie, chef, gbdatum, maandsal, comm, afd)
VALUES (7654, 'MARTENS', 'P', 'VERKOPER', 7698, '1976-09-28', 1250, 1400, 30);
UPDATE medewerkers SET maandsal = 1600 WHERE mnr = 7499;
UPDATE medewerkers SET maandsal = 1250 WHERE mnr = 7521;
UPDATE medewerkers SET maandsal = 2850 WHERE mnr = 7698;
UPDATE medewerkers SET maandsal = 1500 WHERE mnr = 7844;
UPDATE medewerkers SET maandsal = 800 WHERE mnr = 7900;


