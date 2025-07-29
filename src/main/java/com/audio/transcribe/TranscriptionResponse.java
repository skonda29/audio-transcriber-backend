package com.audio.transcribe;

public class TranscriptionResponse {
    private String transcription;
    private String summary;
    private int transcriptionLength;
    private int summaryLength;
    private String timestamp;

    public TranscriptionResponse() {}

    public TranscriptionResponse(String transcription, String summary) {
        this.transcription = transcription;
        this.summary = summary;
        this.transcriptionLength = transcription != null ? transcription.length() : 0;
        this.summaryLength = summary != null ? summary.length() : 0;
        this.timestamp = java.time.Instant.now().toString();
    }

    // Getters and setters
    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
        this.transcriptionLength = transcription != null ? transcription.length() : 0;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
        this.summaryLength = summary != null ? summary.length() : 0;
    }

    public int getTranscriptionLength() {
        return transcriptionLength;
    }

    public void setTranscriptionLength(int transcriptionLength) {
        this.transcriptionLength = transcriptionLength;
    }

    public int getSummaryLength() {
        return summaryLength;
    }

    public void setSummaryLength(int summaryLength) {
        this.summaryLength = summaryLength;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
} 