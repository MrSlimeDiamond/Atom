# Atom
Atom is a Discord bot

## Configuration
Basic configuration looks like
```json
{
    "bot": {
        "token": "TOKEN GOES HERE",
        "admins": ["1234567890"],
        "prefix": "!a-"
    }
}
```
## Modules
The idea of modules is that you can allow certian guilds to do certian things, but not allow other guilds to do other things.

### Pinnerino
Pinnerino allows you to put messages with a certian amount of reacions into a channel
#### Mysql creation statement
```sql
CREATE TABLE pinnerino (OriginalMsg varchar(256), WebhookMessageID varchar(256));
```

#### Example module configuration
```json
    "database": {
        "host": "127.0.0.1",
        "username": "atom",
        "password": "yes",
        "database": "atom",
        "table": "pinnerino"
    },
    "guilds": {
        "696218632618901504": {
            "reactionCount": 2,
            "webhook": {
                "id": "id goes here",
                "token": "token goes here"
            },
            "blacklist": ["123456789"],
            "reaction": "📌"
        }
    }
```