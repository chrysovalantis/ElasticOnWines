# ElasticSearch - Information Retrieval
This API refers to an exercise for the CS660 course (Assignment 2). Using elasticsearch REST API, provides the ability to the user to create indexes, add files to them. It allows you to send some queries and get responses and delete the files and index you create. The inverted index updated after any change. 


##  Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

1. You should download and install [STS buddle](https://spring.io/tools/sts/all) or [InteliJ](https://www.jetbrains.com/idea/)
2. You **must** download and install [Elasticsearch](https://www.elastic.co/start)
### Installing

A step by step series of examples that tell you have to get a development env running

1. Clone this repository to your local machine

```
git clone https://github.com/Chrysovalantis/Dionysos-Inverted-Index
```

2. Open STS or InteliJ and then open the project

```
File -> Open Project from File System -> Directory
```
3. Run the elastic search on **Port: 9200** inside the elastic search folder
```
bin/elasticsearch
```

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Running
* Run Spring Boot App
* Test the app using [My Postman Collection](https://www.getpostman.com/collections/1ae116e91bf4b3f8d78c)

## End Points

|Endpoint                               |Description                        |
|---------------------------------------|-----------------------------------|
|**GET**  /collections/print/{index}| Print all the informations for an index |
|**GET**  /collections/print_indices | Prints all the indices inside ElasticSearch |
|**GET**  /collections/search/{index} | Search inside the given index based on the query param: qyery ( Lucene syntax ) |
|**GET** /collections/individual_evaluation/{index}     | Evaluates a set of given queries individually  |
|**GET** /collections/general_evaluation/{index}                  | Gets the results from the individual evaluation and then calculates the average precision, recall and F1-score.|
|**GET** /collections/evaluation_docs/{index}                  | Prints each query from the cran.qry file with the result documents return from the elasticsearch.|
|**POST**  /collections/{index} | Creates a new index|
|**POST**  /collections/uploadfile/{index} | Upload a file to the index|
|**POST** /collections/uploadMultipleFiles/{index}| Upload multiple files to the indices|
|**DELETE** /collections/deleteDirectory/{indexs}   | Delete the index|
|**DELETE** /collections/deleteFile    | Delete a file in the collection **(Parameters directory, file_id Needed)** |

### Important Node:
All the files **must** be in the exact given structure!!!
