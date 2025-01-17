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

### Use RAG

**Prerequisite: Start Qdrant**

`docker run -p 6333:6333 -p 6334:6334 qdrant/qdrant`

#### Upload knowledge

`
POST http://localhost:8080/upload-knowledge
`

Request body example:

First 5 paragraphs from [2024 United States presidential election's Wikipedia page](https://en.wikipedia.org/wiki/2024_United_States_presidential_election)

`
{
"input": "Presidential elections were held in the United States on November 5, 2024. The Republican Party's ticket—Donald Trump, who was the 45th president of the United States from 2017 to 2021, and JD Vance, the junior U.S. senator from Ohio—defeated the Democratic Party's ticket—Kamala Harris, the incumbent vice president, and Tim Walz, the 41st governor of Minnesota. Trump and Vance are scheduled to be inaugurated as the 47th president and the 50th vice president on January 20, 2025.\n\nThe incumbent president, Joe Biden of the Democratic Party, initially ran for re-election as the party's presumptive nominee, facing little opposition and easily defeating Representative Dean Phillips during the Democratic primaries; however, what was broadly considered a poor debate performance in June 2024 intensified concerns about his age and health, and led to calls within his party for him to leave the race. After initially declining to do so, Biden withdrew on July 21, becoming the first eligible incumbent president to withdraw since Lyndon B. Johnson in 1968. Biden endorsed Harris, who was voted the party's nominee by the delegates on August 5, 2024. Harris selected Walz as her running mate. This was the first time since 2000 in which an incumbent vice president ran for president.\n\n Trump, who lost in 2020 to Biden, ran for re-election again. He was nominated during the 2024 Republican National Convention along with his running mate, Vance, after winning the Republican primaries by easily defeating former Governor Nikki Haley. The Trump campaign was noted for making many false and misleading statements, including the claim that the 2020 election was stolen from Trump, engaging in anti-immigrant fearmongering, and promoting conspiracy theories. Trump's political movement was described by several historians and former Trump administration officials as authoritarian, featuring parallels to fascism, and using dehumanizing rhetoric toward his political opponents.\n\nTrump achieved victory in the Electoral College, winning 312 electoral votes to Harris' 226. Trump won every swing state, including the first win of Nevada by Republicans since 2004, in addition to holding on to all of the states that he won in 2020. Trump won the national popular vote with a plurality of 49.9%, making him the first Republican to do so since George W. Bush in 2004 but with what was the third smallest popular vote margin since 1888. Relative to the 2020 election, he improved his vote share among working class voters, particularly among young men, those without college degrees, and Hispanic voters. Trump became the first president since Grover Cleveland in 1892 to be elected to non-consecutive terms.\n\nAccording to polls, the most important issues for voters were the economy, healthcare, democracy, foreign policy (notably U.S. support for Israel and for Ukraine), illegal immigration, abortion, and climate change. Education and LGBTQ rights were also prominent issues in the campaign. Polled voters consistently cited the economy, particularly inflation, as the most important issue in the election. Analysts have blamed Harris' loss on the 2021–2023 inflation surge, a global anti-incumbent wave, the unpopularity of the Biden administration, and Trump's gains with the working class. "
}
`

#### Chat with your data

**Create chat**

``
GET http://localhost:8080/openai/createChat
``

**Ask question**

``
POST http://localhost:8080/chat/{chatId}
``

Path variable

- chatId - ID of the chat returned by the createChat call.

Request body example:

`
{
"input": "Who won the 2024 US Presidential Election?"
}
`

Response example:

`
{
"messages": [
{
"role": "user",
"message": "Who won the 2024 US Presidential Election?"
},
{
"role": "assistant",
"message": "The winner of the 2024 US Presidential Election was Donald Trump from the Republican Party. He was elected as the 47th president of the United States."
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