-- ------------------------------------------------------------------------
-- Data & Persistency
-- Opdracht S6: Views
--
-- (c) 2020 Hogeschool Utrecht
-- Tijmen Muller (tijmen.muller@hu.main.nl)
-- André Donk (andre.donk@hu.main.nl)
-- ------------------------------------------------------------------------


-- S6.1.
--
-- 1. Maak een view met de naam "deelnemers" waarmee je de volgende gegevens uit de tabellen inschrijvingen en uitvoering combineert:
--    inschrijvingen.cursist, inschrijvingen.cursus, inschrijvingen.begindatum, uitvoeringen.docent, uitvoeringen.locatie

    CREATE OR REPLACE VIEW deelnemers AS
        SELECT i.cursist, i.cursus, i.begindatum, u.docent, u.locatie
        FROM inschrijvingen i
        JOIN uitvoeringen u ON i.cursus = u.cursus;

-- 2. Gebruik de view in een query waarbij je de "deelnemers" view combineert met de "personeels" view (behandeld in de les):

    CREATE OR REPLACE VIEW personeel AS
	    SELECT mnr, voorl, naam as medewerker, afd, functie
        FROM medewerkers;

    CREATE OR REPLACE VIEW deelnemers_personeel AS
        SELECT d.cursist, d.cursus, d.begindatum, d.docent, d.locatie, p.medewerker, p.afd, p.functie
        FROM deelnemers d
        JOIN personeel p ON d.docent = p.mnr;

-- 3. Is de view "deelnemers" updatable ? Waarom ?
-- Ja, de view deelnemers is updatable. deelnemers is namelijk een join van twee tabellen (inschrijvingen en uitvoeringen) die beide updatable zijn.


-- S6.2.
--
-- 1. Maak een view met de naam "dagcursussen". Deze view dient de gegevens op te halen: 
--      code, omschrijving en type uit de tabel curssussen met als voorwaarde dat de lengte = 1. Toon aan dat de view werkt.

    CREATE OR REPLACE VIEW dagcursussen AS
        SELECT code, omschrijving, type FROM cursussen WHERE LENGTH(type) = 1;

-- 2. Maak een tweede view met de naam "daguitvoeringen".

    CREATE OR REPLACE VIEW daguitvoeringen AS
        SELECT d.code, d.omschrijving, d.type, u.begindatum, u.docent, u.locatie
        FROM dagcursussen d
        JOIN uitvoeringen u ON d.code = u.cursus;

--    Deze view dient de uitvoeringsgegevens op te halen voor de "dagcurssussen" (gebruik ook de view "dagcursussen"). Toon aan dat de view werkt
-- 3. Verwijder de views en laat zien wat de verschillen zijn bij DROP view <viewnaam> CASCADE en bij DROP view <viewnaam> RESTRICT

    DROP VIEW daguitvoeringen CASCADE;
-- bij cascade worden alle views die afhankelijk zijn van 'daguitvoeriingen' ook verwijderd

    DROP VIEW dagcursussen RESTRICT;
-- bij restrict wordt de view niet verwijderd, omdat 'daguitvoeringen' afhankelijk is van 'dagcursussen'

