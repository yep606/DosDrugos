package ru.rogoff.tlgbot.config;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SetWebhook;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class WebhookConfig {

    @Value("${tlgbot.webhook.url}")
    private String webhookUrl;

    @Value("classpath:posheve.pem")
    private Resource resource;

    private final TelegramBot bot;

    @PostConstruct
    public void setWebhook() {
        try {
            byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            log.info("Cert bytes: {}", bytes.length);
            SetWebhook request = new SetWebhook().url(webhookUrl).certificate(bytes);
//            SetWebhook request = new SetWebhook().url(webhookUrl);
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