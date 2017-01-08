package nightgames.match.team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nightgames.characters.Character;
import nightgames.global.DebugFlags;
import nightgames.global.Global;
import nightgames.match.Match;
import nightgames.modifier.Modifier;

public class TeamMatch extends Match {

    private static final boolean DO_3_VS_3 = false;
    
    // Key is team name, Character at index 0 is captain.
    private Map<String, List<Character>> teams;

    public TeamMatch(Collection<Character> combatants, Modifier condition) {
        super(combatants, condition);
        teams = new HashMap<>();
    }

    @Override
    protected void preStart() {
        int size = combatants.size();
        List<Character> byLevel = new ArrayList<>(combatants);
        byLevel.sort(Comparator.comparing(Character::getLevel));
        if (size % 3 == 0 && (size % 2 != 0 || DO_3_VS_3)) {
            makeThreesomes(byLevel);
        } else if (size % 2 == 0) {
            makePairs(byLevel);
        } else {
            Character kicked;
            do {
                kicked = Global.pickRandom(byLevel).get();
            } while (kicked.human());
            combatants.remove(kicked);
            byLevel.remove(kicked);
            if (Global.isDebugOn(DebugFlags.DEBUG_SCENE)) {
                System.out.println(kicked.getTrueName() + " was kicked to balance the teams.");
            }
            makePairs(byLevel);
        }

        StringBuilder sb = new StringBuilder("<br/><br/><b><u>TEAMS:</u></b><br/><br/>");
        teams.forEach((name, team) -> {
            team.forEach(c -> matchData.getDataFor(c).setFlag("team", name));
            matchData.getDataFor(team.get(0)).setFlag("isCaptain", "yes");
            sb.append("<b>").append(name).append(":</b><br/>");
            sb.append("\t").append(team.get(0).getTrueName()).append(" (Captain)<br/>");
            for (int i = 1; i < team.size(); i++) {
                sb.append("\t").append(team.get(i).getTrueName()).append("<br/>");
            }
            sb.append("<br/>");
        });
        Global.gui().message(sb.toString());        
    }

    private void makeThreesomes(List<Character> byLevel) {
        while (!byLevel.isEmpty()) {
            Character strongest = byLevel.get(byLevel.size() - 1);
            Character mid = byLevel.get(byLevel.size() / 2);
            Character weakest = byLevel.get(0);

            assert strongest != mid && mid != weakest && strongest != weakest;

            List<Character> team;
            if (weakest.human()) {
                team = Arrays.asList(weakest, strongest, mid);
            } else if (mid.human()) {
                team = Arrays.asList(mid, strongest, weakest);
            } else {
                team = Arrays.asList(strongest, mid, weakest);
            }
            
            String name = teamNameFor(team.get(0));
            teams.put(name, team);
            byLevel.removeAll(team);
        }
    }

    private void makePairs(List<Character> byLevel) {
        while (!byLevel.isEmpty()) {
            Character strongest = byLevel.get(byLevel.size() - 1);
            Character weakest = byLevel.get(0);
            assert strongest != weakest;
            List<Character> team;
            if (strongest.human() || !weakest.human()) {
                team = Arrays.asList(strongest, weakest);
            } else {
                team = Arrays.asList(weakest, strongest);
            }
            String name = teamNameFor(team.get(0));
            teams.put(name, team);
            byLevel.remove(strongest);
            byLevel.remove(weakest);
        }
    }
    
    private static String teamNameFor(Character captain) {
        switch (captain.getType()) {
            case "Angel":
                return Global.pickRandom("Angel's Angels", "The Apostles", "Her Holiness").get();
            case "Cassie":
                return Global.pickRandom("The Wild Witches", "The Sexy Sorcerers", "The Mega Mages").get();
            case "Mara":
                return Global.pickRandom("The Tricksters", "The Sex Machines", "Mara <i>et al.</i>").get();
            case "Jewel":
                return Global.pickRandom("Jewel's Jewels", "The Dojo Damsels", "The Ball Busters").get();
            case "Reyka":
                return Global.pickRandom("The Infernal Pact", "The Succubus Sect", "Reyka's Imps").get();
            case "Airi":
                return Global.pickRandom("Airi's Playmates", "Airi's Little Helpers").get();
            case "Kat":
                return Global.pickRandom("The Tigers", "The Foxy Fighters", "Got Your Tongue").get();
            default:
                return captain.getTrueName() + "'s Team";
        }
    }
}
