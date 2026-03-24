<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:http="xalan://com.example.xalan.extensions.HttpXmlExtension"
    extension-element-prefixes="http">

    <xsl:output method="xml" indent="yes"/>

    <xsl:param name="url"/>
    <xsl:param name="accept" select="'application/xml'"/>
    <xsl:param name="traceId" select="'xslt-trace'"/>

    <xsl:template match="/">
        <result>
            <xsl:variable name="extObj" select="http:new()"/>
            <xsl:variable name="headers" select="http:newHeaders($extObj)"/>
            <xsl:variable name="headersWithAccept" select="http:addHeader($extObj, $headers, 'Accept', $accept)"/>
            <xsl:variable name="headersWithTrace" select="http:addHeader($extObj, $headersWithAccept, 'X-Trace-Id', $traceId)"/>
            <body>
                <xsl:value-of select="http:get($extObj, $url, $headersWithTrace)"/>
            </body>
        </result>
    </xsl:template>
</xsl:stylesheet>
