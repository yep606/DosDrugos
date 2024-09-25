package ru.rogoff.tlgbot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rogoff.tlgbot.model.User;
import ru.rogoff.tlgbot.model.Video;

import java.util.List;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final TelegramBot bot;
    private final VideoService videoService;
    private static final String NOTIFY_MESSAGE = """
            Вышло новое видео!
                        
            <a href="%s">СМОТРЕТЬ</a>
                
            """;

    private final UserService userService;

    //TODO: вынести логику сохранения видео(сохранять всегда один экземпляр)
    public int notifyAll(String url) {
        log.info("Incoming video url: {}", url);
        List<User> users = userService.findAllByNotificationAllowed(true);
        users.forEach(u -> notify(u, NOTIFY_MESSAGE.formatted(url)));
        videoService.save(Video.builder().url(url).build());

        return users.size();
    }

    //TODO: рефактор колхоза
    private void notify(User user, String message) {
        log.info("Notify user: {}, chat: {}, url: {}", user.getUsername(), user.getChatId(), message);
        bot.execute(new SendMessage(user.getChatId(), message).parseMode(HTML));
    }
}
