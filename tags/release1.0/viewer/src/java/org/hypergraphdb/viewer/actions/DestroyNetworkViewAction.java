package org.hypergraphdb.viewer.actions;

import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.util.*;
import java.awt.event.*;

public class DestroyNetworkViewAction extends HGVAction {

  public DestroyNetworkViewAction () {
    super( ActionManager.DESTROY_VIEW_ACTION );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_W, ActionEvent.CTRL_MASK) ;
}
                               
  public DestroyNetworkViewAction ( boolean label ) {
    super();
  }

  public void actionPerformed ( ActionEvent e ) {
    destroyViewFromCurrentNetwork();
  }

  public static void destroyViewFromCurrentNetwork () {
  	HGVKit.destroyNetworkView( HGVKit.getCurrentNetwork() );
  }
}
