```toml
name = 'Connexion'
method = 'POST'
url = 'POST http://localhost:8080/api/auth/connexion'
sortWeight = 2000000
id = '874bf439-cc7c-4d11-95c1-240a55dd6e7c'

[body]
type = 'JSON'
raw = '''
{
  "email": "alice@example.com",
  "password": "root"
}
'''
```
