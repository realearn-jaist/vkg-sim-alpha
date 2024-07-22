# VKGSim

## What is VKGSim?

VKGSim is an advanced Virtual Knowledge Graph (VKG) system that enhances traditional VKG functionalities by incorporating a sophisticated similarity query feature. This feature allows users to perform queries with a defined similarity threshold, making data retrieval more flexible and accurate.

## Key Features

- **VKGSim System**: An advanced VKG system with enhanced querying capabilities.
- **User Login & Upload Section**: Secure user login and data upload functionalities.
- **Mapping Section**: Tools for mapping and managing data.
- **Query Section**:
    - **Standard Query**: Perform traditional queries.
    - **Similarity Query**: Execute queries based on similarity thresholds.

## Folder Documentation

```
src/main/java/io/github/vkgsim
|-- controller
|   |-- OntopController.java
|   |-- RestController.java
|   |-- SimilarityController.java
|   |-- WebController.java
|-- Application.java (run spring boot)

src/main/resources/static/css/
|-- script.js
|-- style.css

src/main/resources/templates/
|-- mainPage.html
|-- mappingPage.html
|-- queryPage.html
|-- errorPage.html
```

## Online Application

[Access the online application](http://1.1.1.1)

## How to Use the Application

(Skip for now)

## Requirements

For dependencies and required libraries, refer to the [pom.xml](pom.xml) file.

## How to Run

Follow these steps to run the VKGSim application:

1. **Clone the Repository**
    - Open your terminal (Command Prompt, Git Bash, etc.).
    - Run the following command to clone the repository:
      ```sh
      git clone <repository-url>
      ```
    - Navigate into the cloned directory:
      ```sh
      cd <repository-folder-name>
      ```

2. **Install Dependencies**
    - Ensure you have [Maven](https://maven.apache.org/install.html) installed on your system.
    - Run the following command to clean and install the required dependencies:
      ```sh
      mvn clean install
      ```

3. **Run the Spring Boot Application**
    - Once the dependencies are installed, start the application with:
      ```sh
      mvn spring-boot:run
      ```

4. **Access the Application**
    - Open your web browser and go to `http://localhost:<port in application.properties>` to access the VKGSim application.

### Additional Notes
- Make sure you have [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) installed (version 11 or later).
- If you encounter any issues, ensure your environment variables for Java and Maven are correctly set up.

## How to Deploy on Server

(Skip for now)

## Authors and Contacts

Chaphowasit Mahayossanan (Mahidol University, Thailand)
- Email: chaphowasit.mah@student.mahidol.edu

Teerapat Phopit (Mahidol University, Thailand)
- Email: teerapat.pho@student.mahidol.edu

Teeradaj Racharak (JAIST, Japan)
- Email: racharak@jaist.ac.jp

Kiattiphum Suwanarsa (Mahidol University, Thailand)
- Email: kiattiphum.intern@gmail.com