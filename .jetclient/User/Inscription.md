```toml
name = 'Inscription'
method = 'POST'
url = 'http://localhost:8080/auth/inscription'
sortWeight = 1000000
id = '22dec05b-11c7-4f80-8d7d-7c5315882de2'

[body]
type = 'JSON'
raw = '''
{
  "email": "nouveau@stagiaire.com",
  "password": "MotDePasse123!",
  "firstName": "Dupont",
  "lastName": "Alice",
  "role": "STAGIAIRE" 
}
'''
```
