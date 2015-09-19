/* 
 * polymap.org
 * Copyright (C) 2015, Falko Bräutigam. All rights reserved.
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

import org.eclipse.swt.widgets.Composite;

import org.polymap.rhei.batik.dashboard.DefaultDashlet;

import org.polymap.cms.ContentProvider.ContentObject;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class ArticleDashlet
        extends DefaultDashlet {
    
    private ContentObject           co;
    
    
    public ArticleDashlet( ContentObject co ) {
        this.co = co;
    }

    
    public ArticleDashlet( String articlePath ) {
        ContentProvider cp = ContentProvider.instance();
        co = cp.findContent( articlePath );
    }


    @Override
    public void createContents( Composite parent ) {
        String content = co.content();
        String title = co.title();
        if (content.startsWith( "#" )) {
              title = StringUtils.substringBefore( content, "\n" ).substring( 1 );
              content = content.substring( title.length() + 2 );
        }
        getSite().title.set( title );
        getSite().toolkit().createFlowText( parent, content );
    }

}
