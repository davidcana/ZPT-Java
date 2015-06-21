package org.javapagetemplates.twoPhasesImpl;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.apache.xerces.xni.parser.XMLDocumentFilter;

import org.cyberneko.html.parsers.SAXParser;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import org.javapagetemplates.common.Filter;
import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.twoPhasesImpl.JPTContext;
import org.javapagetemplates.twoPhasesImpl.model.JPTDocument;
import org.javapagetemplates.twoPhasesImpl.model.JPTElement;
import org.javapagetemplates.twoPhasesImpl.model.attributes.StaticAttributeImpl;
import org.javapagetemplates.twoPhasesImpl.model.attributes.I18N.I18NAttributes;
import org.javapagetemplates.twoPhasesImpl.model.attributes.I18N.I18NContent;
import org.javapagetemplates.twoPhasesImpl.model.attributes.I18N.I18NDefine;
import org.javapagetemplates.twoPhasesImpl.model.attributes.I18N.I18NDomain;
import org.javapagetemplates.twoPhasesImpl.model.attributes.I18N.I18NOnError;
import org.javapagetemplates.twoPhasesImpl.model.attributes.I18N.I18NParams;
import org.javapagetemplates.twoPhasesImpl.model.attributes.METAL.METALDefineMacro;
import org.javapagetemplates.twoPhasesImpl.model.attributes.METAL.METALDefineSlot;
import org.javapagetemplates.twoPhasesImpl.model.attributes.METAL.METALFillSlot;
import org.javapagetemplates.twoPhasesImpl.model.attributes.METAL.METALUseMacro;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TAL.TALAttributes;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TAL.TALCondition;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TAL.TALContent;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TAL.TALDefine;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TAL.TALOmitTag;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TAL.TALOnError;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TAL.TALRepeat;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TAL.TALTag;
import org.javapagetemplates.twoPhasesImpl.model.content.CDATANode;
import org.javapagetemplates.twoPhasesImpl.model.content.TextNode;

/**
 * <p>
 *   Read a JPT from a file (using JNDI to locate it) or from a 
 *   String instance and returns a JPTDocument instance.
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
 *
 * @author <a href="mailto:chris@christophermrossi.com">Chris Rossi</a>
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.1 $
 */
public class JPTDocumentFactory {
	
	static private JPTDocumentFactory instance;
	
	public static final String VOID_STRING = "";
	private static final String ENCODING = "UTF-8";
    public static final String TEMPLATE_ERROR_VAR_NAME = "error";

    
    private static SAXReader htmlReader = null;
    static final SAXReader getHTMLReader() throws SAXNotRecognizedException, SAXNotSupportedException {
    	
        if ( htmlReader == null && JPTContext.getInstance().isUseHtmlReader() ) {
            htmlReader = new SAXReader();
            SAXParser parser = new SAXParser();
            parser.setProperty( "http://cyberneko.org/html/properties/names/elems", "match" );
            parser.setProperty( "http://cyberneko.org/html/properties/names/attrs", "no-change" );
            parser.setProperty( "http://cyberneko.org/html/properties/default-encoding", ENCODING );
            //parser.setProperty( "http://cyberneko.org/html/features/balance-tags/document-fragment ", "true");
            //parser.setProperty( "http://cyberneko.org/html/features/balance-tags", "false" );
            //parser.setProperty( "http://apache.org/xml/features/scanner/notify-builtin-refs", "true");
            //parser.setProperty( "http://cyberneko.org/html/features/scanner/script/strip-comment-delims", "true"); 
            
            // Attach the filter to the parser
            XMLDocumentFilter[] filters = {new Filter()};
            parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
            
            htmlReader.setXMLReader( parser );
        }
        return htmlReader;
    }

    private static SAXReader xmlReader = null;
    static final SAXReader getXMLReader() throws SAXException {
        if ( xmlReader == null ) {
            xmlReader = new SAXReader();
            xmlReader.setEncoding(ENCODING);
            xmlReader.setIgnoreComments(true);
            xmlReader.setFeature(
					"http://apache.org/xml/features/nonvalidating/load-external-dtd",
		            false);
            //xmlReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        }
        return xmlReader;
    }
    
    
    private JPTDocumentFactory(){}
    
    public static JPTDocumentFactory getInstance(){

        if (instance == null){
            instance = new JPTDocumentFactory();
        }

        return instance;
    }
    
    
    public JPTDocument getJPTDocument( URI uri ) throws PageTemplateException {
    	
        try {
        	JPTDocument result = recoverFromCache( uri );
        	
        	if ( result != null ){
        		return result;
        	}
        	
        	result = getNewJPTDocument( uri );
        	
        	saveToCache( uri, result );
        	
            return result;
            
        } catch ( Exception e ) {
            throw new PageTemplateException( e );
        }
    }
    
    
    public JPTDocument getJPTDocument( URI uri, String templateString ) throws PageTemplateException {
    	
        try {
        	JPTDocument result = recoverFromCache( uri );
        	
        	if ( result != null ){
        		return result;
        	}
        	
        	result = getNewJPTDocument( uri, templateString );
        	
        	saveToCache( uri, result );
        	
            return result;
            
        } catch( Exception e ) {
            throw new PageTemplateException( e );
        }
    }

	static private JPTDocument recoverFromCache( URI uri ) throws PageTemplateException {
    	
        if ( ! JPTContext.getInstance().isCacheOn() ){
        	return null;
        }
        
		return JPTContext.getInstance().getJptDocumentCache().get( uri );
	}
    
    static private void saveToCache( URI uri, JPTDocument jptDocument ) throws PageTemplateException {

		if ( ! JPTContext.getInstance().isCacheOn() ){
			return;
		}
		
		JPTContext.getInstance().getJptDocumentCache().put( uri, jptDocument );
	}
    
	private JPTDocument getNewJPTDocument( URI uri ) throws Exception,
			DocumentException, URISyntaxException, SAXException,
			PageTemplateException, IOException {
		
		return getNewJPTDocument( 
					readTemplate( uri ), 
					uri );
	}

    static private JPTDocument getNewJPTDocument( URI uri, String templateString ) 
    		throws DocumentException, URISyntaxException, SAXException, 
    		PageTemplateException, IOException {
    	
		return getNewJPTDocument( 
					readTemplate( templateString ), 
					uri );
	}
    
	static private Document readTemplate(URI uri) 
			throws DocumentException, SAXException, MalformedURLException {
		
		URL url = uri.toURL();
		SAXReader reader = getXMLReader();
		try {
		    return reader.read( url );
		    
		} catch( DocumentException e ) {
		    try {
		        reader = getHTMLReader();
		        if ( reader == null ){
		            throw ( e );
		        }
		        return reader.read( url );
		        
		    } catch( NoClassDefFoundError ee ) {
		        // Allow user to omit nekohtml package
		        // to disable html parsing
		        //System.err.println( "Warning: no nekohtml" );
		        //ee.printStackTrace();
		        throw e;
		    }
		}
		
	}

	static private Document readTemplate( String stringTemplate ) 
			throws DocumentException, SAXException {
		
		SAXReader reader = getXMLReader();
		StringReader stringReader = new StringReader( stringTemplate );
		try {
		    return reader.read( stringReader );
		    
		} catch( DocumentException e ) {
		    try {
		        reader = getHTMLReader();
		        if ( reader == null ){
		            throw ( e );
		        }
		        return reader.read( stringReader );
		        
		    } catch( NoClassDefFoundError ee ) {
		        // Allow user to omit nekohtml package
		        // to disable html parsing
		        //System.err.println( "Warning: no nekohtml" );
		        //ee.printStackTrace();
		        throw e;
		    }
		}
		
	}
	
	static private JPTDocument getNewJPTDocument( Document template, URI uri)
        throws SAXException, PageTemplateException, IOException {
		
        try {
        	JPTDocument result = new JPTDocument( uri );
        	
            // Set up template name and doc type in jptDocument
        	result.setTemplateName(
        			template.getName() );
        	result.setDocType(
        			DocType.generateDocTypeFromDom4jDocument( template ) );
        	
            // Process root element and set it to jptDocument
        	result.setRoot(
            	getNewJPTElement( 
            		template.getRootElement(), 
            		result,
            		new Stack<Map<String, Slot>>() ) );
        	
            return result;
            
        } catch ( Exception e ) {
            throw new PageTemplateException( e );
        }
    }

    static protected JPTElement getNewJPTElement( 
    		Element element, JPTDocument jptDocument,
    		Stack <Map<String, Slot>>slotStack )
        throws SAXException, PageTemplateException, IOException {
    	
    	// Create a new jptElement and set its name
    	JPTElement result = new JPTElement( 
    			element.getName(), 
    			element.getNamespace().getPrefix() );
    	
    	// Map first namespaces to make all namespaces prefixes available
    	mapNamespaces( element, result, jptDocument );
    	
		// Get attributes
		mapAttributes( element, result, jptDocument );
		
		// Continue processing if it is not define tal:content or i18n:content
		if ( processContentIsOn( result, jptDocument ) ){
			mapContent( element, result, jptDocument, slotStack );
		}

		return result;
	}

	private static boolean processContentIsOn( JPTElement jptElement, JPTDocument jptDocument ) {
		
		return ! jptElement.existsTalAttribute( TwoPhasesPageTemplate.TAL_CONTENT, jptDocument )
				&& ! jptElement.existsI18nAttribute( TwoPhasesPageTemplate.I18N_CONTENT, jptDocument );
	}

	@SuppressWarnings({ "unchecked" })
    static private void mapContent( Element element,
                                 JPTElement jptElement,
                                 JPTDocument jptDocument,
                                 Stack <Map<String, Slot>>slotStack)
        throws SAXException, PageTemplateException, IOException {
    	
        // Use default template content
        for ( Iterator<Node> i = element.nodeIterator(); i.hasNext(); ) {
            Node node = i.next();
            switch( node.getNodeType() ) {
            case Node.ELEMENT_NODE:
            	jptElement.addContent(
            			getNewJPTElement( (Element) node, jptDocument , slotStack ) );
                break;
                
            case Node.TEXT_NODE:
            	jptElement.addContent(
            			new TextNode( node.getText() ) );
                break;
                
            case Node.CDATA_SECTION_NODE:
            	jptElement.addContent(
            			new CDATANode( node.getText() ) );
                break;
                
            case Node.NAMESPACE_NODE:  // Already handled
            	/*
                Namespace declared = (Namespace)node;
                if (jptDocument.isNamespaceToDeclare(declared)){
                	jptDocument.addNamespace(declared);
                } else {
                	jptElement.addNamespaceStaticAttribute(declared);
                }
                break;*/
             
            case Node.ATTRIBUTE_NODE: 		// Already handled
            case Node.COMMENT_NODE:			// Remove all comments
            case Node.DOCUMENT_TYPE_NODE:
            case Node.ENTITY_REFERENCE_NODE:
            case Node.PROCESSING_INSTRUCTION_NODE:
            default:
            	// Nothing to do
            }
        }
    }
	
	@SuppressWarnings("unchecked")
	static private void mapNamespaces( Element element, JPTElement jptElement, JPTDocument jptDocument ) 
        throws PageTemplateException {
		
		for ( Iterator<Attribute> i = element.nodeIterator(); i.hasNext(); ) {
			Node node = i.next();
			if ( node.getNodeType() == Node.NAMESPACE_NODE ){
                Namespace declared = ( Namespace ) node;
                if ( jptDocument.isNamespaceToDeclare( declared ) ){
                	jptDocument.addNamespace( declared );
                } else {
                	jptElement.addNamespaceStaticAttribute( declared );
                }
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	static private void mapAttributes( Element element, JPTElement jptElement, JPTDocument jptDocument ) 
        throws PageTemplateException {
		
        String talOmitTag = null;
        boolean isReplace = false;
        
        for ( Iterator<Attribute> i = element.attributeIterator(); i.hasNext(); ) {
            Attribute attribute = i.next();
            Namespace namespace = attribute.getNamespace();
            String name = attribute.getName();
            String namespacePrefix = namespace.getPrefix();
            
            // Handle JPT attributes
			if ( TwoPhasesPageTemplate.TAL_NAMESPACE_URI.equals( namespace.getURI() ) ) {
				
                // tal:define
                if ( name.equals( TwoPhasesPageTemplate.TAL_DEFINE ) ) {
                	jptElement.addDynamicAttribute(
                			new TALDefine( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }

                // tal:condition
                else if ( name.equals( TwoPhasesPageTemplate.TAL_CONDITION ) ) {
                	jptElement.addDynamicAttribute(
                			new TALCondition( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }

                // tal:repeat
                else if ( name.equals( TwoPhasesPageTemplate.TAL_REPEAT ) ) {
                	jptElement.addDynamicAttribute(
                			new TALRepeat( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }

                // tal:content
                else if ( name.equals( TwoPhasesPageTemplate.TAL_CONTENT ) ) {
                	jptElement.addDynamicAttribute(
                			new TALContent( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }

                // tal:replace
                else if ( name.equals( TwoPhasesPageTemplate.TAL_REPLACE ) ) {
                	isReplace = true;
                	jptElement.addDynamicAttribute(
                			new TALContent( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }

                // tal:attributes
                else if ( name.equals( TwoPhasesPageTemplate.TAL_ATTRIBUTES ) ) {
                	jptElement.addDynamicAttribute(
                			new TALAttributes( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }

                // tal:omit-tag
                else if ( name.equals( TwoPhasesPageTemplate.TAL_OMIT_TAG ) ) {
                	talOmitTag = attribute.getValue();
                }

                // tal:on-error
                else if ( name.equals( TwoPhasesPageTemplate.TAL_ON_ERROR ) ) {
                	jptElement.addDynamicAttribute(
                			new TALOnError( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }
                
                // tal:tag
                else if ( name.equals( TwoPhasesPageTemplate.TAL_TAG ) ) {
                	jptElement.addDynamicAttribute(
                			new TALTag( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }
                
                // error
                else {
                    throw new PageTemplateException( 
                    		"Unknown TAL attribute: " + name + " in template");
                }
            }
			
            else if ( TwoPhasesPageTemplate.METAL_NAMESPACE_URI.equals( namespace.getURI() ) ) {
            	
                // metal:use-macro
                if ( name.equals( TwoPhasesPageTemplate.METAL_USE_MACRO ) ) {
                	jptElement.addDynamicAttribute(
                			new METALUseMacro( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }
                
                // metal:define-slot
                else if ( name.equals( TwoPhasesPageTemplate.METAL_DEFINE_SLOT ) ) {
                	jptElement.addDynamicAttribute(
                			new METALDefineSlot( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }

                // metal:define-macro
                else if ( name.equals( TwoPhasesPageTemplate.METAL_DEFINE_MACRO ) ) {
                	jptElement.addDynamicAttribute(
                			new METALDefineMacro( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }
                
                // metal:fill-slot
                else if ( name.equals( TwoPhasesPageTemplate.METAL_FILL_SLOT ) ) {
                	jptElement.addDynamicAttribute(
                			new METALFillSlot( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }
                
                // error
                else {
                    throw new PageTemplateException( 
                    		"Unknown metal attribute: " + name + " in template");
                }
            }
            
            else if ( TwoPhasesPageTemplate.I18N_NAMESPACE_URI.equals( namespace.getURI() ) ) {
            	
                // i18n:domain
                if ( name.equals( TwoPhasesPageTemplate.I18N_DOMAIN ) ) {
                	jptElement.addDynamicAttribute(
                			new I18NDomain( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }
                
                // i18n:define
                else if ( name.equals( TwoPhasesPageTemplate.I18N_DEFINE ) ) {
                	jptElement.addDynamicAttribute(
                			new I18NDefine( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }
                
                // i18n:content
                else if ( name.equals( TwoPhasesPageTemplate.I18N_CONTENT ) ) {
                	jptElement.addDynamicAttribute(
                			new I18NContent( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }
                
                // i18n:replace
                else if ( name.equals( TwoPhasesPageTemplate.I18N_REPLACE ) ) {
                	isReplace = true;
                	jptElement.addDynamicAttribute(
                			new I18NContent( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }
                
                // i18n:attributes
                else if ( name.equals( TwoPhasesPageTemplate.I18N_ATTRIBUTES ) ) {
                	jptElement.addDynamicAttribute(
                			new I18NAttributes(
                					namespacePrefix,
                					attribute.getValue() ) );
                }
                
                // i18n:params
                else if ( name.equals( TwoPhasesPageTemplate.I18N_PARAMS ) ) {
                	jptElement.addDynamicAttribute(
                			new I18NParams( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }
                
                // i18n:on-error
                else if ( name.equals( TwoPhasesPageTemplate.I18N_ON_ERROR ) ) {
                	jptElement.addDynamicAttribute(
                			new I18NOnError( 
                					namespacePrefix,
                					attribute.getValue() ) );
                }
                
                // error
                else {
                    throw new PageTemplateException( 
                    		"Unknown i18n attribute: " + name + " in template");
                }
            }
            
            // Pass on all other attributes
            else {
            	jptElement.addStaticAttribute(
            			new StaticAttributeImpl( 
            					namespacePrefix,
            					name, 
            					attribute.getValue() ) );
            }
        }
        
        //  Add omit-tag
        if ( talOmitTag != null ){
        	jptElement.addDynamicAttribute(
        			new TALOmitTag( 
        					jptDocument.getTalPrefix(),
        					talOmitTag ) );
        	
        } else if ( isReplace ){
        	jptElement.addDynamicAttribute(
        			new TALOmitTag( 
        					jptDocument.getTalPrefix(),
        					VOID_STRING ) );
        }
    }

}

