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

<xsl:template match="interfaces">
  	<!-- interface TOC -->
  	<fo:block font-size="14pt"
          	  font-family="sans-serif" 
  	          line-height="18pt"
  	          space-after.optimum="10pt"
  	          font-weight="bold">
    	Interfaces:
  	</fo:block>
  	<!-- Create TOC of interfaces in this package -->
	<fo:block font-size="12pt" 
  	          line-height="16pt"
  	          font-family="sans-serif">
      <xsl:for-each select="./interface"> 
        <fo:block  text-align="start" >
          <fo:basic-link color="blue">
            <xsl:attribute name="internal-destination">
              <xsl:value-of select="translate(.,' ),-.(','____')"/>
            </xsl:attribute>
            <xsl:value-of select="@name"/>
          </fo:basic-link> 
        </fo:block>
      </xsl:for-each>
	</fo:block>

</xsl:template>

</xsl:stylesheet>
