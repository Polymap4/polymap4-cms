/* 
 * Copyright (C) 2014-2015, Falko Bräutigam. All rights reserved.
 */
package org.polymap.cms;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.polymap.core.runtime.Polymap;

import org.polymap.rhei.batik.layout.desktop.DesktopToolkit;

import org.osgi.framework.BundleContext;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class CmsPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.polymap.cms"; //$NON-NLS-1$

	private static CmsPlugin       instance;
	
 
	public static CmsPlugin instance() {
        return instance;
    }

	// instance *******************************************
	
    public void start( BundleContext context ) throws Exception {
        super.start( context );
        instance = this;
        
        ContentProvider.init( Polymap.getWorkspacePath().append( "cms" ) );
        
        DesktopToolkit.registerMarkdownRenderer( new ContentImageRenderer() );
        DesktopToolkit.registerMarkdownRenderer( new ArticleLinkRenderer() );
    }

    
    public void stop( BundleContext context ) throws Exception {
        instance = null;
        super.stop( context );
    }

}
