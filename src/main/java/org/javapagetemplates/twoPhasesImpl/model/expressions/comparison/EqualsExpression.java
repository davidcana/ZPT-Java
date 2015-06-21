package org.javapagetemplates.twoPhasesImpl.model.expressions.comparison;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.javapagetemplates.common.ExpressionTokenizer;
import org.javapagetemplates.common.exceptions.EvaluationException;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;
import org.javapagetemplates.common.scripting.EvaluationHelper;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.model.expressions.EvaluableToBoolean;
import org.javapagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpressionImpl;

/**
 * <p>
 *   Defines an equals expression with two or more expressions.
 * </p>
 * 
 * 
 *  Java Page Templates
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.0 $
 */
public class EqualsExpression extends JPTExpressionImpl implements EvaluableToBoolean {

	private static final long serialVersionUID = -1879171293873071854L;
	
	private List<JPTExpression> expressions = new ArrayList<JPTExpression>();
	
	
	public EqualsExpression(){}
	public EqualsExpression( String stringExpression ){
		super( stringExpression );
	}

	
	public List<JPTExpression> getExpressions() {
		return this.expressions;
	}

	public void setExpressions( List<JPTExpression> expressions ) {
		this.expressions = expressions;
	}

	public void addExpression( JPTExpression expression ){
		this.expressions.add( expression );
	}

	static public EqualsExpression generate( String exp ) throws ExpressionSyntaxException {
        
		String expression = exp.substring( TwoPhasesPageTemplate.EXPR_EQUALS.length() ).trim();
        
		// Check some conditions
        if ( expression.length() == 0 ) {
            throw new ExpressionSyntaxException( "EQUALS expression void." );
        }
        ExpressionTokenizer segments = new ExpressionTokenizer( 
        		expression, 
        		TwoPhasesPageTemplate.EXPRESSION_DELIMITER );
        
        if ( segments.countTokens() == 1 ) {
        	throw new ExpressionSyntaxException(
        			"Only one element in EQUALS expression, please add at least one more." );
        }
        
        // Iterate through segments
        EqualsExpression result = new EqualsExpression( exp );
        
        String segment1 = segments.nextToken().trim();
        result.addExpression(
        		ExpressionUtils.generate( segment1 ) ); 
        		
        while ( segments.hasMoreTokens() ) {
            String segment = segments.nextToken().trim();
            result.addExpression(
            		ExpressionUtils.generate( segment ) ); 
        }
        
        return result;
	}
	
	@Override
	public Boolean evaluateToBoolean( EvaluationHelper evaluationHelper ) throws EvaluationException {
		
		Iterator<JPTExpression> i = this.expressions.iterator();
		JPTExpression expression1 = i.next();
        Object result1 = expression1.evaluate( evaluationHelper );
        
        while ( i.hasNext() ) {
        	JPTExpression expression = i.next();
        	Object result = expression.evaluate( evaluationHelper );
			if ( ! areEquivalent( result1, result ) ){
            	return false;
            }
        }

        return true;
	}
    
	static boolean areEquivalent( Object object1, Object object2 ){
    	
    	if ( object1 instanceof Number && object2 instanceof Number ){
    		Number number1 = ( Number ) object1;
    		Number number2 = ( Number ) object2;
    		
    		return number1.longValue() == number2.longValue();
    	}
    	
    	return object1.equals( object2 );
    }
    
	@Override
	public Object evaluate(EvaluationHelper evaluationHelper) throws EvaluationException {
		return this.evaluateToBoolean( evaluationHelper );
	}
	
	static public Object evaluate(String exp, EvaluationHelper evaluationHelper) 
			throws ExpressionSyntaxException, EvaluationException {
		return generate( exp ).evaluate( evaluationHelper ); 
	}

}
