## Environmental variables

- DIAL_KEY - DIAL API Key
- DIAL_ENDPOINT - DIAL API Endpoint

## Run

Run the application using Maven:

``
mvn spring-boot:run
``

## Usage

### Create a new chat

#### OpenAI
``
GET http://localhost:8080/openai/createChat
``

#### Mistral [DISABLED, NOT COMPATIBLE WITH FUNCTION CALLING]

``
GET http://localhost:8080/mistral/createChat
``

### Use the plugin

``
POST http://localhost:8080/chat/{chatId}
``

Path variable

- chatId - ID of the chat returned by the createChat call.

Request body example:

`
{
"input": "How many euros is 1 forint?"
}
`

Response example:

`{
"messages": [
{
"role": "user",
"message": "How many euros is 1 forint?"
},
{
"role": "assistant",
"message": "1 forint is approximately 0.00241 euros."
}
],
"model": "OpenAI"
}
`

### Use semantic search

**Prerequisite: Start Qdrant**

`docker run -p 6333:6333 -p 6334:6334 qdrant/qdrant`

#### Create embedding

`POST http://localhost:8080/embed`

Request body example:

`{"text": "Norwegian Forest cat"}`

Response example:

1536 dimension vector:
`[
-0.019600773,
-0.0028670316,
-0.012311655,
-0.013226015,
-6.877817E-4,
0.011017386,
-0.02969736,
-0.009047006,
-0.01173857,
...
]`

#### Create and store embedding

`POST http://localhost:8080/store`

Request body example:

`{"text": "Norwegian Forest cat"}`

Response example:

1536 dimension vector:
`[
-0.019600773,
-0.0028670316,
-0.012311655,
-0.013226015,
-6.877817E-4,
0.011017386,
-0.02969736,
-0.009047006,
-0.01173857,
...
]`

#### Search nearest embedding

`POST http://localhost:8080/search`

Request body example:

`{"text": "Norwegian Forest cat"}`

Response example:

`{
"nearestFinds": [
"Maine Coon",
"Sphynx",
"Siamese"
]
}`