package com.audio.transcribe;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/transcribe")
public class TranscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(TranscriptionController.class);
    
    private final OpenAiAudioTranscriptionModel transcriptionModel;
    private final ChatModel chatModel;

    public TranscriptionController(OpenAiAudioTranscriptionModel transcriptionModel, ChatModel chatModel) {
        this.transcriptionModel = transcriptionModel;
        this.chatModel = chatModel;
    }

    @PostMapping
    public ResponseEntity<TranscriptionResponse> transcribeAudio(@RequestParam("file") MultipartFile file) {
        logger.info("Received transcription request for file: {}", file.getOriginalFilename());
        
        // Validate input
        if (file.isEmpty()) {
            logger.warn("Empty file uploaded");
            return ResponseEntity.badRequest().body(
                new TranscriptionResponse("Error: No file provided", "Please upload an audio file.")
            );
        }

        File tempFile = null;
        try {
            // Create temporary file
            tempFile = File.createTempFile("audio", ".wav");
            file.transferTo(tempFile);
            logger.info("Created temporary file: {}", tempFile.getAbsolutePath());

            FileSystemResource audioFile = new FileSystemResource(tempFile);

            // Step 1: Transcribe audio
            logger.info("Starting audio transcription...");
            AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(audioFile);
            AudioTranscriptionResponse response = transcriptionModel.call(transcriptionRequest);
            String transcription = response.getResult().getOutput();
            logger.info("Transcription completed, length: {}", transcription.length());

            // Step 2: Generate summary
            logger.info("Generating summary...");
            String summary = generateSummary(transcription);
            logger.info("Summary generated, length: {}", summary.length());

            // Step 3: Create response
            TranscriptionResponse result = new TranscriptionResponse(transcription, summary);
            
            return new ResponseEntity<>(result, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error processing transcription request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new TranscriptionResponse(
                    "Error: Failed to process audio file", 
                    "Error details: " + e.getMessage()
                )
            );
        } finally {
            if (tempFile != null && tempFile.exists()) {
                boolean deleted = tempFile.delete();
                logger.info("Temporary file deleted: {}", deleted);
            }
        }
    }

    @PostMapping("/summary-only")
    public ResponseEntity<TranscriptionResponse> transcribeAndSummarizeOnly(@RequestParam("file") MultipartFile file) {
        logger.info("Received summary-only request for file: {}", file.getOriginalFilename());
        
        // Validate input
        if (file.isEmpty()) {
            logger.warn("Empty file uploaded for summary-only");
            return ResponseEntity.badRequest().body(
                new TranscriptionResponse("", "Error: No file provided. Please upload an audio file.")
            );
        }

        File tempFile = null;
        try {
            // Create temporary file
            tempFile = File.createTempFile("audio", ".wav");
            file.transferTo(tempFile);
            logger.info("Created temporary file for summary-only: {}", tempFile.getAbsolutePath());

            FileSystemResource audioFile = new FileSystemResource(tempFile);

            // Transcribe audio
            logger.info("Starting audio transcription for summary-only...");
            AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(audioFile);
            AudioTranscriptionResponse response = transcriptionModel.call(transcriptionRequest);
            String transcription = response.getResult().getOutput();
            logger.info("Transcription completed for summary-only, length: {}", transcription.length());

            // Generate summary only
            logger.info("Generating summary for summary-only endpoint...");
            String summary = generateSummary(transcription);
            logger.info("Summary generated for summary-only, length: {}", summary.length());

            // Return only summary (transcription field will be empty)
            TranscriptionResponse result = new TranscriptionResponse("", summary);
            
            return new ResponseEntity<>(result, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error processing summary-only request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new TranscriptionResponse(
                    "", 
                    "Error processing audio file: " + e.getMessage()
                )
            );
        } finally {
            if (tempFile != null && tempFile.exists()) {
                boolean deleted = tempFile.delete();
                logger.info("Temporary file deleted for summary-only: {}", deleted);
            }
        }
    }

    private String generateSummary(String transcription) {
        if (transcription == null || transcription.trim().isEmpty()) {
            logger.warn("No content to summarize - transcription is empty");
            return "No content to summarize.";
        }

        String promptText = String.format(
            "Please provide a concise summary of the following transcribed audio content. " +
            "Focus on the key points, main topics discussed, and important information. " +
            "Keep the summary clear and well-structured:\n\n%s", 
            transcription
        );

        try {
            logger.info("Calling ChatModel for summary generation...");
            String summary = chatModel.call(promptText);
            logger.info("Summary generation successful");
            return summary;
        } catch (Exception e) {
            logger.error("Error generating summary with ChatModel", e);
            return "Error generating summary: " + e.getMessage();
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<TranscriptionResponse> handleGlobalException(Exception e) {
        logger.error("Unhandled exception in TranscriptionController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            new TranscriptionResponse(
                "Error: Unexpected server error", 
                "Please try again later. Error: " + e.getMessage()
            )
        );
    }
}
