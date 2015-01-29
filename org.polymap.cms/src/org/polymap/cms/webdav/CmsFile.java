/* 
 * polymap.org
 * Copyright (C) 2014-2015, Falko Bräutigam. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.cms.webdav;

import static org.apache.commons.lang3.StringUtils.endsWithAny;
import static org.apache.commons.lang3.StringUtils.replace;

import java.util.Map;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.core.runtime.IPath;

import org.polymap.service.fs.providers.file.FsFile;
import org.polymap.service.fs.spi.BadRequestException;
import org.polymap.service.fs.spi.Range;

import org.polymap.cms.CmsPlugin;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class CmsFile
        extends FsFile {

    private static Log log = LogFactory.getLog( CmsFile.class );

    public static final String[]        CONTENT_FILE_EXTENSIONS = {"txt", "TXT", "md", "MD"};
    
    
    public CmsFile( IPath parentPath, CmsContentProvider provider, File source ) {
        super( parentPath, provider, source );
    }


    @Override
    public CmsContentProvider getProvider() {
        return (CmsContentProvider)super.getProvider();
    }


    @Override
    public void sendContent( OutputStream out, Range range, Map<String,String> params, String acceptedContentType )
            throws IOException, BadRequestException {
        log.info( "accepted: " + acceptedContentType );
        if (StringUtils.contains( acceptedContentType, "text/html")
                && endsWithAny( getName(), CONTENT_FILE_EXTENSIONS )) {
            sendHtmlForm( out );
        }
        else {
            super.sendContent( out, range, params, acceptedContentType );
        }
    }


    @Override
    public String getContentType( String accepts ) {
        // support HTML for in-browser editing
        if (StringUtils.startsWith( accepts, "text/html" )
                && endsWithAny( getName(), CONTENT_FILE_EXTENSIONS )) {
            return "text/html; charset=utf-8";
        }
        else {
            return super.getContentType( accepts );
        }
    }


    protected void sendHtmlForm( OutputStream out ) {
        URL templateUrl = CmsPlugin.instance().getBundle().getResource( "resources/html/CmsFileEditor.html" );
        InputStream in = null;
        try {
            in = templateUrl.openStream();
            String template = IOUtils.toString( in, "UTF-8" );
            
            String html = replace( template, "@filename", getName() );
            html = replace( html, "@filecontent", FileUtils.readFileToString( getFile(), "UTF-8" ) );
            
            IOUtils.copy( new ByteArrayInputStream( html.getBytes( "UTF-8" ) ), out );
        }
        catch (Exception e) {
            throw new RuntimeException( e );
        }
        finally {
            IOUtils.closeQuietly( in );
        }
    }
    
}
