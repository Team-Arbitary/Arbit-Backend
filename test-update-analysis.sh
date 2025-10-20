#!/bin/bash

# Test script for the Update Analysis Result JSON endpoint

# Configuration
BASE_URL="http://localhost:5509/transformer-thermal-inspection/image-analysis"
INSPECTION_NO="76"
TRANSFORMER_NO="TX-1345"

# Your payload
PAYLOAD='[{"id":"1760990457845","bbox":[1.1131725417439702,22.415153906866614,1.2987012987012987,59.037095501183906],"center":[1.7625231910946195,51.93370165745857],"area":76.67155259894014,"anomalyState":"Faulty","confidenceScore":43,"riskType":"Full wire overload","description":"","imageType":"result","avg_temp_change":64.5,"max_temp_change":73.1,"severity":0.43,"type":"heating","confidence":0.43,"reasoning":"Faulty anomaly detected - Full wire overload","consensus_score":0.5,"severity_level":"HIGH","severity_color":[0,0,255],"annotationType":"added","source":"manual","modifiedBy":"hasitha@gmail.com","confirmedBy":"hasitha@gmail.com","addedBy":"hasitha@gmail.com","addedAt":"2025-10-20T20:01:07.973Z","createdBy":"hasitha@gmail.com","userVerified":true},{"id":"1","bbox":[37.82467532467532,40.607734806629836,3.084415584415595,23.20441988950276],"center":[39.366883116883116,52.209944751381215],"area":71.5720743345055,"anomalyState":"Potentially Faulty","confidenceScore":100,"riskType":"Point fault","description":"Significant temperature decrease detected. Localized thermal anomaly. Extreme peak temperature detected. Possible local cooling or heat dissipation.","imageType":"result","avg_temp_change":150,"max_temp_change":170,"severity":1,"type":"heating","confidence":1,"reasoning":"Significant temperature decrease detected. Localized thermal anomaly. Extreme peak temperature detected. Possible local cooling or heat dissipation.","consensus_score":0.5,"severity_level":"MEDIUM","severity_color":[0,165,255],"annotationType":"added","source":"ai","modifiedBy":"user:AI","confirmedBy":"not confirmed by the user","addedBy":"user:AI","addedAt":"2025-10-20T20:05:09.425Z","editedBy":"none","createdBy":"user:AI","userVerified":false,"aiGenerated":true}]'

echo "Testing Update Analysis Result JSON endpoint..."
echo "URL: ${BASE_URL}/result/update/${INSPECTION_NO}/${TRANSFORMER_NO}"
echo ""

# Make the request
response=$(curl -s -w "\n%{http_code}" -X PUT \
  "${BASE_URL}/result/update/${INSPECTION_NO}/${TRANSFORMER_NO}" \
  -H "Content-Type: application/json" \
  -d "${PAYLOAD}")

# Extract status code and body
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | sed '$d')

echo "HTTP Status Code: ${http_code}"
echo ""
echo "Response Body:"
echo "${body}" | python3 -m json.tool 2>/dev/null || echo "${body}"

# Check result
if [ "$http_code" = "200" ]; then
    echo ""
    echo "✅ SUCCESS: Analysis result updated successfully!"
else
    echo ""
    echo "❌ FAILED: Request failed with status code ${http_code}"
fi
