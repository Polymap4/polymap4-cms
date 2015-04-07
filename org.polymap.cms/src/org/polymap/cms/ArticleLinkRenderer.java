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

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import org.polymap.core.ui.UIUtils;

import org.polymap.rhei.batik.BatikApplication;
import org.polymap.rhei.batik.IAppContext;
import org.polymap.rhei.batik.app.DefaultToolkit;
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
        implements IMarkdownRenderer, DisposeListener {

    private static Log log = LogFactory.getLog( ArticleLinkRenderer.class );
    
    private ILinkAction                 action;

    
    @Override
    public boolean render( DefaultToolkit toolkit, IMarkdownNode node, MarkdownRenderOutput out, Widget widget ) {
        log.info( "url=" + node.url() );
        if (node.type() == IMarkdownNode.Type.ExpLink && node.url().startsWith( "!" )) {

            assert action == null;
            action = new ILinkAction() {
                Display display = UIUtils.sessionDisplay(); 

                @Override
                public Display display() {
                    return display;
                }

                @Override
                public void linkPressed() throws Exception {
                    IAppContext context = BatikApplication.instance().getContext();
                    ArticlePanel panel = (ArticlePanel)context.openPanel( toolkit.getPanelPath(), ArticlePanel.ID );
                    panel.setArticle( node.url().substring( 1 ) ); 
                }
            };

            // prevent this from being GCed as long as the widget exists
            widget.addDisposeListener( this );

            String id = LinkActionServiceHandler.register( action );
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


    @Override
    public void widgetDisposed( DisposeEvent ev ) {
        // LinkActionServiceHandler.unregister( action );
    }
    
}
