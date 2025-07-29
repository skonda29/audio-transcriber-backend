package com.audio.transcribe;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Audio Transcription Service</title>
                    <style>
                        body { 
                            font-family: Arial, sans-serif; 
                            max-width: 800px; 
                            margin: 50px auto; 
                            padding: 20px;
                            background-color: #f5f5f5;
                        }
                        .container {
                            background: white;
                            padding: 30px;
                            border-radius: 8px;
                            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                        }
                        h1 { color: #333; }
                        .endpoint { 
                            background: #f8f9fa; 
                            padding: 15px; 
                            margin: 10px 0; 
                            border-left: 4px solid #007bff;
                            border-radius: 4px;
                        }
                        .method { 
                            font-weight: bold; 
                            color: #007bff; 
                        }
                        code { 
                            background: #e9ecef; 
                            padding: 2px 6px; 
                            border-radius: 3px;
                            font-family: monospace;
                        }
                        .upload-form {
                            margin: 20px 0;
                            padding: 20px;
                            border: 2px dashed #ccc;
                            border-radius: 8px;
                            text-align: center;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>🎵 Audio Transcription Service</h1>
                        <p>Welcome to the Audio Transcription API! This service allows you to transcribe audio files and generate summaries using OpenAI's Whisper and GPT models.</p>
                        
                        <h2>Available Endpoints:</h2>
                        
                        <div class="endpoint">
                            <div class="method">POST</div>
                            <strong>/api/transcribe</strong>
                            <p>Transcribe audio file and generate summary</p>
                            <p><strong>Parameters:</strong> <code>file</code> (audio file)</p>
                            <p><strong>Returns:</strong> JSON with transcription and summary</p>
                        </div>
                        
                        <div class="endpoint">
                            <div class="method">POST</div>
                            <strong>/api/transcribe/summary-only</strong>
                            <p>Transcribe audio file and return only the summary</p>
                            <p><strong>Parameters:</strong> <code>file</code> (audio file)</p>
                            <p><strong>Returns:</strong> JSON with summary only</p>
                        </div>

                        <div class="endpoint">
                            <div class="method">GET</div>
                            <strong>/health</strong>
                            <p>Health check endpoint</p>
                            <p><strong>Returns:</strong> Service status</p>
                        </div>
                        
                        <h2>Quick Test:</h2>
                        <div class="upload-form">
                            <form action="/api/transcribe" method="post" enctype="multipart/form-data">
                                <p>Upload an audio file to test the transcription service:</p>
                                <input type="file" name="file" accept="audio/*" required>
                                <br><br>
                                <button type="submit" style="padding: 10px 20px; background: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer;">
                                    Transcribe Audio
                                </button>
                            </form>
                        </div>
                        
                        <h2>Supported Audio Formats:</h2>
                        <p>MP3, MP4, MPEG, MPGA, M4A, WAV, WEBM</p>
                        
                        <h2>Example Usage with cURL:</h2>
                        <pre style="background: #f8f9fa; padding: 15px; border-radius: 4px; overflow-x: auto;">
curl -X POST "http://localhost:8080/api/transcribe" \\
     -H "Content-Type: multipart/form-data" \\
     -F "file=@your-audio-file.wav"</pre>
                    </div>
                </body>
                </html>
                """;
    }

    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return """
                {
                    "status": "UP",
                    "service": "Audio Transcription Service",
                    "timestamp": "%s"
                }
                """.formatted(java.time.Instant.now());
    }

    @GetMapping("/config-check")
    @ResponseBody
    public String configCheck() {
        String openaiKey = System.getenv("OPENAI_API_KEY");
        String springProfile = System.getenv("SPRING_PROFILES_ACTIVE");
        
        return """
                {
                    "status": "Config Check",
                    "openai_key_configured": %s,
                    "spring_profile": "%s",
                    "timestamp": "%s"
                }
                """.formatted(
                    openaiKey != null && !openaiKey.isEmpty() && !openaiKey.equals("your-api-key-here"),
                    springProfile != null ? springProfile : "default",
                    java.time.Instant.now()
                );
    }
} 