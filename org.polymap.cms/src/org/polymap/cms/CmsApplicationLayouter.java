/* 
 * Copyright (C) 2014-2015, Falko Bräutigam. All rights reserved.
 */
package org.polymap.cms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import org.eclipse.jface.window.Window;

import org.eclipse.ui.forms.widgets.ScrolledPageBook;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.BrowserNavigation;

import org.polymap.rhei.batik.PanelIdentifier;
import org.polymap.rhei.batik.layout.desktop.DesktopActionBar;
import org.polymap.rhei.batik.layout.desktop.DesktopActionBar.PLACE;
import org.polymap.rhei.batik.layout.desktop.DesktopAppManager;
import org.polymap.rhei.batik.layout.desktop.PanelNavigator;
import org.polymap.rhei.batik.layout.desktop.StatusManager;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class CmsApplicationLayouter
        extends DesktopAppManager {

    @Override
    public Window initMainWindow( Display display ) {
        browserHistory = RWT.getClient().getService( BrowserNavigation.class );
        browserHistory.pushState( "Start", "Start" );
        browserHistory.addBrowserNavigationListener( this );
        
        // panel navigator area
        actionBar = new DesktopActionBar( context, tk );
        //actionBar.add( new PanelToolbar( this ), PLACE.PANEL_TOOLBAR );
        actionBar.add( panelNavigator = new PanelNavigator( this ), PLACE.PANEL_NAVI );
        //actionBar.add( userPrefs = new UserPreferences( this ), PLACE.USER_PREFERENCES );
        //actionBar.add( statusManager = new StatusManager( this ), PLACE.STATUS );

        // mainWindow
        mainWindow = new CmsAppWindow( this ) {
            @Override
            protected Composite fillNavigationArea( Composite parent ) {
                return actionBar.createContents( parent );
            }
            @Override
            protected Composite fillPanelArea( Composite parent ) {
                scrolledPanelContainer = new ScrolledPageBook( parent, SWT.V_SCROLL /*| SWT.BORDER*/ );
                scrolledPanelContainer.setTouchEnabled( true );
                scrolledPanelContainer.showEmptyPage();
                
//                scrolledPanelContainer = (ScrolledComposite)tk.createComposite( parent, SWT.BORDER, SWT.V_SCROLL );
//                panelArea = (Composite)scrolledPanelContainer.getContent();
//                panelArea.setLayout( new FillLayout( SWT.VERTICAL ) );
//                tk.createLabel( panelArea, "Panels..." );
                return scrolledPanelContainer;
            }
            @Override
            protected StatusManager getStatusManager() {
                return statusManager;
            }
        };
        // open root panel / after main window is created
        display.asyncExec( new Runnable() {
            public void run() {
                openPanel( new PanelIdentifier( "start" ) );
            }
        });
        return mainWindow;
    }


    @Override
    public void dispose() {
        browserHistory.removeBrowserNavigationListener( this );
    }

}
