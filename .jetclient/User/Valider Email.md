```toml
name = 'Valider Email'
method = 'POST'
url = 'http://localhost:8080/auth/validate-email'
sortWeight = 3000000
id = 'c19f8133-a2b8-4384-ac02-e9e90094902a'

[body]
type = 'JSON'
raw = '''
{
  "token": "jeton_de_validation_recu_par_mail"
}
'''
```
