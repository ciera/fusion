package edu.cmu.cs.fusion.test.aspnet.relations;

import edu.cmu.cs.fusion.annot.Relation;
import edu.cmu.cs.fusion.test.aspnet.api.ListItem;
import edu.cmu.cs.fusion.test.aspnet.api.ListItemCollection;

@Relation(types = {ListItem.class, ListItemCollection.class})
public @interface Item {
	public String[] value();
	public Relation.Effect effect() default Relation.Effect.ADD;
	public String test() default "";
}
