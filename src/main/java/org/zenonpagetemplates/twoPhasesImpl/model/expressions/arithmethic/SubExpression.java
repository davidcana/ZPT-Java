package org.zenonpagetemplates.twoPhasesImpl.model.expressions.arithmethic;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.ExpressionSyntaxException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;

/**
 * <p>
 *   Defines an expression to subtract two or more expressions.
 * </p>
 * 
 *  Zenon Page Templates
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
public class SubExpression extends ArithmethicExpression {

	private static final long serialVersionUID = 6602733867308949615L;
	private static final String NAME = "subtract";
	
	public SubExpression(){
		super();
	}

	
	@Override
	protected Number doOperation( Number value1, Number value2 ) {
		return value1.intValue() - value2.intValue();
	}
	
	static public SubExpression generate( String exp ) throws ExpressionSyntaxException {
		
		SubExpression result = new SubExpression();
		
		configure(
				exp,
				result,
				NAME,
				TwoPhasesPageTemplate.EXPR_SUB );
		
		return result;
	}
	
	static public Integer evaluate( String exp, EvaluationHelper evaluationHelper ) 
			throws ExpressionSyntaxException, EvaluationException {
		return generate( exp ).evaluateToNumber( evaluationHelper ).intValue();
	}
}
