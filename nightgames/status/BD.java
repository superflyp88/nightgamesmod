package nightgames.status;

import nightgames.characters.Attribute;
import nightgames.characters.Character;
import nightgames.combat.Combat;

public class BD extends DurationStatus {
	public BD(Character affected) {
		super("Bondage", affected, 10);
		flag(Stsflag.bondage);
	}

	@Override
	public String describe() {
		if(affected.human()){
			return "Fantasies of being tied up continue to dance through your head.";
		}
		else{
			return affected.name()+" is affected by a brief bondage fetish.";
		}
	}

	@Override
	public String initialMessage(Combat c, boolean replaced) {
		return String.format("%s now affected by a bondage fetish.\n", affected.subjectAction("are", "is"));
	}

	@Override
	public float fitnessModifier () {
		return -1;
	}

	@Override
	public int mod(Attribute a) {
		return 0;
	}

	@Override
	public int regen(Combat c) {
		super.regen(c);
		if(affected.bound()){
			affected.arouse(affected.getArousal().max()/20, c);
		}
		return 0;
		
	}

	@Override
	public int damage(Combat c, int x) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double pleasure(Combat c, double x) {
		return affected.is(Stsflag.bound) ? x / 2 : 0;
	}

	@Override
	public int weakened(int x) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int tempted(int x) {
		// TODO Auto-generated method stub
		return affected.is(Stsflag.bound) ? x / 2 : 0;
	}

	@Override
	public int evade() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int escape() {
		// TODO Auto-generated method stub
		return -15;
	}

	@Override
	public int gainmojo(int x) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int spendmojo(int x) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int counter() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int value() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Status instance(Character newAffected, Character newOther) {
		return new BD(newAffected);
	}
}