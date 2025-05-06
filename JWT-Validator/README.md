# JWT Validator

This is a spring boot _REST API_ with the following features.

1. Expose two endpoints.
    - __/api/validate/**__ Extract form the `Authorization` header a `Bearer Token` and validate it.
    - **/api/health-check** Return a health message indicating the service is `Up`.
    - 
2. Validate a JWT token for the following issues:
    - If token can be decoded.
    - If token is not expired.
    - If token signature is valid. Use the public key specified in the kid claim (download the token issuer's public key from the corresponding JWKS).
    - Extract preferred_name claim and set a HTTP Header `X-Trino-User` with this value.
    - Return `200 OK` for success validation or `401 UNAUTHORIZED` for unsucess validation.
   
## Libraries used:

1. com.fasterxml.jackson.core.fasterxml.jackson.2.16.1
2. com.auth0.java-jwt.4.5.0
3. com.auth0.jwks-rsa.0.22.1
4. jre 24
5. spring boot 3.4.5

## Validation Requests

To validate a JWT token, call this endpoint.

```
    curl --location --request POST 'http://172.27.208.1:8081/api/validate' \
    --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IlRWQTVRam9oWjg4TDAxdmlDM0ppNyJ9.eyJpc3MiOiJodHRwczovL2Rldi0yMGVmMDFyaWJtdXRnYnI1LnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJxckNzTHhGUFZib2ozdlFhaklFUVVXT2VMeDQ4MlpjY0BjbGllbnRzIiwiYXVkIjoiaHR0cHM6Ly90ZXN0LWFwaSIsImlhdCI6MTc0NjQwOTQ2NSwiZXhwIjoxNzQ2NDk1ODY1LCJndHkiOiJjbGllbnQtY3JlZGVudGlhbHMiLCJhenAiOiJxckNzTHhGUFZib2ozdlFhaklFUVVXT2VMeDQ4MlpjYyJ9.Ngq8vv0uqhm_NfrUGKep3utp2Eu2_-_LbDitOT_EdGs8y-wCrEVhq6glQMsgO4f274p3bjlKzbfrdg0UV2YgiKAn0wbf5Him_KkCAP8XU2d7Nuf4qm9Y1I7s5QUAkPO7h0pNfZhAQe8tGp7x8yxPmAbbJIx6oyeu6uBq3cOJN5sXSIOcHiAo0wli_KIlNFbgpnMUCSlAvkZcpkyHmx9CnUSSK2Lky4RV7izutHdg559vZxOcss-ImgRkZ2YvSXstysf79nK2PI9Zn0rCvHIqsdEnIGsk9cHJtpv5iIJVpPcGqOqONm7GDEAy5qHz7G_wapshgdRgqid6IupTrCZpdg'
```

Headers returned for a successful validation.

![headers](images/headers.png)