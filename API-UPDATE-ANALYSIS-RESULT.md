# Update Analysis Result JSON API

## Overview

A new API endpoint has been created to update the inspection analysis result JSON that is modified by the frontend user.

## Endpoint Details

### **PUT** `/transformer-thermal-inspection/image-analysis/result/update/{inspectionNo}/{transformerNo}`

Updates the `analysis_result_json` field in the `analysis_result` table for a specific inspection.

**Note:** The actual URL depends on your `base-url.context` configuration in `application.yml`.
If you're using `/api/v1` as context, the URL would be: `/api/v1/image-analysis/result/update/{inspectionNo}/{transformerNo}`

## Request

### Path Parameters

- `inspectionNo` (String, required): The inspection number
- `transformerNo` (String, required): The transformer number

### Request Body

- Content-Type: `application/json`
- Body: Raw JSON array containing the updated analysis results

### Example Request

```http
PUT http://localhost:5509/transformer-thermal-inspection/image-analysis/result/update/76/TX-1345
Content-Type: application/json

[
  {
    "id": "ai-box-2",
    "bbox": [60.39, 40.33, 4.06, 45.30],
    "center": [62.42, 62.98],
    "anomalyState": "Potentially Faulty",
    "confidenceScore": 100,
    "riskType": "Point fault",
    "description": "Significant temperature decrease detected...",
    "imageType": "result",
    "annotationType": "added",
    "modifiedBy": "user:AI",
    "confirmedBy": "confirmed by user",
    "addedBy": "user:AI",
    "addedAt": "2025-10-20T19:41:23.154Z",
    "editedBy": "user:John"
  },
  ...
]
```

## Response

### Success Response (200 OK)

```json
{
  "responseCode": "200",
  "responseDescription": "Analysis result JSON updated successfully",
  "data": {
    "id": 1,
    "inspectionNo": "1",
    "transformerNo": "T-001",
    "annotatedImageData": "...",
    "analysisDate": "2025-10-21T10:30:00",
    "analysisStatus": "SUCCESS",
    "analysisResultJson": "[{...}]",
    "errorMessage": null,
    "processingTimeMs": 150
  }
}
```

### Error Responses

#### 400 Bad Request - Analysis Result Not Found

```json
{
  "responseCode": "400",
  "responseDescription": "No analysis result found for inspection: 1",
  "data": null
}
```

#### 400 Bad Request - Transformer Mismatch

```json
{
  "responseCode": "400",
  "responseDescription": "Transformer number mismatch",
  "data": null
}
```

#### 500 Internal Server Error

```json
{
  "responseCode": "500",
  "responseDescription": "Failed to update analysis result JSON: {error details}",
  "data": null
}
```

## Frontend Integration

### JavaScript/TypeScript Example

```javascript
async function updateAnalysisResult(inspectionNo, transformerNo, analysisData) {
  // Adjust the base URL according to your base-url.context configuration
  const baseUrl = "http://localhost:5509/transformer-thermal-inspection";
  // Or if using /api/v1: const baseUrl = 'http://localhost:5509/api/v1';

  try {
    const response = await fetch(
      `${baseUrl}/image-analysis/result/update/${inspectionNo}/${transformerNo}`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include", // For CORS with credentials
        body: JSON.stringify(analysisData),
      }
    );

    const result = await response.json();

    if (result.responseCode === "200") {
      console.log("Analysis result updated successfully");
      return result.data;
    } else {
      console.error("Failed to update:", result.responseDescription);
      throw new Error(result.responseDescription);
    }
  } catch (error) {
    console.error("Error updating analysis result:", error);
    throw error;
  }
}

// Usage
const updatedJson = [
  {
    id: "1760990457845",
    bbox: [1.11, 22.42, 1.3, 59.04],
    anomalyState: "Faulty",
    confidenceScore: 43,
    userVerified: true,
    // ... rest of your JSON
  },
];

updateAnalysisResult("76", "TX-1345", updatedJson)
  .then((data) => console.log("Updated:", data))
  .catch((err) => console.error("Failed:", err));
```

## How It Works

1. **Validation**: The endpoint first checks if an analysis result exists for the given inspection number
2. **Transformer Verification**: It verifies that the provided transformer number matches the one stored in the database
3. **Update**: The `analysis_result_json` field is updated with the new JSON string
4. **Timestamp**: The `analysis_date` is updated to the current timestamp
5. **Save**: The updated record is saved to the database
6. **Response**: The updated `AnalysisResult` entity is returned

## Database Impact

### Table: `analysis_result`

- **Updated Column**: `analysis_result_json` (TEXT)
- **Updated Column**: `analysis_date` (TIMESTAMP)
- **Operation**: UPDATE (replaces existing JSON, does not add new records)

## Security Considerations

- CORS is configured to allow requests from:
  - `http://localhost:8080`
  - `http://localhost:3000`
  - `http://127.0.0.1:8080`
  - `https://arbit-frontend.vercel.app`
- Credentials are supported for authenticated requests
- The endpoint validates that the inspection exists before updating
- Transformer number validation prevents unauthorized updates

## Testing

### Using curl

```bash
# With default base-url.context (/transformer-thermal-inspection)
curl -X PUT \
  'http://localhost:5509/transformer-thermal-inspection/image-analysis/result/update/76/TX-1345' \
  -H 'Content-Type: application/json' \
  -d '[{"id":"1760990457845","anomalyState":"Faulty","confidenceScore":43,"userVerified":true}]'

# Or if using /api/v1 as base context
curl -X PUT \
  'http://localhost:5509/api/v1/image-analysis/result/update/76/TX-1345' \
  -H 'Content-Type: application/json' \
  -d '[{"id":"1","anomalyState":"Faulty","confidenceScore":100}]'
```

### Using Postman

1. Set method to **PUT**
2. URL: `http://localhost:5509/transformer-thermal-inspection/image-analysis/result/update/{inspectionNo}/{transformerNo}`
   - Replace `{inspectionNo}` with your inspection number (e.g., `76`)
   - Replace `{transformerNo}` with your transformer number (e.g., `TX-1345`)
3. Headers: `Content-Type: application/json`
4. Body: Select **raw** and **JSON**, paste your JSON array
5. Send request

## Files Modified

1. **New File**: `UpdateAnalysisResultRequest.java` - Request DTO (for future use)
2. **Updated**: `ImageAnalysisService.java` - Added `updateAnalysisResultJson` method signature
3. **Updated**: `ImageAnalysisServiceImpl.java` - Implemented `updateAnalysisResultJson` method
4. **Updated**: `ImageAnalysisController.java` - Added PUT endpoint

## Notes

- The endpoint expects the **entire JSON array** from the frontend, not individual items
- The update operation **replaces** the existing JSON completely
- Both `inspectionNo` and `transformerNo` are required for security validation
- The annotated image data is NOT updated by this endpoint (only the JSON metadata)
