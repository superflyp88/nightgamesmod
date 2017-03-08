package nightgames.combat;

import java.io.Serializable;
import java.util.Optional;

import nightgames.actions.Movement;
import nightgames.areas.Area;
import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.characters.State;
import nightgames.characters.Trait;
import nightgames.global.DebugFlags;
import nightgames.global.Encs;
import nightgames.global.Global;
import nightgames.items.Item;
import nightgames.status.Enthralled;
import nightgames.status.Flatfooted;
import nightgames.status.Hypersensitive;
import nightgames.status.Status;
import nightgames.status.Stsflag;
import nightgames.trap.Spiderweb;
import nightgames.trap.Trap;

public class Encounter implements Serializable, IEncounter {

    private static final long serialVersionUID = 3122246133619156539L;
    protected Character p1;
    protected Character p2;
    
    protected boolean p1ff;
    protected boolean p2ff;
    
    protected transient Optional<String> p1Guaranteed;
    protected transient Optional<String> p2Guaranteed;
    protected Area location;
    protected transient Combat fight;
    protected int checkin;
    protected int fightTime;

    public Encounter(Character first, Character second, Area location) {
        this.location = location;
        p1 = first;
        p2 = second;
        checkin = 0;
        fight = null;
        p1Guaranteed = Optional.empty();
        p2Guaranteed = Optional.empty();
        checkEnthrall(p1, p2);
        checkEnthrall(p2, p1);
    }

    protected void checkEnthrall(Character p1, Character p2) {
        Status enthrall = p1.getStatus(Stsflag.enthralled);
        if (enthrall != null) {
            if (((Enthralled) enthrall).master != p2) {
                p1.removelist.add(enthrall);
                p1.addNonCombat(new Flatfooted(p1, 2));
                p1.addNonCombat(new Hypersensitive(p1));
                if (p1.human()) {
                    Global.gui()
                          .message("At " + p2.getName() + "'s interruption, you break free from the"
                                          + " succubus' hold on your mind. However, the shock all but"
                                          + " short-circuits your brain; you "
                                          + " collapse to the floor, feeling helpless and"
                                          + " strangely oversensitive");
                } else if (p2.human()) {
                    Global.gui().message(String.format(
                                    "%s doesn't appear to notice you at first, but when you wave your hand close to %s face %s "
                                    + "eyes open wide and %s immediately drops to the floor. Although the display leaves you "
                                    + "somewhat worried about %s health, %s is still in a very vulnerable position and you never "
                                    + "were one to let an opportunity pass you by.",
                                    p1.getName(), p1.possessiveAdjective(), p1.possessiveAdjective(),
                                    p1.pronoun(),
                                    p1.possessiveAdjective(), p1.pronoun()));
                }
            }
        }
    }

    /**
     * Checks for and runs any scenarios that arise from two Characters encountering each other. 
     * Returns true if something has come up that prevents them from being presented with the usual
     * campus Actions.
     */
    public boolean spotCheck() {
        if (p1.eligible(p2) && p2.eligible(p1)) {
            eligibleSpotCheck();
            return true;
        } else {
            ineligibleSpotCheck();
            return false;
        }
    }
    
    private void eligibleSpotCheck() {
        if (p1.state == State.shower) {
            p2.showerScene(p1, this);
            return;
        } else if (p2.state == State.shower) {
            p1.showerScene(p2, this);
            return;
        } else if (p1.state == State.webbed) {
            spider(p2, p1);
            return;
        } else if (p2.state == State.webbed) {
            spider(p1, p2);
            return;
        } else if (p1.state == State.crafting || p1.state == State.searching) {
            p2.spy(p1, this);
            return;
        } else if (p2.state == State.crafting || p2.state == State.searching) {
            p1.spy(p2, this);
            return;
        } else if (p1.state == State.masturbating) {
            caught(p2, p1);
            return;
        } else if (p2.state == State.masturbating) {
            caught(p1, p2);
            return;
        }
        
        // We need to run both vision checks no matter what, and they have no
        // side effects besides.
        boolean p2_sees_p1 = p2.spotCheck(p1);
        boolean p1_sees_p2 = p1.spotCheck(p1);
        
        if (p2_sees_p1 && p1_sees_p2) {
            p1.faceOff(p2, this);
            p2.faceOff(p1, this);
        } else if (p2_sees_p1) {
            p2.spy(p1, this);
        } else if (p1_sees_p2) {
            p1.spy(p2,  this);
        } else {
            // Ships passing in the night :(
            location.endEncounter();
        }
    }
    
    private void ineligibleSpotCheck() {
        // We can skip a lot of flavor lines if there aren't any humans around
        if (p1.human() || p2.human()) {
            Character human = p1.human() ? p1 : p2;
            Character npc = p1 != human ? p1 : p2;
            Character masturbating =
                            p1.state == State.masturbating ? p1 :
                                (p2.state == State.masturbating ? p2 : null);
            
            if (masturbating != null) {
                if (human == masturbating) {
                    ineligibleHumanCaughtMasturbatingBy(npc);
                } else {
                    ineligibleNpcCaughtMasturbating(npc);
                }
            } else if (p1 == human && !p1.eligible(p2)) {
                Global.gui().message("You encounter " + p2.getName() + ", but you still haven't recovered from your last fight.");
            } else if (p1 == human) {
                Global.gui().message(String.format(
                                "You find %s still naked from your last encounter, but %s's not fair game again until %s replaces %s clothes.",
                                p2.getName(), p2.pronoun(), p2.pronoun(), p2.possessiveAdjective()));
            }
        }
        location.endEncounter();
    }
    
    private void ineligibleHumanCaughtMasturbatingBy(Character npc) {
        Global.gui().message(String.format(
                        "%s catches you masturbating, but fortunately %s's not yet allowed to attack you, so %s just "
                        + "watches you pleasure yourself with an amused grin.",
                        npc.getName(), npc.pronoun(), npc.pronoun()));
    }
    
    private void ineligibleNpcCaughtMasturbating(Character npc) {
        Global.gui().message(String.format(
                        "You stumble onto %s with %s hand between %s legs, masturbating. Since you just fought you still can't touch %s, so "
                        + "you just watch the show until %s orgasms.",
                        npc.getName(), npc.possessiveAdjective(), npc.possessiveAdjective(), npc.directObject(),
                        npc.pronoun()));
    }

    /**
     * @param p The Character making the decision.
     * @param fight Whether the Character wishes to fight (true) or flee (false).
     * @param guaranteed Whether the Character's option is guaranteed to work. If so, the provided
     * String is messaged to the Character.
     */
    protected void fightOrFlight(Character p, boolean fight, Optional<String> guaranteed) {
        if (p == p1) {
            p1ff = fight;
            p1Guaranteed = guaranteed;
            checkin++;
        } else {
            p2ff = fight;
            p2Guaranteed = guaranteed;
            checkin++;
        }
        if (checkin >= 2) {
            doFightOrFlight();
        }
    }
    
    private void doFightOrFlight() {
        if (p1ff && p2ff) {
            startFight();
        } else if (p1ff) {
            fightOrFlee(p1, p2);
        } else if (p2ff) {
            fightOrFlee(p2, p1);
        } else {
            bothFlee();
        }
    }
    
    private void startFight() {
        startFightTimer();
        if (p1.human() || p2.human()) {
            this.fight = Global.gui().beginCombat(p1, p2);
        } else {
            this.fight = new Combat(p1, p2, location);
        }
    }
    
    // One Character wishes to Fight while the other attempts to flee.
    private void fightOrFlee(Character fighter, Character fleer) {
        Optional<String> fighterGuaranteed = (fighter == p1) ? p1Guaranteed : p2Guaranteed;
        Optional<String> fleerGuaranteed = (fleer == p1) ? p1Guaranteed : p2Guaranteed;
        
        // Fighter wins automatically
        if (fighterGuaranteed.isPresent() && !fleerGuaranteed.isPresent()) {
            if (fighter.human() || fleer.human()) {
                Global.gui().message(fighterGuaranteed.get());
            }
            startFightTimer();
            this.fight = Global.gui().beginCombat(fighter, fleer);
            return;
        }

        // Fleer wins automatically
        if (fleerGuaranteed.isPresent()) {
            if (fighter.human() || fleer.human()) {
                Global.gui().message(fleerGuaranteed.get());
            }
            p2.flee(location);
            return;
        }

        // Roll to see who's will triumphs
        if (rollFightVsFlee(fighter, fleer)) {
            if (fighter.human()) {
                Global.gui().message(fleer.getName() + " dashes away before you can move.");
            }
            fleer.flee(location);
        } else {
            startFightTimer();
            if (fighter.human() || fleer.human()) {
                if (fighter.human()) {
                    Global.gui().message(String.format(
                                    "%s tries to run, but you stay right on %s heels and catch %s.",
                                    fleer.getName(), fleer.possessiveAdjective(), fleer.directObject()));
                } else {
                    Global.gui().message(String.format(
                                    "You quickly try to escape, but %s is quicker. %s corners you and attacks.",
                                    fighter.getName(), Global.capitalizeFirstLetter(fighter.pronoun())));
                }
                this.fight = Global.gui()
                                   .beginCombat(fighter, fleer);
            } else {
                this.fight = new Combat(fighter, fleer, location);
            }
        }
    }
    
    /** Weights a roll with the fighter and fleers stats to determine who prevails. Returns
     * true if the fleer escapes, false otherwise.
     */
    private boolean rollFightVsFlee(Character fighter, Character fleer) {
        return fleer.check(Attribute.Speed, 10 + fighter.get(Attribute.Speed) + (fighter.has(Trait.sprinter) ? 5 : 0)
                        + (fleer.has(Trait.sprinter) ? -5 : 0));
    }

    private void startFightTimer() {
        fightTime = 2;
    }
    
    private void bothFlee() {
        boolean humanPresent = p1.human() || p2.human();
        if (p1Guaranteed.isPresent()) {
            if (humanPresent) {
                Global.gui().message(p1Guaranteed.get());
            }
            p1.flee(location);
        } else if (p2Guaranteed.isPresent()) {
            if (humanPresent) {
                Global.gui().message(p2Guaranteed.get());
            }
            p2.flee(location);
        } else if (p1.get(Attribute.Speed) + Global.random(10) >= p2.get(Attribute.Speed) + Global.random(10)) {
            if (p2.human()) {
                Global.gui()
                      .message(p1.getName() + " dashes away before you can move.");
            }
            p1.flee(location);
        } else {
            if (p1.human()) {
                Global.gui()
                      .message(p2.getName() + " dashes away before you can move.");
            }
            p2.flee(location);
        }
    }

    protected void ambush(Character attacker, Character target) {
        startFightTimer();
        target.addNonCombat(new Flatfooted(target, 3));
        if (p1.human() || p2.human()) {
            fight = Global.gui().beginCombat(attacker, target, 0);
            Global.gui().message(Global.format("{self:SUBJECT-ACTION:catch|catches} {other:name-do} by surprise and {self:action:attack|attacks}!", attacker, target));
        } else {
            fight = new Combat(attacker, target, location, 0);
        }
    }

    protected void showerambush(Character attacker, Character target) {
        startFightTimer();
        if (target.human()) {
            if (location.id() == Movement.shower) {
                Global.gui()
                      .message("You aren't in the shower long before you realize you're not alone. Before you can turn around, a soft hand grabs your exposed penis. "
                                      + attacker.getName() + " has the drop on you.");
            } else if (location.id() == Movement.pool) {
                Global.gui()
                      .message("The relaxing water causes you to lower your guard a bit, so you don't notice "
                                      + attacker.getName()
                                      + " until she's standing over you. There's no chance to escape, you'll have to face her nude.");
            }
        } else if (attacker.human()) {
            if (location.id() == Movement.shower) {
                Global.gui()
                      .message("You stealthily walk up behind " + target.getName()
                                      + ", enjoying the view of her wet naked body. When you stroke her smooth butt, "
                                      + "she jumps and lets out a surprised yelp. Before she can recover from her surprise, you pounce!");
            } else if (location.id() == Movement.pool) {
                Global.gui()
                      .message("You creep up to the jacuzzi where " + target.getName()
                                      + " is soaking comfortably. As you get close, you notice that her eyes are "
                                      + "closed and she may well be sleeping. You crouch by the edge of the jacuzzi for a few seconds and just admire her nude body with her breasts "
                                      + "just above the surface. You lean down and give her a light kiss on the forehead to wake her up. She opens her eyes and swears under her breath "
                                      + "when she sees you. She scrambles out of the tub, but you easily catch her before she can get away.");
            }
        }
        if (p1.human() || p2.human()) {
            fight = Global.gui()
                          .beginCombat(p1, p2, 1);
        } else {
            // this.fight=new NullGUI().beginCombat(p1,p2);
            fight = new Combat(p1, p2, location, 0);
        }
    }

    protected void aphrodisiactrick(Character attacker, Character target) {
        attacker.consume(Item.Aphrodisiac, 1);
        attacker.gainXP(attacker.getVictoryXP(target));
        target.gainXP(target.getDefeatXP(attacker));
        if (target.human()) {
            if (location.id() == Movement.shower) {
                Global.gui()
                      .message("The hot shower takes your fatigue away, but you can't seem to calm down. Your cock is almost painfully hard. You need to deal with this while "
                                      + "you have the chance. You jerk off quickly, hoping to finish before someone stumbles onto you. Right before you cum, you are suddenly grabbed from behind and "
                                      + "spun around. " + attacker.getName()
                                      + " has caught you at your most vulnerable and, based on her expression, may have been waiting for this moment. She kisses you and "
                                      + "firmly grasps your twitching dick. In just a few strokes, you cum so hard it's almost painful.\n");
            } else if (location.id() == Movement.pool) {
                Global.gui()
                      .message("As you relax in the jacuzzi, you start to feel extremely horny. Your cock is in your hand before you're even aware of it. You stroke yourself "
                                      + "off underwater and you're just about ready to cum when you hear nearby footsteps. Oh shit, you'd almost completely forgotten you were in the middle of a "
                                      + "match. The footsteps are from " + attacker.getName()
                                      + ", who sits down at the edge of the jacuzzi while smiling confidently. You look for a way to escape, but it's "
                                      + "hopeless. You were so close to finishing you just need to cum now. "
                                      + attacker.getName()
                                      + " seems to be thinking the same thing, as she dips her bare feet into the "
                                      + "water and grasps your penis between them. She pumps you with her feet and you shoot your load into the water in seconds.\n");
            }
        } else if (attacker.human()) {
            if (location.id() == Movement.shower) {
                Global.gui()
                      .message("You empty the bottle of aphrodisiac onto the shower floor, letting the heat from the shower turn it to steam. You watch "
                                      + target.getName() + " and wait "
                                      + "for a reaction. Just when you start to worry that it was all washed down the drain, you see her hand slip between her legs. Her fingers go to work pleasuring herself "
                                      + "and soon she's completely engrossed in her masturbation, allowing you to safely get closer without being noticed. She's completely unreserved, assuming she's alone "
                                      + "and you feel a voyeuristic thrill at the show. You can't just remain an observer though. For this to count as a victory, you need to be in physical contact with her "
                                      + "when she orgasms. When you judge that she's in the home stretch, you embrace her from behind and kiss her neck. She freezes in surprise and you move your hand between "
                                      + "her legs to replace her own. Her pussy is hot, wet, and trembling with need. You stick two fingers into her and rub her clit with your thumb. She climaxes almost "
                                      + "immediately. You give her a kiss on the cheek and leave while she's still too dazed to realize what happened. You're feeling pretty horny, but after a show like that "
                                      + "it's hardly surprising.\n");
            } else if (location.id() == Movement.pool) {
                Global.gui()
                      .message("You sneak up to the jacuzzi, and empty the aphrodisiac into the water without "
                                      + target.getName() + " noticing. You slip away and find a hiding spot. In a "
                                      + "couple minutes, you notice her stir. She glances around, but fails to see you and then closes her eyes and relaxes again. There's something different now though and "
                                      + "her soft moan confirms it. You grin and quietly approach again. You can see her hand moving under the surface of the water as she enjoys herself tremendously. Her moans "
                                      + "rise in volume and frequency. Now's the right moment. You lean down and kiss her on the lips. Her masturbation stops immediately, but you reach underwater and finger "
                                      + "her to orgasm. When she recovers, she glares at you for your unsportsmanlike trick, but she can't manage to get really mad in the afterglow of her climax. You're "
                                      + "pretty turned on by the encounter, but you can chalk this up as a win.\n");
            }
        }
        if (!target.mostlyNude()) {
            attacker.gain(target.getTrophy());
        }
        target.nudify();
        target.defeated(attacker);
        target.getArousal()
              .empty();
        attacker.tempt(20);
        Global.getMatch()
              .score(attacker, target.has(Trait.event) ? 5 : 1);
        attacker.state = State.ready;
        target.state = State.ready;
        location.endEncounter();
    }

    protected void caught(Character attacker, Character target) {
        attacker.gainXP(attacker.getVictoryXP(target));
        target.gainXP(target.getDefeatXP(attacker));
        if (target.human()) {
            Global.gui()
                  .message("You jerk off frantically, trying to finish as fast as possible. Just as you feel the familiar sensation of imminent orgasm, you're grabbed from behind. "
                                  + "You freeze, cock still in hand. As you turn your head to look at your attacker, "
                                  + attacker.getName()
                                  + " kisses you on the lips and rubs the head of your penis with her "
                                  + "palm. You were so close to the edge that just you cum instantly.");
            if (!target.mostlyNude()) {
                Global.gui()
                      .message("You groan in resignation and reluctantly strip off your clothes and hand them over.");
            }
        } else if (attacker.human()) {
            Global.gui()
                  .message("You spot " + target.getName()
                                  + " leaning against the wall with her hand working excitedly between her legs. She is mostly, but not completely successful at "
                                  + "stifling her moans. She hasn't noticed you yet, and as best as you can judge, she's pretty close to the end. It'll be an easy victory for you as long as you work fast. "
                                  + "You sneak up and hug her from behind while kissing the nape of her neck. She moans and shudders in your arms, but doesn't stop fingering herself. She probably realizes "
                                  + "she has no chance of winning even if she fights back. You help her along by licking her neck and fondling her breasts as she hits her climax.");
        }
        if (!target.mostlyNude()) {
            attacker.gain(target.getTrophy());
        }
        target.nudify();
        target.defeated(attacker);
        target.getArousal()
              .empty();
        attacker.tempt(20);
        Global.getMatch()
              .score(attacker, target.has(Trait.event) ? 5 : 1);
        attacker.state = State.ready;
        target.state = State.ready;
        location.endEncounter();
    }

    protected void spider(Character attacker, Character target) {
        attacker.gainXP(attacker.getVictoryXP(target));
        target.gainXP(target.getDefeatXP(attacker));
        if (attacker.human()) {
            Global.gui()
                  .message(target.getName()
                                  + " is naked and helpless in the giant rope web. You approach slowly, taking in the lovely view of her body. You trail your fingers "
                                  + "down her front, settling between her legs to tease her sensitive pussy lips. She moans and squirms, but is completely unable to do anything in her own defense. "
                                  + "You are going to make her cum, that's just a given. If you weren't such a nice guy, you would leave her in that trap afterward to be everyone else's prey "
                                  + "instead of helping her down. You kiss and lick her neck, turning her on further. Her entrance is wet enough that you can easily work two fingers into her "
                                  + "and begin pumping. You gradually lick your way down her body, lingering at her nipples and bellybutton, until you find yourself eye level with her groin. "
                                  + "You can see her clitoris, swollen with arousal, practically begging to be touched. You trap the sensitive bud between your lips and attack it with your tongue. "
                                  + "The intense stimulation, coupled with your fingers inside her, quickly brings her to orgasm. While she's trying to regain her strength, you untie the ropes "
                                  + "binding her hands and feet and ease her out of the web.");
        } else if (target.human()) {
            Global.gui()
                  .message("You're trying to figure out a way to free yourself, when you see " + attacker.getName()
                                  + " approach. You groan in resignation. There's no way you're "
                                  + "going to get free before she finishes you off. She smiles as she enjoys your vulnerable state. She grabs your dangling penis and puts it in her mouth, licking "
                                  + "and sucking it until it's completely hard. Then the teasing starts. She strokes you, rubs you, and licks the head of your dick. She uses every technique to "
                                  + "pleasure you, but stops just short of letting you ejaculate. It's maddening. Finally you have to swallow your pride and beg to cum. She pumps you dick in earnest "
                                  + "now and fondles your balls. When you cum, you shoot your load onto her face and chest. You hang in the rope web, literally and figuratively drained. "
                                  + attacker.getName() + " " + "graciously unties you and helps you down.");
        }
        if (!target.mostlyNude()) {
            attacker.gain(target.getTrophy());
        }
        target.nudify();
        target.defeated(attacker);
        target.getArousal()
              .empty();
        attacker.tempt(20);
        Global.getMatch()
              .score(attacker, target.has(Trait.event) ? 5 : 1);
        attacker.state = State.ready;
        target.state = State.ready;
        location.endEncounter();
        location.remove(location.get(Spiderweb.class));
    }

    public void intrude(Character intruder, Character assist) {
        fight.intervene(intruder, assist);
    }

    public boolean battle() {
        fightTime--;
        if (fightTime <= 0 && !fight.isEnded()) {
            fight.go();
            return true;
        } else {
            return false;
        }
    }

    public Combat getCombat() {
        return fight;
    }

    public Character getPlayer(int i) {
        if (i == 1) {
            return p1;
        } else {
            return p2;
        }
    }

    protected void steal(Character thief, Character target) {
        if (thief.human()) {
            Global.gui()
                  .message("You quietly swipe " + target.getName()
                                  + "'s clothes while she's occupied. It's a little underhanded, but you can still turn them in for cash just as if you defeated her.");
        }
        thief.gain(target.getTrophy());
        target.nudify();
        target.state = State.lostclothes;
        location.endEncounter();
    }

    public void trap(Character opportunist, Character target, Trap trap) {
        if (opportunist.human()) {
            Global.gui()
                  .message("You leap out of cover and catch " + target.getName() + " by surprise.");
        } else if (target.human()) {
            Global.gui()
                  .message("Before you have a chance to recover, " + opportunist.getName() + " pounces on you.");
        }
        trap.capitalize(opportunist, target, this);
    }

    public void engage(Combat fight) {
        this.fight = fight;
        if (fight.p1.human() || fight.p2.human()) {
            Global.gui().watchCombat(fight);
        }
    }

    public void parse(Encs choice, Character self, Character target) {
        parse(choice, self, target, null);
    }

    public void parse(Encs choice, Character self, Character target, Trap trap) {
        if (Global.isDebugOn(DebugFlags.DEBUG_SCENE)) {
            System.out.println(Global.format("{self:true-name} uses %s (%s) on {other:true-name}", self, target, choice, trap));
        }
        switch (choice) {
            case ambush:
                ambush(self, target);
                break;
            case capitalize:
                trap(self, target, trap);
                break;
            case showerattack:
                showerambush(self, target);
                break;
            case aphrodisiactrick:
                aphrodisiactrick(self, target);
                break;
            case stealclothes:
                steal(Global.getPlayer(), target);
                break;
            case fight:
                fightOrFlight(self, true, Optional.empty());
                break;
            case flee:
                fightOrFlight(self, false, Optional.empty());
                break;
            case fleehidden:
                checkin += 2;
                fightOrFlight(self, false, Optional.of(fleeHiddenMessage(self, target)));
                break;
            case smoke:
                fightOrFlight(self, false, Optional.of(smokeMessage(self)));
                self.consume(Item.SmokeBomb, 1);
                break;
            default:
                return;
        }
    }
    
    private String smokeMessage(Character c) {
        return String.format("%s a smoke bomb and %s.", 
                        Global.capitalizeFirstLetter(c.subjectAction("drop", "drops"))
                        , c.action("disappear", "disappears"));
    }

    private String fleeHiddenMessage(Character c, Character other) {
        return Global.format("{self:SUBJECT-ACTION:flee} before {other:subject-action:can} notice {self:direct-object}.", c, other);
    }

    @Override
    public boolean checkIntrude(Character c) {
        return fight != null && !c.equals(p1) && !c.equals(p2);
    }

    @Override
    public void watch() {
        Global.gui().watchCombat(fight);
        fight.go();
    }
}
