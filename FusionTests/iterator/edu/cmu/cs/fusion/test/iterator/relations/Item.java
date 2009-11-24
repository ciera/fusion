package edu.cmu.cs.fusion.test.iterator.relations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

import edu.cmu.cs.fusion.annot.Relation;
import edu.cmu.cs.fusion.test.io.api.FileWriter;
import edu.cmu.cs.fusion.test.iterator.api.Iterator;

@Relation({Object.class, Collection.class})
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Item {
	public String[] value();
	public Relation.Effect effect() default Relation.Effect.ADD;
	public String test() default "";
}
