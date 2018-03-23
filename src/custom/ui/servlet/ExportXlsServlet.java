package custom.ui.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.util.MXException;
import psdi.webclient.system.beans.DataBean;
import psdi.webclient.system.controller.AppInstance;
import psdi.webclient.system.runtime.WebClientRuntime;
import psdi.webclient.system.session.WebClientSession;


public class ExportXlsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public ExportXlsServlet() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=export.xls");
        WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
        WritableSheet s = w.createSheet("Sheet1", 0);
        try {
            WebClientRuntime wcr = WebClientRuntime.getWebClientRuntime();
            WebClientSession wcs = wcr.getWebClientSession(request);
            AppInstance app = wcs.getCurrentApp();
            DataBean dialogBean = app.getDataBean("exportxls");
            DataBean attrTable = wcs.findControl("table_exportattr").getDataBean();
            MboSetRemote attrSet = attrTable.getMboSet(); 
            MboRemote attrMboDel = null;
            for (int i = 0; (attrMboDel = attrSet.getMbo(i)) != null; i++) {
                if (attrMboDel.toBeDeleted()) {
                    attrSet.remove(attrMboDel);
                }
            }
            MboSetRemote tableSet = dialogBean.getParent().getMboSet();
            if (!tableSet.isEmpty() && !attrSet.isEmpty() && !attrSet.getMbo(0).getString("textparam1").isEmpty()) {
                MboRemote attrMbo = null;
                String[] attrArray = new String[attrSet.getSize()];
                // write titles
                for (int i = 0; (attrMbo = attrSet.getMbo(i)) != null; i++) {
                    attrArray[i] = attrMbo.getString("textparam1");
                    writeToCell(s, i, 0, attrArray[i]);
                }
                // write data
                MboRemote currMbo = null;
                for (int y = 0; (currMbo = tableSet.getMbo(y)) != null; y++) {
                    for (int x = 0; x < attrArray.length; x++) {
                        writeToCell(s, x, y + 1, currMbo.getString(attrArray[x]));
                    }
                }
            }
            w.write();
            w.close();
        } catch (WriteException e) {
            System.out.println(e);
        } catch (MXException e) {
            System.out.println(e);
        }
    }

    private void writeToCell(WritableSheet s, int x, int y, String data) throws MXException, WriteException {
        Label label = new Label(x, y, data.trim());
        s.addCell(label);
    }
    
}
