package vn.edu.iuh.fit.antoreechatbotbackend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Service
public class OpenAIService {
    private final WebClient webClient;
    private final Semaphore rateLimiter = new Semaphore(3); // tối đa 3 request cùng lúc
    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    public OpenAIService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.openai.com/v1").build();
    }

    public String getChatResponse(String message) {
        boolean acquired = false;
        try {
            acquired = rateLimiter.tryAcquire(1, TimeUnit.SECONDS);
            if (!acquired) {
                return "⚠️ Vui lòng chờ vài giây rồi thử lại (hạn mức gọi API)";
            }

            Map response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + OPENAI_API_KEY)
                    .bodyValue(Map.of(
                            "model", "gpt-4o-mini",
                            "messages", List.of(
                                    Map.of("role", "system", "content", "You are an English learning assistant."),
                                    Map.of("role", "user", "content", message)
                            )
                    ))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map> choices = (List<Map>) response.get("choices");
            Map firstChoice = (Map) choices.get(0);
            Map messageMap = (Map) firstChoice.get("message");
            return messageMap.get("content").toString();

        } catch (WebClientResponseException.TooManyRequests e) {
            try {
                Thread.sleep(2000);
                return getChatResponse(message);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return "⚠️ OpenAI đang giới hạn tốc độ, thử lại sau vài giây!";
            }
        } catch (Exception e) {
            return "❌ Vui lòng kích hoạt Billing $5 trong tài khoản OpenAI của bạn để dùng OpenAI.";
        } finally {
            if (acquired) {
                rateLimiter.release();
            }
        }
    }
}