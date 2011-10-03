package com.enonic.labs.dav;

import com.enonic.labs.dav.selector.FileNamePattern;
import com.enonic.labs.dav.selector.ResourceFileSelectorInfo;
import com.enonic.labs.dav.selector.ResourceSelector;
import junit.framework.TestCase;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Dec 3, 2010
 * Time: 9:55:00 AM
 */
public class ResourceSelectorTest extends TestCase
{

    FileObject source;
    FileObject target;

    ResourceSelector resourceSelector;

    @Before
    public void setUp() throws Exception
    {

        source = ResourceTestUtils.createFileSystem("ram://ram/test/root");
        target = ResourceTestUtils.createFileSystem("ram://ram2/test/root");

        resourceSelector = new ResourceSelector();

        resourceSelector.setMinDepth(0);

        ResourceTestUtils.createFile(source, "test.xsl");
        ResourceTestUtils.createFile(source, "test.js");
        ResourceTestUtils.createFile(source, "test.css");
        ResourceTestUtils.createFile(source, "test.bat");
        ResourceTestUtils.createFile(source, "test.exe");
        ResourceTestUtils.createFile(source, ".test.xsl");

    }

    @Test
    public void testIncludeFilter() throws Exception
    {
        resourceSelector.appendIncludePatterns(FileNamePattern.XSLT_FILES, FileNamePattern.CSS_FILES, FileNamePattern.JS_FILES);
        resourceSelector.appendExcludePatterns(FileNamePattern.HIDDEN_FILES);
        resourceSelector.setDefaultIncludeFiles(false);

        assertIncludeFile("test.xsl");
        assertIncludeFile("test.js");
        assertIncludeFile("test.css");

        assertExcludeFile("test.exe");
        assertExcludeFile(".test.xsl");

    }

    @Test
    public void testExcludeFilter() throws Exception
    {
        assertTrue(resourceSelector.getIncludePatterns().size() == 0);

        resourceSelector.appendExcludePatterns(FileNamePattern.HIDDEN_FILES);

        assertIncludeFile("test.xsl");
        assertIncludeFile("test.js");
        assertIncludeFile("test.css");
        assertIncludeFile("test.exe");

        assertExcludeFile(".test.xsl");
    }

    private void assertExcludeFile(String fileName)
            throws Exception
    {
        final ResourceFileSelectorInfo fileInfo = getResouceFileSelectorInfo(fileName);

        assertFalse("This file should be excluded: " + fileName, resourceSelector.includeFile(fileInfo));
    }


    private void assertIncludeFile(String fileName)
            throws Exception
    {
        final ResourceFileSelectorInfo fileInfo = getResouceFileSelectorInfo(fileName);

        assertTrue("This file should be included: " + fileName, resourceSelector.includeFile(fileInfo));
    }

    private ResourceFileSelectorInfo getResouceFileSelectorInfo(String fileName)
            throws FileSystemException
    {
        final FileObject file = source.resolveFile(fileName);

        if (!file.exists())
        {
            fail("File doesnt exist");
        }

        assertNotNull(file);
        final ResourceFileSelectorInfo fileInfo = ResourceTestUtils.createSelectorInfo(source, file, 0);
        return fileInfo;
    }
}
