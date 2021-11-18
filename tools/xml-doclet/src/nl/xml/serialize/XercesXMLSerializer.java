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
package nl.xml.serialize;

import java.io.*;

import org.w3c.dom.Document;
import org.apache.xml.serialize.OutputFormat;

/**
 *  Description of tyuthe Class
 *
 * @author     Jonathan van Alteren
 * @created    24 april 2001
 */
public class XercesXMLSerializer implements XMLSerializer
{
    private org.apache.xml.serialize.XMLSerializer xercesSerializer;


    public XercesXMLSerializer()
    {
	xercesSerializer = new org.apache.xml.serialize.XMLSerializer();
    }


    public OutputFormat defaultOutputFormat()
    {
	OutputFormat of = new OutputFormat("XML", "UTF-8", true);
	of.setPreserveSpace(false);
	of.setIndent(2);
	of.setIndenting(true);
	of.setLineWidth(0);
	return of;
    }


    public void serialize(Document document, OutputStream outstream)
	throws SerializeException
    {
	try
	    {
		xercesSerializer.setOutputByteStream(outstream);
		xercesSerializer.setOutputFormat(defaultOutputFormat());
		xercesSerializer.serialize(document);
	    }
	catch(IOException ioe)
	    {
		throw new SerializeException(ioe);
	    }
    }


    public void serialize(Document document, Writer writer)
	throws SerializeException
    {
	try
	    {
		xercesSerializer.setOutputCharStream(writer);
		xercesSerializer.setOutputFormat(defaultOutputFormat());
		xercesSerializer.serialize(document);
	    }
	catch(IOException ioe)
	    {
		throw new SerializeException(ioe);
	    }
    }


    public String toString(Document document)
    {
	try
	    {
		StringWriter stringWriter = new StringWriter();
		xercesSerializer.setOutputCharStream(stringWriter);
		xercesSerializer.setOutputFormat(defaultOutputFormat());
		xercesSerializer.serialize(document);
		return stringWriter.toString();
	    }
	catch(IOException ioe)
	    {
		System.err.println(ioe);
		return null;
	    }
    }
}

