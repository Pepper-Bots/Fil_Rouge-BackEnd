#!/bin/bash

# Créer le répertoire pour les certificats
mkdir -p ./certs

# Générer le certificat auto-signé
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout ./certs/privkey.crt \
    -out ./certs/fullchain.crt \
    -subj "/C=FR/ST=France/L=Metz/O=${CERT_OWNER}/CN=${CERT_DOMAIN}"

# Afficher un message de confirmation
echo "Certificat généré pour ${CERT_DOMAIN} avec le propriétaire ${CERT_OWNER}"