/* 
 * Copyright (C) 2014, Falko Bräutigam. All rights reserved.
 */
package org.polymap.cms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import org.eclipse.rap.rwt.RWT;
import org.polymap.core.ui.FormLayoutFactory;

import org.polymap.rhei.batik.layout.desktop.DesktopAppManager;
import org.polymap.rhei.batik.layout.desktop.DesktopAppWindow;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public abstract class CmsAppWindow
        extends DesktopAppWindow {

    public static final int     MAX_CONTENTS_WIDTH = 800;
    
    
    public CmsAppWindow( DesktopAppManager appManager ) {
        super( appManager );
    }


    @Override
    protected Control createContents( Composite parent ) {
        contents = new Composite( parent, SWT.NONE );
        
        Rectangle bounds = Display.getCurrent().getBounds();
        int margins = Math.max( bounds.width - 800, 0 );
        contents.setLayout( FormLayoutFactory.defaults().margins( 0, margins/2, 10, margins/2 ).create() );

        super.createContents( parent );
        panels.setData( RWT.CUSTOM_VARIANT, "cms-panels"  );
        contents.setData( RWT.CUSTOM_VARIANT, "cms-panels-outer" );
        
        return contents;
    }

}
