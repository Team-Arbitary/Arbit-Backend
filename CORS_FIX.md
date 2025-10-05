# CORS Configuration Fix for Vercel Frontend

## Problem

Frontend deployed at `https://arbit-frontend.vercel.app` was getting CORS error:
```
Failed to load resource: Origin https://arbit-frontend.vercel.app is not allowed by Access-Control-Allow-Origin. Status code: 403
```

## Root Cause

The backend CORS configuration only allowed localhost origins:
- `http://localhost:8080`
- `http://localhost:3000`
- `http://127.0.0.1:8080`

The Vercel frontend URL was not in the allowed origins list.

## Solution Applied

### 1. Updated Global CORS Configuration

**File**: `app/src/main/java/com/uom/Software_design_competition/application/config/CorsConfig.java`

**Changes**:
- Added `https://arbit-frontend.vercel.app` to allowed origins in both methods
- Fixed the `corsConfigurationSource()` to use specific origins instead of wildcard (required when using credentials)

```java
@Override
public void addCorsMappings(@NonNull CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:8080", 
                "http://localhost:3000", 
                "http://127.0.0.1:8080",
                "https://arbit-frontend.vercel.app"  // ✅ Added
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
}
```

### 2. Updated Controller-Level CORS Annotations

Updated `@CrossOrigin` annotations on all controllers:

**Files Updated**:
1. ✅ `ImageAnalysisController.java`
2. ✅ `ImageInspectionManagementController.java`
3. ✅ `InspectionManagementController.java`
4. ✅ `TransformerManagementController.java`

**Change Applied**:
```java
@CrossOrigin(origins = {
    "http://localhost:8080", 
    "http://localhost:3000", 
    "http://127.0.0.1:8080", 
    "https://arbit-frontend.vercel.app"  // ✅ Added
}, allowCredentials = "true")
```

## Allowed Origins Summary

The backend now accepts requests from:
- ✅ `http://localhost:8080` - Local development
- ✅ `http://localhost:3000` - React dev server
- ✅ `http://127.0.0.1:8080` - Alternative localhost
- ✅ `https://arbit-frontend.vercel.app` - Production frontend

## CORS Configuration Details

**Allowed Methods**:
- GET
- POST
- PUT
- DELETE
- OPTIONS
- PATCH

**Allowed Headers**: All (`*`)

**Credentials**: Enabled (`true`)
- This allows cookies and authentication headers
- Required for session-based authentication

**Max Age**: 3600 seconds (1 hour)
- Browser caches preflight OPTIONS requests for this duration

## Testing

### Test from Vercel Frontend
```javascript
// Frontend code should now work
fetch('https://your-backend.onrender.com/transformer-thermal-inspection/inspection-management/view-all', {
  credentials: 'include',  // Important: include credentials
  headers: {
    'Content-Type': 'application/json'
  }
})
```

### Test with curl
```bash
# Preflight request
curl -X OPTIONS \
  -H "Origin: https://arbit-frontend.vercel.app" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: Content-Type" \
  https://your-backend.onrender.com/transformer-thermal-inspection/inspection-management/view-all

# Actual request
curl -X GET \
  -H "Origin: https://arbit-frontend.vercel.app" \
  -H "Content-Type: application/json" \
  https://your-backend.onrender.com/transformer-thermal-inspection/inspection-management/view-all
```

## Important Notes

### 1. Credentials and Wildcards
❌ **Cannot use**: `allowedOrigins("*")` with `allowCredentials(true)`
✅ **Must use**: Specific origin list when credentials are enabled

### 2. Deploy Changes
After making these changes, you MUST:
1. Rebuild the application
2. Redeploy to Render
3. Wait for deployment to complete (~3-5 minutes)

### 3. Adding More Origins
To add more frontend URLs (e.g., preview deployments):

**Option A**: Add specific URLs
```java
.allowedOrigins(
    "http://localhost:8080",
    "http://localhost:3000",
    "https://arbit-frontend.vercel.app",
    "https://arbit-frontend-preview.vercel.app"  // Add here
)
```

**Option B**: Use pattern matching (if needed)
```java
configuration.setAllowedOriginPatterns(Arrays.asList(
    "http://localhost:*",
    "https://*.vercel.app"
));
```

## Deployment Steps

### For Local Testing
```bash
cd app
./mvnw spring-boot:run
```

### For Docker
```bash
# Build and run
docker-compose down
docker-compose build
docker-compose up -d
```

### For Render
1. Commit changes:
   ```bash
   git add .
   git commit -m "Fix CORS for Vercel frontend"
   git push origin main
   ```

2. Render will auto-deploy (or manually trigger in dashboard)

3. Verify deployment:
   ```bash
   curl -H "Origin: https://arbit-frontend.vercel.app" \
     https://your-app.onrender.com/transformer-thermal-inspection/actuator/health
   ```

## Verification Checklist

After deployment, verify:

- [ ] Backend deployed successfully
- [ ] No errors in backend logs
- [ ] Frontend can load data from backend
- [ ] No CORS errors in browser console
- [ ] Authenticated requests work (if applicable)
- [ ] All CRUD operations work from frontend

## Troubleshooting

### Still Getting CORS Errors?

1. **Check Response Headers**:
   - Open browser DevTools → Network tab
   - Look for the preflight OPTIONS request
   - Check `Access-Control-Allow-Origin` header in response

2. **Verify Backend URL**:
   - Ensure frontend is calling the correct backend URL
   - Check for `http` vs `https` mismatch

3. **Clear Cache**:
   - Clear browser cache
   - Try incognito/private mode
   - Hard refresh (Ctrl+Shift+R or Cmd+Shift+R)

4. **Check Logs**:
   ```bash
   # In Render dashboard
   View Logs → Look for CORS-related errors
   ```

5. **Verify Origin Match**:
   - Origin must match EXACTLY (including protocol and trailing slashes)
   - `https://arbit-frontend.vercel.app` ≠ `https://arbit-frontend.vercel.app/`

### Frontend Still Blocked?

Check if frontend is using credentials:
```javascript
// Make sure to include credentials if using authentication
fetch(url, {
  credentials: 'include',  // Important!
  // ...
})
```

### Multiple Vercel Deployments?

If you have preview deployments, you may need to allow pattern:
```java
configuration.setAllowedOriginPatterns(Arrays.asList(
    "http://localhost:*",
    "https://arbit-frontend*.vercel.app"
));
```

## Security Considerations

✅ **Good Practices Applied**:
- Specific origin list (not wildcard)
- Credentials enabled only for trusted origins
- Reasonable max age for preflight cache

⚠️ **Additional Recommendations**:
1. Use HTTPS in production (Render provides this)
2. Implement authentication/authorization
3. Rate limit API endpoints
4. Monitor for unusual CORS requests
5. Keep origin list minimal and up-to-date

## Reference Links

- [Spring Boot CORS Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-cors)
- [MDN CORS Guide](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
- [Render.com CORS FAQ](https://render.com/docs/cors)

---

**Status**: ✅ CORS Configuration Updated

**Next Step**: Deploy to Render and test with frontend
