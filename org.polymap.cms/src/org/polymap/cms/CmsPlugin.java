/* 
 * Copyright (C) 2014-2015, Falko Bräutigam. All rights reserved.
 */
package org.polymap.cms;

import java.io.File;

import org.osgi.framework.BundleContext;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.eclipse.core.runtime.Path;

import org.polymap.core.CorePlugin;

import org.polymap.rhei.batik.toolkit.DefaultToolkit;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class CmsPlugin 
        extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.polymap.cms"; //$NON-NLS-1$

	private static CmsPlugin       instance;
	
 
	public static CmsPlugin instance() {
        return instance;
    }

	// instance *******************************************
	
    public void start( BundleContext context ) throws Exception {
        super.start( context );
        instance = this;
        
        ContentProvider.init( new Path( new File( CorePlugin.getDataLocation( this ), "content" ).getAbsolutePath() ) );
        
        DefaultToolkit.registerMarkdownRenderer( () -> new ContentImageRenderer() );
        DefaultToolkit.registerMarkdownRenderer( () -> new ArticleLinkRenderer() );
    }

    
    public void stop( BundleContext context ) throws Exception {
        instance = null;
        super.stop( context );
    }

}
