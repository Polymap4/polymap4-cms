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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.swt.widgets.Display;

import org.polymap.core.runtime.Polymap;

import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.internal.LinkActionServiceHandler;
import org.polymap.rhei.batik.toolkit.IPanelToolkit.LinkAction;
import org.polymap.rhei.batik.toolkit.IPanelToolkit.MarkdownNode;
import org.polymap.rhei.batik.toolkit.IPanelToolkit.MarkdownNodeType;
import org.polymap.rhei.batik.toolkit.IPanelToolkit.MarkdownRenderer;
import org.polymap.rhei.batik.toolkit.IPanelToolkit.RenderOutput;

/**
 * Render !article style links.
 * <p/>
 * <b>Examples:</b>
 * <pre>
 *    [The Work](!_article_name_)
 * </pre>
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class ArticleLinkRenderer
        implements MarkdownRenderer {

    private static Log log = LogFactory.getLog( ArticleLinkRenderer.class );

    
    @Override
    public boolean render( final MarkdownNode node, RenderOutput out, final IAppContext context ) {
        log.info( "url=" + node.url() );
        if (node.type() == MarkdownNodeType.ExpLink 
                && node.url().startsWith( "!" )) {
            
            String id = LinkActionServiceHandler.register( new LinkAction() {
                Display display = Polymap.getSessionDisplay();
                
                @Override
                public void linkPressed() throws Exception {
                    display.asyncExec( new Runnable() {
                        public void run() {
                            ArticlePanel panel = (ArticlePanel)context.openPanel( ArticlePanel.ID );
                            panel.setArticle( node.url().substring( 1 ) ); 
                        }
                    });
                }
            });
            
            String linkUrl = "javascript:sendServiceHandlerRequest('" + LinkActionServiceHandler.SERVICE_HANDLER_ID + "','" + id + "');"
                    /*+ "org.eclipse.swt.Request.getInstance().enableUICallBack();"*/;
            out.setUrl( linkUrl );
            out.setText( node.text() );
            out.setTitle( node.title() );
            return true;
        }
        else {
            return false;
        }
    }
    
}
