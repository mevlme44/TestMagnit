<?xml version="1.0" encoding="utf-8"?>
  <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="Entries"> 
      <Entries> 
        <xsl:for-each select="Entry">
	  <Entry>
	    <xsl:attribute name="field">
              <xsl:value-of select="text()"/>
	    </xsl:attribute>
	  </Entry>
        </xsl:for-each> 
     </Entries>
  </xsl:template>
</xsl:stylesheet>