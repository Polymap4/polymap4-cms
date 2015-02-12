/* 
 * Copyright (C) 2014-2015, Falko Bräutigam. All rights reserved.
 */
package org.polymap.cms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.polymap.core.ui.FormLayoutFactory;

import org.polymap.rhei.batik.app.DefaultAppDesign;
import org.polymap.rhei.batik.app.IAppDesign;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class CmsAppDesign
        extends DefaultAppDesign 
        implements IAppDesign {

    public static final int         MAX_CONTENT_WIDTH = 800;
    
    
    @Override
    public Shell createMainWindow( Display display ) {
        super.createMainWindow( display );
        
        Rectangle bounds = display.getBounds();
        int margins = Math.max( bounds.width - MAX_CONTENT_WIDTH, 0 );
        mainWindow.setLayout( FormLayoutFactory.defaults().margins( 0, margins/2, 10, margins/2 ).create() );
        
//        contents = new Composite( parent, SWT.NONE );
//        
//        Rectangle bounds = Display.getCurrent().getBounds();
//        int margins = Math.max( bounds.width - 800, 0 );
//        contents.setLayout( FormLayoutFactory.defaults().margins( 0, margins/2, 10, margins/2 ).create() );
//
//        super.createMainWindow( display );
//        panels.setData( RWT.CUSTOM_VARIANT, "cms-panels"  );
//        contents.setData( RWT.CUSTOM_VARIANT, "cms-panels-outer" );
        
        return mainWindow;
    }

    
    @Override
    protected Composite fillHeaderArea( Composite parent ) {
        Label l = new Label( parent, SWT.NONE );
        l.setText( "Ulrike Philipp" );
        return parent;
    }

}
