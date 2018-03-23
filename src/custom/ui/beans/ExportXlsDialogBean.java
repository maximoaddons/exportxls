package custom.ui.beans;

import java.rmi.RemoteException;
import javax.servlet.http.HttpServletRequest;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;


public class ExportXlsDialogBean extends DataBean {

    public ExportXlsDialogBean() {
    }

    @Override
    protected void initialize() throws MXException, RemoteException {
        super.initialize();
    }
    
    public int export() throws MXException, RemoteException {
        HttpServletRequest request = clientSession.getRequest();
        String url = (new StringBuilder()).append(clientSession.getMaximoBaseURL()).append(request.getContextPath()).append("/exportxls/").toString();
        url = (new StringBuilder()).append(url).append("?").append(clientSession.getUISessionUrlParameter()).toString();
        this.app.openURL(url, true, "xls_window", "height=600,width=800,alwaysOnTop=yes,status=1,toolbar=no,menubar=1,resizable=1,location=0");
        return EVENT_HANDLED;
    }

}
