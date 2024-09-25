package ru.rogoff.tlgbot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rogoff.tlgbot.model.Video;

@Repository
public interface VideoRepo extends JpaRepository<Video, Long> {
}
