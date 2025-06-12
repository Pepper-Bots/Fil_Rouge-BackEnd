```toml
name = 'Changer mot de passe'
method = 'POST'
url = 'http://localhost:8080/auth/change-password'
sortWeight = 4000000
id = '21895f6c-c5dc-41d1-987e-7f68f8cc8a87'

[body]
type = 'JSON'
raw = '''
{
  "oldPassword": "AncienMotDePasse123!",
  "newPassword": "NouveauMotDePasse456!"
}
'''
```
