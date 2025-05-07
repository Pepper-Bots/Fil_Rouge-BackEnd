-- Étape 1 : insérer les utilisateurs de base dans la table USER
INSERT INTO user (last_name, first_name, email, password, user_type)
VALUES ('Alice', 'Dupont', 'alice@example.com', 'password1', 'ADMINISTRATEUR'),
       ('Bruno', 'Durand', 'bruno@example.com', 'password2', 'ADMINISTRATEUR'),
       ('Cécile', 'Martin', 'cecile@example.com', 'password3', 'ADMINISTRATEUR'),
       ('David', 'Bernard', 'david@example.com', 'password4', 'ADMINISTRATEUR'),
       ('Dupont', 'Romain', 'romain_dupont@live.fr', 'root', 'STAGIAIRE');

-- Étape 2 : insérer dans ADMIN (en réutilisant les IDs ci-dessus)
INSERT INTO admin (id, type_admin, niveau_droit)
VALUES (1, 'RESPONSABLE_ETABLISSEMENT', 'SUPER_ADMIN'),
       (2, 'RESPONSABLE_FORMATION', 'ADMIN'),
       (3, 'ASSISTANT_VIE_SCOLAIRE', 'MODERATEUR'),
       (4, 'ASSISTANT_ADMINISTRATIF', 'BASIQUE');

INSERT INTO stagiaire (id, date_naissance, numero_de_telephone, adresse)
VALUES (1, '1990-03-01', '0660606060', '12 rue des Rosses');


-- Insertion de 20 dossiers dans la table dossier
INSERT INTO dossier (id, code_dossier, statut_dossier_id, statut_document_id,
                     date_de_creation, last_updated, stagiaire_id, formation_id, createur_id)
VALUES (1, 'DSR001', 2, 1, '2025-01-05 09:00:00', '2025-01-07 14:00:00', 1, 1, 1),   -- Java avancé
       (2, 'DSR002', 1, 2, '2025-01-08 10:15:00', '2025-01-09 16:30:00', 2, 2, 2),   -- Développement Web
       (3, 'DSR003', 3, 3, '2025-01-12 11:00:00', '2025-01-13 15:45:00', 3, 3, 3),   -- Data Science
       (4, 'DSR004', 1, 2, '2025-01-15 08:45:00', '2025-01-17 14:20:00', 4, 4, 4),   -- UI/UX Design
       (5, 'DSR005', 2, 1, '2025-01-20 13:30:00', '2025-01-22 17:10:00', 5, 5, 1),   -- Cybersécurité
       (6, 'DSR006', 3, 3, '2025-01-25 09:45:00', '2025-01-26 10:00:00', 6, 6, 2),   -- DevOps
       (7, 'DSR007', 1, 1, '2025-02-01 14:10:00', '2025-02-03 18:20:00', 7, 1, 3),   -- Java avancé
       (8, 'DSR008', 2, 2, '2025-02-05 15:30:00', '2025-02-06 16:40:00', 8, 2, 4),   -- Développement Web
       (9, 'DSR009', 3, 3, '2025-02-10 10:00:00', '2025-02-12 12:00:00', 9, 3, 1),   -- Data Science
       (10, 'DSR010', 2, 1, '2025-02-14 11:30:00', '2025-02-15 14:00:00', 10, 4, 2), -- UI/UX Design
       (11, 'DSR011', 1, 3, '2025-02-18 08:00:00', '2025-02-19 09:30:00', 11, 5, 3), -- Cybersécurité
       (12, 'DSR012', 3, 2, '2025-02-22 13:20:00', '2025-02-23 14:45:00', 12, 6, 4), -- DevOps
       (13, 'DSR013', 1, 1, '2025-03-01 10:00:00', '2025-03-02 11:15:00', 13, 1, 1), -- Java avancé
       (14, 'DSR014', 2, 2, '2025-03-05 12:10:00', '2025-03-06 13:40:00', 14, 2, 2), -- Développement Web
       (15, 'DSR015', 3, 3, '2025-03-10 09:00:00', '2025-03-12 10:20:00', 15, 3, 3), -- Data Science
       (16, 'DSR016', 1, 2, '2025-03-15 14:45:00', '2025-03-17 16:10:00', 16, 4, 4), -- UI/UX Design
       (17, 'DSR017', 2, 1, '2025-03-20 11:30:00', '2025-03-22 12:50:00', 17, 5, 1), -- Cybersécurité
       (18, 'DSR018', 3, 2, '2025-03-25 15:10:00', '2025-03-26 17:25:00', 18, 6, 2), -- DevOps
       (19, 'DSR019', 1, 3, '2025-04-01 09:30:00', '2025-04-02 10:40:00', 19, 1, 3), -- Java avancé
       (20, 'DSR020', 2, 1, '2025-04-05 13:00:00', '2025-04-06 15:10:00', 20, 2, 4);-- Développement Web

INSERT INTO formation (id, titre, description, date_debut, date_fin)
VALUES (1, 'Développement Web Front-End', 'Apprendre HTML, CSS, JavaScript et React.', '2025-06-01', '2025-08-30'),
       (2, 'Développement Web Back-End', 'Apprentissage de Node.js, Express et bases de données.', '2025-07-01',
        '2025-09-30'),
       (3, 'Full Stack Web', 'Formation complète front-end et back-end avec projets pratiques.', '2025-06-15',
        '2025-10-15'),
       (4, 'Sécurité Réseaux', 'Introduction à la sécurité des réseaux informatiques.', '2025-05-20', '2025-07-20'),
       (5, 'Pentesting - Tests d\'intrusion', 'Découverte des techniques d\'intrusion et d\'audit.', '2025-06-10',
        '2025-09-10'),
       (6, 'Développement Web avec Java Spring', 'Conception d\'applications web sécurisées avec Spring Boot.',
        '2025-07-05', '2025-10-05'),
       (7, 'Cyberdéfense et SOC', 'Mise en place d\'un centre opérationnel de sécurité.', '2025-08-01', '2025-10-31'),
       (8, 'Développement Web avec PHP et Laravel', 'Projet web avec PHP, MySQL et le framework Laravel.', '2025-05-01',
        '2025-07-31'),
       (9, 'Sécurité des Applications Web', 'Protection des applis web contre les vulnérabilités courantes.',
        '2025-07-10', '2025-09-20'),
       (10, 'Initiation à la cybersécurité', 'Panorama des menaces et bonnes pratiques en entreprise.', '2025-06-05',
        '2025-08-05');

-- Création des modèles de message
INSERT INTO notification_template (id, type, message)
VALUES (1, 'INFORMATION',
        'Un nouveau document a été envoyé par un stagiaire. Veuillez vérifier sa validité et sa lisibilité.'),
       (2, 'WARNING_ABSENCE',
        'Vous avez atteint un seuil critique de retards ou d’absences. Veuillez justifier votre situation rapidement.'),
       (3, 'WARNING_DOCUMENT',
        'Un ou plusieurs documents envoyés ont été refusés. Merci de les corriger et les renvoyer au plus vite.'),
       (4, 'RAPPEL', 'Votre dossier d’inscription est incomplet. Merci de fournir les documents manquants.');

-- NOTIFICATIONS
-- Admins : INFORMATION
INSERT INTO notification (id, template_id, destinataire_id)
VALUES (1, 1, 101),
       (2, 1, 102),
       (3, 1, 103),
       (4, 1, 104),
       (5, 1, 105),
       (6, 1, 106),
       (7, 1, 107),
       (8, 1, 108);

-- Stagiaires 1 à 10 : retards / absences
INSERT INTO notification (id, template_id, destinataire_id)
VALUES (9, 2, 1),
       (10, 2, 2),
       (11, 2, 3),
       (12, 2, 4),
       (13, 2, 5),
       (14, 2, 6),
       (15, 2, 7),
       (16, 2, 8),
       (17, 2, 9),
       (18, 2, 10);

-- Stagiaires 11 à 15 : documents refusés
INSERT INTO notification (id, template_id, destinataire_id)
VALUES (19, 3, 11),
       (20, 3, 12),
       (21, 3, 13),
       (22, 3, 14),
       (23, 3, 15);

-- Stagiaires 16 à 20 : dossier incomplet
INSERT INTO notification (id, template_id, destinataire_id)
VALUES (24, 4, 16),
       (25, 4, 17),
       (26, 4, 18),
       (27, 4, 19),
       (28, 4, 20);

INSERT INTO statut_document (id, nom)
VALUES (1, ENVOYE),
       (2, VALIDE),
       (3, REFUSE),
       (4, MANQUANT);

SELECT *
FROM user
WHERE id = 1;
