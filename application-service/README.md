# GridMarket

## Installation guide

1. Clone a repository: `git clone https://github.com/vlekic02/GridMarket.git`
2. Cd into GridMarket folder
3. Run docker using `docker-compose up -d`
4. Go to `http://localhost:8080/v1/application/health` to check if everything is working correctly

## Contribution guide

### 1. Setup git hooks

Hooks are container inside of `git_hooks` directory and should not be skipped. Since hooks are
contained in directory which is not tracked by version control system, we have to do one time setup.

- If not set already, give all hooks execute permission: `cd git_hooks/; chmod +x *`
- Create symbolic link pointing to our git_hooks directory: `ln -s $PWD/git_hooks/* .git/hooks/`

### 2. Tickets

1. Upon taking issue in Jira, create a new branch that starts with ticket id,
   for example `GM-8-tests`, after that Jira bot should move issue to `In Progress`
2. When committing code changes, each commit message should start with ticket id, for example
   `GM-8 add unit tests`
3. Upon completion of the ticket, open a new pull request targeting `dev` branch, after that Jira
   bot
   should move issue to `In review`
4. After ticket is reviewed and merged, Jira bot will move issue to `Done`

## Interface definition

**Example response:**

```
HTTP 200 OK
```

### List all applications

```
GET /<version>/applications
```

**Example response:**

```json
[
  {
    "type": "application",
    "id": "1",
    "attributes": {
      "name": "Example",
      "originalPrice": 25,
      "path": "/system/path",
      "realPrice": 20
    },
    "relationships": {
      "discount": {
        "data": {
          "type": "discount",
          "id": "1",
          "attributes": {
            "discountType": "PERCENTAGE",
            "name": "Black friday",
            "valid": true,
            "value": 20
          }
        }
      },
      "publisher": {
        "data": {
          "type": "user",
          "id": "1"
        }
      }
    }
  },
  {
    "type": "application",
    "id": "2",
    "attributes": {
      "description": "Some description",
      "name": "Application 2",
      "originalPrice": 15,
      "path": "/system/path2",
      "realPrice": 15
    },
    "relationships": {
      "publisher": {
        "data": {
          "type": "user",
          "id": "3"
        }
      }
    }
  }
]
```

### Get single application

```
GET /<version>/applications/<application_id>
```

**Example responses:**

```json
 {
  "type": "application",
  "id": "1",
  "attributes": {
    "name": "Test",
    "originalPrice": 25,
    "path": "/system/path",
    "realPrice": 20
  },
  "relationships": {
    "discount": {
      "data": {
        "type": "discount",
        "id": "1",
        "attributes": {
          "discountType": "PERCENTAGE",
          "name": "Black friday",
          "valid": true,
          "value": 20
        }
      }
    },
    "publisher": {
      "data": {
        "type": "user",
        "id": "1"
      }
    }
  }
}
```

```json
{
  "status": 404,
  "message": "Specified application not found !"
}
```

### Get application reviews

```
GET /<version>/applications/<application_id>/reviews
```

**Example responses:**

```json
[
  {
    "type": "review",
    "id": "1",
    "attributes": {
      "message": "Nice application",
      "stars": 5
    },
    "relationships": {
      "application": {
        "data": {
          "type": "application",
          "id": "1"
        }
      },
      "author": {
        "data": {
          "type": "user",
          "id": "2"
        }
      }
    }
  },
  {
    "type": "review",
    "id": "2",
    "attributes": {
      "message": "Meh... don't like it",
      "stars": 2
    },
    "relationships": {
      "application": {
        "data": {
          "type": "application",
          "id": "1"
        }
      },
      "author": {
        "data": {
          "type": "user",
          "id": "4"
        }
      }
    }
  }
]
```

```json
{
  "status": 404,
  "message": "Specified application not found !"
}
```

### Post new application review

```
POST /<version>/applications/<application_id>/reviews
```

**Request body example:**

```json
{
  "message": "Meh... could be better",
  "stars": 3
}
```
