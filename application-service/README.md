# GridMarket

## Installation guide

1. Clone a repository: `git clone https://github.com/vlekic02/GridMarket.git`
2. Unzip folder: `unzip GridMarket.zip`
3. Cd into Grid market folder
4. Run docker using `docker-compose up -d`
5. Go to `http://localhost:8080/v1/application/health` to check if everything is working correctly

## Contribution guide

### 1. Setup git hooks

Hooks are container inside of `git_hooks` directory and should not be skipped. Since hooks are
contained in directory which is not tracked by version control system, we have to do one time setup.

- Remove existing hooks directory: `rm -rf .git/hooks`
- If not set already, give all hooks execute permission: `cd git_hooks/; chmod +x *`
- Create symbolic link pointing to our git_hooks directory: `ln -s $PWD/git_hooks .git/hooks`

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

### Health check

```
GET /<version>/application/health
```

**Example response:**

```
HTTP 200 OK
```

### List all applications

```
GET /<version>/applications/
```

**Request headers:**  
`Authorization: Bearer <API_KEY>`

**Example response:**

```json
[
  {
    "id": 1,
    "name": "Example 1",
    "description": "Example description",
    "publisher": 2,
    "price": 20,
    "discount": null
  },
  {
    "id": 2,
    "name": "Example 2",
    "description": null,
    "publisher": 1,
    "price": 25,
    "discount": 3
  }
]
```

### Get single application

```
GET /<version>/applications/<application_id>
```

**Request headers:**  
`Authorization: Bearer <API_KEY>`

**Example responses:**

```json
  {
  "id": 25,
  "name": "Example 25",
  "description": null,
  "publisher": 10,
  "price": 35,
  "discount": null
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

**Request headers:**  
`Authorization: Bearer <API_KEY>`

**Example responses:**

```json
[
  {
    "id": 1,
    "author": 2,
    "message": "Awesome application !",
    "stars": 5
  },
  {
    "id": 4,
    "author": 1,
    "message": "I don't like this one !",
    "stars": 1
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

**Request headers:**  
`Authorization: Bearer <API_KEY>`

**Request body example:**

```json
{
  "message": "Meh... could be better",
  "stars": 3
}
```