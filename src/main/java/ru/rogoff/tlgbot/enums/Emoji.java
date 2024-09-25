package ru.rogoff.tlgbot.enums;

public enum Emoji {
    HOURGLASS_NOT_DONE("\u23F3"),    // ⏳
    RED_HEART("\u2764\uFE0F"),       // ❤️
    THUMBS_UP("\uD83D\uDC4D"),       // 👍
    CHECK_MARK("\u2705"),           // ✅
    CROSS_MARK("\u274c"),           // ❌
    DOCUMENT("\uD83D\uDCC4"),      // 📄
    MOVIE_CAMERA("\uD83C\uDFA5"),  // 🎥
    LEFTWARDS_ARROW("\u21A9"),    // ↩
    LIGHT_BULB("\uD83D\uDCA1"),   // 💡
    EXCLAMATION_MARK("\u2757"),    // ❗
    STAR("\u2B50");                // ⭐

    private final String unicode;

    Emoji(String unicode) {
        this.unicode = unicode;
    }

    @Override
    public String toString() {
        return unicode;
    }
    }
