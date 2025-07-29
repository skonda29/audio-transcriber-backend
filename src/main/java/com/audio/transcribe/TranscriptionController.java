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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/transcribe")
public class TranscriptionController {

    private final OpenAiAudioTranscriptionModel transcriptionModel;
    private final ChatModel chatModel;

    public TranscriptionController(OpenAiAudioTranscriptionModel transcriptionModel, ChatModel chatModel) {
        this.transcriptionModel = transcriptionModel;
        this.chatModel = chatModel;
    }

    @PostMapping
    public ResponseEntity<TranscriptionResponse> transcribeAudio(@RequestParam("file") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("audio", ".wav");
        file.transferTo(tempFile);

        try {
            FileSystemResource audioFile = new FileSystemResource(tempFile);

            // Step 1: Transcribe audio
            AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(audioFile);
            AudioTranscriptionResponse response = transcriptionModel.call(transcriptionRequest);
            String transcription = response.getResult().getOutput();

            // Step 2: Generate summary
            String summary = generateSummary(transcription);

            // Step 3: Create response
            TranscriptionResponse result = new TranscriptionResponse(transcription, summary);
            
            return new ResponseEntity<>(result, HttpStatus.OK);
        } finally {
            tempFile.delete();
        }
    }

    @PostMapping("/summary-only")
    public ResponseEntity<TranscriptionResponse> transcribeAndSummarizeOnly(@RequestParam("file") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("audio", ".wav");
        file.transferTo(tempFile);

        try {
            FileSystemResource audioFile = new FileSystemResource(tempFile);

            // Transcribe audio
            AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(audioFile);
            AudioTranscriptionResponse response = transcriptionModel.call(transcriptionRequest);
            String transcription = response.getResult().getOutput();

            // Generate summary only
            String summary = generateSummary(transcription);

            // Return only summary (transcription field will be empty)
            TranscriptionResponse result = new TranscriptionResponse("", summary);
            
            return new ResponseEntity<>(result, HttpStatus.OK);
        } finally {
            tempFile.delete();
        }
    }

    private String generateSummary(String transcription) {
        if (transcription == null || transcription.trim().isEmpty()) {
            return "No content to summarize.";
        }

        String promptText = String.format(
            "Please provide a concise summary of the following transcribed audio content. " +
            "Focus on the key points, main topics discussed, and important information. " +
            "Keep the summary clear and well-structured:\n\n%s", 
            transcription
        );

        try {
            return chatModel.call(promptText);
        } catch (Exception e) {
            return "Error generating summary: " + e.getMessage();
        }
    }
}
