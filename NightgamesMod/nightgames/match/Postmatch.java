package nightgames.match;

import java.util.Collections;
import java.util.List;

import nightgames.characters.Character;
import nightgames.characters.Player;
import nightgames.global.Global;
import nightgames.global.Scene;

abstract class Postmatch implements Scene {

    protected final Player player;
    protected final List<Character> combatants;
    
    Postmatch(List<Character> combatants) {
        this.combatants = Collections.unmodifiableList(combatants);
        player = Global.getPlayer();
    }
    
    abstract void runInternal();
    
    final void run() {
        runInternal();
        Global.endNight();
    }
    
}
