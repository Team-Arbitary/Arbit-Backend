# Transformer Thermal Inspection - Arbit Backend

This repository contains backend for managing transformer thermal inspections, built with Spring Boot. It integrates with a PostgreSQL database and exposes a RESTful API.

-----

## Prerequisites

Ensure the following are installed on your system before proceeding with the setup:

* **JDK 17+**
* **Maven 3.9.+**
* **PostgreSQL**
* **IntelliJ IDEA** or **VS Code**

-----

## Setup Instructions

### 1. Database Configuration

To get your backend running, you'll need to configure the PostgreSQL database and the Spring Boot application properties. Here are the essential configurations you need to set.

#### PostgreSQL Database Configuration

You must ensure that a PostgreSQL database is created and that the application's user credentials match the database setup.

* **Database Name**: Create a database named `trasformer_inspection_db`.
* **User and Password**: The application uses the username `postgres` and password `1234`. You need to ensure a user with these credentials exists and has privileges to access the database.

-----

### 2. Running the Backend

#### Using IntelliJ IDEA

1. **Open the Project**: Launch IntelliJ IDEA and import the project as a Maven project. IntelliJ will automatically download the necessary dependencies.
2. **Locate the Main Class**: Navigate to the main application class at `src/main/java/com/uom/Software_design_competition/SoftwareDesignCompetitionApplication.java`.
3. **Run the Application**: Right-click on `SoftwareDesignCompetitionApplication.java` and select **Run 'SoftwareDesignCompetitionApp...\`**.

#### Using VS Code

1. **Open the Folder**: Open the project folder in VS Code.
2. **Install Extensions**: If you haven't already, install the **Java Extension Pack** from the VS Code Marketplace.
3. **Build and Run**: Open the integrated terminal (Ctrl+\`) and run the following Maven commands:
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

Once the application starts, it will be live at:

* **Port**: `5509`
* **Base Context URL**: `/transformer-thermal-inspection`

-----

## Configuration Details

The core configurations for the application are found in `src/main/resources/application.yml`.

* **Database**: The `spring.datasource` section defines the PostgreSQL connection URL, username, and password.
* **Server**: The `server` section sets the application's port and base URL context.
* **Logging**: The `log` section includes configurations for a UUID identifier key and debug mode.
* **REST**: The `rest` section defines connection and read timeouts.

-----

## Features

### Available APIs

#### 1. Transformer Management APIs
- **Create Transformer Record**
    - **Endpoint**: `/transformer-management/create`
    - **Method**: POST
    - **Description**: Adds a new transformer record.

- **Get Transformer by ID**
    - **Endpoint**: `/transformer-management/view/{id}`
    - **Method**: GET
    - **Description**: Retrieves a transformer record by its ID.

- **Get All Transformers**
    - **Endpoint**: `/transformer-management/view-all`
    - **Method**: GET
    - **Description**: Retrieves all transformer records.

- **Update Transformer**
    - **Endpoint**: `/transformer-management/update`
    - **Method**: PUT
    - **Description**: Updates an existing transformer record.

- **Delete Transformer**
    - **Endpoint**: `/transformer-management/delete/{id}`
    - **Method**: DELETE
    - **Description**: Deletes a transformer record by its ID.

- **Filter Transformer Records**
    - **Endpoint**: `/transformer-management/filter`
    - **Method**: POST
    - **Description**: Filters transformer records based on specific criteria.

---

#### 2. Inspection Management APIs
- **Create Inspection Record**
    - **Endpoint**: `/inspection-management/create`
    - **Method**: POST
    - **Description**: Adds a new inspection record.

- **Get Inspection by ID**
    - **Endpoint**: `/inspection-management/view/{id}`
    - **Method**: GET
    - **Description**: Retrieves an inspection record by its ID.

- **Get Inspections by Transformer Number**
    - **Endpoint**: `/inspection-management/transformer/{transformerNo}`
    - **Method**: GET
    - **Description**: Retrieves all inspections for a specific transformer.

- **Get All Inspections**
    - **Endpoint**: `/inspection-management/view-all`
    - **Method**: GET
    - **Description**: Retrieves all inspection records.

- **Update Inspection**
    - **Endpoint**: `/inspection-management/update`
    - **Method**: PUT
    - **Description**: Updates an existing inspection record.

- **Delete Inspection**
    - **Endpoint**: `/inspection-management/delete/{id}`
    - **Method**: DELETE
    - **Description**: Deletes an inspection record by its ID.

- **Filter Inspection Records**
    - **Endpoint**: `/inspection-management/filter`
    - **Method**: POST
    - **Description**: Filters inspection records based on specific criteria.

---

#### 3. Image Inspection Management APIs
- **Upload Image**
    - **Endpoint**: `/image-inspection-management/upload`
    - **Method**: POST
    - **Description**: Uploads a baseline or thermal image.

- **Get Baseline Image**
    - **Endpoint**: `/image-inspection-management/baseline/{transformerNo}`
    - **Method**: GET
    - **Description**: Retrieves the baseline image for a specific transformer.

- **Update Baseline Image**
    - **Endpoint**: `/image-inspection-management/baseline/{transformerNo}`
    - **Method**: PUT
    - **Description**: Updates the baseline image for a specific transformer.

- **Delete Baseline Image**
    - **Endpoint**: `/image-inspection-management/baseline/{transformerNo}`
    - **Method**: DELETE
    - **Description**: Deletes the baseline image for a specific transformer.

- **Get Thermal Image**
    - **Endpoint**: `/image-inspection-management/thermal/{inspectionNo}`
    - **Method**: GET
    - **Description**: Retrieves the thermal image for a specific inspection.

- **Update Thermal Image**
    - **Endpoint**: `/image-inspection-management/thermal/{inspectionNo}`
    - **Method**: PUT
    - **Description**: Updates the thermal image for a specific inspection.

- **Delete Thermal Image**
    - **Endpoint**: `/image-inspection-management/thermal/{inspectionNo}`
    - **Method**: DELETE
    - **Description**: Deletes the thermal image for a specific inspection.
### Database Integration
- **PostgreSQL**: The backend uses PostgreSQL for data persistence, ensuring reliable and scalable storage of inspection and transformer data.

-----

## Limitations and Known Issues

* **No Authentication/Authorization**: The backend currently lacks any form of user authentication or authorization, making all endpoints publicly accessible.
* **Limited Error Handling**: Error handling for database and API failures is minimal.
* **Port Conflicts**: The application will fail to start if port `5509` is already in use. You can resolve this by changing the `server.port` in the `application.yml` file.
* **Database Connectivity**: The application is dependent on a running PostgreSQL service. Ensure the database is accessible before starting the backend; otherwise, it will fail to launch.