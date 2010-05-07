/**
 * Copyright (c) 2006, 2007, 2008 Marwan Abi-Antoun, Jonathan Aldrich, Nels E. Beckman,
 * Kevin Bierhoff, David Dickey, Ciera Jaspan, Thomas LaToza, Gabriel Zenarosa, and others.
 *
 * This file is part of Crystal.
 *
 * Crystal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Crystal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Crystal.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.cmu.cs.fusion.alias;

import org.eclipse.jdt.core.dom.ITypeBinding;

import edu.cmu.cs.crystal.analysis.alias.ObjectLabel;

public class LiteralLabel implements ObjectLabel {
	private Object literal;
	private ITypeBinding type;
	
	public LiteralLabel(Object literal, ITypeBinding type) {
		this.type = type;
		this.literal = literal;
	}

	/**
	 * A literal label is never a summary node. It is what it is....
	 */
	public boolean isSummary() {return false;}
	
	public String toString() {return literal.toString();}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((literal == null) ? 0 : literal.hashCode());
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
		LiteralLabel other = (LiteralLabel) obj;
		if (literal == null) {
			if (other.literal != null)
				return false;
		} else if (!literal.equals(other.literal))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see edu.cmu.cs.crystal.analysis.alias.ObjectLabel#getType()
	 */
	public ITypeBinding getType() {
		return type;
	}

	/**
	 * @return
	 */
	public Object getLiteral() {
		return literal;
	}
}
