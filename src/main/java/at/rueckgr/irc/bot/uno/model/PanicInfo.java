package at.rueckgr.irc.bot.uno.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PanicInfo {
    List<PanicMode> currentPanicState;

    List<CardType> forbiddenCards;

    List<CardType> preferredCards;

    boolean drawIfPossible;
}
