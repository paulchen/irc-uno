package at.rueckgr.irc.bot.uno.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInfo {
    private String name;

    private long cardCount;
}
