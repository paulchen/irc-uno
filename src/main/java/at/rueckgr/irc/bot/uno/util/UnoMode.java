package at.rueckgr.irc.bot.uno.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UnoMode {
    ATTACK("+a"),
    EXTREME("+e");

    private final String code;
}
