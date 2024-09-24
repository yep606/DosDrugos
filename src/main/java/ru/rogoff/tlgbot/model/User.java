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

    private Long telegramUserId;
    private String firstName;
    private String lastName;
    private String username;
    private String question;
    private boolean notificationAllowed = false;
    @Enumerated(value = EnumType.STRING)
    private State state = State.GREETING;
}
