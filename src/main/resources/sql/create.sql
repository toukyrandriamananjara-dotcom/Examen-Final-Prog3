-- Genre
CREATE TYPE gender_type AS ENUM ('MALE', 'FEMALE');

-- Occupation des membres
CREATE TYPE member_occupation_type AS ENUM ('JUNIOR', 'CONFIRMED', 'SECRETARY', 'TREASURER', 'DEPUTY_PRESIDENT', 'PRESIDENT');

-- Type de compte financier
CREATE TYPE account_type_enum AS ENUM ('CASH', 'MOBILE_BANKING', 'BANK_TRANSFER');

-- Service mobile money
CREATE TYPE mobile_banking_service_type AS ENUM ('ORANGE_MONEY', 'MVOLA', 'AIRTEL_MONEY');

-- Nom des banques
CREATE TYPE bank_name_type AS ENUM ('BRED', 'MCB', 'BMOI', 'BOA', 'BGFI', 'AFG', 'ACCES_BANQUE', 'BAOBAB', 'SIPEM');

-- Fréquence des cotisations
CREATE TYPE frequency_type AS ENUM ('WEEKLY', 'MONTHLY', 'ANNUALLY', 'PUNCTUALLY');

-- Statut d'activité
CREATE TYPE status_type AS ENUM ('ACTIVE', 'INACTIVE');

-- Mode de paiement
CREATE TYPE payment_mode_type AS ENUM ('CASH', 'MOBILE_BANKING', 'BANK_TRANSFER');

-- Type de relation (pour les parrains)
CREATE TYPE relation_type_enum AS ENUM ('FAMILY', 'FRIEND', 'COLLEAGUE', 'OTHER');

