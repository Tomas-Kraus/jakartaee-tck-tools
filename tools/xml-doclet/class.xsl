<!--

    Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Public License v. 2.0, which is available at
    http://www.eclipse.org/legal/epl-2.0.

    This Source Code may also be made available under the following Secondary
    Licenses when the conditions for such availability set forth in the
    Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
    version 2 with the GNU Classpath Exception, which is available at
    https://www.gnu.org/software/classpath/license.html.

    SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0

-->
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:template match="class">
	<fo:block font-size="10pt"
            font-family="serif" 
            break-before="page"
            space-after.optimum="3pt"
            font-weight="bold">
		<fo:basic-link color="blue">
			<xsl:attribute name="internal-destination">
				<xsl:value-of select="translate(../../@name,' ),-.(','____')"/>
			</xsl:attribute>
			<xsl:value-of select="../../@name"/>
		</fo:basic-link>
	</fo:block>  

  	<fo:block font-size="16pt"
            font-family="sans-serif" 
            line-height="24pt"
            space-after.optimum="10pt"
            font-weight="bold">
    <xsl:attribute name="id">
      <xsl:value-of select="translate(.,' ),-.(','____')"/>
    </xsl:attribute>
    Class <xsl:value-of select="@name"/>
  </fo:block>
  <xsl:apply-templates select="./extends"/>
  <xsl:apply-templates select="./comment"/>
  <xsl:apply-templates select="./fields"/>
  <xsl:apply-templates select="./constructors"/>
  <xsl:apply-templates select="./methods"/>
  <xsl:apply-templates select="./fields" mode="detail"/>
  <xsl:apply-templates select="./constructors" mode="detail"/>
  <xsl:apply-templates select="./methods" mode="detail"/>
</xsl:template>

<xsl:template match="extends">
  	<fo:block font-size="10pt"
            font-family="sans-serif" >
    extends:
	<fo:block font-size="8pt" 
			  space-before.optimum="5pt"
			  start-indent="10pt">
    	<xsl:value-of select="@package"/>.<xsl:value-of select="@name"/>
    </fo:block>
  </fo:block>
</xsl:template>

<!-- leading comment for this class -->
<xsl:template match="class/comment/lead">
  	<fo:block font-size="12pt"
              space-before.optimum="10pt"
              font-family="sans-serif" >
		<xsl:value-of select="."/>
	</fo:block>
</xsl:template>

<xsl:template match="class/comment/detail">
  	<fo:block font-size="12pt"
              space-before.optimum="10pt"
              font-family="sans-serif" >
		<xsl:value-of select="."/>
	</fo:block>
</xsl:template>

<xsl:template name="summarytable">
	<xsl:param name="element">constructors</xsl:param>

	<fo:block font-size="16pt" 
              font-family="sans-serif" 
    	      line-height="20pt"
        	  background-color="#CCCCFF"
              space-before.optimum="15pt"
              border-width="0.5mm">
		<fo:block start-indent="10pt">
			<xsl:choose>
				<xsl:when test="$element='constructors'">Constructor Summary</xsl:when>
				<xsl:when test="$element='methods'">Method Summary</xsl:when>
				<xsl:when test="$element='fields'">Field Summary</xsl:when>
			</xsl:choose>
		</fo:block>
	</fo:block>
	
	<fo:table>
		<fo:table-column column-width="3.5cm"/>
		<fo:table-column column-width="14cm"/>
		<fo:table-body font-size="12pt" 
						space-before.optimum="2pt"
						border-width="0.5mm">
						line-height="16pt"
						font-family="serif">
		<xsl:for-each select="child::*">
			<fo:table-row space-before.optimum="3pt">
			<fo:table-cell>
				<fo:block end-indent="2pt" text-align="end" font-size="7pt" font-family="monospace">
					<xsl:if test="$element='constructors'">
						<xsl:value-of select="@modifiers"/>
						<xsl:text> </xsl:text>
					</xsl:if>
					<xsl:if test="$element='fields'">
						<xsl:value-of select="@type"/>
					</xsl:if>
					<xsl:if test="$element='methods'">
						<xsl:value-of select="./returns/@type"/>
					</xsl:if>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell>
			<fo:block start-indent="2pt" font-family="monospace" font-size="10pt">
<!--				<fo:basic-link color="blue" font-weight="bold" font-size="12pt">
					<xsl:attribute name="internal-destination">
						<xsl:value-of select="translate(.,' ),-.(','____')"/>
					</xsl:attribute>
					<xsl:choose>
						<xsl:when test="$element='constructors'">
							<xsl:value-of select="../../@name"/>
						</xsl:when>
						<xsl:when test="$element='methods'">
							<xsl:value-of select="@name"/>
						</xsl:when>
						<xsl:when test="$element='fields'">
							<xsl:value-of select="@name"/>
						</xsl:when>
					</xsl:choose>
				</fo:basic-link>-->
				<xsl:if test="$element!='fields'">
					(<xsl:for-each select="./parameters/parameter">
						<xsl:value-of select="@package"/>.<xsl:value-of select="@type"/>
						<xsl:text> </xsl:text> <xsl:value-of select="@name"/>
						<xsl:if test="not(position()=last())"> 
							<xsl:text>, </xsl:text> 
						</xsl:if>
					</xsl:for-each>)
				</xsl:if>
			</fo:block>
			<fo:block start-indent="30pt"
					  font-size="10pt">
				<xsl:value-of select="./comment/lead/."/>
			</fo:block>
			</fo:table-cell>
			</fo:table-row>
		</xsl:for-each>

		</fo:table-body>		
	</fo:table>
</xsl:template>

<!-- field summary -->
<xsl:template match="fields">
	<xsl:call-template name="summarytable">
		<xsl:with-param name="element">fields</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<!-- constructor summary -->
<xsl:template match="constructors">
	<xsl:call-template name="summarytable"/>
</xsl:template>

<!-- method summary -->
<xsl:template match="methods">
	<xsl:call-template name="summarytable">
		<xsl:with-param name="element">methods</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<xsl:template name="detailView">
	<xsl:param name="element">methods</xsl:param>

	<fo:block font-size="16pt" 
              font-family="sans-serif" 
    	      line-height="20pt"
        	  background-color="#CCCCFF"
              border-width="0.5mm"
              space-before.optimum="15pt">
		<fo:block start-indent="10pt">
			<xsl:choose>
				<xsl:when test="$element='constructors'">Constructor Detail</xsl:when>
				<xsl:when test="$element='methods'">Method Detail</xsl:when>
				<xsl:when test="$element='fields'">Field Detail</xsl:when>
			</xsl:choose>
		</fo:block>
	</fo:block>
	<fo:block font-family="sans-serif">
		<xsl:for-each select="child::*">
			<fo:block font-size="14pt"
						font-weight="bold"
						space-before.optimum="20pt">
			    <xsl:attribute name="id">
      				<xsl:value-of select="../../../../@name"/>
      				<xsl:value-of select="../../../"/>
      				<xsl:value-of select="../../@name"/>
      				<xsl:value-of select="@modifiers"/>
      				<xsl:value-of select="@name"/>
					<xsl:if test="$element!='fields'">
						<xsl:for-each select="./parameters/parameter">
							<xsl:value-of select="@name"/>
						</xsl:for-each>
					</xsl:if>
    			</xsl:attribute>
				
				<xsl:choose>
					<xsl:when test="$element='constructors'"><xsl:value-of select="../../@name"/></xsl:when>
					<xsl:when test="$element='methods'"><xsl:value-of select="@name"/></xsl:when>
					<xsl:when test="$element='fields'"><xsl:value-of select="@name"/></xsl:when>
				</xsl:choose>
			</fo:block>
			<fo:block font-size="11pt"
						font-family="monospace"
						space-before.optimum="7pt">
				<xsl:value-of select="@modifiers"/><xsl:text> </xsl:text>
						<xsl:choose>
							<xsl:when test="$element='methods'">
								<xsl:value-of select="./returns/@package"/>.<xsl:value-of select="./returns/@type"/>
								<xsl:text> </xsl:text>
							</xsl:when>
							<xsl:when test="$element='fields'">
								<xsl:value-of select="@package"/>.<xsl:value-of select="@type"/>
								<xsl:text> </xsl:text>
							</xsl:when>
						</xsl:choose>
					<fo:inline font-weight="bold">
						<xsl:choose>
							<xsl:when test="$element='constructors'"><xsl:value-of select="../../@name"/></xsl:when>
							<xsl:when test="$element='methods'"><xsl:value-of select="@name"/></xsl:when>
							<xsl:when test="$element='fields'"><xsl:value-of select="@name"/></xsl:when>
						</xsl:choose>
					</fo:inline>
					<xsl:if test="$element!='fields'">
						( <xsl:for-each select="./parameters/parameter">
							<xsl:value-of select="@package"/>.<xsl:value-of select="@type"/>
							<xsl:text> </xsl:text> <xsl:value-of select="@name"/>
							<xsl:if test="not(position()=last())"> 
								<xsl:text>, </xsl:text> 
							</xsl:if>
						</xsl:for-each> )
					</xsl:if>
			</fo:block>
			<fo:block font-size="11pt"
						font-family="serif"
						start-indent="20pt"
						space-before.optimum="7pt">
				<xsl:apply-templates select="./comment"/>
				<xsl:apply-templates select="./parameters"/>
			</fo:block>
		</xsl:for-each>
	</fo:block>
</xsl:template>

<xsl:template match="methods" mode="detail">
	<xsl:call-template name="detailView"/>
</xsl:template>

<xsl:template match="fields" mode="detail">
	<xsl:call-template name="detailView">
		<xsl:with-param name="element">fields</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<xsl:template match="constructors" mode="detail">
	<xsl:call-template name="detailView">
		<xsl:with-param name="element">constructors</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<xsl:template match="comment/lead">
  	<fo:block font-size="11pt"
              space-before.optimum="10pt"
              font-family="sans-serif" >
		<xsl:value-of select="."/>
	</fo:block>
</xsl:template>

<xsl:template match="comment/detail">
  	<fo:block font-size="11pt"
              font-family="sans-serif" >
		<xsl:apply-templates/>
	</fo:block>
</xsl:template>

<xsl:template match="parameters">
	<fo:block font-size="11pt"
				font-weight="bold"
				font-family="monospace">
		Parameters:
	</fo:block>
	<xsl:apply-templates select="./parameter"/>
</xsl:template>

<xsl:template match="parameter">
	<fo:block font-size="11pt"
			start-indent="50pt"
			font-family="monospace">
		<xsl:value-of select="@name"/>
		<fo:inline font-family="serif">
		- <xsl:apply-templates select="./comment"/>
		</fo:inline>
	</fo:block>
</xsl:template>

</xsl:stylesheet>