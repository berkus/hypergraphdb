//-------------------------------------------------------------------------
// $Revision: 1.1 $
// $Date: 2005/12/25 01:22:41 $
// $Author: bobo $
//-------------------------------------------------------------------------
package org.hypergraphdb.viewer.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import org.hypergraphdb.viewer.ActionManager;
import org.hypergraphdb.viewer.HGVKit;
import org.hypergraphdb.viewer.util.HGVAction;
//-------------------------------------------------------------------------
public class SelectAllEdgesAction extends HGVAction  {

  public SelectAllEdgesAction () {
        super (ActionManager.SELECT_ALL_EDGES_ACTION);
        setAcceleratorCombo( java.awt.event.KeyEvent.VK_A, ActionEvent.ALT_MASK) ;
    }

    public void actionPerformed (ActionEvent e) {		
      //GinyUtils.selectAllEdges( org.hypergraphdb.viewer.getCurrentNetworkView() );
    	if(HGVKit.getCurrentNetwork()!=null)
    	  HGVKit.getCurrentNetwork().getFlagger().flagAllEdges();
    }//action performed
}
