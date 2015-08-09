package at.rueckgr.irc.bot.uno.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum CardType {
    ZERO("ZERO", "0", 2),
    ONE("ONE", "1", 2),
    TWO("TWO", "2", 2),
    THREE("THREE", "3", 2),
    FOUR("FOUR", "4", 2),
    FIVE("FIVE", "5", 2),
    SIX("SIX", "6", 2),
    SEVEN("SEVEN", "7", 2),
    EIGHT("EIGHT", "8", 2),
    NINE("NINE", "9", 2),
    REVERSE("R", "R", 3),
    DRAW2("D2", "D2", 5),
    SKIP("S", "S", 4),
    WILD("WILD", "W", 1),
    WD4("WD4", "WD4", 0);

    private final String longName;
    private final String shortName;
    private final int evilness;
}
