# craft_demo

## Overview
* This project contains a gRPC C++ server to handle updates to a user's personal information (PI). A Spring Boot application is provided which contains the endpoint to provide user data. Upon receiving the payload, the endpoint will populate the proto-generated UserInfo object along with a timestamp and attempt a RPC to the gRPC server to set the updated user data. The gRPC server will then store the user data and log the request IDs (trace ID and user ID) along with the timestemp. If successful, returns HttpStatus.NO_CONTENT, otherwise HttpStatus.INTERNAL_SERVER_ERROR. 

## Features
* C++ gRPC Server configured to listen on localhost:50051
* Spring Boot application running on localhost:8080 providing UserInfo PUT endpoint

## Requirements
* Requires gRPC installation with protoc (to convert the .proto file to source code)

## Build
* The cpp and java must be built independently using CMake and Gradle, respectively.

## Usage 
* Run userinfo_server
  - Expected output: Server listening on 0.0.0.0:50051
* Run DemoApplication
  - Expected output: Creating connection: localhost:50051
* Submit a PUT request
  - Sample input data is provided in craft_demo/sample_data/sample_request.http
  - Example output:
     - userinfo_server: 2021/12/06 12:06:13: Received request #1 for user ID 7
     - DemoApplication: Successfully updated request #1
  
## Future Enhancements
* Unit tests
* Data field validation
* Share common a .proto file between C++/java builds. Currently this file must be duplicated.
* Additional error handling
* Migrate in-memory user data map to a database
* Authentication/encryption when handling PI
