### Image API Guide

#### **Base URL**
`http://localhost:5509`

---

### **1. Baseline Images API**

#### **POST** `/api/baseline-images`

- **Description**: Upload a baseline image with metadata.
- **Request Body**:
  ```json
  {
    "transformerId": "string", // ID of the associated transformer
    "name": "string",          // Name of the image
    "type": "string",          // Type of the image (e.g., "image/png")
    "data": "byte[]",          // Base64-encoded image data
    "uploader": "string",      // Uploader's name or ID
    "environmentCondition": "string" // "Sunny", "Cloudy", or "Rainy"
  }
  ```
- **Response**:
  ```json
  {
    "id": "string",            // ID of the saved image
    "transformerId": "string", // Associated transformer ID
    "name": "string",          // Name of the image
    "type": "string",          // Type of the image
    "uploader": "string",      // Uploader's name or ID
    "environmentCondition": "string" // Environmental condition
  }
  ```

---

#### **GET** `/api/baseline-images/{id}`

- **Description**: Retrieve a baseline image by its ID.
- **Path Parameter**:
    - `id`: The ID of the baseline image.
- **Response**:
  ```json
  {
    "id": "string",            // ID of the image
    "transformerId": "string", // Associated transformer ID
    "name": "string",          // Name of the image
    "type": "string",          // Type of the image
    "data": "byte[]",          // Base64-encoded image data
    "uploadDateTime": "string",// Upload timestamp
    "uploader": "string",      // Uploader's name or ID
    "environmentCondition": "string" // Environmental condition
  }
  ```

---

#### **DELETE** `/api/baseline-images/{id}`

- **Description**: Delete a baseline image by its ID.
- **Path Parameter**:
    - `id`: The ID of the baseline image.
- **Response**: `204 No Content`

---

### **2. Inspection Images API**

#### **POST** `/api/inspection-images`

- **Description**: Upload an inspection image with metadata.
- **Request Body**:
  ```json
  {
    "inspectionId": "string",  // ID of the associated inspection record
    "name": "string",          // Name of the image
    "type": "string",          // Type of the image (e.g., "image/png")
    "data": "byte[]",          // Base64-encoded image data
    "uploader": "string",      // Uploader's name or ID
    "environmentCondition": "string" // "Sunny", "Cloudy", or "Rainy"
  }
  ```
- **Response**:
  ```json
  {
    "id": "string",            // ID of the saved image
    "inspectionId": "string",  // Associated inspection record ID
    "name": "string",          // Name of the image
    "type": "string",          // Type of the image
    "uploader": "string",      // Uploader's name or ID
    "environmentCondition": "string" // Environmental condition
  }
  ```

---

#### **GET** `/api/inspection-images/{id}`

- **Description**: Retrieve an inspection image by its ID.
- **Path Parameter**:
    - `id`: The ID of the inspection image.
- **Response**:
  ```json
  {
    "id": "string",            // ID of the image
    "inspectionId": "string",  // Associated inspection record ID
    "name": "string",          // Name of the image
    "type": "string",          // Type of the image
    "data": "byte[]",          // Base64-encoded image data
    "uploadDateTime": "string",// Upload timestamp
    "uploader": "string",      // Uploader's name or ID
    "environmentCondition": "string" // Environmental condition
  }
  ```

---

#### **DELETE** `/api/inspection-images/{id}`

- **Description**: Delete an inspection image by its ID.
- **Path Parameter**:
    - `id`: The ID of the inspection image.
- **Response**: `204 No Content`

---

### Notes:
1. **Base64 Encoding**: Use JavaScript's `FileReader` API to convert image files to Base64 strings before sending them in the `data` field.
2. **Error Handling**: Handle `404 Not Found` for `GET` requests and ensure proper validation for `POST` requests.
3. **Headers**: Include `Content-Type: application/json` in all requests.

### Image Record Connection

#### **1. Image Storage**

- **Database**: Images are stored in a **MongoDB** database.
- **Collections**:
    - `BaselineImage`: Stores baseline images.
    - `InspectionImage`: Stores inspection images.
- **Fields**:
    - `id`: Unique identifier for the image.
    - `transformerId` or `inspectionId`: Links the image to a specific transformer or inspection record.
    - `name`: Name of the image.
    - `type`: MIME type of the image (e.g., `image/png`).
    - `data`: Binary data of the image (stored as a `byte[]`).
    - `uploadDateTime`: Timestamp of when the image was uploaded.
    - `uploader`: Name or ID of the uploader.
    - `environmentCondition`: Environmental condition during the image capture (e.g., "Sunny").

---

#### **2. Connection to Records**

- **Baseline Images**:
    - Each baseline image is linked to a **transformer record** via the `transformerId` field.
    - This ensures that baseline images are associated with specific transformers for inspection purposes.

- **Inspection Images**:
    - Each inspection image is linked to an **inspection record** via the `inspectionId` field.
    - This allows inspection images to be tied to specific inspection events.

---

#### **3. Workflow**

1. **Uploading an Image**:
    - The client sends a `POST` request with metadata (`transformerId` or `inspectionId`, `name`, etc.) and the image data (Base64-encoded).
    - The server saves the image in the database and links it to the corresponding record.

2. **Retrieving an Image**:
    - The client sends a `GET` request with the image ID.
    - The server fetches the image from the database and returns its metadata and binary data.

3. **Deleting an Image**:
    - The client sends a `DELETE` request with the image ID.
    - The server removes the image from the database.

---

#### **4. Example Database Documents**

- **BaselineImage**:
  ```json
  {
    "id": "123",
    "transformerId": "T001",
    "name": "baseline1.png",
    "type": "image/png",
    "data": "<binary_data>",
    "uploadDateTime": "2023-10-01T12:00:00",
    "uploader": "John Doe",
    "environmentCondition": "Sunny"
  }
  ```

- **InspectionImage**:
  ```json
  {
    "id": "456",
    "inspectionId": "I001",
    "name": "inspection1.png",
    "type": "image/png",
    "data": "<binary_data>",
    "uploadDateTime": "2023-10-02T15:30:00",
    "uploader": "Jane Smith",
    "environmentCondition": "Cloudy"
  }
  ```

---

#### **5. Notes **

- **Binary Data**: Images are stored as binary data (`byte[]`) in MongoDB.
- **Linking**: Always ensure `transformerId` or `inspectionId` is provided to maintain the connection between images and records.
- **Validation**: Validate the metadata (e.g., `type`, `environmentCondition`) before saving the image.

## **Backend Files**

Here is the list of files you need to modify for the APIs:

1. **Controllers**:
    - `Software-design-competition/src/main/java/com/uom/Software_design_competition/application/controller/BaselineImageController.java`
    - `Software-design-competition/src/main/java/com/uom/Software_design_competition/application/controller/InspectionImageController.java`

2. **Services**:
    - `Software-design-competition/src/main/java/com/uom/Software_design_competition/domain/service/BaselineImageService.java`
    - `Software-design-competition/src/main/java/com/uom/Software_design_competition/domain/service/InspectionImageService.java`

3. **Repositories**:
    - `Software-design-competition/src/main/java/com/uom/Software_design_competition/domain/repository/BaselineImageRepository.java`
    - `Software-design-competition/src/main/java/com/uom/Software_design_competition/domain/repository/InspectionImageRepository.java`

4. **Entities**:
    - `Software-design-competition/src/main/java/com/uom/Software_design_competition/domain/entity/BaselineImage.java`
    - `Software-design-competition/src/main/java/com/uom/Software_design_competition/domain/entity/InspectionImage.java`

5. **DTOs**:
    - `Software-design-competition/src/main/java/com/uom/Software_design_competition/application/transport/request/BaselineImageRequest.java`
    - `Software-design-competition/src/main/java/com/uom/Software_design_competition/application/transport/response/BaselineImageResponse.java`
    - `Software-design-competition/src/main/java/com/uom/Software_design_competition/application/transport/request/InspectionImageRequest.java`
    - `Software-design-competition/src/main/java/com/uom/Software_design_competition/application/transport/response/InspectionImageResponse.java`

6. **Configuration** (if needed for database or application settings):
    - `Software-design-competition/src/main/resources/application.yml`

These files collectively define the API endpoints, business logic, data access, and data transfer objects.