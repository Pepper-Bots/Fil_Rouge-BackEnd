```toml
name = 'create stagiaire'
method = 'POST'
url = 'http://localhost:8080/stagiaire'
sortWeight = 6000000
id = '4d45a37e-8ab0-4109-bcfd-ee9fd649c773'

[body]
type = 'JSON'
raw = '''
{
  "premiereConnexion" : true,
  "dateNaissance" : '1990-03-01' ,
  "phoneNumber" : 0000000000,
  "adresse" : null,
  "ville" : {
    "idVille" : 2,
    "codePostal" : 57850,
    "nomVille" : 'truc',
    "regionId" : 'ARA'
  },
  "photoProfil" : null
}'''
```
