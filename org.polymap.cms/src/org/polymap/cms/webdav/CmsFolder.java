/* 
 * polymap.org
 * Copyright (C) 2014, Falko Bräutigam. All rights reserved.
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

import java.util.Map;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bradmcevoy.http.FileItem;

import org.eclipse.core.runtime.IPath;

import org.polymap.service.fs.providers.FsFolder;
import org.polymap.service.fs.spi.BadRequestException;
import org.polymap.service.fs.spi.NotAuthorizedException;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class CmsFolder
        extends FsFolder {

    private static Log log = LogFactory.getLog( CmsFolder.class );
    

    public CmsFolder( String name, IPath parentPath, CmsContentProvider provider, File dir ) {
        super( name, parentPath, provider, dir );
    }


    @Override
    public CmsContentProvider getProvider() {
        return (CmsContentProvider)super.getProvider();
    }


    @Override
    protected String getDescription( String contentType ) {
        log.info( "accepted: " + contentType );
        return "<div style=\"background:#f0f0f0; padding:5px; width:800px; margin-left:auto; margin-right:auto;\">" +
                "<h2>Kleine Anleitung</h2>" + 
                "<p>Das ist die Übersicht der <b>Dateien (\"Files\")</b> der Web-Seite. Jede Datei enthält den Text einer <b>Seite</b> im Web. Man könnte auch \"Artikel\" oder \"Beitrag\" dazu sagen. Es gibt auch noch andere Dateien, zum Beispiel für Bilder. Die Dateien mit den Texten enden auf *.txt.</p>" + 
                "<p>Zum <b>Bearbeiten</b> klickst du auf einen Eintrag unten. Danach öffnet sich ein einfacher Editor. Darin kannst du den Text ändern und danach speichern.</p>" + 
                "<p>Im <b>Unterverzeichnis (\"Folder\")</b> \"images\" liegen alle Bilder. Das Unterverzeichnis \"frontpage\" hat eine besondere Funktion. Alle darin gespeicherten Text-Dateien werden auf der ersten Seite als einzelne Absätze angezeigt.</p>" +
                "</div>";
    }


    @Override
    public String processForm( Map<String,String> params, Map<String,FileItem> files ) 
            throws IOException, NotAuthorizedException, BadRequestException {
        String filename = params.get( "filename" );
        String text = params.get( "edited" );
        getProvider().createNew( this, filename, new ByteArrayInputStream( text.getBytes( "UTF-8" ) ) );
        return null;
    }

}
