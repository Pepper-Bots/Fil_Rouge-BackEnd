```toml
name = 'Renouvellement mdp'
method = 'POST'
url = 'http://localhost:8080/auth/reset-password'
sortWeight = 6000000
id = '75fd66c9-2759-4796-a854-fdac4fda1632'

[body]
type = 'JSON'
raw = '''
{
  "token": "jeton_de_reinitialisation",
  "newPassword": "NouveauMotDePasse456!"
}
'''
```
