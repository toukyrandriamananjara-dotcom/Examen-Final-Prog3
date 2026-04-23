-- =====================================================
-- ÉTAPE 2 : Table MEMBERS
-- Note : Pas de FK vers collectivities car collectivities n'existe pas encore
-- =====================================================

CREATE TABLE members (
                         id VARCHAR(50) PRIMARY KEY,
                         first_name VARCHAR(100) NOT NULL,
                         last_name VARCHAR(100) NOT NULL,
                         birth_date DATE NOT NULL,
                         gender gender_type NOT NULL,
                         address VARCHAR(255),
                         profession VARCHAR(100),
                         phone_number BIGINT,
                         email VARCHAR(100),
                         occupation member_occupation_type NOT NULL,
                         collectivity_id VARCHAR(50),  -- FK sera ajouté plus tard
                         membership_date DATE NOT NULL,
                         annual_contribution_amount BIGINT
);

-- =====================================================
-- ÉTAPE 3 : Table MEMBER_REFEREES (relation parrain/filleul)
-- =====================================================

CREATE TABLE member_referees (
                                 member_id VARCHAR(50) NOT NULL,
                                 referee_id VARCHAR(50) NOT NULL,
                                 relation_type relation_type_enum,
                                 PRIMARY KEY (member_id, referee_id),
                                 FOREIGN KEY (member_id) REFERENCES members(id),
                                 FOREIGN KEY (referee_id) REFERENCES members(id)
);

-- =====================================================
-- ÉTAPE 4 : Table COLLECTIVITIES
-- =====================================================

CREATE TABLE collectivities (
                                id VARCHAR(50) PRIMARY KEY,
                                number VARCHAR(10),
                                name VARCHAR(100),
                                location VARCHAR(100),
                                agricultural_specialty VARCHAR(100),
                                creation_date DATE NOT NULL,
                                federation_approval BOOLEAN DEFAULT FALSE,
                                annual_contribution_amount BIGINT,
                                president_id VARCHAR(50),
                                deputy_president_id VARCHAR(50),
                                treasurer_id VARCHAR(50),
                                secretary_id VARCHAR(50)
);