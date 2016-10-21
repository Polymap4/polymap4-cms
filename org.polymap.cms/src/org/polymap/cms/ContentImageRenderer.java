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

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Widget;

import org.polymap.rhei.batik.toolkit.DefaultToolkit;
import org.polymap.rhei.batik.toolkit.IMarkdownNode;
import org.polymap.rhei.batik.toolkit.IMarkdownRenderer;
import org.polymap.rhei.batik.toolkit.MarkdownRenderOutput;

import org.polymap.cms.ContentProvider.ContentObject;
import org.polymap.rap.updownload.download.DownloadService;

/**
 * Images and download.
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class ContentImageRenderer
        implements IMarkdownRenderer, DisposeListener {

    private static final Log log = LogFactory.getLog( ContentImageRenderer.class );
    
    private DownloadService.ContentProvider provider;
    
    
    @Override
    public boolean render( DefaultToolkit toolkit, IMarkdownNode node, MarkdownRenderOutput out, Widget widget ) {
        if (node.type() == IMarkdownNode.Type.ExpImage
                || node.type() == IMarkdownNode.Type.ExpLink && node.url().startsWith( "#" )) {
            log.debug( "url=" + node.url() + ", text=" + node.text() );

            String nodeUrl = node.url().startsWith( "#" ) 
                    ? substringAfter( node.url(), "#" )
                    : node.url();
                    
            final ContentObject co = ContentProvider.instance().findContent( nodeUrl );
            if (!co.exists()) {
                log.warn( "Image does not exist: " + nodeUrl );
                return false;
            }

            // download handler
            assert provider == null; 
            provider = new DownloadService.ContentProvider() {
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
            };
            
            // prevent this from being GCed as long as the widget exists
            widget.addDisposeListener( this );

            String url = DownloadService.registerContent( provider );
            out.setUrl( url );
            out.setText( node.text() );
            return true;
        }
        return false;
    }
    
    @Override
    public void widgetDisposed( DisposeEvent ev ) {
        //DownloadService.unregisterContent( provider );
    }    

}
