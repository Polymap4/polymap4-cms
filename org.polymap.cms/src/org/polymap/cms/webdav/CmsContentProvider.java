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
package org.polymap.cms.webdav;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.polymap.service.fs.providers.file.FsContentProvider;
import org.polymap.service.fs.providers.file.FsFolder;
import org.polymap.service.fs.spi.IContentFolder;
import org.polymap.service.fs.spi.IContentNode;
import org.polymap.service.fs.spi.IContentProvider;
import org.polymap.service.fs.spi.IContentSite;

import org.polymap.cms.ContentProvider;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class CmsContentProvider
        extends FsContentProvider
        implements IContentProvider {

    private static Log log = LogFactory.getLog( CmsContentProvider.class );
    

    public CmsContentProvider() {
    }

    
    @Override
    public void init( IContentSite site ) {
        super.init( site );
        
        File dir = ContentProvider.instance().getRootDir();
        FsFolder root = new CmsFolder( "CMS", new Path( "/" ), this, dir );
        roots = Collections.singletonList( root );
    }


    @Override
    public List<? extends IContentNode> getChildren( IPath path ) {
        // check admin
//        if (!SecurityUtils.isAdmin()) {
//            return null;
//        }
        
        // roots
        if (path.segmentCount() == 0) {
            return roots;
        }

        // folder
        IContentFolder parent = getSite().getFolder( path );
        if (parent instanceof FsFolder) {
            File[] files = ((FsFolder)parent).getDir().listFiles();
            List<IContentNode> result = new ArrayList( files.length );
            
            for (File f : files) {
                if (f.isFile()) { 
                    result.add( new CmsFile( parent.getPath(), this, f ) );
                }
                else if (f.isDirectory()) {
                    result.add( new CmsFolder( f.getName(), parent.getPath(), this, f ) );                    
                }
            
            }
            return result;
        }
        return null;
    }

}
