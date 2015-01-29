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

import java.util.concurrent.ExecutionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import org.eclipse.swt.widgets.Display;

import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleUtil;

import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.internal.LinkActionServiceHandler;
import org.polymap.rhei.batik.toolkit.ILinkAction;
import org.polymap.rhei.batik.toolkit.IMarkdownNode;
import org.polymap.rhei.batik.toolkit.IMarkdownRenderer;
import org.polymap.rhei.batik.toolkit.MarkdownRenderOutput;

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
        implements IMarkdownRenderer {

    private static Log log = LogFactory.getLog( ArticleLinkRenderer.class );

    /* 
     * FIXME Horrible hack: keep strong references to prevent providers from GCed
     * see also ContentImageProvider 
     */
    private static Cache<String,ILinkAction> actions = CacheBuilder.newBuilder().maximumSize( 100 ).build();

    
    @Override
    public boolean render( final IMarkdownNode node, MarkdownRenderOutput out, final IAppContext context ) {
        log.info( "url=" + node.url() );
        if (node.type() == IMarkdownNode.Type.ExpLink 
                && node.url().startsWith( "!" )) {
            
            try {
                ILinkAction action = actions.get( "", () -> new ILinkAction() {
                    Display display = LifeCycleUtil.getSessionDisplay();
                    
                    @Override
                    public Display display() {
                        return display;
                    }
                    
                    @Override
                    public void linkPressed() throws Exception {
                        ArticlePanel panel = (ArticlePanel)context.openPanel( ArticlePanel.ID );
                        panel.setArticle( node.url().substring( 1 ) ); 
                    }
                });

                String id = LinkActionServiceHandler.register( action );
                String linkUrl = "javascript:sendServiceHandlerRequest('" + LinkActionServiceHandler.SERVICE_HANDLER_ID + "','" + id + "');"
                        /*+ "org.eclipse.swt.Request.getInstance().enableUICallBack();"*/;
                out.setUrl( linkUrl );
                out.setText( node.text() );
                out.setTitle( node.title() );
                return true;
            }
            catch (ExecutionException e) {
                throw new RuntimeException( e );
            }
        }
        else {
            return false;
        }
    }
    
}
