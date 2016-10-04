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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.core.runtime.IProgressMonitor;

import org.polymap.core.runtime.UIJob;
import org.polymap.core.runtime.UIThreadExecutor;
import org.polymap.core.ui.FormDataFactory;
import org.polymap.core.ui.FormLayoutFactory;
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

    private IPanelToolkit   tk;

    private IPanelSection   section;
    
    
    public void setArticle( String path ) {
        this.articlePath = path;
    }


    /**
     * Creates the basic layout of the article panel.
     * <p/>
     * Override this in order to provide specific layout. 
     */
    protected void createLayout( Composite parent ) {
        site().setSize( 550, 650, 700 );

        FillLayout fill = new FillLayout( SWT.VERTICAL );
        fill.marginHeight = 10; //site().layoutPreferences().getMarginTop();
        fill.marginWidth = site().layoutPreferences().getMarginLeft();
        parent.setLayout( fill );
        
        tk = site().toolkit();
        section = tk.createPanelSection( parent, "...", SWT.BORDER );
        section.getBody().setLayout( FormLayoutFactory.defaults().margins( 10, 0 ).create() );        
    }
    
    
    /**
     * Creates the text control.
     * <p/>
     * Override this in order to provide specific behaviour. 
     */
    protected void createText( String title, String content ) {
        section.setTitle( title );
        site().title.set( title );
        
        Label flowText = tk.createFlowText( section.getBody(), content );
        flowText.setLayoutData( FormDataFactory.filled().width( 500 ).height( 500 ).create() );
        section.getBody().layout();
        
        // XXX delayed refresh: trying to help RAP to get the font/text size right (?)
        new UIJob( "delay", true ) {
            @Override
            protected void runWithException( IProgressMonitor monitor ) throws Exception {
                UIThreadExecutor.async( () -> {
                    log.info( "..." );
                    flowText.setLayoutData( FormDataFactory.filled().width( 1000 ).height( 1000 ).create() );
                    site().layout( true );
                });
            }
        }.scheduleWithUIUpdate( 1000 );
    }
    
    
    @Override
    public void createContents( final Composite parent ) {
        createLayout( parent );
        
        // delay after ArticleLinkRenderer has called setArticle
        UIThreadExecutor.async( () -> {
            ContentProvider cp = ContentProvider.instance();

            ContentObject co = cp.findContent( articlePath );
            getSite().setTitle( co.title() );

            String content = co.content();
            String title = null;
            if (content.startsWith( "#" )) {
                title = StringUtils.substringBefore( content, "\n" ).substring( 1 );
                content = content.substring( title.length() + 2 );
            }

            createText( title, content );
        });
    }
    
}
