package edu.cmu.cs.fusion.test.aspnet.relations;

import edu.cmu.cs.fusion.annot.Relation;
import edu.cmu.cs.fusion.test.aspnet.api.ListControl;
import edu.cmu.cs.fusion.test.aspnet.api.ListItem;

@Relation(types = {ListItem.class, ListControl.class})
public @interface Child {
	public String[] value();
	public Relation.Effect effect() default Relation.Effect.ADD;
	public String test() default "";
}
