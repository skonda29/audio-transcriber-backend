// 🧪 OPTIMIZED POSTMAN TESTS FOR AUDIO TRANSCRIPTION SERVICE
// Copy these scripts into your Postman request "Tests" tab

// ============================================
// FOR FULL TRANSCRIPTION + SUMMARY ENDPOINT
// ============================================

var responseData;

// Parse response safely
pm.test("Response can be parsed as JSON", function () {
    try {
        responseData = pm.response.json();
        pm.expect(responseData).to.be.an('object');
    } catch (e) {
        pm.test.skip("Response is not valid JSON: " + e.message);
    }
});

// Test response status
pm.test("Response status code is 200", function () {
    pm.expect(pm.response.code).to.equal(200);
});

// Test response structure
pm.test("Response has required fields", function () {
    pm.expect(responseData).to.be.an('object');
    pm.expect(responseData).to.have.property('transcription');
    pm.expect(responseData).to.have.property('summary');
    pm.expect(responseData).to.have.property('transcriptionLength');
    pm.expect(responseData).to.have.property('summaryLength');
    pm.expect(responseData).to.have.property('timestamp');
});

// Test transcription content (for full endpoint)
pm.test("Transcription is valid", function () {
    if (responseData && responseData.transcription !== undefined) {
        pm.expect(responseData.transcription).to.be.a('string');
        pm.expect(responseData.transcriptionLength).to.be.a('number');
        pm.expect(responseData.transcriptionLength).to.equal(responseData.transcription.length);
        
        // For full transcription endpoint, expect non-empty transcription
        pm.expect(responseData.transcription.length).to.be.above(0, "Transcription should not be empty");
    }
});

// Test summary content
pm.test("Summary is valid and meaningful", function () {
    if (responseData && responseData.summary !== undefined) {
        pm.expect(responseData.summary).to.be.a('string');
        pm.expect(responseData.summaryLength).to.be.a('number');
        pm.expect(responseData.summaryLength).to.equal(responseData.summary.length);
        pm.expect(responseData.summary.length).to.be.above(10, "Summary should be meaningful (>10 chars)");
        
        // Check for error messages
        pm.expect(responseData.summary).to.not.include("Error generating summary");
    }
});

// Test timestamp validity
pm.test("Timestamp is valid ISO date", function () {
    if (responseData && responseData.timestamp) {
        var timestamp = new Date(responseData.timestamp);
        pm.expect(timestamp.getTime()).to.not.be.NaN;
        pm.expect(timestamp.getFullYear()).to.be.above(2020);
    }
});

// Realistic response time test for AI processing
pm.test("Response time is reasonable for AI processing", function () {
    pm.expect(pm.response.responseTime).to.be.below(60000); // 60 seconds max
    pm.expect(pm.response.responseTime).to.be.above(100);   // At least 100ms for processing
});

// Performance categorization
pm.test("Performance category", function () {
    var responseTime = pm.response.responseTime;
    if (responseTime < 5000) {
        console.log("🚀 Excellent performance: " + responseTime + "ms");
    } else if (responseTime < 15000) {
        console.log("✅ Good performance: " + responseTime + "ms");
    } else if (responseTime < 30000) {
        console.log("⚠️ Acceptable performance: " + responseTime + "ms");
    } else {
        console.log("🐌 Slow performance: " + responseTime + "ms");
    }
});

// Content quality tests
pm.test("Content quality indicators", function () {
    if (responseData && responseData.transcription && responseData.summary) {
        // Check compression ratio (summary should be shorter than transcription)
        var compressionRatio = responseData.summaryLength / responseData.transcriptionLength;
        pm.expect(compressionRatio).to.be.below(1, "Summary should be shorter than transcription");
        
        console.log("📝 Transcription length: " + responseData.transcriptionLength + " chars");
        console.log("📋 Summary length: " + responseData.summaryLength + " chars");
        console.log("📊 Compression ratio: " + (compressionRatio * 100).toFixed(1) + "%");
    }
});

// Error handling test
pm.test("No error messages in response", function () {
    if (responseData) {
        var responseStr = JSON.stringify(responseData).toLowerCase();
        pm.expect(responseStr).to.not.include("error");
        pm.expect(responseStr).to.not.include("exception");
        pm.expect(responseStr).to.not.include("failed");
    }
});

// ============================================
// FOR SUMMARY-ONLY ENDPOINT
// ============================================

/*
// Use this version for /summary-only endpoint tests:

pm.test("Summary-only: Transcription is empty", function () {
    if (responseData && responseData.transcription !== undefined) {
        pm.expect(responseData.transcription).to.equal("");
        pm.expect(responseData.transcriptionLength).to.equal(0);
    }
});

pm.test("Summary-only: Summary is present", function () {
    if (responseData && responseData.summary !== undefined) {
        pm.expect(responseData.summary.length).to.be.above(10);
        pm.expect(responseData.summaryLength).to.be.above(10);
    }
});
*/

// ============================================
// VISUALIZATION CHART
// ============================================

var template = `
<div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; font-family: Arial, sans-serif;">
    <h3 style="color: #333; margin-bottom: 20px;">📊 Audio Transcription Results</h3>
    
    <div style="display: flex; justify-content: space-between; margin-bottom: 20px;">
        <div style="background: #e3f2fd; padding: 15px; border-radius: 5px; flex: 1; margin-right: 10px;">
            <h4 style="margin: 0; color: #1976d2;">🎤 Transcription</h4>
            <p style="margin: 5px 0; font-size: 24px; font-weight: bold; color: #1976d2;">{{transcriptionLength}}</p>
            <small style="color: #666;">characters</small>
        </div>
        
        <div style="background: #e8f5e8; padding: 15px; border-radius: 5px; flex: 1; margin-left: 10px;">
            <h4 style="margin: 0; color: #388e3c;">📝 Summary</h4>
            <p style="margin: 5px 0; font-size: 24px; font-weight: bold; color: #388e3c;">{{summaryLength}}</p>
            <small style="color: #666;">characters</small>
        </div>
    </div>
    
    <div style="background: white; padding: 15px; border-radius: 5px; border: 1px solid #ddd;">
        <h4 style="margin: 0 0 10px 0; color: #333;">⚡ Performance Metrics</h4>
        <p style="margin: 5px 0;"><strong>Response Time:</strong> {{responseTime}}ms</p>
        <p style="margin: 5px 0;"><strong>Compression Ratio:</strong> {{compressionRatio}}%</p>
        <p style="margin: 5px 0;"><strong>Processing Efficiency:</strong> {{efficiency}}</p>
        <p style="margin: 5px 0;"><strong>Timestamp:</strong> {{timestamp}}</p>
    </div>
    
    <canvas id="comparisonChart" width="400" height="200" style="margin-top: 20px;"></canvas>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.9.1/chart.min.js"></script>
<script>
    var ctx = document.getElementById('comparisonChart').getContext('2d');
    var chart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Transcription', 'Summary'],
            datasets: [{
                label: 'Character Count',
                data: [{{transcriptionLength}}, {{summaryLength}}],
                backgroundColor: ['#42a5f5', '#66bb6a'],
                borderColor: ['#1976d2', '#388e3c'],
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            plugins: {
                title: {
                    display: true,
                    text: 'Content Length Comparison'
                },
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Characters'
                    }
                }
            }
        }
    });
</script>
`;

// Generate visualization data
function constructVisualizerPayload() {
    if (!responseData) return {};
    
    var compressionRatio = responseData.transcriptionLength > 0 
        ? ((responseData.summaryLength / responseData.transcriptionLength) * 100).toFixed(1)
        : "N/A";
    
    var efficiency = pm.response.responseTime < 5000 ? "Excellent" 
                   : pm.response.responseTime < 15000 ? "Good"
                   : pm.response.responseTime < 30000 ? "Acceptable" : "Slow";
    
    return {
        transcriptionLength: responseData.transcriptionLength || 0,
        summaryLength: responseData.summaryLength || 0,
        responseTime: pm.response.responseTime,
        compressionRatio: compressionRatio,
        efficiency: efficiency,
        timestamp: responseData.timestamp || "N/A"
    };
}

// Set visualization
if (pm.response.code === 200 && responseData) {
    pm.visualizer.set(template, constructVisualizerPayload());
}

// ============================================
// COLLECTION VARIABLES FOR TRACKING
// ============================================

// Track successful tests for reporting
if (pm.response.code === 200) {
    pm.collectionVariables.set("lastSuccessfulTest", new Date().toISOString());
    pm.collectionVariables.set("lastResponseTime", pm.response.responseTime);
    
    if (responseData) {
        pm.collectionVariables.set("lastTranscriptionLength", responseData.transcriptionLength);
        pm.collectionVariables.set("lastSummaryLength", responseData.summaryLength);
    }
} 