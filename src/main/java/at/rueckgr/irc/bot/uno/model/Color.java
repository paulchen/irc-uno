package at.rueckgr.irc.bot.uno.model;

public enum Color {
    RED("RED", "R"),
    BLUE("BLUE", "B"),
    GREEN("GREEN", "G"),
    YELLOW("YELLOW", "Y"),
    WILD("WILD", "W");

    private final String longName;
    private final String shortName;

    Color(String longName, String shortName) {
        this.longName = longName;
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

    public boolean isWildcard() {
        return this.equals(WILD);
    }
}
