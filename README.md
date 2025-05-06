# 📦 Fil_Rouge-BackEnd – Application ADMIN MNS

Projet backend développé en Java avec Spring Boot dans le cadre du projet Fil Rouge. 
Cette application constitue la partie **administration** de l’école **MNS** (Metz Numeric School).

## 🚀 Technologies utilisées

- Java 17+  
- Spring Boot 3.x  
- Spring Data JPA  
- Spring Security (à ajouter si besoin)  
- Base de données : PostgreSQL / MySQL (à préciser)  
- Maven  
- Git

## 📁 Structure du projet

Fil_Rouge-BackEnd/
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com.example.mns/
│ │ │ ├── controller/
│ │ │ ├── service/
│ │ │ ├── model/
│ │ │ ├── repository/
│ │ │ └── FilRougeBackendApplication.java
│ │ └── resources/
│ │ ├── application.properties
│ │ └── ...
├── pom.xml
└── README.md

## ⚙️ Installation et exécution locale

### Prérequis

- Java JDK 17 ou +
- Maven
- PostgreSQL ou MySQL installé (selon ta config)

### Étapes

1. Cloner le dépôt :
```bash
git clone https://github.com/ton-utilisateur/Fil_Rouge-BackEnd.git
cd Fil_Rouge-BackEnd
```

2. Configurer la base de données dans src/main/resources/application.properties :

spring.datasource.url=jdbc:postgresql://localhost:5432/mns_db
spring.datasource.username=postgres
spring.datasource.password=ton_mot_de_passe
spring.jpa.hibernate.ddl-auto=update

3. Lancer l'application

./mvnw spring-boot:run

L'application sera disponible sur http://localhost:8080.

## 🧪 Tests

À venir : intégration de tests unitaires et d'intégration avec JUnit et Mockito.

## 📌 À faire / TODO

  - Définir les entités principales du modèle de données

  - Implémenter l’authentification et la gestion des rôles (admin, utilisateur, etc.)

  - Ajouter les endpoints REST nécessaires à l’administration

  - Connecter le backend au frontend (Angular)

## 👤 Auteur

Projet développé par Hélène Rizzon dans le cadre de la formation Concepteur Développeur d’Applications.


    
