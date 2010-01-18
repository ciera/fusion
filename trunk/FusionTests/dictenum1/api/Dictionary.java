package api;

import edu.cmu.cs.fusion.annot.Constraint;
import edu.cmu.cs.fusion.annot.Constraints;
import edu.cmu.cs.fusion.annot.Relation.Effect;
import relations.Dictionary_Keys;
import relations.Dictionary_Size;

@Constraints({
	@Constraint(
			op = "Dictionary.keys() : Enumeration",
			trigger = "TRUE",
			requires = "Dictionary_Size(target)",
			effects = {}
	)
})
public class Dictionary<K> {
	
	@Dictionary_Size({"target"})
	public void size() {}
	
	@Dictionary_Keys({"target"})
	@Dictionary_Size(value = {"target"}, effect = Effect.REMOVE)	
	public Enumeration keys() { 
		return new Enumeration();
	}
}
