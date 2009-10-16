package edu.cmu.cs.fusion.test.aspnet.relations;

import edu.cmu.cs.fusion.annot.Relation;
import edu.cmu.cs.fusion.test.aspnet.api.ListControl;
import edu.cmu.cs.fusion.test.aspnet.api.ListItemCollection;

@Relation(types = {ListItemCollection.class, ListControl.class})
public @interface Items {
	public String[] value();
	public Relation.Effect effect() default Relation.Effect.ADD;
	public String test() default "";
}
