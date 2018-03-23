package custom.ui.beans;

import java.rmi.RemoteException;
import psdi.mbo.MboConstants;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.beans.common.RelationshipTreeBean;
import psdi.webclient.controls.TreeNode;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.WebClientEvent;


public class ExportRelTreeBean extends RelationshipTreeBean {

    private final String dialogId = "exportxls";
    DataBean dialog;
    
    public ExportRelTreeBean() {
        super();
    }

    @Override
    protected void initialize() throws MXException, RemoteException {
        dialog = app.getDataBean(dialogId);
        this.getMboSet().setOwner(dialog.getParent().getMbo());
        super.initialize();
    }

    @Override
    public int selectrecord() throws MXException, RemoteException {
        WebClientEvent event = sessionContext.getCurrentEvent();
        DataBean attrTable = this.clientSession.findControl("table_exportattr").getDataBean();
        MboSetRemote attrSet = attrTable.getMboSet(); 
        TreeNode selectedNode = (TreeNode) event.getSourceControlInstance();
        // get selected node attribute
        String attrToBeAdded = selectedNode.getNodeLabel().split(" ")[0];
        attrToBeAdded = formAttr(selectedNode, attrToBeAdded);
        MboRemote attrNew;
        if (attrSet.getMbo(0).getString("textparam1").isEmpty()) {
            attrNew = attrSet.getMbo(0);
        } else {
            attrNew = attrSet.addAtEnd(MboConstants.NOACCESSCHECK);
        }
        attrNew.setValue("textparam1", attrToBeAdded, MboConstants.NOACCESSCHECK);
        MboRemote attrMbo = null;
        for (int i = 0; (attrMbo = attrSet.getMbo(i)) != null; i++) {
            if (attrMbo.toBeDeleted()) {
                attrSet.remove(attrMbo);
            }
        }
        attrTable.refreshTable();
        return 1;
    }
    
    private String formAttr(TreeNode treeN, String attr) {
        // if node is under relationship node, get relationship name and add it to the attribute
        if (treeN.getParentInstance().getParentInstance() instanceof TreeNode) {
            TreeNode parentNode = (TreeNode) treeN.getParentInstance();
            String rel = parentNode.getNodeLabel();
            attr = rel + "." + attr;
            attr = formAttr(parentNode, attr);
        }
        return attr;
    }

    @Override
    public String getTreeControlObjectName() throws RemoteException, MXException {
        // DataBean dialog = this.getParent();  // res: dialog is null
        String tableMboName = dialog.getParent().getMbo().getName();
        return tableMboName;
    }

}
