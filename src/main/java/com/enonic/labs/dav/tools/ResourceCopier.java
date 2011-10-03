package com.enonic.labs.dav.tools;


import com.enonic.labs.dav.ProgressCounter;
import com.enonic.labs.dav.selector.DefaultResourceSelector;
import com.enonic.labs.dav.selector.ResourceSelector;
import com.enonic.labs.dav.utils.ResourceFileUtils;
import org.apache.commons.vfs2.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Nov 1, 2010
 * Time: 10:55:28 PM
 */
public class ResourceCopier
{
    private static final Logger LOG = Logger.getLogger(ResourceCopier.class.getName());

    boolean tryKeepLastModifiedTime = false;

    public ResourceCopier(boolean tryKeepLastModifiedTime)
    {
        this.tryKeepLastModifiedTime = tryKeepLastModifiedTime;
    }

    public void copy(FileObject source, FileObject target) throws Exception
    {
        DefaultResourceSelector selector = new DefaultResourceSelector();
        selector.setMinDepth(1);

        copyFrom(source, target, selector);
    }

    public void copy(FileObject source, FileObject target, ResourceSelector resourceSelector) throws Exception
    {
        copyFrom(source, target, resourceSelector);
    }

    public void setTryKeepLastModifiedTime(boolean tryKeepLastModifiedTime)
    {
        this.tryKeepLastModifiedTime = tryKeepLastModifiedTime;
    }

    /**
     * Copies files from source to target.
     */
    public void copyFrom(final FileObject source, FileObject target, final FileSelector selector)
            throws FileSystemException
    {
        if (!source.exists())
        {
            throw new FileSystemException("vfs.provider/copy-missing-file.error", source);
        }
        if (!target.isWriteable())
        {
            throw new FileSystemException("vfs.provider/copy-read-only.error", new Object[]{source.getType(), source.getName(), target}, null);
        }

        LOG.info("Finding files to copy from source: " + source.getName().toString());

        // Locate the files to copy across
        final ArrayList files = new ArrayList();
        source.findFiles(selector, false, files);

        LOG.info("Found " + files.size() + " to copy...");

        boolean doKeepLastModifiedTimeFiles = false;

        if (tryKeepLastModifiedTime)
        {
            if (target.getFileSystem().hasCapability(Capability.SET_LAST_MODIFIED_FILE))
            {
                doKeepLastModifiedTimeFiles = true;
            }
            else
            {
                System.out.println("Warning - Not able to retaing 'LAST_MODIFIED' time on files: FileSystem not capable");
            }
        }

        // Copy everything across
        final int count = files.size();

        ProgressCounter progressCounter = new ProgressCounter(100, count);
        progressCounter.setOperation("Copied");
        progressCounter.setShowAsPercent(true);

        for (int i = 0; i < count; i++)
        {
            progressCounter.addElement();

            final FileObject srcFile = (FileObject) files.get(i);


            final String relativePath = ResourceFileUtils.getRelativePath(source, srcFile);
            final FileObject destFile = target.resolveFile(relativePath, NameScope.DESCENDENT_OR_SELF);

            if (destFile.exists())  // && destFile.getType() != srcFile.getType())
            {
                destFile.delete(Selectors.SELECT_ALL);
            }

            // Copy across
            try
            {
                if (srcFile.getType().hasContent())
                {
                    FileUtil.copyContent(srcFile, destFile);

                    final long sourceLastModified = srcFile.getContent().getLastModifiedTime();

                    if (doKeepLastModifiedTimeFiles && sourceLastModified > 0)
                    {
                        destFile.getContent().setLastModifiedTime(sourceLastModified);
                    }

                }
                else if (srcFile.getType().hasChildren())
                {
                    destFile.createFolder();
                }
            }
            catch (final IOException e)
            {
                throw new FileSystemException("vfs.provider/copy-file.error", new Object[]{srcFile, destFile}, e);
            }
        }

        System.out.println("Copied: " + count + " objects to " + target.getName().getURI());

    }
}

