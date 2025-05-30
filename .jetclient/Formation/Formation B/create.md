```toml
name = 'create'
method = 'POST'
sortWeight = 1000000
id = 'ea2d5c50-75b8-4c02-b201-a8b52c2393fa'

[body]
type = 'JSON'
raw = '''
{
  "id": 2,
  "titre": "Développeur Web Confirmé",
  "niveau": "BAC_3",
  "documentsObligatoires": [
    { "id": 5, "typeDocument": "JUSTIFICATIF_IDENTITE" },
    { "id": 6, "typeDocument": "DIPLOME_BAC_2" },
    { "id": 7, "typeDocument": "CV" },
    { "id": 8, "typeDocument": "LETTRE_MOTIVATION" }
  ]
}
'''
```
