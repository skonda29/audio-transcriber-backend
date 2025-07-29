package com.audio.transcribe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.ai.openai.api-key=test-key",
    "spring.ai.openai.base-url=http://localhost:8080"
})
class TranscriptionControllerTest {

    @MockBean
    private OpenAiAudioTranscriptionModel transcriptionModel;

    @MockBean
    private ChatModel chatModel;

    @Test
    void contextLoads() {
        // Test that the application context loads successfully with new components
        assertNotNull(transcriptionModel);
        assertNotNull(chatModel);
    }

    @Test
    void transcriptionResponseStructureTest() {
        // Test the structure of TranscriptionResponse
        TranscriptionResponse response = new TranscriptionResponse(
            "Sample transcription text", 
            "Sample summary text"
        );
        
        assertNotNull(response.getTranscription());
        assertNotNull(response.getSummary());
        assertNotNull(response.getTimestamp());
        assert(response.getTranscriptionLength() > 0);
        assert(response.getSummaryLength() > 0);
    }
} 