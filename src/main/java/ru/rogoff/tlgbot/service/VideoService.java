package ru.rogoff.tlgbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rogoff.tlgbot.model.Video;
import ru.rogoff.tlgbot.repo.VideoRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepo repo;

    // TODO: колхоз ебанный - исправить
    public Video findVideo() {
        List<Video> videos = repo.findAll();
        if (videos.isEmpty()) {
            return Video.builder().build();
        } else {
            return videos.get(0);
        }
    }

    public void save(Video video){
        repo.save(video);
    }
}
