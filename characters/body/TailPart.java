package characters.body;

import java.io.PrintWriter;
import java.util.Scanner;

import combat.Combat;

import characters.Attribute;
import characters.Character;

public enum TailPart implements BodyPart {
	demonic("demonic ", .2, 1.2, 1),
	cat("cat's ", .3, 1.5, 1.5), 
	normal("", 0, 1, 1);

	public String desc;
	public double hotness;
	public double pleasure;
	public double sensitivity;
	TailPart(String desc, double hotness, double pleasure, double sensitivity) {
		this.desc = desc;
		this.hotness = hotness;
		this.pleasure = pleasure;
		this.sensitivity = sensitivity;
	}

	@Override
	public void describeLong(StringBuilder b, Character c) {
		b.append("A lithe " + describe(c) + " swings lazily behind " + c.nameOrPossessivePronoun() + " back.");
	}

	@Override
	public String describe(Character c) {
		return desc + "tail";
	}

	@Override
	public double priority(Character c) {
		return this.getPleasure(null);
	}

	@Override
	public String fullDescribe(Character c) {
		return desc + "tail";
	}
	
	@Override
	public String toString() {
		return desc + "tail";
	}
	
	public boolean isType(String type) {
		return type.equalsIgnoreCase("tail");
	}

	@Override
	public String getType() {
		return "tail";
	}

	@Override
	public double getHotness(Character self, Character opponent) {
		return hotness;
	}

	@Override
	public double getPleasure(BodyPart target) {
		return pleasure;
	}

	@Override
	public double getSensitivity(BodyPart target) {
		return sensitivity;
	}
	
	@Override
	public boolean isReady(Character self) {
		return true;
	}

	@Override
	public void save(PrintWriter saver) {
		saver.write(this.name());
	}

	@Override
	public BodyPart load(Scanner loader) {
		return TailPart.valueOf(loader.nextLine());
	}

	@Override
	public double applyBonuses(Character self, Character opponent,
			BodyPart target, double damage, Combat c) {
		return 0;
	}

	@Override
	public String getFluids(Character c) {
		return this == TailPart.demonic ? "tail-cum" : "";
	}

	@Override
	public boolean isErogenous() {
		return false;
	}
	
	@Override
	public boolean isNotable() {
		return true;
	}
	
	@Override
	public double applyReceiveBonuses(Character self, Character opponent,
			BodyPart target, double damage, Combat c) {
		return 0;
	}

	@Override
	public BodyPart upgrade() {
		return this;
	}

	@Override
	public BodyPart downgrade() {
		return this;
	}
	@Override
	public String prefix() {
		if (desc.length() > 0)
			return "aieou".indexOf(desc.charAt(0)) >= 0 ? "an " : "a ";
		else 
			return "a";
	}
	@Override
	public int compare(BodyPart other) {
		return 0;
	}
	@Override
	public boolean isVisible(Character c) {
		return true;
	}

	@Override
	public double applySubBonuses(Character self, Character opponent,
			BodyPart with, BodyPart target, double damage, Combat c) {
		return 0;
	}

	@Override
	public int mod(Attribute a, int total) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void tickHolding(Combat c, Character self, Character opponent, BodyPart otherOrgan) {

	}
}
