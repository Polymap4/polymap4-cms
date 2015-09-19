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
package org.polymap.cms;

import static com.google.common.collect.Iterables.transform;
import static java.util.Arrays.asList;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;

import org.eclipse.core.runtime.IPath;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class ContentProvider {

    private static Log log = LogFactory.getLog( ContentProvider.class );
    
    private static ContentProvider  instance;
    

    public static void init( IPath rootPath ) {
        instance = new ContentProvider( rootPath );    
    }
    
    
    public static ContentProvider instance() {
        assert instance != null;
        return instance;
    }
    
    
    // instance *******************************************
    
    private File                    rootDir;
    
    
    public ContentProvider( IPath rootPath ) {
        rootDir = rootPath.toFile();
        rootDir.mkdirs();
    }

    
    public <T extends ContentObject> T findContent( String path ) {
        File f = new File( rootDir, path );
        return (T)new ContentObject( f );
    }
    

    public <T extends ContentObject> Iterable<T> listContent( String path ) {
        File dir = new File( rootDir, path );
        return transform( asList( dir.listFiles() ), new Function<File,T>() {
            public T apply( File input ) {
                return (T)new ContentObject( input );
            }
        });
    }
    

    /**
     * API and base class of content object, such as text or image. 
     */
    public static class ContentObject {
        
        private File            f;
        
        public ContentObject( File f ) {
            assert f != null;
            this.f = f;
        }

        public boolean exists() {
            return f.exists();    
        }
        
        public void putContent( String content ) {
            try {
                FileUtils.write( f, content, "UTF8" );
            }
            catch (IOException e) {
                throw new RuntimeException( e );
            }    
        }
        
        public String content() {
            try {
                return FileUtils.readFileToString( f, "UTF8" );
            }
            catch (IOException e) {
                return "Fehler: " + e.getLocalizedMessage();
            }
        }

        public InputStream contentStream() throws IOException {
            return new BufferedInputStream( new FileInputStream( f ) );
        }
        
        public String contentType() {
            String ext = FilenameUtils.getExtension( f.getName() );
            if (ext.equalsIgnoreCase( "txt" )) {
                return "text/text; charset=UTF8";
            }
            else if (ext.equalsIgnoreCase( "jpg" ) || ext.equalsIgnoreCase( "jpeg" )) {
                return "image/jpeg";
            }
            else if (ext.equalsIgnoreCase( "png" )) {
                return "image/png";
            }
            else if (ext.equalsIgnoreCase( "gif" )) {
                return "image/gif";
            }
            else if (ext.equalsIgnoreCase( "pdf" )) {
                return "application/pdf";
            }
            else {
                return "text/text; charset=UTF8";
            }
        }
        
        public String title() {
            return FilenameUtils.getBaseName( f.getName() );
        }
                
        /**
         * The unique name that identifies this article. This is the base name of the
         * file.
         */
        public String name() {
            return FilenameUtils.getBaseName( f.getName() );
        }
        
    }
    
}
