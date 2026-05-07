-- =============================================
-- 1. CRÉATION DES TABLES + RELATIONS
-- =============================================

-- Table des Collectivités
CREATE TABLE collectivites (
                               id VARCHAR(20) PRIMARY KEY,
                               numero INT NOT NULL,
                               nom VARCHAR(100) NOT NULL,
                               localite VARCHAR(100),
                               specialisation VARCHAR(100)
);

-- Table des Membres
CREATE TABLE membres (
                         id VARCHAR(20) PRIMARY KEY,
                         collectivite_id VARCHAR(20) NOT NULL,
                         nom VARCHAR(100) NOT NULL,
                         prenom VARCHAR(100) NOT NULL,
                         date_naissance DATE,
                         genre CHAR(1),
                         adresse TEXT,
                         profession VARCHAR(100),
                         telephone VARCHAR(20),
                         email VARCHAR(100),
                         occupation VARCHAR(100),
                         id_membres_referents TEXT,

                         CONSTRAINT fk_membre_collectivite
                             FOREIGN KEY (collectivite_id) REFERENCES collectivites(id) ON DELETE CASCADE
);

-- Table des Cotisations
CREATE TABLE cotisations (
                             id VARCHAR(20) PRIMARY KEY,
                             collectivite_id VARCHAR(20) NOT NULL,
                             label VARCHAR(100),
                             statut VARCHAR(50),
                             frequence VARCHAR(50),
                             eligible_depuis DATE,
                             montant NUMERIC(15,2) NOT NULL,

                             CONSTRAINT fk_cotisation_collectivite
                                 FOREIGN KEY (collectivite_id) REFERENCES collectivites(id) ON DELETE CASCADE
);

-- Table des Comptes
CREATE TABLE comptes (
                         id VARCHAR(30) PRIMARY KEY,
                         collectivite_id VARCHAR(20) NOT NULL,
                         type_compte VARCHAR(50),
                         montant_initial NUMERIC(15,2) DEFAULT 0,
                         titulaire VARCHAR(100),
                         numero_telephone VARCHAR(20),

                         CONSTRAINT fk_compte_collectivite
                             FOREIGN KEY (collectivite_id) REFERENCES collectivites(id) ON DELETE CASCADE
);

-- Table des Transactions / Paiements
CREATE TABLE transactions (
                              id SERIAL PRIMARY KEY,
                              collectivite_id VARCHAR(20) NOT NULL,
                              membre_id VARCHAR(20) NOT NULL,
                              montant NUMERIC(15,2) NOT NULL,
                              compte_credite VARCHAR(30),
                              moyen_paiement VARCHAR(50),
                              date_paiement DATE,
                              date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              type_operation VARCHAR(20) DEFAULT 'PAIEMENT',

                              CONSTRAINT fk_transaction_collectivite
                                  FOREIGN KEY (collectivite_id) REFERENCES collectivites(id) ON DELETE CASCADE,

                              CONSTRAINT fk_transaction_membre
                                  FOREIGN KEY (membre_id) REFERENCES membres(id) ON DELETE RESTRICT,

                              CONSTRAINT fk_transaction_compte
                                  FOREIGN KEY (compte_credite) REFERENCES comptes(id) ON DELETE SET NULL
);

-- =============================================
-- 2. INSERTION DES DONNÉES
-- =============================================

-- 2.1 Collectivités
INSERT INTO collectivites (id, numero, nom, localite, specialisation) VALUES
                                                                          ('col-1', 1, 'Mpanorina', 'Ambatondrazaka', 'Riziculture'),
                                                                          ('col-2', 2, 'Dobo voalohany', 'Ambatondrazaka', 'Pisciculture'),
                                                                          ('col-3', 3, 'Tantely mamy', 'Brickville', 'Apiculture');

-- 2.2 Cotisations
INSERT INTO cotisations (id, collectivite_id, label, statut, frequence, eligible_depuis, montant) VALUES
                                                                                                      ('cot-1', 'col-1', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01', 100000),
                                                                                                      ('cot-2', 'col-2', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01', 100000),
                                                                                                      ('cot-3', 'col-3', 'Cotisation annuelle', 'ACTIVE', 'ANNUALLY', '2026-01-01', 50000);

-- 2.3 Comptes
INSERT INTO comptes (id, collectivite_id, type_compte, montant_initial, titulaire, numero_telephone) VALUES
                                                                                                         ('C1-A-CASH', 'col-1', 'CASH', 0, NULL, NULL),
                                                                                                         ('C1-A-MOBILE-1', 'col-1', 'ORANGE_MONEY', 0, 'Mpanorina', '0370489612'),
                                                                                                         ('C2-A-CASH', 'col-2', 'CASH', 0, NULL, NULL),
                                                                                                         ('C2-A-MOBILE-1', 'col-2', 'ORANGE_MONEY', 0, 'Dobo voalohany', '0320489612'),
                                                                                                         ('C3-A-CASH', 'col-3', 'CASH', 0, NULL, NULL);

-- 2.4 Membres Collectivité 1
INSERT INTO membres (id, collectivite_id, nom, prenom, date_naissance, genre, adresse, profession, telephone, email, occupation, id_membres_referents) VALUES
                                                                                                                                                           ('C1-M1', 'col-1', 'Nom membre 1', 'Prénom membre 1', '1980-02-01', 'M', 'Lot II V M Ambatondrazaka', 'Riziculteur', '0341234567', 'member.1@f ed-agri.mg', 'Président', 'Aucun'),
                                                                                                                                                           ('C1-M2', 'col-1', 'Nom membre 2', 'Prénom membre 2', '1982-03-05', 'M', 'Lot II F Ambatondrazaka', 'Agriculteur', '0321234567', 'member.2@f ed-agri.mg', 'Vice président', 'Aucun'),
                                                                                                                                                           ('C1-M3', 'col-1', 'Nom membre 3', 'Prénom membre 3', '1992-03-10', 'M', 'Lot II J Ambatondrazaka', 'Collecteur', '0331234567', 'member.3@f ed-agri.mg', 'Secrétaire', 'C1-M1; C1-M2'),
                                                                                                                                                           ('C1-M4', 'col-1', 'Nom membre 4', 'Prénom membre 4', '1988-05-22', 'F', 'Lot A K 50 Ambatondrazaka', 'Distributeur', '0381234567', 'member.4@f ed-agri.mg', 'Trésorier', 'C1-M1; C1-M2'),
                                                                                                                                                           ('C1-M5', 'col-1', 'Nom membre 5', 'Prénom membre 5', '1999-08-21', 'M', 'Lot UV 80 Ambatondrazaka', 'Riziculteur', '0373434567', 'member.5@f ed-agri.mg', 'Confirmé', 'C1-M1; C1-M2'),
                                                                                                                                                           ('C1-M6', 'col-1', 'Nom membre 6', 'Prénom membre 6', '1998-08-22', 'F', 'Lot UV 6 Ambatondrazaka', 'Riziculteur', '0372234567', 'member.6@f ed-agri.mg', 'Confirmé', 'C1-M1; C1-M2'),
                                                                                                                                                           ('C1-M7', 'col-1', 'Nom membre 7', 'Prénom membre 7', '1998-01-31', 'M', 'Lot UV 7 Ambatondrazaka', 'Riziculteur', '0374234567', 'member.7@f ed-agri.mg', 'Confirmé', 'C1-M1; C1-M2'),
                                                                                                                                                           ('C1-M8', 'col-1', 'Nom membre 8', 'Prénom membre 6', '1975-08-20', 'M', 'Lot UV 8 Ambatondrazaka', 'Riziculteur', '0370234567', 'member.8@f ed-agri.mg', 'Confirmé', 'C1-M6; C1-M7');

-- 2.5 Membres Collectivité 2
INSERT INTO membres (id, collectivite_id, nom, prenom, date_naissance, genre, adresse, profession, telephone, email, occupation, id_membres_referents) VALUES
                                                                                                                                                           ('C2-M1', 'col-2', 'Nom membre 1', 'Prénom membre 1', '1980-02-01', 'M', 'Lot II V M Ambatondrazaka', 'Riziculteur', '0341234567', 'member.1@f ed-agri.mg', 'Confirmé', 'Aucun'),
                                                                                                                                                           ('C2-M2', 'col-2', 'Nom membre 2', 'Prénom membre 2', '1982-03-05', 'M', 'Lot II F Ambatondrazaka', 'Agriculteur', '0321234567', 'member.2@f ed-agri.mg', 'Confirmé', 'Aucun'),
                                                                                                                                                           ('C2-M3', 'col-2', 'Nom membre 3', 'Prénom membre 3', '1992-03-10', 'M', 'Lot II J Ambatondrazaka', 'Collecteur', '0331234567', 'member.3@f ed-agri.mg', 'Confirmé', 'C1-M1; C1-M2'),
                                                                                                                                                           ('C2-M4', 'col-2', 'Nom membre 4', 'Prénom membre 4', '1988-05-22', 'F', 'Lot A K 50 Ambatondrazaka', 'Distributeur', '0381234567', 'member.4@f ed-agri.mg', 'Confirmé', 'C1-M1; C1-M2'),
                                                                                                                                                           ('C2-M5', 'col-2', 'Nom membre 5', 'Prénom membre 5', '1999-08-21', 'M', 'Lot UV 80 Ambatondrazaka', 'Riziculteur', '0373434567', 'member.5@f ed-agri.mg', 'Président', 'C1-M1; C1-M2'),
                                                                                                                                                           ('C2-M6', 'col-2', 'Nom membre 6', 'Prénom membre 6', '1998-08-22', 'F', 'Lot UV 6 Ambatondrazaka', 'Riziculteur', '0372234567', 'member.6@f ed-agri.mg', 'Vice président', 'C1-M1; C1-M2'),
                                                                                                                                                           ('C2-M7', 'col-2', 'Nom membre 7', 'Prénom membre 7', '1998-01-31', 'M', 'Lot UV 7 Ambatondrazaka', 'Riziculteur', '0374234567', 'member.7@f ed-agri.mg', 'Secrétaire', 'C1-M1; C1-M2'),
                                                                                                                                                           ('C2-M8', 'col-2', 'Nom membre 8', 'Prénom membre 6', '1975-08-20', 'M', 'Lot UV 8 Ambatondrazaka', 'Riziculteur', '0370234567', 'member.8@f ed-agri.mg', 'Trésorier', 'C1-M6; C1-M7');

-- 2.6 Membres Collectivité 3
INSERT INTO membres (id, collectivite_id, nom, prenom, date_naissance, genre, adresse, profession, telephone, email, occupation, id_membres_referents) VALUES
                                                                                                                                                           ('C3-M1', 'col-3', 'Nom membre 9', 'Prénom membre 9', '1988-01-02', 'M', 'Lot 33 J Antsirabe', 'Apiculteur', '034034567', 'member.9@f ed-agri.mg', 'Président', 'C1-M1; C1-M2'),
                                                                                                                                                           ('C3-M2', 'col-3', 'Nom membre 10', 'Prénom membre 10', '1982-03-05', 'M', 'Lot 2 J Antsirabe', 'Agriculteur', '0338634567', 'member.10@f ed-agri.mg', 'Vice président', 'C1-M1; C1-M2'),
                                                                                                                                                           ('C3-M3', 'col-3', 'Nom membre 11', 'Prénom membre 11', '1992-03-12', 'M', 'Lot 8 KM Antsirabe', 'Collecteur', '0338234567', 'member.11@f ed-agri.mg', 'Secrétaire', 'C3-M1; C3-M2'),
                                                                                                                                                           ('C3-M4', 'col-3', 'Nom membre 12', 'Prénom membre 12', '1988-05-10', 'F', 'Lot A K 50 Antsirabe', 'Distributeur', '0383234567', 'member.12@f ed-agri.mg', 'Trésorier', 'C3-M1; C3-M2'),
                                                                                                                                                           ('C3-M5', 'col-3', 'Nom membre 13', 'Prénom membre 13', '1999-11-08', 'M', 'Lot UV 80 Antsirabe', 'Apiculteur', '0373365567', 'member.13@f ed-agri.mg', 'Confirmé', 'C3-M1; C3-M2'),
                                                                                                                                                           ('C3-M6', 'col-3', 'Nom membre 14', 'Prénom membre 14', '1998-08-09', 'F', 'Lot UV 6 Antsirabe', 'Apiculteur', '0378234567', 'member.14@f ed-agri.mg', 'Confirmé', 'C3-M1; C3-M2'),
                                                                                                                                                           ('C3-M7', 'col-3', 'Nom membre 15', 'Prénom membre 15', '1998-01-13', 'M', 'Lot UV 7 Antsirabe', 'Apiculteur', '0374914567', 'member.15@f ed-agri.mg', 'Confirmé', 'C3-M1; C3-M2'),
                                                                                                                                                           ('C3-M8', 'col-3', 'Nom membre 16', 'Prénom membre 16', '1975-02-08', 'M', 'Lot UV 8 Antsirabe', 'Apiculteur', '0370634567', 'member.16@f ed-agri.mg', 'Confirmé', 'C3-M1; C3-M2');

-- 2.7 Transactions
INSERT INTO transactions (collectivite_id, membre_id, montant, compte_credite, moyen_paiement, date_paiement) VALUES
-- Collectivité 1
('col-1', 'C1-M1', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('col-1', 'C1-M2', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('col-1', 'C1-M3', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('col-1', 'C1-M4', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('col-1', 'C1-M5', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('col-1', 'C1-M6', 100000, 'C1-A-CASH', 'CASH', '2026-01-01'),
('col-1', 'C1-M7', 60000,  'C1-A-CASH', 'CASH', '2026-01-01'),
('col-1', 'C1-M8', 90000,  'C1-A-CASH', 'CASH', '2026-01-01'),
-- Collectivité 2
('col-2', 'C2-M1', 60000, 'C2-A-CASH', 'CASH', '2026-01-01'),
('col-2', 'C2-M2', 90000, 'C2-A-CASH', 'CASH', '2026-01-01'),
('col-2', 'C2-M3', 100000,'C2-A-CASH', 'CASH', '2026-01-01'),
('col-2', 'C2-M4', 100000,'C2-A-CASH', 'CASH', '2026-01-01'),
('col-2', 'C2-M5', 100000,'C2-A-CASH', 'CASH', '2026-01-01'),
('col-2', 'C2-M6', 100000,'C2-A-CASH', 'CASH', '2026-01-01'),
('col-2', 'C2-M7', 40000, 'C2-A-MOBILE-1', 'MOBILE MONEY', '2026-01-01'),
('col-2', 'C2-M8', 60000, 'C2-A-MOBILE-1', 'MOBILE MONEY', '2026-01-01');

-- =============================================
-- 3. INDEX
-- =============================================
CREATE INDEX idx_membres_collectivite ON membres(collectivite_id);
CREATE INDEX idx_transactions_membre ON transactions(membre_id);
CREATE INDEX idx_transactions_collectivite ON transactions(collectivite_id);
CREATE INDEX idx_transactions_date ON transactions(date_paiement);

-- =============================================
-- Vérification finale
-- =============================================
SELECT 'Collectivités' AS table_name, COUNT(*) FROM collectivites
UNION ALL
SELECT 'Membres', COUNT(*) FROM membres
UNION ALL
SELECT 'Cotisations', COUNT(*) FROM cotisations
UNION ALL
SELECT 'Comptes', COUNT(*) FROM comptes
UNION ALL
SELECT 'Transactions', COUNT(*) FROM transactions;