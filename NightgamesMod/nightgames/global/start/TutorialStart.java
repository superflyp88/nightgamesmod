package nightgames.global.start;

import nightgames.characters.Character;
import nightgames.global.Global;
import nightgames.global.Scene;
import nightgames.gui.KeyableButton;
import nightgames.modifier.standard.NoModifier;
import nightgames.skills.Tactics;

public class TutorialStart implements GameStarter, Scene {

    /*
     * Tutorial: 
     * Girl npcs only, but should take player gender into account.
     * It begins much like The Silver Bard's, but continues on for longer
     * and has some variation.
     *  - General introduction: concepts, rules, etc. Explain win conditions,
     *    introduce main characters, pair up.
     *  - Player + Cassie, Mara + Jewel. Basic, skittish teasing. Tickle and the 
     *    like. Maya says it's pretty lame, but we should just build some 
     *    confidence (i.e. mojo) and then it'll be more fun. Mara and Jewel are
     *    a bit more adventureous, but not much.
     *  - Player + Jewel, Mara + Angel. Maya explains about stripping. Player
     *    goes for Jewel's jeans, but fails. Maya explains it's pretty difficult
     *    to strip bottoms of someone who is standing. Jewel launches a surprise attack
     *    and strips the player's shirt. Angel compliments him. Mara takes advantage of
     *    the distraction and strips Angel's shirt, revealing that she does not wear a bra.
     *  - Player + Mara, Cassie + Angel. Maya explains positions. Player gets a choice to
     *    wait and see or charge. Charge: get on top of Mara. Wait: Mara removes her bra,
     *    distracting the player, and then trips them. In either case, Mara feels between
     *    the player's legs. If they are male or female, they a compliment. If she's any
     *    of the 'special' genders, Mara makes a curious remark, which the rest hears.
     *  - Same pairs. Maya says how it's important to get out of submissive positions,
     *    because the one on top can do what they want. If the player is on top, he massages
     *    Mara's breasts and she ties his hands together and rolls over, just like in TSB's tutorial.
     *    If Mara is on top, she tickles and teases the player. When the player almost escapes,
     *    she ties them up.
     *  - Same pairs. Player has to struggle twice, Mara first removes their bottoms and then
     *    fondles the player. When the player breaks free, Maya ends the bout, but the player
     *    snags Mara's pants as she gets up.
     *  - Player + Angel, Jewel + Cassie. Status time. Maya says that combatants may find
     *    themselves affected by the fight in unusual ways, both good and bad. She whispers
     *    in Angel's ear. Then, Angel begins a strip tease, ending up naked. The player ends
     *    up Charmed, and this is explained.
     *  - Same pairs. Transformations! Maya explains that she has access to some rather
     *    exotic substances, and that the combatants might find a way to get them as
     *    well (Black Market). She hands the player a bottle, and tells him to get it
     *    onto Angel's breasts. She is, wisely, hesitant, but after a bit of wrestling
     *    the player succeeds. The bottle turns out to be a Bust Draft, and Angel's already
     *    consideranle breasts swell up even more, to her great enjoyment. Suddenly, Mara
     *    splashes something onto the player from behind. Turns out it's a Priapus Draft.
     *    If the player already had a dick, the girls voice their appreciation of the increased
     *    size. If not, everyone except Maya is amazed at the new appendage, and the player
     *    is rather nervous about it.
     *  - Player + Cassie, the others just watch. Maya suggests that we might as well put the new parts to good
     *    use. She has Cassie get naked, and the player charges her just as she finishes.
     *    They are a bit nervous and exchange a few kisses. Soon the player tries to fuck her,
     *    but finds that she is still a little dry due to the nerves. Maya says she really shouldn't
     *    have to explain this part, but that foreplay is important. The player proceeds to stimulate
     *    Cassie until she is sufficiently wet and then inserts. (Seperate flavor text if
     *    the player did not have a dick before.) It soon becomes clear that Cassie is much
     *    closer than the player is, and Maya says that whoever is on top and can control the
     *    pace hasa natural advantage. Not needing to be told twice, Cassie rolls the two of
     *    them over and starts riding the player with a vengance. They and up cumming pretty much
     *    simultaneously. Mara compliments them after a bit, saying that if they keep this up
     *    they'll be pros in no time. It's made clear that the other girls had been diddling
     *    themselves the whole time.
     */
    
    private enum Stage {
        INTRO(null), 
        BASICS("Cassie"), 
        STRIPPING("Jewel"), 
        POSITIONING_INTRO("Mara"), 
        POSITIONING_WAIT("Mara"), 
        POSITIONING_CHARGE("Mara"),
        ESCAPE_DOM("Mara"), 
        ESCAPE_SUB("Mara"), 
        STRUGGLE_1("Mara"), 
        STRUGGLE_2("Mara"), 
        STATUS("Angel"), 
        TF_INTRO("Angel"), 
        TF_HAD_DICK("Mara"),
        TF_NO_DICK("Mara"), 
        FUCK_INTRO("Cassie"), 
        FUCK_HAD_DICK("Cassie"), 
        FUCK_NO_DICK("Cassie"), 
        EPILOGUE("Cassie"), 
        DONE(null);
        
        final String partner;
        
        private Stage(String partner) {
            this.partner = partner;
        }
    }
    
    private Stage current = Stage.INTRO;
    
    @Override
    public void respond(String response) {
        switch (current) {
            case INTRO:
                message("Go go go!");
                choice("Let's get started...", Tactics.positioning, Stage.BASICS);
                break;
            case BASICS:
                break;
            case STRIPPING:
                break;
            case POSITIONING_INTRO:
                break;
            case POSITIONING_WAIT:
                break;
            case POSITIONING_CHARGE:
                break;
            case ESCAPE_DOM:
                break;
            case ESCAPE_SUB:
                break;
            case STRUGGLE_1:
                break;
            case STRUGGLE_2:
                break;
            case STATUS:
                break;
            case TF_INTRO:
                break;
            case TF_HAD_DICK:
                break;
            case TF_NO_DICK:
                break;
            case FUCK_INTRO:
                break;
            case FUCK_HAD_DICK:
                break;
            case FUCK_NO_DICK:
                break;
            case EPILOGUE:
                break;
            case DONE:
                actuallyStart();
                break;
            default:
                Global.gui().message("<b>The tutorial is broken! Please report"
                                + " this. Starting the game, now. Sorry.</b><br/>");
                actuallyStart();
        }
    }

    @Override
    public void startGame() {
        Global.current = this;
        respond(null);       
    }
    
    private void actuallyStart() {
        Global.gui().clearText();
        Global.setUpMatch(new NoModifier());
    }
    
    private void choice(String text, Tactics color, Stage next) {
        Global.gui().addToCommandPanel(new TutorialButton(text, color, next));
    }
    
    private void message(String fmt, Object... args) {
        Character npc = current.partner == null ? Global.noneCharacter() : 
            Global.getCharacterByType(current.partner);
        if (npc == null) {
            npc = Global.noneCharacter();
        }
        Global.gui().message(Global.format(fmt, Global.getPlayer(), npc, args));
    }

    private class TutorialButton extends KeyableButton {

        private static final long serialVersionUID = 4905308524000195104L;
        private Tactics color;
        
        public TutorialButton(String text, Tactics color, Stage next) {
            super(text);
            this.color = color;
            getButton().addActionListener(e -> {
                current = next;
                respond(null);
            });
        }

        @Override
        public String getText() {
            return getButton().getText();
        }
        
    }
}
