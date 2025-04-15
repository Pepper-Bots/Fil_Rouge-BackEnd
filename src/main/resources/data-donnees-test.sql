-- Insertion des valeurs possibles pour Etat_Dossier
INSERT INTO fil_rouge.etat_dossier (id, status)
VALUES (1, 'COMPLET'),
       (2, 'INCOMPLET'),
       (3, 'VALIDE'),
       (4, 'A_VALIDER');

-- Insertion de 20 dossiers d'exemple
INSERT INTO fil_rouge.dossier (id, etat_dossier_id, date_de_creation, date_de_modification, nom_stagiaire,
                               prenom_stagiaire,
                               nom_formation)
VALUES (1, 1, '2025-01-15', '2025-01-20', 'Dupont', 'Martin', 'Dossier stage marketing'),
       (2, 2, '2025-01-18', '2025-01-25', 'Dubois', 'Julie', 'Dossier stage comptabilité'),
       (3, 3, '2025-01-20', '2025-02-01', 'Martin', 'Sophie', 'Dossier stage développement web'),
       (4, 4, '2025-01-22', '2025-01-30', 'Bernard', 'Thomas', 'Dossier stage ressources humaines'),
       (5, 1, '2025-02-01', '2025-02-05', 'Petit', 'Lucas', 'Dossier stage commercial'),
       (6, 2, '2025-02-03', '2025-02-10', 'Robert', 'Emma', 'Dossier stage communication'),
       (7, 3, '2025-02-05', '2025-02-15', 'Richard', 'Hugo', 'Dossier stage design'),
       (8, 4, '2025-02-07', '2025-02-12', 'Moreau', 'Léa', 'Dossier stage logistique'),
       (9, 1, '2025-02-10', '2025-02-18', 'Simon', 'Clara', 'Dossier stage data science'),
       (10, 2, '2025-02-12', '2025-02-20', 'Laurent', 'Nathan', 'Dossier stage juridique'),
       (11, 3, '2025-02-15', '2025-02-25', 'Leroy', 'Camille', 'Dossier stage finance'),
       (12, 4, '2025-02-18', '2025-02-28', 'Michel', 'Inès', 'Dossier stage qualité'),
       (13, 1, '2025-03-01', '2025-03-05', 'Lefebvre', 'Théo', 'Dossier stage informatique'),
       (14, 2, '2025-03-03', '2025-03-10', 'Garcia', 'Chloé', 'Dossier stage achats'),
       (15, 3, '2025-03-05', '2025-03-15', 'David', 'Antoine', 'Dossier stage production'),
       (16, 4, '2025-03-07', '2025-03-12', 'Bertrand', 'Manon', 'Dossier stage audit'),
       (17, 1, '2025-03-10', '2025-03-18', 'Morel', 'Quentin', 'Dossier stage supply chain'),
       (18, 2, '2025-03-12', '2025-03-20', 'Fournier', 'Lucie', 'Dossier stage administration'),
       (19, 3, '2025-03-15', '2025-03-25', 'Girard', 'Maxime', 'Dossier stage export'),
       (20, 4, '2025-03-18', '2025-03-28', 'Vincent', 'Océane', 'Dossier stage recherche');