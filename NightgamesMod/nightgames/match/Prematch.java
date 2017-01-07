package nightgames.match;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import nightgames.global.Flag;
import nightgames.global.Global;
import nightgames.global.Scene;
import nightgames.modifier.Modifier;

abstract class Prematch implements Scene {
    List<PrematchEvent> specialEvents;
    
    Modifier type;
    PrematchEvent chosenEvent;
    
    Prematch(PrematchEvent...events) {
        this(Arrays.asList(events));
    }

    Prematch(List<PrematchEvent> events) {
        specialEvents = Collections.unmodifiableList(events);
    }
    
    final void run() {
        Global.current = this;
        Global.unflag(Flag.victory);
        
        boolean ran = false;
        for (PrematchEvent evt : specialEvents) {
            if (evt.valid()) {
                evt.run();
                type = evt.type;
                chosenEvent = evt;
                ran = true;
                break;
            }
        }
        if (!ran) {
            PrematchEvent def = new PrematchEvent.DefaultEvent();
            def.run();
            type = def.type; 
            chosenEvent = def;
        }
    }
    
}
