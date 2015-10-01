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
package org.polymap.cms;

import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.core.runtime.IPath;
import org.eclipse.rap.rwt.RWT;

import org.polymap.core.runtime.config.Config;
import org.polymap.core.runtime.config.Configurable;
import org.polymap.core.runtime.config.Mandatory;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class ContentProvider
        extends Configurable {

    private static Log log = LogFactory.getLog( ContentProvider.class );
    
    private static final String     LANG_SEPARATOR = "_";
    
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
    
    /** 
     * The default {@link Locale} used as fall-back. Defaults to {@link Locale#getDefault()}. 
     */
    @Mandatory
    public Config<Locale>           defaultLocale;
    
    
    public ContentProvider( IPath rootPath ) {
        rootDir = rootPath.toFile();
        rootDir.mkdirs();
        defaultLocale.set( Locale.getDefault() );
    }

    
    public File getRootDir() {
        return rootDir;
    }


    /**
     * Finds the object for the given path. Uses the Locale of the current
     * {@link RWT#getLocale() RWT session} and falls back to {@link #defaultLocale}.
     *
     * @param path The relative path of the object.
     * @return Newly created {@link ContentObject}. Check {@link ContentObject#exists()}.
     */
    public <T extends ContentObject> T findContent( String path ) {
        return findContent( path, RWT.getLocale(), false );
    }
    

    /**
     * 
     *
     * @param path The relative path of the object.
     * @param locale The Locale to search content for. Must not be null.
     * @param strict True specifies that {@link #defaultLocale} is not consulted.
     * @return Newly created {@link ContentObject}. Check {@link ContentObject#exists()}.
     */
    public <T extends ContentObject> T findContent( String path, Locale locale, boolean strict ) {
        File f = new File( rootDir, path );
        return (T)new ContentObject( f, locale, strict );
    }
    

    /**
     * Returns a list of objects that have content for the Locale of the current
     * {@link RWT#getLocale() RWT session} and falls back to {@link #defaultLocale}.
     *
     * @param path The relative path of the folder.
     */
    public <T extends ContentObject> List<T> listContent( String path ) {
        return listContent( path, RWT.getLocale(), false );
    }

    /**
     * 
     *
     * @param path The relative path of the folder.
     * @param locale The Locale to search content for. Must not be null.
     * @param strict True specifies that {@link #defaultLocale} is not consulted.
     */
    public <T extends ContentObject> List<T> listContent( String path, Locale locale, boolean strict ) {
        File dir = new File( rootDir, path );
        
        // files that have at least one lang content
        Set<File> files = Arrays.stream( dir.listFiles() )
                .map( f -> withoutLocale( f ).getAbsoluteFile() )
                .collect( Collectors.toCollection( TreeSet::new ) );
        
        //
        return files.stream()
                .map( f -> (T)new ContentObject( f, locale, strict ) )
                .filter( co -> co.exists() )  // if strict
                .collect( Collectors.toList() );
    }
    
    
    protected File withLocale( File f, Locale l ) {
        String lang = l.getLanguage().toLowerCase();
        return new File( f.getParentFile(), 
                getBaseName( f.getName() ) + "_" + lang + "." + getExtension( f.getName() ) );
    }
    

    protected File withoutLocale( File f ) {
        return f.getName().contains( LANG_SEPARATOR )
            ? new File( f.getParentFile(), substringBeforeLast( f.getName(), LANG_SEPARATOR ) + "." + getExtension( f.getName() ) )
            : f;
    }
    

    /**
     * API and base class of content object, such as text or image. 
     */
    public class ContentObject {
    
        private Locale          locale;
        
        private File            f;
        
        /** The name of {@link #f} without locale extension. */
        private String          name;
        
        
        public ContentObject( File f, Locale locale, boolean strict ) {
            assert f != null && locale != null;
            
            // negotiate locale
            this.name = f.getName();
            File requested = new File( f.getAbsolutePath() );
            
            this.f = withLocale( f, locale );
            this.locale = locale;
            
            if (!this.f.exists() && !strict) {
                this.f = requested;
                this.locale = defaultLocale.get();
            }
        }
        
        public Locale locale() {
            return locale;
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
            return FilenameUtils.getBaseName( name );
        }
                
        /**
         * The unique name that identifies this article. This is the base name of the
         * file.
         */
        public String name() {
            return FilenameUtils.getBaseName( name );
        }
        
    }
    
}
