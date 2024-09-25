package ru.rogoff.tlgbot.model;

import jakarta.persistence.*;
import lombok.*;
import ru.rogoff.tlgbot.enums.State;

@Table(name = "users")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long telegramUserId;

    @Column
    private Long chatId;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String username;

    @Column
    private String question;

    @Column
    private boolean notificationAllowed = false;

    @Enumerated(value = EnumType.STRING)
    private State state = State.GREETING;
}
