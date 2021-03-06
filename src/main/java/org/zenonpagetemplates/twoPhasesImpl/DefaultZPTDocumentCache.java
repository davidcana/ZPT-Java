package org.zenonpagetemplates.twoPhasesImpl;

import java.net.URI;
import java.util.Map;
import java.util.TreeMap;

import org.zenonpagetemplates.twoPhasesImpl.model.ZPTDocument;

/**
 * <p>
 *   Default implementation of ZPTDocumentCache interface not for production 
 *   use (only for testing purposes).
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
 *
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.0 $
 */
public class DefaultZPTDocumentCache implements ZPTDocumentCache {
	
	private Map<String, ZPTDocument> documents = new TreeMap<String, ZPTDocument>();
	
	DefaultZPTDocumentCache(){}
	
	@Override
	public void put( URI uri, ZPTDocument zptDocument ) {
		this.documents.put( uri.toString(), zptDocument );
	}
	
	@Override
	public ZPTDocument get( URI uri ) {
		return this.documents.get( uri.toString() );
	}

}
