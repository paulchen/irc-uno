package at.rueckgr.irc.bot.uno.model;

import lombok.Value;

import java.text.MessageFormat;

@Value
public class Card {
    CardType cardType;
    Color color;

    public String getLongName() {
        return MessageFormat.format("{0} {1}", color.getLongName(), cardType.getLongName());
    }

    public boolean isWildcard() {
        return Color.WILD.equals(color);
    }
}
