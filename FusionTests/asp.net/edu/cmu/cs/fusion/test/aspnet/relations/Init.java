package edu.cmu.cs.fusion.test.aspnet.relations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.cmu.cs.fusion.annot.Relation;
import edu.cmu.cs.fusion.test.aspnet.api.DropDownList;
import edu.cmu.cs.fusion.test.aspnet.api.Page;

@Relation({Page.class})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Init {
	public String[] value();
	public Relation.Effect effect() default Relation.Effect.ADD;
	public String test() default "";
}
