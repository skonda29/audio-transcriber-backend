# 🧪 Postman Testing Guide for Audio Transcription Service

## 🚀 Quick Setup

### 1. **Environment Setup**
```bash
# 1. Set your OpenAI API key in application.properties
echo "spring.ai.openai.api-key=sk-your-actual-api-key-here" > src/main/resources/application.properties

# 2. Start the application
./mvnw spring-boot:run

# 3. Verify service is running
curl http://localhost:8080/actuator/health
```

### 2. **Required Audio Files**
- **Supported formats**: WAV, MP3, M4A, FLAC, OGG
- **Max file size**: 25MB (OpenAI limit)
- **Recommended**: Short audio clips (30 seconds - 2 minutes) for testing

---

## 📡 **Postman Collection Setup**

### **Collection Variables**
```json
{
  "base_url": "http://localhost:8080",
  "api_path": "/api/transcribe"
}
```

---

## 🎯 **Endpoint 1: Full Transcription + Summary**

### **Request Configuration**
```
Method: POST
URL: {{base_url}}{{api_path}}
```

### **Headers**
```
Content-Type: multipart/form-data
Accept: application/json
```

### **Body (form-data)**
```
Key: file
Type: File
Value: [Select your audio file]
```

### **Expected Response**
```json
{
  "transcription": "Hello, this is a sample audio recording where I discuss the importance of artificial intelligence in modern business applications...",
  "summary": "The speaker discusses AI's role in business, covering key points about automation, efficiency improvements, and future technological trends.",
  "transcriptionLength": 156,
  "summaryLength": 127,
  "timestamp": "2024-01-15T10:30:45.123Z"
}
```

### **Postman Tests Script**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response contains transcription and summary", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('transcription');
    pm.expect(jsonData).to.have.property('summary');
    pm.expect(jsonData).to.have.property('timestamp');
    pm.expect(jsonData.transcriptionLength).to.be.above(0);
    pm.expect(jsonData.summaryLength).to.be.above(0);
});

pm.test("Response time is less than 30 seconds", function () {
    pm.expect(pm.response.responseTime).to.be.below(30000);
});
```

---

## 🎯 **Endpoint 2: Summary Only**

### **Request Configuration**
```
Method: POST
URL: {{base_url}}{{api_path}}/summary-only
```

### **Headers**
```
Content-Type: multipart/form-data
Accept: application/json
```

### **Body (form-data)**
```
Key: file
Type: File
Value: [Select your audio file]
```

### **Expected Response**
```json
{
  "transcription": "",
  "summary": "The speaker discusses AI's role in business, covering automation, efficiency improvements, and future trends.",
  "transcriptionLength": 0,
  "summaryLength": 98,
  "timestamp": "2024-01-15T10:32:15.456Z"
}
```

### **Postman Tests Script**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response contains summary only", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData.transcription).to.equal("");
    pm.expect(jsonData.summary).to.not.be.empty;
    pm.expect(jsonData.transcriptionLength).to.equal(0);
    pm.expect(jsonData.summaryLength).to.be.above(0);
});
```

---

## 🧪 **Test Scenarios**

### **1. Valid Audio File Test**
- **File**: Upload a clear audio recording
- **Expected**: Success with both transcription and summary
- **Response Time**: < 30 seconds

### **2. Different Audio Formats**
```
Test Files:
├── sample.wav (recommended)
├── sample.mp3
├── sample.m4a
└── sample.flac
```

### **3. Edge Cases**
```
Scenario A: Very Short Audio (< 1 second)
├── Expected: Basic transcription with minimal summary
└── Status: 200 OK

Scenario B: Silent Audio
├── Expected: "No content to summarize" message
└── Status: 200 OK

Scenario C: Large File (> 25MB)
├── Expected: OpenAI API error
└── Status: May vary based on error handling
```

### **4. Error Testing**
```
Test Case 1: No File Uploaded
├── Request: Empty form-data
├── Expected: 400 Bad Request
└── Response: Error message about missing file

Test Case 2: Invalid File Type
├── Request: Upload .txt file
├── Expected: Error from OpenAI API
└── Response: Error handling message

Test Case 3: Invalid API Key
├── Request: Wrong API key in properties
├── Expected: 401 Unauthorized
└── Response: Authentication error
```

---

## 📊 **Performance Testing**

### **Response Time Benchmarks**
```
File Size vs Expected Response Time:
├── 1MB audio (~1 min): 5-10 seconds
├── 5MB audio (~5 min): 10-20 seconds
├── 10MB audio (~10 min): 15-30 seconds
└── 25MB audio (~25 min): 25-45 seconds
```

### **Load Testing Setup**
```javascript
// Collection Runner Configuration
{
  "iterations": 5,
  "delay": 5000,
  "data": [
    {"audio_file": "sample1.wav"},
    {"audio_file": "sample2.mp3"},
    {"audio_file": "sample3.m4a"}
  ]
}
```

---

## 🔧 **Troubleshooting**

### **Common Issues**

#### **Issue 1: Connection Refused**
```
Error: connect ECONNREFUSED 127.0.0.1:8080
Solution: 
1. Ensure application is running: ./mvnw spring-boot:run
2. Check port availability: lsof -i :8080
3. Verify application started successfully in logs
```

#### **Issue 2: OpenAI API Key Error**
```
Error: 401 Unauthorized / Invalid API key
Solution:
1. Set valid API key in application.properties
2. Ensure API key has sufficient credits
3. Check OpenAI API status
```

#### **Issue 3: File Upload Error**
```
Error: 400 Bad Request / MultipartException
Solution:
1. Use form-data in Postman body
2. Set key name as "file"
3. Select actual audio file
4. Don't set Content-Type manually (let Postman handle it)
```

#### **Issue 4: Timeout Error**
```
Error: Request timeout
Solution:
1. Increase Postman timeout to 60 seconds
2. Use smaller audio files for testing
3. Check internet connectivity for OpenAI API calls
```

---

## 📱 **Postman Collection Export**

```json
{
  "info": {
    "name": "Audio Transcription Service",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Transcribe + Summarize",
      "request": {
        "method": "POST",
        "header": [],
        "body": {
          "mode": "formdata",
          "formdata": [
            {
              "key": "file",
              "type": "file",
              "src": []
            }
          ]
        },
        "url": {
          "raw": "{{base_url}}/api/transcribe",
          "host": ["{{base_url}}"],
          "path": ["api", "transcribe"]
        }
      }
    },
    {
      "name": "Summary Only",
      "request": {
        "method": "POST",
        "header": [],
        "body": {
          "mode": "formdata",
          "formdata": [
            {
              "key": "file",
              "type": "file",
              "src": []
            }
          ]
        },
        "url": {
          "raw": "{{base_url}}/api/transcribe/summary-only",
          "host": ["{{base_url}}"],
          "path": ["api", "transcribe", "summary-only"]
        }
      }
    }
  ]
}
```

---

## 🎯 **Success Criteria**

✅ **API Responsiveness**: < 30 seconds response time  
✅ **Accurate Transcription**: Clear text output matching audio  
✅ **Quality Summary**: Concise, relevant summary of key points  
✅ **Error Handling**: Graceful failure with meaningful messages  
✅ **JSON Structure**: Consistent response format with metadata  

Happy Testing! 🚀 