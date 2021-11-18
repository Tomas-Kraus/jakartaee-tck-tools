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

/**
 *
 * @author     Jonathan van Alteren
 * @created    24 april 2001
 */
public class XMLUtil
{

    public final static String PREFIX = "[XMLDoclet] ";
    private final static String[] KILL_MODIFIERS =
    {"interface"};


    public static String packagename(ProgramElementDoc ped)
    {
	String packagename = null;
	String qualifiedName = ped.qualifiedName();

	int lastdot = qualifiedName.lastIndexOf('.');
	if (lastdot != -1)
	    {
		packagename = qualifiedName.substring(0, lastdot);
	    }
	else
	    {
		packagename = "";
	    }
	return packagename;
    }


    public static String className(ProgramElementDoc ped)
    {
	String classname = null;
	String qualifiedName = ped.qualifiedName();

	int lastdot = qualifiedName.lastIndexOf('.');
	if (lastdot != -1)
	    {
		classname = qualifiedName.substring(lastdot + 1, qualifiedName.length());
	    }
	else
	    {
		classname = "";
	    }
	return classname;
    }


    public static String packagename(Type type)
    {
	String packagename = null;
	String qualifiedName = type.qualifiedTypeName();

	int lastdot = qualifiedName.lastIndexOf('.');
	if (lastdot != -1)
	    {
		packagename = qualifiedName.substring(0, lastdot);
	    }
	else
	    {
		packagename = "";
	    }
	return packagename;
    }


    public static String modifiers(ProgramElementDoc ped)
    {
	String modifier = ped.modifiers();
	for (int i = 0; i < KILL_MODIFIERS.length; i++)
	    {
		String kill = KILL_MODIFIERS[i];
		int index = modifier.indexOf(kill);
		if (index != -1)
		    {
			StringBuffer killed = new StringBuffer();
			killed.append(modifier.substring(0, index));
			killed.append(modifier.substring(index + kill.length(), modifier.length()));
			modifier = killed.toString();
		    }
	    }
	return modifier;
    }


    /*
     * The following code takes a string and removes any XML/HTML tags found in the
     * specified string.  The entire tag is removed from the begin tag to the end
     * tag inclusively.   Nested tags are supported.  Note the tags are not in
     * the form "<" and ">", they are denoted by "&lt;" and "&gt;" respectively.
     * If a &lt; tag is in the specified string but not matching &gt; is found
     * the &lt; is left as is.
     */
    private static final String AMP_BEGIN_TAG  = "&lt;";
    private static final String AMP_END_TAG    = "&gt;";
    private static final String BEGIN_TAG      = "<";
    private static final String END_TAG        = ">";
    private static final String LINK_BEGIN_TAG = "{@link";
    private static final String LINK_END_TAG   = "}";
    private static final int LINK_START_TAG_LENGTH = LINK_BEGIN_TAG.length();
    private static final String CODE_BEGIN_TAG = "{@code";
    private static final String CODE_END_TAG   = "}";
    private static final int CODE_START_TAG_LENGTH = CODE_BEGIN_TAG.length();
    private static void removeTags(StringBuffer result, int index, String beginTag, String endTag) {
	if (result == null) { return; }
	int endTagLength = endTag.length();
	int startIndex = index;
	    while ((startIndex = result.indexOf(beginTag, startIndex)) >= 0) {
	            int checkNested = result.indexOf(beginTag, startIndex + 1);
	            int endIndex    = result.indexOf(endTag, startIndex);
 	            //System.out.println(startIndex + ", " + checkNested + ", " + endIndex);
	            if (checkNested != -1 && endIndex > checkNested) {
		        removeTags(result, checkNested, beginTag, endTag);
		        endIndex = result.indexOf(endTag, startIndex);
	            }
	            if (endIndex != -1) {
 		        //System.out.println("Before \"" + result.toString() + "\"");
 		        //System.out.println("Delete \"" + result.substring(startIndex, endIndex + endTagLength) + "\"");
                        if (beginTag.equals(LINK_BEGIN_TAG)) {
                            result.deleteCharAt(result.indexOf(LINK_END_TAG));
                            result.delete(startIndex, startIndex + LINK_START_TAG_LENGTH);
                        } else if (beginTag.equals(CODE_BEGIN_TAG)) { 
                            result.deleteCharAt(result.indexOf(CODE_END_TAG));
                            result.delete(startIndex, startIndex + CODE_START_TAG_LENGTH);                        
                        } else {
		            result.delete(startIndex, endIndex + endTagLength);
 		            //System.out.println("After  \"" + result.toString() + "\"\n\n");
                        }
	            } else {
		        break;
	            }
	    }
    }
    public static String removeTags(String str) {
	if (str == null) { return ""; }
	StringBuffer result = new StringBuffer(str);
	removeTags(result, 0, AMP_BEGIN_TAG, AMP_END_TAG);
	removeTags(result, 0, BEGIN_TAG, END_TAG);
    removeTags(result, 0, LINK_BEGIN_TAG, LINK_END_TAG);
    removeTags(result, 0, CODE_BEGIN_TAG, CODE_END_TAG);
	return result.toString();	
    }

    /*
     * Test driver for the removeTags utility method.
     */
    public static void main(String[] args) {
	String f = "&lt;&gt;";
	String a = "";
	String b = "ABC&lt;ABC&gt;&lt;_as_&lt;__&lt;TTTTTTTTTTTTTTTTTTTTTTT&gt;TTTTTTT&gt;SS&lt;___&gt;SSSSSSSSSSS&gt;DEF";
	String c = "The &lt;quick brown&gt; fox jumped over the &lt;lazy&gt; dog";
	String d = "&lt;&lt;DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD&gt;A";
	String e = "A&lt;&lt;AAAAAA&gt;&gt;&lt;&gt;B";
	String g = "A<as>ab</asdf>B<as<<as<as>as><as>as>>";
	String h = "<bold>A</bold>";
        String i = "{@link Application} Is a test {@link Appl} another test";
	System.out.println("f \"" + XMLUtil.removeTags(f) + "\"");
	System.out.println("a \"" + XMLUtil.removeTags(a) + "\"");
	System.out.println("b \"" + XMLUtil.removeTags(b) + "\"");
	System.out.println("c \"" + XMLUtil.removeTags(c) + "\"");
	System.out.println("d \"" + XMLUtil.removeTags(d) + "\"");
	System.out.println("e \"" + XMLUtil.removeTags(e) + "\"");
	System.out.println("g \"" + XMLUtil.removeTags(g) + "\"");
	System.out.println("h \"" + XMLUtil.removeTags(h) + "\"");
	System.out.println("i \"" + XMLUtil.removeTags(i) + "\"");
    }

}

