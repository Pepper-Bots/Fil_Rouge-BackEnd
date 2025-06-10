-- 14. Insertions exemples (statuts)
INSERT INTO statut_document (nom)
VALUES ('EN_ATTENTE'),
       ('VALIDÉ'),
       ('REJETÉ'),
       ('MANQUANT');

INSERT INTO statut_dossier (nom_statut)
VALUES ('INCOMPLET'),
       ('EN_COURS'),
       ('COMPLET'),
       ('VALIDÉ');


-- Étape 1 : insérer les utilisateurs de base dans la table USER
INSERT INTO `user` (last_name,
                    first_name,
                    email,
                    password,
                    enabled,
                    jeton_verification_email,
                    nom_role,
                    reset_password_token)
VALUES
-- 1. Administrateurs
('Dupont', 'Alice', 'alice@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE,
 'token-verif-123', 'ADMINISTRATEUR', NULL),
('Durand', 'Bruno', 'bruno@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE, NULL,
 'ADMINISTRATEUR', NULL),
('Martin', 'Cécile', 'cecile@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE, NULL,
 'ADMINISTRATEUR', NULL),
('Bernard', 'David', 'david@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE, NULL,
 'ADMINISTRATEUR', NULL),

-- 2. Stagiaires
('Dupont', 'Romain', 'romain_dupont@live.fr', '$2a$10$Dow1Kt9EdIVVQ8KQfBGoH.NkbZoCPoEdWkqITpCTBLuRFK5kZzCO2', TRUE,
 NULL, 'STAGIAIRE', NULL),
('Martin', 'Julie', 'julie.martin@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE,
 NULL, 'STAGIAIRE', NULL),
('Legrand', 'Paul', 'paul.legrand@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE,
 NULL, 'STAGIAIRE', NULL),
('Durand', 'Sophie', 'sophie.durand@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE,
 NULL, 'STAGIAIRE', NULL),
('Petit', 'Lucas', 'lucas.petit@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE,
 NULL, 'STAGIAIRE', NULL),
('Moreau', 'Camille', 'camille.moreau@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG',
 TRUE, NULL, 'STAGIAIRE', NULL),
('Fournier', 'Léo', 'leo.fournier@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE,
 NULL, 'STAGIAIRE', NULL),
('Garnier', 'Emma', 'emma.garnier@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE,
 NULL, 'STAGIAIRE', NULL),
('Henry', 'Maxime', 'maxime.henry@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE,
 NULL, 'STAGIAIRE', NULL),
('Roux', 'Manon', 'manon.roux@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE, NULL,
 'STAGIAIRE', NULL),
('Guerin', 'Tom', 'tom.guerin@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE, NULL,
 'STAGIAIRE', NULL),
('Schmitt', 'Chloé', 'chloe.schmitt@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE,
 NULL, 'STAGIAIRE', NULL),
('Robert', 'Nathan', 'nathan.robert@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE,
 NULL, 'STAGIAIRE', NULL),
('Lemoine', 'Élise', 'elise.lemoine@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE,
 NULL, 'STAGIAIRE', NULL),
('Blanc', 'Axel', 'axel.blanc@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE, NULL,
 'STAGIAIRE', NULL),
('Chevalier', 'Laura', 'laura.chevalier@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG',
 TRUE, NULL, 'STAGIAIRE', NULL),
('Faure', 'Noah', 'noah.faure@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE, NULL,
 'STAGIAIRE', NULL),
('André', 'Sarah', 'sarah.andre@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE,
 NULL, 'STAGIAIRE', NULL),
('Renaud', 'Julien', 'julien.renaud@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE,
 NULL, 'STAGIAIRE', NULL),
('Collet', 'Anaïs', 'anais.collet@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE,
 NULL, 'STAGIAIRE', NULL),

-- 3. Utilisateurs de test génériques
('Test1', 'User1', 'test1@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE, NULL,
 'STAGIAIRE', NULL),
('Test2', 'User2', 'test2@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE, NULL,
 'STAGIAIRE', NULL),
('Test3', 'User3', 'test3@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE, NULL,
 'STAGIAIRE', NULL),
('Test4', 'User4', 'test4@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE, NULL,
 'STAGIAIRE', NULL),
('Test5', 'User5', 'test5@example.com', '$2y$10$Yec37M1taxQ3TvvKGraTh.8Y4ME1PjcTR1YGMefoUNBebj0RBoPTG', TRUE, NULL,
 'STAGIAIRE', NULL);


-- Étape 2 : insérer dans ADMIN (en réutilisant les IDs ci-dessus)
INSERT INTO admin (id, type_admin, niveau_droit)
VALUES (1, 'RESPONSABLE_ETABLISSEMENT', 'SUPER_ADMIN'),
       (2, 'RESPONSABLE_FORMATION', 'ADMIN'),
       (3, 'ASSISTANT_VIE_SCOLAIRE', 'MODERATEUR'),
       (4, 'ASSISTANT_ADMINISTRATIF', 'BASIQUE');


INSERT INTO region (id_region, nom_region, nom_pays)
VALUES ('ARA', 'Auvergne-Rhône-Alpes', 'France'),
       ('IDF', 'Île-de-France', 'France'),
       ('PAC', 'Provence-Alpes-Côte d\'Azur', 'France');

-- Insertion des villes avec des IDs spécifiques (pour l'exemple)
INSERT INTO ville (id_ville, id_region, code_postal, nom_ville)
VALUES (1, 'ARA', '69001', 'Lyon'),
       (2, 'ARA', '69100', 'Villeurbanne'),
       (3, 'ARA', '69500', 'Bron'),
       (4, 'ARA', '69600', 'Oullins'),
       (5, 'ARA', '69300', 'Caluire-et-Cuire'),
       (6, 'ARA', '69200', 'Vénissieux'),
       (7, 'ARA', '69400', 'Villefranche-sur-Saône'),
       (8, 'ARA', '38700', 'La Tronche'),
       (9, 'ARA', '38100', 'Grenoble'),
       (10, 'ARA', '73000', 'Chambéry'),
       (11, 'ARA', '26000', 'Valence'),

       -- Villes hors ARA
       (12, 'IDF', '75000', 'Paris'), -- Île-de-France
       (13, 'PAC', '13000', 'Marseille');
-- Provence-Alpes-Côte d'Azur

-- Insertion dans la table stagiaire en utilisant ville_id
INSERT INTO stagiaire (id, premiere_connexion, date_naissance, phone_number, adresse, ville_id, photo_profil)
VALUES (5, true, '1990-03-01', '0660606060', '12 rue des Rosses', 1, 'default-profile.png'),
       (6, true, '1985-07-14', '0678451223', '8 avenue des Lilas', 2, null),
       (7, true, '1992-11-22', '0654239876', '34 boulevard Victor Hugo', 3, null),
       (8, true, '1980-06-30', '0612789543', '21 rue des Vignes', 4, null),
       (9, true, '1978-04-19', '0634561287', '5 impasse des Fleurs', 5, null),
       (10, true, '1991-01-08', '0645129384', '17 rue des Jardins', 6, null),
       (11, true, '1983-09-12', '0625897412', '98 rue de la République', 7, null),
       (12, true, '1994-10-02', '0678932154', '66 avenue des Champs', 8, null),
       (13, true, '1987-05-06', '0612347895', '43 rue Jean Jaurès', 9, null),
       (14, true, '1995-12-29', '0698123475', '7 place du Marché', 10, null),
       (15, true, '1986-03-17', '0665432187', '28 rue des Cerisiers', 11, null),
       (16, true, '1988-07-30', '0698342150', '55 avenue de Paris', 1, null),
       (17, true, '1984-11-09', '0655012374', '72 rue des Prés', 2, null),
       (18, true, '1996-04-04', '0689234578', '16 rue Pasteur', 3, null),
       (19, true, '1999-01-01', '0678123999', '91 boulevard Saint-Michel', 4, null),
       (20, true, '1989-12-12', '0634567890', '2 avenue du Général Leclerc', 5, null),
       (21, true, '1992-05-03', '0708090103', '28, rue des Bleuets', 6, null),
       (22, true, '1982-08-24', '0654891234', '13 allée des Marronniers', 7, null),
       (23, true, '1993-02-11', '0689741235', '10 rue Voltaire', 8, null),
       (24, true, '1981-06-05', '0623789120', '3 rue Lamartine', 9, null),
       (25, true, '1990-09-23', '0678123490', '4 place de la Mairie', 10, null),
       (26, true, '1993-06-15', '0611122233', '15 rue Lafayette', 11, null),
       (27, true, '1995-01-20', '0666677788', '1 avenue des Champs', 12, null),-- Paris (IDF)
       (28, true, '1987-03-11', '0655566677', '24 rue Paradis', 13, null),-- Marseille (PAC)
       (29, true, '1990-11-30', '0644433322', '12 boulevard Haussmann', 12, null);
-- Paris (IDF)


-- 6. FORMATION
INSERT INTO formation (id_formation, nom, niveau, description, date_debut, date_fin)
VALUES (1, 'Développement Web Front-End', 'A', 'Apprendre HTML, CSS, JavaScript et React.', '2025-06-01', '2025-08-30'),
       (2, 'Développement Web Back-End', 'A', 'Apprentissage de Node.js, Express et bases de données.', '2025-07-01',
        '2025-09-30'),
       (3, 'Full Stack Web', 'B', 'Formation complète front-end et back-end avec projets pratiques.', '2025-06-15',
        '2025-10-15'),
       (4, 'Sécurité Réseaux', 'B', 'Introduction à la sécurité des réseaux informatiques.', '2025-05-20',
        '2025-07-20'),
       (5, 'Pentesting - Tests d''intrusion', 'B', 'Découverte des techniques d''intrusion et d''audit.', '2025-06-10',
        '2025-09-10'),
       (6, 'Développement Web avec Java Spring', 'C', 'Conception d''applications web sécurisées avec Spring Boot.',
        '2025-07-05', '2025-10-05'),
       (7, 'Cyberdéfense et SOC', 'C', 'Mise en place d''un centre opérationnel de sécurité.', '2025-08-01',
        '2025-10-31'),
       (8, 'Développement Web avec PHP et Laravel', 'A', 'Projet web avec PHP, MySQL et le framework Laravel.',
        '2025-05-01', '2025-07-31'),
       (9, 'Sécurité des Applications Web', 'C', 'Protection des applis web contre les vulnérabilités courantes.',
        '2025-07-10', '2025-09-20'),
       (10, 'Initiation à la cybersécurité', 'A', 'Panorama des menaces et bonnes pratiques en entreprise.',
        '2025-06-05', '2025-08-05');


-- 10. DOSSIER
INSERT INTO dossier (id_dossier, code_dossier, statut_dossier_id, date_de_creation, derniere_mise_a_jour,
                     date_modification,
                     stagiaire_id, formation_id, createur_id)
VALUES (1, 'DSR001', 2, '2025-01-05 09:00:00', '2025-01-07 14:00:00', '2025-01-07 14:00:00', 5, 1, 1),   -- Java avancé
       (2, 'DSR002', 1, '2025-01-08 10:15:00', '2025-01-09 16:30:00', '2025-01-07 14:00:00', 6, 2, 2),-- Développement Web
       (3, 'DSR003', 3, '2025-01-12 11:00:00', '2025-01-13 15:45:00', '2025-01-07 14:00:00', 7, 3, 3),-- Data Science
       (4, 'DSR004', 1, '2025-01-15 08:45:00', '2025-01-17 14:20:00', '2025-01-07 14:00:00', 8, 4, 4),-- UI/UX Design
       (5, 'DSR005', 2, '2025-01-20 13:30:00', '2025-01-22 17:10:00', '2025-01-07 14:00:00', 9, 5, 1),-- Cybersécurité
       (6, 'DSR006', 3, '2025-01-25 09:45:00', '2025-01-26 10:00:00', '2025-01-07 14:00:00', 10, 6, 2),-- DevOps
       (7, 'DSR007', 1, '2025-02-01 14:10:00', '2025-02-03 18:20:00', '2025-01-07 14:00:00', 11, 1, 3),-- Java avancé
       (8, 'DSR008', 2, '2025-02-05 15:30:00', '2025-02-06 16:40:00', '2025-01-07 14:00:00', 12, 2, 4),-- Développement Web
       (9, 'DSR009', 3, '2025-02-10 10:00:00', '2025-02-12 12:00:00', '2025-01-07 14:00:00', 13, 3, 1),-- Data Science
       (10, 'DSR010', 2, '2025-02-14 11:30:00', '2025-02-15 14:00:00', '2025-01-07 14:00:00', 14, 4, 2),-- UI/UX Design
       (11, 'DSR011', 1, '2025-02-18 08:00:00', '2025-02-19 09:30:00', '2025-01-07 14:00:00', 15, 5, 3),-- Cybersécurité
       (12, 'DSR012', 3, '2025-02-22 13:20:00', '2025-02-23 14:45:00', '2025-01-07 14:00:00', 16, 6, 4), -- DevOps
       (13, 'DSR013', 1, '2025-03-01 10:00:00', '2025-03-02 11:15:00', '2025-01-07 14:00:00', 17, 1, 1),-- Java avancé
       (14, 'DSR014', 2, '2025-03-05 12:10:00', '2025-03-06 13:40:00', '2025-01-07 14:00:00', 18, 2, 2),-- Développement Web
       (15, 'DSR015', 3, '2025-03-10 09:00:00', '2025-03-12 10:20:00', '2025-01-07 14:00:00', 19, 3, 3),-- Data Science
       (16, 'DSR016', 1, '2025-03-15 14:45:00', '2025-03-17 16:10:00', '2025-01-07 14:00:00', 20, 4, 4),-- UI/UX Design
       (17, 'DSR017', 2, '2025-03-20 11:30:00', '2025-03-22 12:50:00', '2025-01-07 14:00:00', 21, 5, 1),-- Cybersécurité
       (18, 'DSR018', 3, '2025-03-25 15:10:00', '2025-03-26 17:25:00', '2025-01-07 14:00:00', 22, 6, 2), -- DevOps
       (19, 'DSR019', 1, '2025-04-01 09:30:00', '2025-04-02 10:40:00', '2025-01-07 14:00:00', 23, 1, 3),-- Java avancé
       (20, 'DSR020', 2, '2025-04-05 13:00:00', '2025-04-06 15:10:00', '2025-01-07 14:00:00', 24, 2, 4),-- Développement Web
       (21, 'DSR021', 1, '2025-03-20 11:30:00', '2025-04-06 15:10:00', '2025-01-07 14:00:00', 25, 3, 2),-- Développement Web
       (22, 'DSR022', 1, '2025-03-20 11:30:00', '2025-04-06 15:10:00', '2025-01-07 14:00:00', 26, 3, 2),-- Développement Web
       (23, 'DSR023', 1, '2025-03-20 11:30:00', '2025-04-06 15:10:00', '2025-01-07 14:00:00', 27, 3, 2),-- Développement Web
       (24, 'DSR024', 1, '2025-03-20 11:30:00', '2025-04-06 15:10:00', '2025-01-07 14:00:00', 28, 3, 2),-- Développement Web
       (25, 'DSR025', 1, '2025-03-20 11:30:00', '2025-04-06 15:10:00', '2025-01-07 14:00:00', 29, 3,
        2); -- Développement Web


INSERT INTO document (nom_fichier, type, statut_document_id, dossier_id, commentaire, date_depot, url_fichier)
VALUES ('Justificatif de domicile', 'JUSTIFICATIF', 2, 1, NULL, NOW(), NULL),
       ('CV Paul', 'CV', 1, 1, NULL, NOW(), NULL),
       ('Lettre de motivation', 'LETTRE_MOTIVATION', 1, 2, NULL, NOW(), NULL),
       ('Justificatif d’identité', 'JUSTIFICATIF', 2, 2, NULL, NOW(), NULL);


-- 1) Inscription n°1 pour le stagiaire dont id = 3, sur la formation id = 5, en EN_ATTENTE,
--    pas encore de dossier (dossier_id = NULL), date_modification et date_validation NULL :

-- Exemple inscription
INSERT INTO inscription (id_inscription, date_inscription, date_modification, date_validation, statut_inscription,
                         stagiaire_id,
                         formation_id, dossier_id)
VALUES (1, CURDATE(), NULL, NULL, 'EN_ATTENTE', 5, 1, 1);


INSERT INTO motif (id, libelle)
VALUES (1, 'RETRAIT_DOSSIER'),
       (2, 'JUSTIFICATIF_INFIRMITÉ');


INSERT INTO evenement (id_evenement, date_debut, date_fin, est_retard, stagiaire_id, motif_id, document_id)
VALUES (1, '2025-10-15', '2025-10-16', FALSE, 5, 1, NULL);


-- 7. DOCUMENTS OBLIGATOIRES (type_document = enum en string, respecte la casse de l'enum)
INSERT INTO liste_documents_obligatoires (id, formation_id, type_document)
VALUES (1, 1, 'JUSTIFICATIF'),
       (2, 2, 'LETTRE_MOTIVATION'),
       (3, 3, 'CV'),
       (4, 4, 'AUTRE'),
       (5, 5, 'JUSTIFICATIF'),
       (6, 6, 'LETTRE_MOTIVATION'),
       (7, 7, 'CV'),
       (8, 8, 'AUTRE'),
       (9, 9, 'JUSTIFICATIF'),
       (10, 10, 'LETTRE_MOTIVATION');



INSERT INTO piece_jointe_stagiaire (id, stagiaire_id, formation_id, type_document, chemin_fichier, statut_document_id)
VALUES (1, 5, 1, 'CV', 'cv_roman_dupont.pdf', 1),
       (2, 5, 1, 'JUSTIFICATIF', 'justificatif_roman_dupont.pdf', 2);

-- Création des modèles de message
# INSERT INTO notification_template (id, type, message)
# VALUES (1, 'INFORMATION',
#         'Un nouveau document a été envoyé par un stagiaire. Veuillez vérifier sa validité et sa lisibilité.'),
#        (2, 'WARNING_ABSENCE',
#         'Vous avez atteint un seuil critique de retards ou d’absences\. Veuillez justifier votre situation rapidement.'),
#        (3, 'WARNING_DOCUMENT',
#         'Un ou plusieurs documents envoyés ont été refusés\. Merci de les corriger et les renvoyer au plus vite.'),
#        (4, 'RAPPEL', 'Votre dossier d’inscription est incomplet\. Merci de fournir les documents manquants.');

-- NOTIFICATIONS
-- Admins : INFORMATION
# INSERT INTO notification (id, template_id, destinataire_id)
# VALUES (1, 1, 101),
#        (2, 1, 102),
#        (3, 1, 103),
#        (4, 1, 104),
#        (5, 1, 105),
#        (6, 1, 106),
#        (7, 1, 107),
#        (8, 1, 108);
#
# -- Stagiaires 1 à 10 : retards / absences
# INSERT INTO notification (id, template_id, destinataire_id)
# VALUES (9, 2, 1),
#        (10, 2, 2),
#        (11, 2, 3),
#        (12, 2, 4),
#        (13, 2, 5),
#        (14, 2, 6),
#        (15, 2, 7),
#        (16, 2, 8),
#        (17, 2, 9),
#        (18, 2, 10);
#
# -- Stagiaires 11 à 15 : documents refusés
# INSERT INTO notification (id, template_id, destinataire_id)
# VALUES (19, 3, 11),
#        (20, 3, 12),
#        (21, 3, 13),
#        (22, 3, 14),
#        (23, 3, 15);
#
# -- Stagiaires 16 à 20 : dossier incomplet
# INSERT INTO notification (id, template_id, destinataire_id)
# VALUES (24, 4, 16),
#        (25, 4, 17),
#        (26, 4, 18),
#        (27, 4, 19),
#        (28, 4, 20);





