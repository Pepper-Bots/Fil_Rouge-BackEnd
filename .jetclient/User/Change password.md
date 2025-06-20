```toml
name = 'Change password'
method = 'POST'
url = 'POST http://localhost:8080/api/auth/change-password '
sortWeight = 3000000
id = '29da68fb-f6fb-4b40-a36d-4b9af798c79d'

[body]
type = 'JSON'
raw = '''
{
  "email": "alice@example.com",
  "newPassword": "123456"
}'''
```
