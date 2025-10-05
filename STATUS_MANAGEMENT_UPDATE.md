# Status Management Update

## Overview
Updated the status management system for inspections and isolated images to properly reflect analysis completion state.

## Status Rules Implemented

### 1. Inspection Status (InspectionRecords)
- **Complete**: When a Result image exists for the inspection (analysis has been completed)
- **Not Started**: When no Result image exists for the inspection (analysis not done yet)

### 2. Image Status (ImageInspect - Baseline, Thermal, Result)
- **Complete**: When analysis is done and Result image exists
- **Not Started**: When analysis has not been done yet (no Result image)

## Files Modified

### 1. ImageAnalysisServiceImpl.java
**Changes:**
- Updated status from "Completed" to "Complete" for consistency
- Result image status set to "Complete" when analysis completes
- Baseline and Thermal images status updated to "Complete" after successful analysis

**Key Methods:**
- `performAnalysis()`: Sets all image statuses to "Complete" after successful analysis

### 2. InspectionManagementServiceImpl.java
**Changes:**
- Updated `getStatusFromImageInspect()` method to calculate status dynamically
- Status now determined by checking if Result image exists using `existsByInspectionNoAndImageType()`
- Simplified logic: Result exists = "Complete", No result = "Not Started"

**Key Methods:**
- `getInspectionsByTransformerNo()`: Returns inspections with calculated status
- `getInspectionById()`: Returns inspection with calculated status
- `getAllInspections()`: Returns all inspections with calculated status
- `getStatusFromImageInspect()`: Helper method that determines status based on Result image existence

### 3. ImageInspectionManagementServiceImpl.java
**Changes:**
- Removed intermediate "In progress" status from update operations
- All uploads and updates now set status to "Not Started" initially
- GET methods now calculate status dynamically based on Result image existence

**Key Methods:**
- `uploadImage()`: Sets initial status to "Not Started"
- `updateBaselineImageByTransformerNo()`: Sets status to "Not Started" after update
- `updateThermalImageByInspectionNo()`: Sets status to "Not Started" after update
- `getBaselineImageByTransformerNo()`: Calculates and returns current status dynamically
- `getThermalImageByInspectionNo()`: Calculates and returns current status dynamically
- `getResultImageByInspectionNo()`: Always returns "Complete" status

## Status Flow

### Normal Workflow:
1. **Upload Baseline Image** → Status: "Not Started"
2. **Upload Thermal Image** → Status: "Not Started"
3. **Trigger Manual Analysis** → Analysis runs
4. **Analysis Completes** → 
   - Creates Result image with status "Complete"
   - Updates Baseline and Thermal images to "Complete"
   - Inspection status becomes "Complete"

### Image Update:
1. **Update Baseline/Thermal Image** → Status resets to "Not Started"
2. **Need to re-run analysis** → Manual trigger required
3. **Analysis Completes** → Status becomes "Complete" again

### Image Deletion:
1. **Delete Baseline or Thermal Image** → 
   - Deletes Result image if exists
   - Resets remaining images to "Not Started"
   - Inspection status becomes "Not Started"

## API Behavior

### GET /inspections (all variations)
- Returns inspection records with dynamically calculated status
- Status reflects whether analysis result exists

### GET /baseline/{transformerNo}
- Returns baseline image with dynamically calculated status
- Status is "Complete" if Result exists, "Not Started" otherwise

### GET /thermal/{inspectionNo}
- Returns thermal image with dynamically calculated status
- Status is "Complete" if Result exists, "Not Started" otherwise

### GET /result/{inspectionNo}
- Returns result image with status always set to "Complete"

### POST /analyze/{inspectionNo}
- Triggers manual analysis
- Upon success, creates Result image and updates all related statuses to "Complete"

## Testing Recommendations

1. **Test Initial Upload**
   - Upload baseline image → Verify status is "Not Started"
   - Upload thermal image → Verify status is "Not Started"
   - Verify inspection status is "Not Started"

2. **Test Analysis Completion**
   - Trigger analysis → Verify successful completion
   - Check Result image exists with status "Complete"
   - Verify baseline and thermal images show "Complete"
   - Verify inspection status shows "Complete"

3. **Test Image Update**
   - Update baseline or thermal image
   - Verify status resets to "Not Started"
   - Verify inspection status shows "Not Started"

4. **Test Image Deletion**
   - Delete baseline or thermal image
   - Verify Result image is deleted
   - Verify remaining images show "Not Started"
   - Verify inspection status shows "Not Started"

## Database Consistency

The status is now calculated dynamically based on:
- **Result image existence**: `SELECT EXISTS(SELECT 1 FROM image_inspect WHERE inspection_no = ? AND image_type = 'Result')`

This ensures:
- Status is always accurate regardless of database state
- No stale status values in the database
- Consistent behavior across all API endpoints
