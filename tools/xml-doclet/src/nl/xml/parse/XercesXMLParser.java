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

import java.io.*;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.apache.xerces.parsers.DOMParser;

/**
 *  Description of the Cltyass
 *
 * @author     Jonathan van Alteren
 * @created    24 april 2001
 */
public class XercesXMLParser implements XMLParser
{
    private DOMParser parser;
    private InputSource source;


    public XercesXMLParser()
    {
	parser = new DOMParser();
	source = new InputSource();
    }


    public Document newDocument()
    {
	parser.startDocument();
	return parser.getDocument();
    }


    public Document parse(String string)
	throws ParseException
    {
	return parse(new StringReader(string));
    }


    public Document parse(InputStream instream)
	throws ParseException
    {
	try
	    {
		source.setByteStream(instream);
		parser.parse(source);
		return parser.getDocument();
	    }
	catch(IOException ioe)
	    {
		throw new ParseException(ioe);
	    }
	catch(SAXException saxe)
	    {
		throw new ParseException(saxe);
	    }
    }


    public Document parse(Reader reader)
	throws ParseException
    {
	try
	    {
		source.setCharacterStream(reader);
		parser.parse(source);
		return parser.getDocument();
	    }
	catch(IOException ioe)
	    {
		throw new ParseException(ioe);
	    }
	catch(SAXException saxe)
	    {
		throw new ParseException(saxe);
	    }
    }
}

