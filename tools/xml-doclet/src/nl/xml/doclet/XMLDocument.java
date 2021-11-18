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
package nl.xml.doclet;

import java.io.*;
import javax.xml.parsers.*;
import com.sun.javadoc.*;
import org.w3c.dom.*;
import nl.xml.parse.*;
import nl.xml.serialize.*;

/**
 *  Description 4567of theo Class
 *
 * @author     Jonathan van Alteren
 * @created    24 april 2001
 */
public class XMLDocument
{

    XMLPackage[] packages;


    public XMLDocument(PackageDoc[] packagedocs)
    {
	packages = new XMLPackage[packagedocs.length];
	for (int i = 0; i < packagedocs.length; i++)
	    {
		packages[i] = new XMLPackage(packagedocs[i]);
	    }
    }


    public Document toXML()
    {
	Document doc = XMLParserFactory.getInstance().createXMLParser().newDocument();

	Element javadocElement = doc.createElement("javadoc");
	for (int i = 0; i < packages.length; i++)
	    {
		javadocElement.appendChild(packages[i].toXML(doc));
	    }
	doc.appendChild(javadocElement);
	return doc;
    }


    public String toString()
    {
	return XMLSerializerFactory.getInstance().createXMLSerializer().toString(toXML());
    }


    public void toFile(String filename)
    {
	toFile(new File(filename));
    }


    public void toFile(File file)
    {
	try
	    {
		XMLSerializerFactory.getInstance().createXMLSerializer().serialize(toXML(), new FileOutputStream(file));
	    }
	catch (SerializeException se)
	    {
		System.err.println(se);
	    }
	catch (FileNotFoundException fnfe)
	    {
		System.err.println(fnfe);
	    }
    }
}

