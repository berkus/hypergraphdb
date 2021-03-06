package org.hypergraphdb.app.owl.model.classexpr.restrict;

import org.hypergraphdb.HGHandle;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

/**
 * OWLDataExactCardinalityHGDB.
 * 
 * @author Thomas Hilpold (CIAO/Miami-Dade County)
 * @created Oct 18, 2011
 */
public class OWLDataExactCardinalityHGDB extends OWLDataCardinalityRestrictionHGDB implements OWLDataExactCardinality
{
	/**
	 * @param args
	 *            [0]...property, [1]...filler
	 */
	public OWLDataExactCardinalityHGDB(HGHandle... args)
	{
		super(args[0], 0, args[1]);
		// TODO we call with 0 cardinality here, test that HG sets it later.
		if (args.length != 2)
			throw new IllegalArgumentException("Must be exactly 2 handles.");
	}

	public OWLDataExactCardinalityHGDB(HGHandle property, int cardinality, HGHandle filler)
	{
		super(property, cardinality, filler);
	}

	/**
	 * Gets the class expression type for this class expression
	 * 
	 * @return The class expression type
	 */
	public ClassExpressionType getClassExpressionType()
	{
		return ClassExpressionType.DATA_EXACT_CARDINALITY;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (super.equals(obj))
		{
			return obj instanceof OWLDataExactCardinality;
		}
		return false;
	}

	public void accept(OWLClassExpressionVisitor visitor)
	{
		visitor.visit(this);
	}

	public void accept(OWLObjectVisitor visitor)
	{
		visitor.visit(this);
	}

	public <O> O accept(OWLClassExpressionVisitorEx<O> visitor)
	{
		return visitor.visit(this);
	}

	public <O> O accept(OWLObjectVisitorEx<O> visitor)
	{
		return visitor.visit(this);
	}

	public OWLClassExpression asIntersectionOfMinMax()
	{
		return getOWLDataFactory().getOWLObjectIntersectionOf(
				getOWLDataFactory().getOWLDataMinCardinality(getCardinality(), getProperty(), getFiller()),
				getOWLDataFactory().getOWLDataMaxCardinality(getCardinality(), getProperty(), getFiller()));
	}

}
