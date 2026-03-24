<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ext="xalan://com.example.xalan.extensions.HttpXmlExtension"
    extension-element-prefixes="ext">

    <xsl:output method="xml" indent="yes"/>

    <xsl:param name="url"/>

    <xsl:template match="/">
        <result>
            <xsl:variable name="extObj" select="ext:new()"/>
            <body>
                <xsl:value-of select="ext:get($extObj, $url)"/>
            </body>
        </result>
    </xsl:template>
</xsl:stylesheet>

