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
 *  This class is used for processing the classes. It can call
 *
 * @author     Jonathan van Alteren
 * @created    24 april 2001
 * @see		XMLMethod
 * @see		XMLField
 * @see		XMLPackage
 */
public class XMLClass {
    /* Inherited members */
    Map inheritedFields = new HashMap();
    List declaredFields = new ArrayList();
    //Map inheritedConstructors = new HashMap();
    Map inheritedMethods = new HashMap();
    List declaredMethods = new ArrayList();
    private String processInheritedMembers = "false";
    private boolean isDeprecated = false;
    private Map inheritedInterfaces = new HashMap();


    ClassDoc classdoc;
    XMLField[] fields;
    XMLConstructor[] constructors;
    XMLMethod[] methods;


    private boolean inheritable(ProgramElementDoc a) {
	return (!a.isPrivate() && !a.isStatic());
    }

    private boolean equalFieldDoc(FieldDoc a, FieldDoc b) {
	boolean result = false;
	String aType = a.type().qualifiedTypeName() + a.type().dimension();
	String bType = b.type().qualifiedTypeName() + b.type().dimension();
	String aName = a.name() + aType;
	String bName = b.name() + bType;
// 	System.out.println("    aName = " + aName);
// 	System.out.println("    bName = " + bName);
	return aName.equals(bName);
    }

//     private boolean equalMethodDoc(MethodDoc a, MethodDoc b) {
// 	boolean result = false;
// 	if (!inheritable(a) || !inheritable(b)) {
// 	    return result;
// 	}
// 	String aType = a.type().qualifiedTypeName() + a.type().dimension();
// 	String bType = b.type().qualifiedTypeName() + b.type().dimension();
// 	String aName = a.name() + aType;
// 	String bName = b.name() + bType;
// 	return aName.equals(bName);
//     }

    private boolean fieldDeclaredInClass(FieldDoc doc) {
	boolean result = false;
	int numFields = (declaredFields == null) ? 0 : declaredFields.size();
	FieldDoc fd = null;
	for (int i = 0; i < numFields; i++) {
	    fd = (FieldDoc)(declaredFields.get(i));
	    if (equalFieldDoc(fd, doc)) {
		result = true;
		break;
	    }
	}
	return result;
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
        FieldDoc[] fielddocs = pclassdoc.fields();
        List fields = new ArrayList();
        for (int i = 0; i < fielddocs.length; i++) {
	    if (!fielddocs[i].isStatic() && !fieldDeclaredInClass(fielddocs[i])) {
		fields.add(new XMLField(fielddocs[i]));
		declaredFields.add(fielddocs[i]);
	    }
        }
        inheritedFields.put(className, fields);

        /*****************
	  ConstructorDoc[] constructordocs = pclassdoc.constructors();
	  List constructors = new ArrayList();
	  for (int i = 0; i < constructordocs.length; i++)
	  {
	  constructors.add(new XMLConstructor(constructordocs[i]));
	  }
	  inheritedConstructors.put(className, constructors);
	*****************/

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

    public XMLClass(ClassDoc classdoc) {
	//	System.out.println("PROCESSING CLASS \"" + classdoc.qualifiedName() + "\"");
        this.classdoc = classdoc;
        Tag[] deprecated = classdoc.tags("deprecated");
        if (deprecated.length > 0) {
            System.out.println("DEPRECATED CLASS FOUND");
            isDeprecated = true;
        }

        FieldDoc[] fielddocs = classdoc.fields();
	declaredFields.addAll(Arrays.asList(fielddocs)); // declared Fields
        fields = new XMLField[fielddocs.length];
        for (int i = 0; i < fielddocs.length; i++) {
            fields[i] = new XMLField(fielddocs[i]);
        }

        ConstructorDoc[] constructordocs = classdoc.constructors();
        constructors = new XMLConstructor[constructordocs.length];
        for (int i = 0; i < constructordocs.length; i++) {
            constructors[i] = new XMLConstructor(constructordocs[i]);
        }

        MethodDoc[] methoddocs = classdoc.methods();
	declaredMethods.addAll(Arrays.asList(methoddocs)); //declared methods
        methods = new XMLMethod[methoddocs.length];
        for (int i = 0; i < methoddocs.length; i++) {
            methods[i] = new XMLMethod(methoddocs[i]);
        }

        processInheritedMembers = System.getProperty
            ("process.inherited.members", "false");
        if (!processInheritedMembers.equalsIgnoreCase("false")) {
            processParent(classdoc.superclass());
        }
	//	System.out.println("=> " + inheritedFields);
    }


    public Node toXML(Document doc) {
        Element classElement = doc.createElement("class");
        String modifiers = classdoc.modifiers();
        if (modifiers.length() > 0) {
            classElement.setAttribute("modifiers", modifiers);
        }
        classElement.setAttribute("name", classdoc.name());

        //extends
        ClassDoc superclass = classdoc.superclass();
        if (superclass != null) {
            Element extendsElement = doc.createElement("extends");

            extendsElement.setAttribute("name", superclass.name());

            String extendspackagename = XMLUtil.packagename((ProgramElementDoc) superclass);
            if (extendspackagename.length() > 0) {
                extendsElement.setAttribute("package", extendspackagename);
            }
            classElement.appendChild(extendsElement);

            //more superclasses?
            superclass = superclass.superclass();
            if (superclass != null) {
                Element superclassesElement = doc.createElement("superclasses");

                while (superclass != null) {
                    Element superclassElement = doc.createElement("superclass");
                    superclassElement.setAttribute("name", superclass.name());

                    String superpackagename = XMLUtil.packagename((ProgramElementDoc) superclass);
                    if (superpackagename.length() > 0) {
                        superclassElement.setAttribute("package", superpackagename);
                    }
                    superclassesElement.appendChild(superclassElement);

                    superclass = superclass.superclass();
                }
                classElement.appendChild(superclassesElement);
            }
        }

        // description
        XMLComment comment = new XMLComment(classdoc, classElement);
        classElement.appendChild(comment.toXML(doc));

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
		//		System.out.println("parentName = " + parentName);
		List parentFields = (List) (inheritedFields.get(parentName));
		//		System.out.println("list = " + parentFields);
		int numFields = (parentFields == null) ? 0 : parentFields.size();
		for (int j = 0; j < numFields; j++) {
		    XMLField field = (XMLField) (parentFields.get(j));
		    fieldsElement.appendChild(field.toXML(doc, parentName));
		} // end parent field loop
	    } // end parent class loop
	} // end if including inherited members block
	
	if (fieldsElement != null) {
	    classElement.appendChild(fieldsElement);
	}

        //constructors
	Element constructorsElement = null;
        if (constructors.length > 0) {
            constructorsElement = doc.createElement("constructors");
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

	/********************
	if (!processInheritedMembers.equalsIgnoreCase("false")) {
	    if (constructorsElement == null) {
		constructorsElement = doc.createElement("constructors");
	    }
	    Set parentClasses = inheritedConstructors.keySet();
	    for (Iterator i = parentClasses.iterator(); i.hasNext(); ) {
		String parentName = (String)(i.next());
		List parentCtors = (List)(inheritedConstructors.get(parentName));
		int numCtors = (parentCtors == null) ? 0 : parentCtors.size();
		for (int j = 0; j < numCtors; j++) {
		    XMLConstructor ctor = (XMLConstructor)(parentCtors.get(j));
		    constructorsElement.appendChild(ctor.toXML(doc, parentName));
		} // end parent ctor loop
	    } // end parent class loop
	} // end if including inherited members block
	********************/

	if (constructorsElement != null) {
            classElement.appendChild(constructorsElement);
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
	    classElement.appendChild(methodsElement);
        }

        return classElement;
    }
}
