// In NX sometimes user should know dimensions of 3D model, for example he needs to count how many
// models fit in some kind of container for transportation. You can create a framing shape around the model, then measure it 
// dimensions with ruler tool. Here is a faster way to calculate model size. 


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import javax.swing.JOptionPane;

import nxopen.Session;
import nxopen.SessionFactory;
import nxopen.UFSession;
import nxopen.BaseSession;
import nxopen.ListingWindow;
import nxopen.NXException;
import nxopen.Selection;
import nxopen.UI;
import nxopen.uf.UFConstants;

public class ModelDimensions {

	public static void main(String[] args) throws NXException, Exception
    {
        Session theSession = null ;
        UFSession ufSession = null ;
        Selection.SelectObjectData selectedObj = null;		
        ListingWindow infoWindow = null;

        try
        {
            theSession = (Session)SessionFactory.get("Session");
            ufSession = (UFSession)SessionFactory.get("UFSession");
            infoWindow = theSession.listingWindow();
            if (!infoWindow.isOpen()) infoWindow.open();

            selectedObj = select_body();	// user should select the model whose dimensions he need to know
            while( selectedObj != null )
            {
               
                double[] dimensions = ufSession.modlGeneral().askBoundingBox(selectedObj.object.tag());
		    // this method returns 6 double values -  [0] - minimum x value [1] - minimum y value [2] - minimum z value [3] - maximum x value [4] - maximum y value [5] - maximum z value
		    // based on this we can figure out size of desired box
                infoWindow.writeLine( "Размеры выбранного объекта:" );
                infoWindow.writeLine( " Длина объекта: " + new BigDecimal(dimensions[3]-dimensions[0]).setScale(1, RoundingMode.UP).doubleValue() + " мм");
                infoWindow.writeLine( " Ширина объекта: " + new BigDecimal(dimensions[4]-dimensions[1]).setScale(1, RoundingMode.UP).doubleValue()+ " мм");
                infoWindow.writeLine( " Высота объекта: " + new BigDecimal(dimensions[5]-dimensions[2]).setScale(1, RoundingMode.UP).doubleValue()+ " мм");
                
                selectedObj = select_body();
            }
                
        }
        catch (NXException ex)
        {
            new JOptionPane().showMessageDialog(null, "Код ошибки: " + ex.errorCode() +"\n"+           		
            										  "Описание: " + ufSession.UF().getFailMessage(ex.errorCode()), "Ошибка", 1);
        }
        catch (Exception ex)
        {
        	new JOptionPane().showMessageDialog(null, "Ошибка: " + ex.getMessage(), "Ошибка", 1);          
        }
    }
   
    public static Selection.SelectObjectData select_body() throws NXException, RemoteException
    {
        UI theUI = (UI)SessionFactory.get("UI");
        Selection.MaskTriple mask[] = { new Selection.MaskTriple(UFConstants.UF_solid_type, UFConstants.UF_solid_body_subtype, 0) };
        Selection.SelectObjectData selectedObj = theUI.selectionManager().selectObject("Select Body", "Select Body",
                   Selection.SelectionScope.WORK_PART, Selection.SelectionAction.CLEAR_AND_ENABLE_SPECIFIC, false, false, mask );

        if ( selectedObj.response == Selection.Response.OBJECT_SELECTED ||
                selectedObj.response == Selection.Response.OBJECT_SELECTED_BY_NAME )
        {
            return selectedObj;
        }
        else
            return null;
    }
    
    public static int getUnloadOption() 
    { 
        return BaseSession.LibraryUnloadOption.IMMEDIATELY; 
    }
}
