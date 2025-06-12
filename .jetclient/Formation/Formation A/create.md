```toml
name = 'create'
method = 'POST'
url = 'http://localhost:8080/formations/1/documents-obligatoires '
sortWeight = 1000000
id = '61eece9a-4665-4e6f-9b71-965952d34758'

[body]
type = 'JSON'
raw = '''
{
  "id": 1,
  "titre": "Développeur Web Junior",
  "niveau": "BAC_2",
  "documentsObligatoires": [
    { "id": 1, "typeDocument": "JUSTIFICATIF_IDENTITE" },
    { "id": 2, "typeDocument": "DIPLOME_BAC" },
    { "id": 3, "typeDocument": "CV" },
    { "id": 4, "typeDocument": "LETTRE_MOTIVATION" }
  ]
}
'''
```
