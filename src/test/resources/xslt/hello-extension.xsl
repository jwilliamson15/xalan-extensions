<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ext="xalan://com.example.xalan.extensions.HelloXmlExtension"
    extension-element-prefixes="ext">

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
        <result>
            <!-- Create an instance, then call the public instance method. -->
            <xsl:variable name="extObj" select="ext:new()"/>
            <message>
                <xsl:value-of select="ext:greet($extObj, 'Josh')"/>
            </message>
        </result>
    </xsl:template>
</xsl:stylesheet>

