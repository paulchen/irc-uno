package at.rueckgr.irc.bot.uno.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum CardType {
    ZERO("ZERO", "0", 0),
    ONE("ONE", "1", 0),
    TWO("TWO", "2", 0),
    THREE("THREE", "3", 0),
    FOUR("FOUR", "4", 0),
    FIVE("FIVE", "5", 0),
    SIX("SIX", "6", 0),
    SEVEN("SEVEN", "7", 0),
    EIGHT("EIGHT", "8", 0),
    NINE("NINE", "9", 0),
    REVERSE("R", "R", 1),
    DRAW2("D2", "D2", 2),
    SKIP("S", "S", 3),
    WILD("WILD", "W", 0),
    WD4("WD4", "WD4", 5);

    private final String longName;
    private final String shortName;
    private final int evilness;
}
