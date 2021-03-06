package org.zenonpagetemplates.twoPhasesImpl.model.content;

import java.io.IOException;

import org.xml.sax.SAXException;
import org.zenonpagetemplates.twoPhasesImpl.ZPTXMLWriter;

/**
 * <p>
 *   Represents a CDATANode from the contents of a ZPTElement.
 * </p>
 * 
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
public class CDATANode implements ContentItem {

	private static final long serialVersionUID = 2561838426693988556L;
	
	private String text;
	
	public CDATANode(){}
	public CDATANode( String text ){
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	public void setText( String text ) {
		this.text = text;
	}
	
	@Override
	public void writeToXMLWriter( ZPTXMLWriter xmlWriter ) throws IOException,
			SAXException {
		xmlWriter.writeCDATANode( this );
	}
	
}
