package org.javapagetemplates.twoPhasesImpl.model.attributes.METAL;

import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.javapagetemplates.twoPhasesImpl.model.attributes.JPTAttributeImpl;
import org.javapagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;

/**
 * <p>
 *   Allows to set an expression and evaluate it as a macro.
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
public class METALUseMacro extends JPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = 62833153964900427L;
	
	private JPTExpression expression;
	
	
	public METALUseMacro(){}
	public METALUseMacro(String namespaceUri, String expression) throws PageTemplateException{
		super( namespaceUri );
		this.expression = ExpressionUtils.generate( expression );
	}

	
	public JPTExpression getExpression() {
		return expression;
	}
	
	public void setExpression(JPTExpression expression) {
		this.expression = expression;
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.METAL_USE_MACRO;
	}
	
	@Override
	public String getValue() {
		return this.expression.getStringExpression();
	}
}
