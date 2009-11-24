package edu.cmu.cs.fusion;

public class Variant {
	public static int SOUND = 1;
	public static int COMPLETE = 2;
	public static int PRAGMATIC = 4;
	
	public static Variant SOUND_VARIANT = new Variant(SOUND);
	public static Variant COMPLETE_VARIANT = new Variant(COMPLETE);
	public static Variant PRAGMATIC_VARIANT = new Variant(PRAGMATIC);

	int val;
		
	public Variant(int variants) {
		this.val = variants;
	}

	@Override
	public String toString() {
		String str = "";
		if (isSound())
			str += "Sound";
		if (isComplete())
			str += str.equals("") ? "Complete" : "/Complete";
		if (isPragmatic())
			str += str.equals("") ? "Pragmatic" : "/Pragmatic";
		return str;
	}
	
	public boolean isSound() {
		return (val & 0x1) == SOUND;
	}
	
	public boolean isComplete() {
		return (val & 0x2) == COMPLETE;
	}
	
	public boolean isPragmatic() {
		return (val & 0x4) == PRAGMATIC;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + val;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Variant other = (Variant) obj;
		if (val != other.val)
			return false;
		return true;
	}

	public Variant merge(Variant variant) {
		return new Variant(val | variant.val);
	}
}
