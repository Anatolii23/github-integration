Integration with GitHub to get some required info.
You need to generate token -> github.com -> Settings -> Developer Settings -> Generate new token

GET http://127.0.0.1:8080/api/users/repo/:login

Response 

    [
    {
        "repoName": "github-integration",
        "owner": "Anatolii23",
        "branches": [
            {
                "name": "master",
                "commit": {
                    "sha": "2e581fd00dbaa6cb9ee879904a745dbd1775aa5c"
                }
            }
        ]
    }
    ]
}

Exception

{

    “status”: ${responseCode}

    “message”: ${whyHasItHappened}

}