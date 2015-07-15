package at.rueckgr.irc.bot.uno.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Color {
    RED("RED", "R"),
    BLUE("BLUE", "B"),
    GREEN("GREEN", "G"),
    YELLOW("YELLOW", "Y"),
    WILD("WILD", "W");

    private final String longName;
    private final String shortName;

    public boolean isWildcard() {
        return WILD.equals(this);
    }
}
