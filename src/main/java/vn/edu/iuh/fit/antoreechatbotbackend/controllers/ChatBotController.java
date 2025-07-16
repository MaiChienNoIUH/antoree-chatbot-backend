package vn.edu.iuh.fit.antoreechatbotbackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.antoreechatbotbackend.services.OpenAIService;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatBotController {

    @Autowired
    private OpenAIService openAIService;

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String response = openAIService.getChatResponse(message);
        return Map.of("reply", response);
    }
}
