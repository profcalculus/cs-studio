/**
 * 
 */
package org.csstudio.sds.cosyrules.color;

import org.csstudio.sds.model.IRule;
import org.eclipse.core.runtime.IPath;
import static org.csstudio.sds.cosyrules.color.MaintenanceRulePreference.*;

/**
 * @author hrickens
 *
 */
public class MaintenanceRule implements IRule {

    private static IPath DEFAULT_PATH;
    private IPath _dispayPath = null;
    private String _preFileName = null;
//    private Map<String, IPath> _rTypUrlMap;

    /**
     * 
     */
    public MaintenanceRule() {
        DEFAULT_PATH = MAINTENANCE_UNKNOWN_DISPLAY_PATH.getValue();
        _dispayPath = MAINTENANCE_DISPLAY_PATH.getValue();
        _preFileName = MAINTENANCE_PRE_FILE_NAME.getValue();
    }
    
    /* (non-Javadoc)
     * @see org.csstudio.sds.model.IRule#evaluate(java.lang.Object[])
     */
    @Override
    public Object evaluate(Object[] arguments) {
        IPath iPath =  DEFAULT_PATH;
        if(arguments.length>0) {
            Object obj = arguments[0];
            if (obj instanceof String) {
                String rtyp = (String) obj;
                iPath= _dispayPath.append(_preFileName+rtyp+".css-sds");
            }
        }
        return iPath;
    }
    
    /* (non-Javadoc)
     * @see org.csstudio.sds.model.IRule#getDescription()
     */
    @Override
    public String getDescription() {
        return "Gibt das Maintenance Display f�r den �bergebenen RTYP wieder";
    }
    
}