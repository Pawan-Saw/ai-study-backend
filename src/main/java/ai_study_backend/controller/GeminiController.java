package ai_study_backend.controller;

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
}) // ✅ Fixed
public class GeminiController {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .codecs(config -> config.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
            .build();

    @PostMapping("/ask")
    public Mono<String> ask(@RequestBody Map<String, String> body) {
        String prompt = body.get("prompt");

        // ✅ gemini-2.0-flash — stable model
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
                        System.out.println("Gemini API Error: " + errorBody); // ✅ Log karega
                        return Mono.error(new RuntimeException("Gemini Error: " + errorBody));
                    })
            )
            .bodyToMono(String.class)
            .onErrorResume(e -> {
                System.out.println("Gemini Exception: " + e.getMessage()); // ✅ Log karega
                return Mono.just("{\"error\": \"" + e.getMessage() + "\"}");
            });
    }
}