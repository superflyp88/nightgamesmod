package nightgames.match;

import java.util.Collection;

import nightgames.areas.Area;
import nightgames.characters.Character;
import nightgames.characters.Player;
import nightgames.combat.Encounter;
import nightgames.combat.IEncounter;
import nightgames.ftc.FTCEncounter;
import nightgames.ftc.FTCMatch;
import nightgames.global.Scene;
import nightgames.modifier.Modifier;
import nightgames.modifier.standard.FTCModifier;
import nightgames.modifier.standard.NoModifier;

public enum MatchType {
    NORMAL,
    FTC;

    public Match buildMatch(Collection<Character> combatants, Modifier condition) {
        switch (this) {
            case FTC:
                assert condition.name().equals("FTC");
                if (combatants.size() != 5) {
                    return new Match(combatants, new NoModifier());
                }
                return new FTCMatch(combatants, ((FTCModifier) condition).getPrey());
            case NORMAL:
                return new Match(combatants, condition);
            default:
                throw new Error();
        }
    }
    
    public IEncounter buildEncounter(Character first, Character second, Area location) {
        switch (this) {
            case FTC:
                return new FTCEncounter(first, second, location);
            case NORMAL:
                return new Encounter(first, second, location);
            default:
                throw new Error();
        }
    }

    public Prematch buildPrematch(Player player) {
        switch (this) {
            case FTC:
                return new FTCPrematch(player);
            case NORMAL:
                return new DefaultPrematch();
            default:
                throw new Error();
        }
    }
    
    public void runPrematch(Player player) {
        buildPrematch(player).run();
    }

}
