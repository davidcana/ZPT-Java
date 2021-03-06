package org.zenonpagetemplates.twoPhasesImpl.model.attributes.METAL;

import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.ZPTAttributeImpl;

/**
 * <p>
 *   Allows to set a macro definition.
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
public class METALDefineMacro extends ZPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = -5999876512010524950L;
	
	private String name;
	
	
	public METALDefineMacro(){}
	public METALDefineMacro( String namespaceURI, String name ){
		super( namespaceURI );
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setName( String name ) {
		this.name = name;
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.METAL_DEFINE_MACRO;
	}

	@Override
	public String getValue() {
		return this.name;
	}
}
