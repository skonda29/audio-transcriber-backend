# 🚀 Deploy to Render - Complete Guide

## 📋 **Pre-Deployment Checklist**

✅ **OpenAI API Key ready**  
✅ **GitHub repository pushed**  
✅ **Production configuration files created**  
✅ **CORS updated for Render domains**  

---

## 🔧 **Step 1: Repository Setup**

1. **Push your code to GitHub:**
   ```bash
   git add .
   git commit -m "Prepare for Render deployment"
   git push origin main
   ```

---

## 🌐 **Step 2: Render Web Service Deployment**

### **Method A: Using render.yaml (Recommended)**

1. **Go to [Render Dashboard](https://dashboard.render.com/)**
2. **Click "New" → "Blueprint"**
3. **Connect your GitHub repository**
4. **Render will automatically detect `render.yaml`**

### **Method B: Manual Setup**

1. **Go to [Render Dashboard](https://dashboard.render.com/)**
2. **Click "New" → "Web Service"**
3. **Connect your GitHub repository**
4. **Configure settings:**
   ```
   Name: audio-transcribe-backend
   Environment: Java
   Build Command: ./mvnw clean package -DskipTests
   Start Command: java -jar target/*.jar
   Plan: Free
   ```

---

## 🔑 **Step 3: Environment Variables**

In Render dashboard, add these environment variables:

| Key | Value | Description |
|-----|-------|-------------|
| `OPENAI_API_KEY` | `sk-your-api-key-here` | Your OpenAI API key |
| `SPRING_PROFILES_ACTIVE` | `production` | Activates production config |

---

## ⚙️ **Step 4: Health Check Configuration**

Render will automatically use: `https://your-app.onrender.com/actuator/health`

---

## 🌍 **Step 5: Update Frontend Configuration**

Once deployed, update your frontend to use the new backend URL:

```javascript
// In your frontend AudioUploader.jsx
const response = await axios.post(
    "https://your-app-name.onrender.com/api/transcribe", // Replace with your Render URL
    formData,
    // ... rest of config
);
```

---

## 📊 **Expected Deployment Results**

### **✅ Successful Deployment:**
- **Build time:** ~2-3 minutes
- **Cold start:** ~30 seconds (free tier)
- **Health check:** `200 OK` at `/actuator/health`
- **API endpoint:** `https://your-app.onrender.com/api/transcribe`

### **🔍 Test Your Deployment:**
```bash
# Test health endpoint
curl https://your-app-name.onrender.com/actuator/health

# Test API endpoint (should return 500 without file)
curl -X POST https://your-app-name.onrender.com/api/transcribe
```

---

## 🐛 **Troubleshooting**

### **Common Issues:**

#### **1. Build Failures**
```
Error: Build failed during Maven compilation
Solution: Check Maven logs, ensure Java 17 compatibility
```

#### **2. OpenAI API Key Issues**
```
Error: 401 Unauthorized from OpenAI
Solution: Verify API key is correctly set in environment variables
```

#### **3. Cold Start Delays**
```
Issue: First request takes 30+ seconds
Solution: This is normal on free tier, consider upgrading for instant response
```

#### **4. CORS Issues**
```
Error: CORS policy blocking requests
Solution: Already configured for *.onrender.com domains
```

---

## 🎯 **Production Optimizations**

### **Free Tier Limitations:**
- **Cold starts:** ~30 seconds of inactivity
- **Monthly hours:** 750 hours/month
- **Memory:** 512MB
- **CPU:** Shared

### **Upgrade Benefits:**
- **No cold starts**
- **More memory/CPU**
- **Custom domains**
- **SSL certificates**

---

## 📱 **Frontend Deployment (Bonus)**

Deploy your React frontend to Netlify/Vercel and connect to your Render backend:

1. **Build frontend with production backend URL**
2. **Deploy to Netlify/Vercel**
3. **Update CORS in backend to include frontend domain**

---

## 🔐 **Security Best Practices**

✅ **Environment variables for secrets**  
✅ **HTTPS enforced by default**  
✅ **CORS properly configured**  
✅ **Health checks enabled**  
✅ **Production logging levels**  

---

## 📈 **Monitoring Your App**

### **Render Dashboard provides:**
- **Real-time logs**
- **Performance metrics**
- **Deployment history**
- **Health check status**

### **Monitor endpoints:**
- **Health:** `/actuator/health`
- **API:** `/api/transcribe`
- **Logs:** Available in Render dashboard

---

## 🎉 **Success Criteria**

Your deployment is successful when:

✅ **Build completes without errors**  
✅ **Health check returns `{"status":"UP"}`**  
✅ **API responds to POST requests**  
✅ **OpenAI integration works**  
✅ **CORS allows frontend connections**  

**Your AI Audio Transcription Service is now live! 🚀**

---

## 🔗 **Quick Commands**

```bash
# Test after deployment
curl https://your-app-name.onrender.com/actuator/health

# Get app logs
# (Available in Render dashboard)

# Update deployment
git push origin main
# (Auto-deploys with connected GitHub)
```

Happy deploying! 🎊 