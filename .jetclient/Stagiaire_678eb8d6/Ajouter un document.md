```toml
name = 'Ajouter un document'
method = 'POST'
url = 'http://localhost:8080/stagiaires/5/documents'
sortWeight = 1000000
id = '1bb39888-8c89-4f94-b9b3-fd75d60bcde5'

[body]
type = 'JSON'
raw = '''
[
  {
    "id": 123,
    "typeDocument": "CV",
    "obligatoire": true,
    "transmis": true,
    "statut": "Validé",
    "commentaire": null,
    "fichier": "cv_helene.pdf"
  }
]
'''
```
