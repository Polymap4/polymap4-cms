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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.swt.widgets.Composite;

import org.polymap.core.ui.FormDataFactory;
import org.polymap.core.ui.FormLayoutFactory;
import org.polymap.core.ui.UIUtils;

import org.polymap.rhei.batik.DefaultPanel;
import org.polymap.rhei.batik.PanelIdentifier;
import org.polymap.rhei.batik.toolkit.IPanelSection;
import org.polymap.rhei.batik.toolkit.IPanelToolkit;

import org.polymap.cms.ContentProvider.ContentObject;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class ArticlePanel
        extends DefaultPanel {

    private static Log log = LogFactory.getLog( ArticlePanel.class );

    public static final PanelIdentifier ID = new PanelIdentifier( "article" );

    private String          articlePath;
    
    
    public void setArticle( String path ) {
        this.articlePath = path;
    }


    @Override
    public void createContents( final Composite parent ) {
        // delay after ArticleLinkRenderer has called setArticle
        UIUtils.sessionDisplay().asyncExec( new Runnable() {
            public void run() {
                IPanelToolkit tk = getSite().toolkit();
                ContentProvider cp = ContentProvider.instance();
                
                ContentObject co = cp.findContent( articlePath );
                getSite().setTitle( co.title() );
                
                String content = co.content();
                String title = null;
                if (content.startsWith( "#" )) {
                      title = StringUtils.substringBefore( content, "\n" ).substring( 1 );
                      content = content.substring( title.length() + 2 );
                }

                log.info( "parent width: " + parent.getBounds().width );
                
                
                IPanelSection section = tk.createPanelSection( parent, title );
                section.getBody().setLayout( FormLayoutFactory.defaults().create() );
                
                tk.createFlowText( section.getBody(), content )
                        .setLayoutData( FormDataFactory.filled().width( 500 ).create() );
                
                getSite().setTitle( title );
            }
        });
    }
    
}
