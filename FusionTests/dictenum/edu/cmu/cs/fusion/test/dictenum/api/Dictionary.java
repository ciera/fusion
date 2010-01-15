package edu.cmu.cs.fusion.test.dictenum.api;

import edu.cmu.cs.fusion.annot.Constraint;
import edu.cmu.cs.fusion.annot.Constraints;
import edu.cmu.cs.fusion.annot.Relation.Effect;
import edu.cmu.cs.fusion.test.dictenum.relations.Dictionary_Get;
import edu.cmu.cs.fusion.test.dictenum.relations.Dictionary_Keys;
import edu.cmu.cs.fusion.test.dictenum.relations.Enumeration_NextElement;

@Constraints({
	@Constraint(
			op = "Dictionary.get() : void",
			trigger = "TRUE",
			requires = "Enumeration_NextElement(e)",
			effects = {}
	)	
})
public class Dictionary {

	@Dictionary_Keys({"target"})
	public void keys() {}
	
	@Dictionary_Get({"target"})
	@Enumeration_NextElement(value = {"*"}, effect = Effect.REMOVE)
	public void get() {}
	
}

