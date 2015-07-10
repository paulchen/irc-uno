package at.rueckgr.irc.bot.uno.model;

public enum CardType {
    ZERO("ZERO", "0"),
    ONE("ONE", "1"),
    TWO("TWO", "2"),
    THREE("THREE", "3"),
    FOUR("FOUR", "4"),
    FIVE("FIVE", "5"),
    SIX("SIX", "6"),
    SEVEN("SEVEN", "7"),
    EIGHT("EIGHT", "8"),
    NINE("NINE", "9"),
    REVERSE("R", "R"),
    DRAW2("D2", "D2"),
    SKIP("S", "S"),
    WILD("WILD", "W"),
    WD4("WD4", "WD4");

    private final String longName;
    private final String shortName;

    CardType(final String longName, final String shortName) {
        this.longName = longName;
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }
}
