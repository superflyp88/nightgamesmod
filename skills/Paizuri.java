package skills;

import java.util.List;

import global.Global;
import characters.Attribute;
import characters.Character;
import characters.Trait;
import characters.body.BodyPart;
import characters.body.BreastsPart;

import combat.Combat;
import combat.Result;

public class Paizuri extends Skill {

	public Paizuri(Character self) {
		super("Use Breasts", self);
	}

	static int MIN_REQUIRED_BREAST_SIZE = 1;

	@Override
	public boolean usable(Combat c, Character target) {
		return getSelf().hasBreasts() && getSelf().body.getLargestBreasts().size > MIN_REQUIRED_BREAST_SIZE && target.hasDick() && getSelf().topless()
				&& target.pantsless() && c.getStance().reachBottom(getSelf())
				&& c.getStance().front(getSelf())
				&& getSelf().canAct() && !c.getStance().penetration(getSelf());
	}

	@Override
	public void resolve(Combat c, Character target) {
		BreastsPart breasts = getSelf().body.getLargestBreasts();
		//try to find a set of breasts large enough, if none, default to largest.
		for (int i = 0 ; i < 3; i++) {
			BreastsPart otherbreasts = getSelf().body.getRandomBreasts();
			if (otherbreasts.size > MIN_REQUIRED_BREAST_SIZE) {
				breasts = otherbreasts;
				break;
			}
		}

		int m = (4 + Global.random(3));
		if (target.human()) {
			c.write(getSelf(), receive(0, Result.normal, target, breasts));
		}
		target.body.pleasure(getSelf(), getSelf().body.getRandom("breasts"), target.body.getRandom("cock"), m, c);					

		getSelf().buildMojo(c, 25);
	}

	@Override
	public boolean requirements(Character user) {
		return user.get(Attribute.Seduction) >= 28
				&& !user.has(Trait.petite) && user.hasBreasts();
	}

	@Override
	public Skill copy(Character user) {
		return new Paizuri(user);
	}

	public int speed() {
		return 4;
	}

	public Tactics type(Combat c) {
		return Tactics.pleasure;
	}

	@Override
	public String deal(Combat c, int damage, Result modifier, Character target) {
		// TODO Auto-generated method stub
		return null;
	}

	public String receive(int damage, Result modifier, Character target, BreastsPart breasts) {
		StringBuilder b = new StringBuilder();
		b.append(getSelf().name() + " squeezes your dick between her ");
		b.append(breasts.describe(getSelf()));
		b.append(". She rubs them up and down your shaft and teasingly licks your tip.");
		return b.toString();
	}

	@Override
	public String describe() {
		return "Rub your opponent's dick between your boobs";
	}

	@Override
	public String receive(Combat c, int damage, Result modifier, Character target) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean makesContact() {
		return true;
	}

	public String getTargetOrganType(Combat c, Character target) {
		return "cock";
	}
	public String getWithOrganType(Combat c, Character target) {
		return "breasts";
	}
}
