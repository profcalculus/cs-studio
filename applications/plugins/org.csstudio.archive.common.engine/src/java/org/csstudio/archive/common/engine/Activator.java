/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveEngineConfigServiceTracker;
import org.csstudio.archive.common.service.ArchiveWriterServiceTracker;
import org.csstudio.archive.common.service.IArchiveEngineConfigService;
import org.csstudio.archive.common.service.IArchiveWriterService;
import org.csstudio.platform.service.osgi.OsgiServiceUnavailableException;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


/** Plugin Activator
 *  @author Kay Kasemir
 *  @author Bastian Knerr
 */
public class Activator extends Plugin {
    /**
     * The id of this Java plug-in (value <code>{@value}</code> as defined in MANIFEST.MF.
     */
    public static final String PLUGIN_ID = "org.csstudio.archive.common.engine"; //$NON-NLS-1$

    /** The shared instance */
    private static Activator INSTANCE;

    // FIXME (bknerr) : find out about proper dependency injection for osgi eclipse rcp
    private ArchiveEngineConfigServiceTracker _archiveEngineConfigServiceTracker;
    private ArchiveWriterServiceTracker _archiveWriterServiceTracker;

    /**
     * Don't instantiate.
     * Called by framework.
     */
    public Activator() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Activator " + PLUGIN_ID + " does already exist.");
        }
        INSTANCE = this; // Antipattern is required by the framework!
    }

    /** @return the shared instance */
    @Nonnull
    public static Activator getDefault() {
        return INSTANCE;
    }

    /** {@inheritDoc} */
    @Override
    public void start(@Nonnull final BundleContext context) throws Exception {
        super.start(context);

        _archiveEngineConfigServiceTracker = new ArchiveEngineConfigServiceTracker(context);
        _archiveEngineConfigServiceTracker.open();

        _archiveWriterServiceTracker = new ArchiveWriterServiceTracker(context);
        _archiveWriterServiceTracker.open();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(@Nonnull final BundleContext context) throws Exception {

        if (_archiveEngineConfigServiceTracker != null) {
            _archiveEngineConfigServiceTracker.close();
        }

        if (_archiveWriterServiceTracker != null) {
            _archiveWriterServiceTracker.close();
        }

        super.stop(context);
    }


    /**
     * Returns the archive engine config service from the service tracker.
     * @return the archive service or <code>null</code> if not available.
     * @throws OsgiServiceUnavailableException
     */
    @Nonnull
    public IArchiveEngineConfigService getArchiveEngineConfigService() throws OsgiServiceUnavailableException
    {
        final IArchiveEngineConfigService service =
            (IArchiveEngineConfigService) _archiveEngineConfigServiceTracker.getService();
        if (service == null) {
            throw new OsgiServiceUnavailableException("Archive engine config service unavailable.");
        }
        return service;
    }

    /**
     * Returns the archive writer service from the service tracker.
     * @return the archive service or <code>null</code> if not available.
     * @throws OsgiServiceUnavailableException
     */
    @Nonnull
    public IArchiveWriterService getArchiveWriterService() throws OsgiServiceUnavailableException
    {
        final IArchiveWriterService service =
            (IArchiveWriterService) _archiveWriterServiceTracker.getService();
        if (service == null) {
            throw new OsgiServiceUnavailableException("Archive writer service unavailable.");
        }
        return service;
    }
}