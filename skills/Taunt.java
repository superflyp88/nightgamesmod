package skills;

import status.Shamed;
import global.Global;
import characters.Attribute;
import characters.Character;
import characters.Emotion;
import characters.Trait;

import combat.Combat;
import combat.Result;

public class Taunt extends Skill {

	public Taunt(Character self) {
		super("Taunt", self);
	}

	@Override
	public boolean usable(Combat c, Character target) {
		return target.nude()&&!c.getStance().sub(getSelf())&&getSelf().canSpend(5)&&getSelf().canAct()&&!getSelf().has(Trait.shy);
	}

	@Override
	public void resolve(Combat c, Character target) {
		getSelf().spendMojo(c, 5);
		if(getSelf().human()){
			c.write(getSelf(),deal(c,0,Result.normal, target));
		}
		else if(target.human()){
			c.write(getSelf(),receive(c,0,Result.normal, target));
		}
		int m = (6+Global.random(4)) * Math.min(2, (1 + getSelf().getSkimpiness()));
		if(target.has(Trait.imagination)){
			m += 4;
			target.tempt(c, getSelf(), m);
			if(Global.random(4)>=1){
				target.add(new Shamed(target));
			}
		} else {
			target.tempt(c, getSelf(), m);
			if(Global.random(4)>=2){
				target.add(new Shamed(target));
			}
		}
		target.emote(Emotion.angry,30);
		target.emote(Emotion.nervous,15);
		getSelf().emote(Emotion.dominant, 20);
		target.loseMojo(c, 10);
		
	}

	@Override
	public boolean requirements(Character user) {
		return user.get(Attribute.Cunning)>=8;
	}

	@Override
	public Skill copy(Character user) {
		return new Taunt(user);
	}
	public int speed(){
		return 9;
	}
	public Tactics type(Combat c) {
		return Tactics.pleasure;
	}

	@Override
	public String deal(Combat c, int damage, Result modifier, Character target) {
		return "You tell "+target.name()+" that if she's so eager to be fucked senseless, you're available during off hours.";
	}

	@Override
	public String receive(Combat c, int damage, Result modifier, Character target) {
		return getSelf().taunt();
	}

	@Override
	public String describe() {
		return "Embarrass your opponent, may inflict Shamed";
	}
}
