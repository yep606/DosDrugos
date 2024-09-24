package ru.rogoff.tlgbot.config;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SetWebhook;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class WebhookConfig {

    @Value("${tlgbot.webhook.url}")
    private String webhookUrl;

    private final TelegramBot bot;

    @PostConstruct
    public void setWebhook() {
        try {
            SetWebhook request = new SetWebhook().url(webhookUrl);
            boolean ok = bot.execute(request).isOk();
            if (ok) {
                log.info("Webhook successfully set at URL: {}", webhookUrl);
            }
            else {
                log.info("Bad request at URL: {}", webhookUrl);
            }
        } catch (Exception e) {
            log.error("Failed to set webhook: {}", e.getMessage(), e);
        }
    }
}
