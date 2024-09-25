package ru.rogoff.tlgbot.model;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "video")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String url;
}
