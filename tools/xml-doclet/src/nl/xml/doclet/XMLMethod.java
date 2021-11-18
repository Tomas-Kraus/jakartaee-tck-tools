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
 * @author Jonathan van Alteren
 * @created 24 april 2001
 */
public class XMLMethod {

    MethodDoc commentMethoddoc;
    MethodDoc methoddoc;
    String methName;


    public XMLMethod(MethodDoc methoddoc) {
        this.methoddoc = methoddoc;
        //this.commentMethoddoc = methoddoc;
        methName = methoddoc.name() + methoddoc.signature();
    }


    public Node toXML(Document doc) {
        return toXML(doc, null);
    }


    private MethodDoc findComment(ClassDoc doc) {
        if (doc == null) {
            return null;
        }
        boolean found = false;
        ;
        MethodDoc result = null;
        MethodDoc[] methods = doc.methods();
        int numMethods = (methods == null) ? 0 : methods.length;
        for (int i = 0; i < numMethods; i++) {
            String methodName = methods[i].name() + methods[i].signature();
            if (methodName.equals(this.methName)) {
                String comment = methods[i].commentText();
                if (comment != null && comment.length() > 0) {
                    result = methods[i];
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            result = findComment(doc.superclass());
        }
        return result;
    }


    private MethodDoc findInterfaceDoc() {
        MethodDoc result = null;
        // returns an empty array if no interfaces
        ClassDoc[] intfs = methoddoc.containingClass().interfaces();
        for (int i = 0; i < intfs.length; i++) {
            ClassDoc intf = intfs[i];
            result = findComment(intf);
            if (result != null) {
                break;
            }
        }
        return result;
    }


    private MethodDoc findParentMethodDoc() {
        MethodDoc result = null;
        // if the method already has a comment return it
        if (methodHasComment(this.methoddoc)) {
            result = this.methoddoc;
        } else { // start seaching the parent hierarchy
            MethodDoc doc = this.methoddoc.overriddenMethod(); // overridden method
            while (doc != null) {
                System.out.println(
                    "&&&&&&&&&&&& \"" +
                    methoddoc.containingClass().qualifiedName() +
                    "\"  \"" +
                    doc.containingClass().qualifiedName() +
                    "\"");
                if (methodHasComment(doc)) {
                    result = doc;
                    break;
                } else {
                    doc = doc.overriddenMethod();
                }
            }
            if (result == null) {
                doc = this.methoddoc.overriddenMethod(); // overridden method's interfaces
                while (doc != null) {
                    ClassDoc[] interfaces = doc.containingClass().interfaces();
                    result = searchInterfacesForComment(interfaces);
                    if (result != null) {
                        break;
                    } else {
                        doc = doc.overriddenMethod();
                    }
                }
            }
        }
        return result;
    }


    private MethodDoc searchInterfacesForComment(ClassDoc[] interfaces) {
        MethodDoc result = null;
        for (int i = 0; i < interfaces.length; i++) {
            System.out.println("&&&&&&&&&&&& \"" +
                               methoddoc.containingClass().qualifiedName() +
                               "\"  \"" + interfaces[i].qualifiedName() + "\"");
            result = findComment(interfaces[i]);
            if (result != null) {
                break;
            }
        }
        return result;
    }


    private MethodDoc findMethodDoc() {
        MethodDoc doc = this.commentMethoddoc.overriddenMethod();
        if (doc == null) {
            doc = this.commentMethoddoc;
        }
        return doc;
    }


    private boolean methodHasComment(MethodDoc doc) {
        //String commentText = doc.getRawCommentText().trim();
        String commentText = doc.commentText().trim();
        return commentText.length() > 0;
    }


    private boolean methodHasComment() {
        String commentText = commentMethoddoc.getRawCommentText().trim();
        return commentText.length() > 0;
    }


    public Node toXML(Document doc, String parentName) {
        /*
         * If this method has no comment, search the parent classes as well
         * as the implemented interfaces to find a comment that matches this
         * method signature.
         */
        //	MethodDoc previousMethoddoc = this.commentMethoddoc;
        // find a parent method with the appropriate java doc comment
        this.commentMethoddoc = findParentMethodDoc();

        // didn't find a comment in any overridden methods perhaps we can find one in
        // an interface that declares the method we're implementing.
        if (this.commentMethoddoc == null) {
            this.commentMethoddoc = findInterfaceDoc();
        }
        // we give up, set the comment's MethodDoc to the original MethodDoc
        if (this.commentMethoddoc == null) {
            this.commentMethoddoc = methoddoc;
        }
// 	if (this.commentMethoddoc != methoddoc) {
// 	    System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");	
// 	    System.out.println("* Method  \"" + methoddoc.containingClass().qualifiedName() + "." +
// 			       methoddoc.name() + methoddoc.signature() + "\"");
// 	    System.out.println("* Comment \"" + commentMethoddoc.containingClass().qualifiedName() + "." +
// 			       commentMethoddoc.name() + commentMethoddoc.signature() + "\"");
// 	    System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
// 	}

        Element methodElement = doc.createElement("method");

        methodElement.setAttribute("name", methoddoc.name());
        if (parentName != null) {
            methodElement.setAttribute("inherited.from", parentName);
        }

        String modifiers = methoddoc.modifiers();
        if (modifiers.length() > 0) {
            methodElement.setAttribute("modifiers", modifiers);
        }

        /*
         * Use the MethodDoc object (commentMethoddoc) to create the comments.  This
         * MethodDoc object is the one that contains the comments for this method.  It
         * could be the same MethodDoc object that describes this method or it may be
         * an ancestor MethodDoc instance that contains a comment for the method.
         */
        XMLComment comment = new XMLComment(this.commentMethoddoc,
                                            methodElement);
        methodElement.appendChild(comment.toXML(doc));

        //parameters
        //	XMLParameters parameters = new XMLParameters(methoddoc.parameters(), methoddoc.paramTags());
        XMLParameters parameters = new XMLParameters(methoddoc.parameters(),
                                                     commentMethoddoc.paramTags());
        Element parametersElement = (Element) parameters.toXML(doc);
        if (parametersElement != null) {
            methodElement.appendChild(parametersElement);
        }

        //returns
        Element returnsElement = doc.createElement("returns");

        returnsElement.setAttribute("type", methoddoc.returnType().typeName());

        String packagename = XMLUtil.packagename(methoddoc.returnType());
        if (packagename.length() > 0) {
            returnsElement.setAttribute("package", packagename);
        }

        String dimension = methoddoc.returnType().dimension();
        if (dimension.length() > 0) {
            returnsElement.setAttribute("dimension", dimension);
        }

        Element commentElement = doc.createElement("comment");
        //	Tag[] tags = methoddoc.tags("return");
        Tag[] tags = commentMethoddoc.tags("return");
        for (int i = 0; i < tags.length; i++) {
            commentElement.appendChild(
                doc.createTextNode(XMLUtil.removeTags(tags[i].text())));
            returnsElement.appendChild(commentElement);
        }
        methodElement.appendChild(returnsElement);

        // deprecations
        //	Tag[] tagsDeprecated = methoddoc.tags("deprecated");
        Tag[] tagsDeprecated = commentMethoddoc.tags("deprecated");
        if (tagsDeprecated.length > 0) {
            Element deprecated = doc.createElement("deprecated");
            deprecated.appendChild(doc.createTextNode("true"));
            methodElement.appendChild(deprecated);
        }

        /* PROCESS EXCEPTIONS */
        ClassDoc[] throwsTags = methoddoc.thrownExceptions(); // reflected exceptions
        //	Tag[] tagComments = methoddoc.tags(); // javadoc exceptions, just used to pull throws comment from
        Tag[] tagComments = commentMethoddoc.tags(); // javadoc exceptions, just used to pull throws comment from

        /////////////////// Just for DEBUG
        // 	System.out.println("** " + methoddoc.name());
        // 	int nct = (throwsTags == null) ? 0 : throwsTags.length;
        // 	for (int i = 0; i < nct; i++) {
        // 	    System.out.println("\tClassDocs " + throwsTags[i].qualifiedTypeName());
        // 	}
        // 	int ntc = (tagComments == null) ? 0 : tagComments.length;
        // 	for (int i = 0; i < ntc; i++) {
        // 	    System.out.println("\tTagComments Name " + tagComments[i].name());
        // 	    if (tagComments[i] instanceof ThrowsTag)
        // 		System.out.println("\tTagComments " + ((ThrowsTag)tagComments[i]).exceptionName());
        // 	}
        // 	System.out.println();
        ///////////////////

        Element throwsElement = null;
        List processedExceptions = new ArrayList();
        int numTags = (throwsTags == null) ? 0 : throwsTags.length;
        if (numTags > 0) {
            throwsElement = doc.createElement("throws");
            for (int i = 0; i < numTags; i++) {
                Element throwElement = doc.createElement("throw");
                throwElement.setAttribute("name",
                                          throwsTags[i].qualifiedTypeName());
                throwElement.appendChild(
                    doc.createTextNode(
                        extractTagComment(tagComments, throwsTags[i])));
                throwsElement.appendChild(throwElement);
                processedExceptions.add(throwsTags[i].qualifiedTypeName());
                // System.out.println("$$$ Reflected Exception \"" +
                // throwsTags[i].qualifiedTypeName() + "\"");
            }
            methodElement.appendChild(throwsElement);
        }

        /*
         * Grab the throws tags from the javadoc comments.  Add them to the output
         * document if the throws clause has not been added above.
         */
        String addCommentExceptions = System.getProperty(
            "include.comment.exceptions", "false");
        if (addCommentExceptions.equalsIgnoreCase("true")) {
            //	    System.out.println("$$$ include.comment.exceptions = true $$$");
            //	    ThrowsTag[] throwsTagsC = methoddoc.throwsTags();
            ThrowsTag[] throwsTagsC = commentMethoddoc.throwsTags();
            numTags = (throwsTagsC == null) ? 0 : throwsTagsC.length;
            if (numTags > 0) {
                if (throwsElement == null) {
                    throwsElement = doc.createElement("throws");
                    methodElement.appendChild(throwsElement);
                }
                for (int i = 0; i < numTags; i++) {
                    String exceptionName = (throwsTagsC[i].exception() == null) ?
                        throwsTagsC[i].exceptionName() :
                        throwsTagsC[i].exception().qualifiedName();
                    if (!exceptionProcessed(exceptionName, processedExceptions)) {
                        Element throwElement = doc.createElement("throw");
                        throwElement.setAttribute("name", exceptionName);
                        String removedTagsComment = XMLUtil.removeTags
                            (throwsTagsC[i].exceptionComment());

                        if (removedTagsComment.startsWith("{@inheritDoc}")) {
                            // process exception comments from super until
                            // we have the text
                            boolean found = false;
                            for (MethodDoc meth = methoddoc.overriddenMethod();
                                 meth != null; meth = meth.overriddenMethod()) {
                                ThrowsTag[] tTags = meth.throwsTags();
                                found = false;
                                for (int ii = 0; ii < tTags.length; ii++) {
                                    String eName = (tTags[ii].exception() == null) ?
                                        tTags[ii].exceptionName() :
                                        tTags[ii].exception().qualifiedName();
                                    if (eName.equals(exceptionName)) {
                                        String eComment = XMLUtil.removeTags(
                                            tTags[ii].exceptionComment());
                                        if (eComment.startsWith("{@inheritDoc}")) {
                                            break;
                                        }
                                        removedTagsComment = eComment;
                                        found = true;
                                        break;
                                    }
                                }
                                if (found)
                                    break;
                            }
                            if (!found) {
                                System.err.println("Unable to find documentation" +
                                                   " linked by {@inheritDoc} within" +
                                                   " inheritence hierarchy.");
                            }
                        }
                        throwElement.appendChild(doc.createTextNode
                                                 (removedTagsComment));
                        throwsElement.appendChild(throwElement);
                        //			System.out.println("$$$ NON-Reflected Exception \"" +
                        //					   exceptionName + "\"");
                    } // end if
                } // end for
            } // end if
        } // end if
        return methodElement;
    }


    private boolean exceptionProcessed(String name, List list) {
        boolean result = false;
        int numExceptions = list.size();
        for (int i = 0; i < numExceptions; i++) {
            String exceptionName = (String) (list.get(i));
            if (exceptionName.endsWith(name)) {
                result = true;
                //		System.out.println("\t$$$ Exception Already Processed - Looking for \"" +
                //				   name + " Found \"" + exceptionName + "\"");
                break;
            }
        }
        if (!result) {
// 	    System.out.println("\t$$$ Exception NOT Processed - Was looking for \"" +
// 			       name + "\"");
        }
        return result;
    }


    /*
     * Return the throws comment from the javadoc if it exists.  The comment has to match
     * the exception name, either the class name of the exception or the fully qualified
     * exception name of the exception.
     */
    private String extractTagComment(Tag[] tags, ClassDoc classDoc) {
        String result = "";
        if (tags == null || tags.length == 0 || classDoc == null) {
            return result;
        }
        // reflected exception names
        String exceptionName = classDoc.typeName();
        String qualifiedExceptionName = classDoc.qualifiedTypeName();
        for (int i = 0; i < tags.length; i++) {
            if (tags[i] instanceof ThrowsTag) {
                ThrowsTag throwsTag = (ThrowsTag) tags[i];
                String tagExceptionName = throwsTag.exceptionName();
                if (tagExceptionName.equalsIgnoreCase(exceptionName) ||
                    tagExceptionName.equalsIgnoreCase(qualifiedExceptionName)) {
                    result = throwsTag.exceptionComment();
                    break;
                }
            }
        }
        return XMLUtil.removeTags(result);
    }

}

