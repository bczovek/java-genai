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