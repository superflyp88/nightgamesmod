package skills;

import global.Global;
import characters.Attribute;
import characters.Character;

import combat.Combat;
import combat.Result;

public class SuckNeck extends Skill {

	public SuckNeck(Character self) {
		super("Suck Neck", self);
	}

	@Override
	public boolean usable(Combat c, Character target) {
		return c.getStance().kiss(getSelf())&&getSelf().canAct();
	}

	@Override
	public void resolve(Combat c, Character target) {
		if(target.roll(this, c, accuracy())){
			if(getSelf().get(Attribute.Dark)>=1){
				if(getSelf().human()){
					c.write(getSelf(),deal(c,0,Result.special, target));
				}
				else if(target.human()){
					c.write(getSelf(),receive(c,0,Result.special, target));
				}
				int m = 10 + Math.min(20, getSelf().get(Attribute.Dark) / 2);
				target.weaken(c, m);
				getSelf().heal(c, m);
			}
			else{
				if(getSelf().human()){
					c.write(getSelf(),deal(c,0,Result.normal, target));
				}
				else if(target.human()){
					c.write(getSelf(),receive(c,0,Result.normal, target));
				}
			}
			int m = 1 + Global.random(8);
			target.body.pleasure(getSelf(), getSelf().body.getRandom("mouth"), target.body.getRandom("skin"), m, c);					
			getSelf().buildMojo(c, 5);
		}
		else{
			if(getSelf().human()){
				c.write(getSelf(),deal(c,0,Result.miss, target));
			}
			else if(target.human()){
				c.write(getSelf(),receive(c,0,Result.miss, target));
			}
		}
	}

	@Override
	public boolean requirements(Character user) {
		return getSelf().get(Attribute.Seduction)>=12;
	}

	@Override
	public Skill copy(Character user) {
		return new SuckNeck(user);
	}
	public int speed(){
		return 5;
	}
	public int accuracy(){
		return 7;
	}
	public Tactics type(Combat c) {
		return Tactics.pleasure;
	}
	public String getLabel(Combat c){
		if(getSelf().get(Attribute.Dark)>=1){
			return "Drain energy";
		}
		else{
			return getName(c);
		}
	}

	@Override
	public String deal(Combat c, int damage, Result modifier, Character target) {
		if(modifier == Result.miss){
			return "You lean in to kiss "+target.name()+"'s neck, but she slips away.";
		}
		else if(modifier == Result.special){
			return "You draw close to "+target.name()+" as she's momentarily too captivated to resist. You run your tongue along her neck and bite gently. She shivers and you " +
					"can feel the energy of her pleasure flow into you, giving you strength.";
		}
		else{
			return "You lick and suck "+target.name()+"'s neck hard enough to leave a hickey.";
		}
	}

	@Override
	public String receive(Combat c, int damage, Result modifier, Character target) {
		if(modifier == Result.miss){
			return getSelf().name()+" goes after your neck, but you push her back.";
		}
		else if(modifier == Result.special){
			return getSelf().name()+" presses her lips against your neck. She gives you a hickey and your knees start to go weak. It's like your strength is being sucked out through " +
					"your skin.";
		}
		else{
			return getSelf().name()+" licks and sucks your neck, biting lightly when you aren't expecting it.";
		}
	}

	@Override
	public String describe() {
		return "Suck on opponent's neck. Highly variable effectiveness";
	}

	@Override
	public boolean makesContact() {
		return true;
	}

	public String getTargetOrganType(Combat c, Character target) {
		return "skin";
	}
	public String getWithOrganType(Combat c, Character target) {
		return "mouth";
	}
}
