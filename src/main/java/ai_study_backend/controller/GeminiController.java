package ai_study_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = {
    "https://ai-planner-pawan-dev.netlify.app",
    "http://localhost:5173"
})
public class GeminiController {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .codecs(config -> config.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/ask")
    public Mono<String> ask(@RequestBody Map<String, String> body) {
        String prompt = body.get("prompt");

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", prompt)
                ))
            )
        );

        return webClient.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .onStatus(
                status -> status.is4xxClientError() || status.is5xxServerError(),
                response -> response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        System.out.println("Gemini API Error: " + errorBody);
                        return Mono.error(new RuntimeException("Gemini Error: " + errorBody));
                    })
            )
            .bodyToMono(String.class)
            .map(response -> {
                try {
                    // ✅ Backend me hi text extract karo
                    JsonNode root = objectMapper.readTree(response);
                    String text = root.path("candidates")
                        .get(0)
                        .path("content")
                        .path("parts")
                        .get(0)
                        .path("text")
                        .asText();
                    System.out.println("Gemini text extracted: " + text.substring(0, Math.min(50, text.length())));
                    return text; // ✅ Sirf plain text return karo
                } catch (Exception e) {
                    System.out.println("Parse error: " + e.getMessage());
                    return "Sorry, could not get response";
                }
            })
            .onErrorResume(e -> {
                System.out.println("Gemini Exception: " + e.getMessage());
                return Mono.just("Sorry, AI error occurred");
            });
    }
}