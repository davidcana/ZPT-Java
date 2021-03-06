package org.zenonpagetemplates.onePhaseImpl;

import org.zenonpagetemplates.common.AbstractZPTContext;

/**
 * <p>
 *   Extends AbstractZPTContext class with methods to get and set 
 *   the TemplateCache instance.
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
 * @version $Revision: 1.1 $
 */
public class ZPTContext extends AbstractZPTContext {
	
	private TemplateCache templateCache = new DefaultTemplateCache();
    
    private static ZPTContext instance;

    private ZPTContext(){ }

    
	public TemplateCache getTemplateCache() {
		return this.templateCache;
	}
	
	public void setTemplateCache( TemplateCache templateCache ) {
		
		if ( templateCache == null ){
			throw new IllegalArgumentException( "Unable to set templateCache to null" );
		}
		
		this.templateCache = templateCache;
	}

    public static ZPTContext getInstance(){

        if (instance == null){
            instance = new ZPTContext();
        }

        return instance;
    }

}
