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

import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.util.concurrent.ExecutionException;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.toolkit.IMarkdownNode;
import org.polymap.rhei.batik.toolkit.IMarkdownRenderer;
import org.polymap.rhei.batik.toolkit.MarkdownRenderOutput;

import org.polymap.cms.ContentProvider.ContentObject;
import org.polymap.rap.updownload.download.DownloadServiceHandler;

/**
 * Images and download.
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class ContentImageRenderer
        implements IMarkdownRenderer {

    private static Log log = LogFactory.getLog( ContentImageRenderer.class );
    
    /* FIXME Horrible hack: keep strong references to prevent providers from GCed */
    private static Cache<String,DownloadServiceHandler.ContentProvider> providers = 
            CacheBuilder.newBuilder().maximumSize( 100 ).build();

    
    @Override
    public boolean render( IMarkdownNode node, MarkdownRenderOutput out, IAppContext context ) {
        if (node.type() == IMarkdownNode.Type.ExpImage
                || node.type() == IMarkdownNode.Type.ExpLink && node.url().startsWith( "#" )) {
            log.info( "url=" + node.url() + ", text=" + node.text() );

            String nodeUrl = node.url().startsWith( "#" ) 
                    ? substringAfter( node.url(), "#" )
                    : node.url();
                    
            final ContentObject co = ContentProvider.instance().findContent( nodeUrl );
            if (!co.exists()) {
                log.warn( "Image does not exist: " + nodeUrl );
                return false;
            }

            try {
                // download handler
                DownloadServiceHandler.ContentProvider provider = providers.get( nodeUrl, () -> new DownloadServiceHandler.ContentProvider() {
                    @Override
                    public InputStream getInputStream() throws Exception {
                        return co.contentStream();
                    }
                    @Override
                    public String getFilename() {
                        return co.title();
                    }
                    @Override
                    public String getContentType() {
                        return co.contentType();
                    }
                    @Override
                    public boolean done( boolean success ) {
                        return false;
                    }
                });
                String url = DownloadServiceHandler.registerContent( provider );
                out.setUrl( url );
                out.setText( node.text() );
            }
            catch (ExecutionException e) {
                throw new RuntimeException( e );
            }
            return true;
        }
        return false;
    }
    
}
