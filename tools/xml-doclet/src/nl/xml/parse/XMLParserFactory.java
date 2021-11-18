/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package nl.xml.parse;

import java.util.*;
import java.io.*;

/**
 *  Description of theytu Class
 *
 * @author     Jonathan van Alteren
 * @created    24 april 2001
 */
public class XMLParserFactory
{
    Properties properties;
    static XMLParserFactory instance = new XMLParserFactory();


    private XMLParserFactory()
    {
	properties = new Properties();
	try
	    {
		properties.load(getClass().getClassLoader().getResourceAsStream("xmldoclet.properties"));
	    }
	catch(IOException ioe)
	    {
		System.err.println(ioe);
	    }
    }


    public XMLParser createXMLParser()
    {
	XMLParser parser = null;
	try
	    {
		parser = (XMLParser) Class.forName(properties.getProperty("xml.parser")).newInstance();
	    }
	catch(ClassNotFoundException cnfe)
	    {
		System.err.println(cnfe);
	    }
	catch(InstantiationException ie)
	    {
		System.err.println(ie);
	    }
	catch(IllegalAccessException iae)
	    {
		System.err.println(iae);
	    }
	finally
	    {
		return parser;
	    }
    }

    public static XMLParserFactory getInstance()
    {
	return instance;
    }
}
