```toml
name = 'update document'
description = 'Ajouter un document obligatoire'
method = 'POST'
url = 'http://localhost:8080/formations/1/documents-obligatoires'
sortWeight = 6000000
id = 'cf95008c-1b02-4f78-ba39-7fcd44f47a77'

[body]
type = 'JSON'
raw = '''
{
  "typeDocument": "DIPLOME_BAC"
}'''
```
