# VKGSim

## What is VKGSim?

VKGSim is an advanced Virtual Knowledge Graph (VKG) system designed to significantly enhance the functionalities of traditional VKG systems. One of its standout features is the incorporation of a sophisticated similarity query capability. This advanced feature allows users to perform queries with a defined similarity threshold, which adds a layer of flexibility and precision to data retrieval processes.

Furthermore, VKGSim supports a comprehensive range of traditional VKG functionalities, ensuring that users can perform standard queries and data management tasks seamlessly. The system is designed with user-friendliness in mind, offering a secure login and data upload section, an intuitive mapping section for managing data mappings, and a versatile query section. The query section is divided into two main types: standard queries for conventional data retrieval and similarity queries for enhanced, flexible searches.

## Key Features

- **VKGSim System**: An advanced VKG system that offers enhanced querying capabilities, incorporating sophisticated algorithms for more precise and flexible data retrieval.
- **User Authentication & Data Upload Module**: Provides robust, secure user authentication mechanisms alongside seamless data upload functionalities, ensuring data integrity and user privacy.
- **Data Mapping & Management Suite**: Comprehensive tools designed for intricate data mapping and efficient management, facilitating seamless integration and utilization of diverse data sources.
- **Advanced Query Interface**:
  - **Standard Query**: Execute traditional queries with high efficiency and accuracy, supporting a wide range of query languages and formats.
  - **Similarity Query**: Leverage cutting-edge similarity search algorithms to perform queries with defined similarity thresholds, enabling nuanced and context-aware data retrieval for more relevant results.

## Folder Documentation

```
libs/library.jar

ontop-cli/inputFiles (folder of users will create here)
ontop-cli/jdbc (database driver folder)

src/main/java/io/github/vkgsim
|-- controller
|   |-- OntopController.java
|   |-- RestController.java
|   |-- SimilarityController.java
|   |-- WebController.java
|-- Application.java (run spring boot)

src/test/java/io/github/vkgsim
|-- controller
    |-- OntopControllerTest.java
    |-- RestControllerTest.java
    |-- SimilarityControllerTest.java
    |-- WebControllerTest.java

src/main/resources/static/css/
|-- script.js
|-- style.css

src/main/resources/templates/
|-- mainPage.html
|-- mappingPage.html
|-- queryPage.html
|-- errorPage.html

```

**Note** : libs/library.jar come from **sim-elh-explainer-jar** : (https://github.com/TeerapatPho/sim-elh-explainer-jar/tree/main)

## Online Application

[Access the online application](http://54.66.58.44:8080)

## How to Use the Application

1. Enter username for creating folder

![alt text](images/image.png)

2. Upload all necessary files

- owl file
- mapping file
- database propertie file
- database driver file

![alt text](images/image-1.png)

3. Go to Mapping Section on Navigator Bar

- **Navigate to the Mapping Section**

  - Locate and select the Mapping Section from the navigation bar.
  - You will initially see your mapping file displayed.

- **Enter Your Base IRI**

  - Input your base IRI in the designated field.
  - If you press the button to generate the mapping, it will use Ontop bootstrapping and overwrite your existing file.
  - Ontop bootstrapping will provide you <your_ontology_name>\_tmp file on your folder

- **Edit Your Mapping File**

  - You need to modify your mapping file to ensure it works with your database and follows the .obda format.
  - Make the necessary edits to align with your database schema and configuration.

- **Save Your Mapping File**

  - After making the necessary edits, save your mapping file to ensure your changes are applied.

  ![alt text](images/image-2.png)

4. Go to Query Section on Navigator Bar

- you can select "Standard Query" to send a query that uses your mapping file, ontology (including your original ontology and Ontop bootstrapping ontology if available), and the database schema.

![alt text](images/image-3.png)

- you can select "Similarity Query" to perform a query similar to the "Standard Query," but with an additional step: it will generate a similarity class based on the threshold. This class will be appended to the mapping file, so you need to refresh the mapping page to see the result.

- you will see the "Explanation" section, which tells you which concepts were used to append to the mapping file. If you click on the "Details" button, it will show you all the reasons why these concept pairs were appended based on the similarity threshold.

![alt text](images/image-4.png)

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

## How to Deploy VKGSim on a Server

To deploy the VKGSim application on an EC2 instance, follow these steps:

### 1. Create an EC2 Instance

1. **Log in to AWS Management Console**:

   - Navigate to the [AWS Management Console](https://aws.amazon.com/console/).
   - Select the EC2 service.

2. **Launch an Instance**:

   - Click on "Launch Instance".
   - Choose an Amazon Machine Image (AMI). For this guide, select an Ubuntu Server AMI.
   - Select an instance type (e.g., t2.micro for testing).
   - Configure instance details, add storage, and configure security groups to allow inbound traffic on port 8080 (default for Spring Boot).

3. **Review and Launch**:
   - Review your instance configuration and click "Launch".
   - Choose or create a new key pair for SSH access, then launch the instance.

### 2. Install Ontop

1. **SSH into your EC2 Instance**:

   - Open your terminal.
   - Use the SSH command provided by AWS to connect to your EC2 instance. It will look something like this:
     ```sh
     ssh -i /path/to/your-key-pair.pem ubuntu@ec2-xx-xx-xx-xx.compute-1.amazonaws.com
     ```

2. **Install Ontop**:
   - Follow the setup guide for Ontop from their [official documentation](https://ontop-vkg.org/tutorial/basic/setup.html#database-setup) or [README.MD](ontop-cli/README.md). This includes downloading Ontop, setting up the database, and configuring Ontop CLI.

### 3. Clone the VKGSim Repository

1. **Install Git**:

   - If Git is not already installed, install it with the following command:
     ```sh
     sudo apt-get update
     sudo apt-get install git
     ```

2. **Clone the Repository**:
   - Run the following command to clone the VKGSim repository:
     ```sh
     git clone <repository-url>
     ```
   - Navigate into the cloned directory:
     ```sh
     cd <repository-folder-name>
     ```

### 4. Update the OntopController

1. **Edit OntopController.java**:

   - Open `OntopController.java` in your preferred text editor:
     ```sh
     nano src/main/java/io/github/vkgsim/controller/OntopController.java
     ```

2. **Modify the executeCommand function**:
   - Change the `ProcessBuilder` line from:
     ```java
     ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "cd ontop-cli && " + command);
     ```
     to:
     ```java
     ProcessBuilder processBuilder = new ProcessBuilder("", "-c", "cd ontop-cli && " + command);
     ```

### 5. Install Dependencies

1. **Install Maven**:

   - Ensure Maven is installed by running:
     ```sh
     sudo apt-get install maven
     ```

2. **Clean and Install Dependencies**:
   - Navigate to your project directory and run:
     ```sh
     mvn clean install
     ```

### 6. Run the Spring Boot Application

1. **Start the Application**:
   - Run the following command to start the Spring Boot application:
     ```sh
     mvn spring-boot:run
     ```

### 7. Access the Application

1. **Open your web browser**:
   - Navigate to `http://<ec2-public-ip>:8080` to access the VKGSim application.

### Additional Notes

- Ensure you have [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) installed (version 11 or later).
- Make sure your security group in AWS allows inbound traffic on the port you are using (default 8080).
- If you encounter any issues, verify your environment variables for Java and Maven are correctly set up.

## Authors and Contacts

Chaphowasit Mahayossanan (Mahidol University, Thailand)

- Email: chaphowasit.mah@student.mahidol.edu

Teerapat Phopit (Mahidol University, Thailand)

- Email: teerapat.pho@student.mahidol.edu

Teeradaj Racharak (JAIST, Japan)

- Email: racharak@jaist.ac.jp

Kiattiphum Suwanarsa (Mahidol University, Thailand)

- Email: kiattiphum.intern@gmail.com
