-- Insertion des valeurs possibles pour Etat_Dossier
# INSERT INTO fil_rouge.etat_dossier (id, status)
# VALUES (1, 'COMPLET'),
#        (2, 'INCOMPLET'),
#        (3, 'VALIDE'),
#        (4, 'A_VALIDER');

-- Insertion de 20 dossiers d'exemple
INSERT INTO fil_rouge.dossier (id, date_de_creation, date_de_modification, nom_stagiaire, prenom_stagiaire,
                               nom_formation)
VALUES (1, '2025-01-15', '2025-01-20', 'Dupont', 'Martin', 'Dossier stage marketing'),
       (2, '2025-01-18', '2025-01-25', 'Dubois', 'Julie', 'Dossier stage comptabilité'),
       (3, '2025-01-20', '2025-02-01', 'Martin', 'Sophie', 'Dossier stage développement web'),
       (4, '2025-01-22', '2025-01-30', 'Bernard', 'Thomas', 'Dossier stage ressources humaines'),
       (5, '2025-02-01', '2025-02-05', 'Petit', 'Lucas', 'Dossier stage commercial'),
       (6, '2025-02-03', '2025-02-10', 'Robert', 'Emma', 'Dossier stage communication'),
       (7, '2025-02-05', '2025-02-15', 'Richard', 'Hugo', 'Dossier stage design'),
       (8, '2025-02-07', '2025-02-12', 'Moreau', 'Léa', 'Dossier stage logistique'),
       (9, '2025-02-10', '2025-02-18', 'Simon', 'Clara', 'Dossier stage data science'),
       (10, '2025-02-12', '2025-02-20', 'Laurent', 'Nathan', 'Dossier stage juridique'),
       (11, '2025-02-15', '2025-02-25', 'Leroy', 'Camille', 'Dossier stage finance'),
       (12, '2025-02-18', '2025-02-28', 'Michel', 'Inès', 'Dossier stage qualité'),
       (13, '2025-03-01', '2025-03-05', 'Lefebvre', 'Théo', 'Dossier stage informatique'),
       (14, '2025-03-03', '2025-03-10', 'Garcia', 'Chloé', 'Dossier stage achats'),
       (15, '2025-03-05', '2025-03-15', 'David', 'Antoine', 'Dossier stage production'),
       (16, '2025-03-07', '2025-03-12', 'Bertrand', 'Manon', 'Dossier stage audit'),
       (17, '2025-03-10', '2025-03-18', 'Morel', 'Quentin', 'Dossier stage supply chain'),
       (18, '2025-03-12', '2025-03-20', 'Fournier', 'Lucie', 'Dossier stage administration'),
       (19, '2025-03-15', '2025-03-25', 'Girard', 'Maxime', 'Dossier stage export'),
       (20, '2025-03-18', '2025-03-28', 'Vincent', 'Océane', 'Dossier stage recherche');

-- Notifications pour les Admins
INSERT INTO notification (id, user_id, type, contenu)
VALUES (1, 101, 'INFORMATION',
        'Un nouveau document a été envoyé par un stagiaire. Veuillez vérifier sa validité et sa lisibilité.'),
       (2, 102, 'INFORMATION',
        'Un nouveau document a été envoyé par un stagiaire. Veuillez vérifier sa validité et sa lisibilité.'),
       (3, 103, 'INFORMATION',
        'Un nouveau document a été envoyé par un stagiaire. Veuillez vérifier sa validité et sa lisibilité.'),
       (4, 104, 'INFORMATION',
        'Un nouveau document a été envoyé par un stagiaire. Veuillez vérifier sa validité et sa lisibilité.'),
       (5, 105, 'INFORMATION',
        'Un nouveau document a été envoyé par un stagiaire. Veuillez vérifier sa validité et sa lisibilité.'),
       (6, 106, 'INFORMATION',
        'Un nouveau document a été envoyé par un stagiaire. Veuillez vérifier sa validité et sa lisibilité.'),
       (7, 107, 'INFORMATION',
        'Un nouveau document a été envoyé par un stagiaire. Veuillez vérifier sa validité et sa lisibilité.'),
       (8, 108, 'INFORMATION',
        'Un nouveau document a été envoyé par un stagiaire. Veuillez vérifier sa validité et sa lisibilité.');

-- Stagiaires 1 à 10 : avertissement retards/absences
INSERT INTO notification (id, user_id, type, contenu)
VALUES (9, 1, 'WARNING',
        'Vous avez atteint un seuil critique de retards ou d’absences. Veuillez justifier votre situation rapidement.'),
       (10, 2, 'WARNING',
        'Vous avez atteint un seuil critique de retards ou d’absences. Veuillez justifier votre situation rapidement.'),
       (11, 3, 'WARNING',
        'Vous avez atteint un seuil critique de retards ou d’absences. Veuillez justifier votre situation rapidement.'),
       (12, 4, 'WARNING',
        'Vous avez atteint un seuil critique de retards ou d’absences. Veuillez justifier votre situation rapidement.'),
       (13, 5, 'WARNING',
        'Vous avez atteint un seuil critique de retards ou d’absences. Veuillez justifier votre situation rapidement.'),
       (14, 6, 'WARNING',
        'Vous avez atteint un seuil critique de retards ou d’absences. Veuillez justifier votre situation rapidement.'),
       (15, 7, 'WARNING',
        'Vous avez atteint un seuil critique de retards ou d’absences. Veuillez justifier votre situation rapidement.'),
       (16, 8, 'WARNING',
        'Vous avez atteint un seuil critique de retards ou d’absences. Veuillez justifier votre situation rapidement.'),
       (17, 9, 'WARNING',
        'Vous avez atteint un seuil critique de retards ou d’absences. Veuillez justifier votre situation rapidement.'),
       (18, 10, 'WARNING',
        'Vous avez atteint un seuil critique de retards ou d’absences. Veuillez justifier votre situation rapidement.');

-- Stagiaires 11 à 15 : documents refusés
INSERT INTO notification (id, user_id, type, contenu)
VALUES (19, 11, 'WARNING',
        'Un ou plusieurs documents envoyés ont été refusés. Merci de les corriger et les renvoyer au plus vite.'),
       (20, 12, 'WARNING',
        'Un ou plusieurs documents envoyés ont été refusés. Merci de les corriger et les renvoyer au plus vite.'),
       (21, 13, 'WARNING',
        'Un ou plusieurs documents envoyés ont été refusés. Merci de les corriger et les renvoyer au plus vite.'),
       (22, 14, 'WARNING',
        'Un ou plusieurs documents envoyés ont été refusés. Merci de les corriger et les renvoyer au plus vite.'),
       (23, 15, 'WARNING',
        'Un ou plusieurs documents envoyés ont été refusés. Merci de les corriger et les renvoyer au plus vite.');

-- Stagiaires 16 à 20 : dossier incomplet
INSERT INTO notification (id, user_id, type, contenu)
VALUES (24, 16, 'RAPPEL', 'Votre dossier d’inscription est incomplet. Merci de fournir les documents manquants.'),
       (25, 17, 'RAPPEL', 'Votre dossier d’inscription est incomplet. Merci de fournir les documents manquants.'),
       (26, 18, 'RAPPEL', 'Votre dossier d’inscription est incomplet. Merci de fournir les documents manquants.'),
       (27, 19, 'RAPPEL', 'Votre dossier d’inscription est incomplet. Merci de fournir les documents manquants.'),
       (28, 20, 'RAPPEL', 'Votre dossier d’inscription est incomplet. Merci de fournir les documents manquants.');

INSERT INTO statut_document (id, nom)
VALUES (1, ENVOYE),
       (2, VALIDE),
       (3, REFUSE),
       (4, MANQUANT);
