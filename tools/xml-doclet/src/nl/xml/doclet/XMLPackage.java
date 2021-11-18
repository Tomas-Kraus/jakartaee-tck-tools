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

import com.sun.javadoc.*;
import org.w3c.dom.*;
import java.util.*;

/**
 *
 * @author     Jonathan van Alteren
 * @created    24 april 2001
 */
public class XMLPackage
{

    PackageDoc packagedoc;
    XMLClass[] classes;
    XMLInterface[] interfaces;

    public XMLPackage(PackageDoc packagedoc)
    {
	this.packagedoc = packagedoc;

	List concreteClasses  = new ArrayList();
	List interfaceClasses = new ArrayList();
	ClassDoc[] allClasses = packagedoc.allClasses();
	for (int i = 0; i < allClasses.length; i++) {
	    if (allClasses[i].isInterface()) {
		interfaceClasses.add(new XMLInterface(allClasses[i]));
	    } else {
		concreteClasses.add(new XMLClass(allClasses[i]));
	    }
	}
 
	this.classes    = (XMLClass[])(concreteClasses.toArray(new XMLClass[concreteClasses.size()]));
	this.interfaces = (XMLInterface[])(interfaceClasses.toArray(new XMLInterface[interfaceClasses.size()]));


	/**** ORIGINAL CODE which did not pick up classes derived from Error or Exception
	ClassDoc[] ordinaryClasses = packagedoc.ordinaryClasses();
	classes = new XMLClass[ordinaryClasses.length];
	for (int i = 0; i < ordinaryClasses.length; i++)
	    {
		classes[i] = new XMLClass(ordinaryClasses[i]);
	    }

	ClassDoc[] interfaceDocs = packagedoc.interfaces();
	interfaces = new XMLInterface[interfaceDocs.length];
	for (int i = 0; i < interfaceDocs.length; i++)
	    {
		interfaces[i] = new XMLInterface(interfaceDocs[i]);
	    }
	END ORIGINAL CODE ******/
    }

    public Node toXML(Document doc)
    {
	Element packageElement = doc.createElement("package");
	packageElement.setAttribute("name", packagedoc.name());

	if (classes.length > 0)
	    {
		Element classesElement = doc.createElement("classes");
		for (int i = 0; i < classes.length; i++)
		    {
			classesElement.appendChild(classes[i].toXML(doc));
		    }
		packageElement.appendChild(classesElement);
	    }

	if (interfaces.length > 0)
	    {
		Element interfacesElement = doc.createElement("interfaces");
		for (int i = 0; i < interfaces.length; i++)
		    {
			interfacesElement.appendChild(interfaces[i].toXML(doc));
		    }
		packageElement.appendChild(interfacesElement);
	    }

	return packageElement;
    }
}

