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

import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author     Jonathan van Alteren
 * @created    24 april 2001
 */
public class XMLInterface {

    ClassDoc interfacedoc;
    XMLField[] fields;
    XMLConstructor[] constructors;
    XMLMethod[] methods;
    private boolean isDeprecated = false;
    private String processInheritedMembers = "false";
    /* Inherited members */
    Map inheritedFields = new HashMap();
    //Map inheritedConstructors = new HashMap();
    Map inheritedMethods = new HashMap();
    List declaredMethods = new ArrayList();

    public XMLInterface(ClassDoc interfacedoc) {
        this.interfacedoc = interfacedoc;
        Tag[] deprecated = interfacedoc.tags("deprecated");
        if (deprecated.length > 0) {
            System.out.println("DEPRECATED CLASS FOUND");
            isDeprecated = true;
        }
        FieldDoc[] fielddocs = interfacedoc.fields();
        fields = new XMLField[fielddocs.length];
        for (int i = 0; i < fielddocs.length; i++) {
            fields[i] = new XMLField(fielddocs[i]);
        }

        ConstructorDoc[] constructordocs = interfacedoc.constructors();
        constructors = new XMLConstructor[constructordocs.length];
        for (int i = 0; i < constructordocs.length; i++) {
            constructors[i] = new XMLConstructor(constructordocs[i]);
        }

        MethodDoc[] methoddocs = interfacedoc.methods();
        methods = new XMLMethod[methoddocs.length];
        for (int i = 0; i < methoddocs.length; i++) {
            methods[i] = new XMLMethod(methoddocs[i]);
        }

        processInheritedMembers = System.getProperty
            ("process.inherited.members", "false");
        if (!processInheritedMembers.equalsIgnoreCase("false")) {
            ClassDoc[] interfaces = interfacedoc.interfaces();
            if (interfaces != null) {
                for (int i = 0; i < interfaces.length; i++) {
                    //System.out.println("************************************************");
                    processParent(interfaces[i]);
                }
            }
        }
    }

    private boolean methodDeclaredInClass(MethodDoc doc) {
	boolean result = false;
	int numMethods = (declaredMethods == null) ? 0 : declaredMethods.size();
	MethodDoc md = null;
	for (int i = 0; i < numMethods; i++) {
	    md = (MethodDoc)(declaredMethods.get(i));
	    if (md.overriddenMethod() == doc) {
		System.out.println("=> " + md.containingClass().qualifiedName() + "." +
				   md.name() + md.signature() + " OVERRIDES " +
				   doc.containingClass().qualifiedName() + "." +
				   doc.name() + doc.signature());
		result = true;
		break;
	    }
	}
	return result;	
    }

    private void processParent(ClassDoc pclassdoc) {
        String className = pclassdoc.qualifiedTypeName();
        MethodDoc[] methoddocs = pclassdoc.methods();
        List methods = new ArrayList();
        for (int i = 0; i < methoddocs.length; i++) {
	    if (!methodDeclaredInClass(methoddocs[i])) {
		methods.add(new XMLMethod(methoddocs[i]));
		declaredMethods.add(methoddocs[i]);
	    }
        }
        inheritedMethods.put(className, methods);

        ClassDoc superClassDoc = pclassdoc.superclass();
        if (superClassDoc != null) {
            processParent(superClassDoc);
        }
    }

    public Node toXML(Document doc) {
        Element interfaceElement = doc.createElement("interface");

        String modifiers = XMLUtil.modifiers(interfacedoc);
        if (modifiers.length() > 0) {
            interfaceElement.setAttribute("modifiers", modifiers);
        }
        interfaceElement.setAttribute("name", interfacedoc.name());

        //extends
        Element superElement = doc.createElement("extends");

        ClassDoc[] superinterfaces = interfacedoc.interfaces();
        if (superinterfaces.length > 0) {
            superElement.setAttribute("name", superinterfaces[0].name());

            String superpackagename = XMLUtil.packagename((ProgramElementDoc) superinterfaces[0]);
            if (superpackagename.length() > 0) {
                superElement.setAttribute("package", superpackagename);
            }
            interfaceElement.appendChild(superElement);
        }

        // description
        XMLComment comment = new XMLComment(interfacedoc, interfaceElement);
        interfaceElement.appendChild(comment.toXML(doc));

        //fields
	Element fieldsElement = null;
        if (fields.length > 0) {
            fieldsElement = doc.createElement("fields");
            for (int i = 0; i < fields.length; i++) {
                if (isDeprecated) {
                    Element deprecatedElement = doc.createElement("deprecated");
                    deprecatedElement.appendChild(doc.createTextNode("true"));
                    Node node = fields[i].toXML(doc);
                    node.appendChild(deprecatedElement);
                    fieldsElement.appendChild(node);
                } else {
                    fieldsElement.appendChild(fields[i].toXML(doc));
                }
            }
	}
	if (!processInheritedMembers.equalsIgnoreCase("false")) {
	    if (fieldsElement == null) {
		fieldsElement = doc.createElement("fields");
	    }
	    Set parentClasses = inheritedFields.keySet();
	    for (Iterator i = parentClasses.iterator(); i.hasNext();) {
		String parentName = (String) (i.next());
		List parentFields = (List) (inheritedFields.get(parentName));
		int numFields = (parentFields == null) ? 0 : parentFields.size();
		for (int j = 0; j < numFields; j++) {
		    XMLField field = (XMLField) (parentFields.get(j));
		    fieldsElement.appendChild(field.toXML(doc, parentName));
		} // end parent field loop
	    } // end parent class loop
	} // end if including inherited members block
	
	if (fieldsElement != null) {
            interfaceElement.appendChild(fieldsElement);
        }

        //constructors
        if (constructors.length > 0) {
            Element constructorsElement = doc.createElement("constructors");
            for (int i = 0; i < constructors.length; i++) {
                if (isDeprecated) {
                    Element deprecatedElement = doc.createElement("deprecated");
                    deprecatedElement.appendChild(doc.createTextNode("true"));
                    Node node = constructors[i].toXML(doc);
                    node.appendChild(deprecatedElement);
                    constructorsElement.appendChild(node);
                } else {
                    constructorsElement.appendChild(constructors[i].toXML(doc));
                }
            }
        }

        //methods
	Element methodsElement = null;
        if (methods.length > 0) {
            methodsElement = doc.createElement("methods");
            for (int i = 0; i < methods.length; i++) {
                if (isDeprecated) {
                    Element deprecatedElement = doc.createElement("deprecated");
                    deprecatedElement.appendChild(doc.createTextNode("true"));
                    Node node = methods[i].toXML(doc);
                    node.appendChild(deprecatedElement);
                    methodsElement.appendChild(node);
                } else {
                    methodsElement.appendChild(methods[i].toXML(doc));
                }
            }
	}

	if (!processInheritedMembers.equalsIgnoreCase("false")) {
	    if (methodsElement == null) {
		methodsElement = doc.createElement("methods");
	    }
	    Set parentClasses = inheritedMethods.keySet();
	    for (Iterator i = parentClasses.iterator(); i.hasNext();) {
		String parentName = (String) (i.next());
		List parentMethods = (List) (inheritedMethods.get(parentName));
		int numMethods = (parentMethods == null) ? 0 : parentMethods.size();
		for (int j = 0; j < numMethods; j++) {
		    XMLMethod meth = (XMLMethod) (parentMethods.get(j));
		    methodsElement.appendChild(meth.toXML(doc, parentName));
		} // end parent method loop
	    } // end parent class loop
	} // end if including inherited members block
	
	if (methodsElement != null) {
	    interfaceElement.appendChild(methodsElement);
        }

        return interfaceElement;
    }
}

