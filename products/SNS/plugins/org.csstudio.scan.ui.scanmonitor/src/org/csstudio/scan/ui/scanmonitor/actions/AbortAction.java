/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 * 
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.ui.scanmonitor.actions;

import org.csstudio.scan.server.ScanInfo;
import org.csstudio.scan.ui.scanmonitor.Messages;
import org.csstudio.scan.client.ScanInfoModel;

/** Action that aborts a scan
 *  @author Kay Kasemir
 */
public class AbortAction extends AbstractGUIAction
{
    /** Initialize
     *  @param model
     *  @param info
     */
    public AbortAction(final ScanInfoModel model, final ScanInfo info)
    {
        super(model, info, Messages.Abort, "icons/abort.gif"); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    @Override
    protected void runModelAction() throws Exception
    {
        model.abort(info);
    }
}
