```toml
name = 'List Documents'
description = 'Lister les documents obligatoires'
method = 'GET'
url = 'http://localhost:8080/formations/2/documents-obligatoires'
sortWeight = 7000000
id = 'a02b7b22-250c-4eec-a80a-05f44b2a991f'

[body]
type = 'JSON'
raw = '''
[
  {
    "id": 5,
    "formation": 2,
    "typeDocument": "JUSTIFICATIF_IDENTITE"
  },
  {
    "id": 6,
    "formation": 2,
    "typeDocument": "DIPLOME_BAC_2"
  },
  {
    "id": 7,
    "formation": 2,
    "typeDocument": "CV"
  },
  {
    "id": 8,
    "formation": 2,
    "typeDocument": "LETTRE_MOTIVATION"
  }
]
'''
```
