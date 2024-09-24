package ru.rogoff.tlgbot.controller;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.utility.BotUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.rogoff.tlgbot.service.UpdateProcessor;

@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final UpdateProcessor processor;

    @PostMapping("/webhook")
    public void webhook(@RequestBody String updateRequest) {
        Update update = BotUtils.parseUpdate(updateRequest);
        processor.processUpdate(update);
    }

    @GetMapping("/test")
    public String test(){
        return "Success: 200";
    }
}
