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

import org.polymap.core.ui.download.DownloadServiceHandler;

import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.toolkit.IPanelToolkit.MarkdownNode;
import org.polymap.rhei.batik.toolkit.IPanelToolkit.MarkdownNodeType;
import org.polymap.rhei.batik.toolkit.IPanelToolkit.MarkdownRenderer;
import org.polymap.rhei.batik.toolkit.IPanelToolkit.RenderOutput;

import org.polymap.cms.ContentProvider.ContentObject;

/**
 * Images and download.
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class ContentImageRenderer
        implements MarkdownRenderer {

    private static Log log = LogFactory.getLog( ContentImageRenderer.class );


    @Override
    public boolean render( MarkdownNode node, RenderOutput out, IAppContext context ) {
        if (node.type() == MarkdownNodeType.ExpImage
                || node.type() == MarkdownNodeType.ExpLink && node.url().startsWith( "#" )) {
            log.info( "url=" + node.url() + ", text=" + node.text() );

            String nodeUrl = node.url().startsWith( "#" ) 
                    ? substringAfter( node.url(), "#" )
                    : node.url();
                    
            final ContentObject co = ContentProvider.instance().findContent( nodeUrl );
            if (!co.exists()) {
                log.warn( "Image does not exist: " + nodeUrl );
                return false;
            }

            String url = DownloadServiceHandler.registerContent( new DownloadServiceHandler.ContentProvider() {
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
                    return true;
                }
            });
            out.setUrl( url );
            out.setText( node.text() );
            return true;
        }
        return false;
    }
    
}
