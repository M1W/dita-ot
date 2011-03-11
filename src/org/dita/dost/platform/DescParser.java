/*
 * This file is part of the DITA Open Toolkit project hosted on
 * Sourceforge.net. See the accompanying license.txt file for 
 * applicable licenses.
 */

/*
 * (c) Copyright IBM Corp. 2005, 2006 All Rights Reserved.
 */
package org.dita.dost.platform;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser to parse description file of plugin.
 * @author Zhang, Yuan Peng
 */
final class DescParser extends DefaultHandler{
    
    private final Map<String, ExtensionPoint> extensionPoints;
	private String currentPlugin = null;
	private final Features features;
	
	/**
	 * DescParser Constructor.
	 * @deprecated use {@link #DescParser(File, File)} instead
	 */
	@Deprecated
	public DescParser(){
		this(null, null);
	}
	
	/**
	 * Constructor initialize Feature with location.
	 * @param location location
	 */
	public DescParser(final File location, final File ditaDir) {
		super();
		features = new Features(location, ditaDir);
	    extensionPoints = new HashMap<String, ExtensionPoint>();
	}
	
	/**
	 * Process configuration start element.
	 */
	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
		if( "plugin".equals(qName) ){
			currentPlugin = attributes.getValue("id");
		} else if ("extension-point".equals(qName)){
            addExtensionPoint(attributes);
		} else if ("feature".equals(qName)){
			features.addFeature(attributes.getValue("extension"), attributes);
		} else if ("require".equals(qName)){
			features.addRequire(attributes.getValue("plugin"), attributes.getValue("importance"));
		} else if ("meta".equals(qName)){
			features.addMeta(attributes.getValue("type"), attributes.getValue("value"));
		} else if ("template".equals(qName)){
			features.addTemplate(attributes.getValue("file"));
		}
	}
	
	/**
	 * Get extension points
	 * 
	 * @return extension points, empty map if not extension points are defined
	 */
	public Map<String, ExtensionPoint> getExtensionPoints() {
	    return Collections.unmodifiableMap(extensionPoints);
	}
	
	/**
	 * Get plug-in features.
	 * 
	 * @return plug-in features
	 */
	public Features getFeatures() {
		return features;
	}
	
	/**
	 * Get plugin ID.
	 * 
	 * @return plugin ID, <code>null</code> if not defined
	 */
	public String getPluginId() {
		return currentPlugin; 
	}
	
	// Private methods ---------------------------------------------------------
	
	/**
     * Add extension point.
     * 
     * @param atts extension point element attributes
     * @throws NullPointerException if extension ID is {@code null}
     */
    private void addExtensionPoint(final Attributes atts) {
        final String id = atts.getValue("id");
        if (id == null) {
            throw new NullPointerException("id attribute not set on extension-point");
        }
        final String name = atts.getValue("name");
        extensionPoints.put(id, new ExtensionPoint(id, name, currentPlugin));
    }
	
}
