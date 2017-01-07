package nightgames.match;

import nightgames.areas.Area;
import nightgames.characters.Character;
import nightgames.characters.Player;
import nightgames.combat.Encounter;
import nightgames.combat.IEncounter;
import nightgames.ftc.FTCEncounter;
import nightgames.global.Scene;

public enum MatchType {
    NORMAL,
    FTC;

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
