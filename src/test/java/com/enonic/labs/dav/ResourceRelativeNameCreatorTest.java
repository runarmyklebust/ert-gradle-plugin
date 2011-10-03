package com.enonic.labs.dav;

import com.enonic.labs.dav.utils.ResourceRelativeNameCreator;
import junit.framework.TestCase;
import org.apache.commons.vfs2.FileObject;
import org.junit.Before;
import org.junit.Test;


/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Dec 3, 2010
 * Time: 2:28:23 PM
 */
public class ResourceRelativeNameCreatorTest extends TestCase
{

    ResourceRelativeNameCreator relativeNameCreator;
    FileObject root;

    @Before
    public void setUp() throws Exception
    {
        root = ResourceTestUtils.createFileSystem("ram://ram/test/root");

        relativeNameCreator = new ResourceRelativeNameCreator(root);
    }

    @Test
    public void testBasePath() throws Exception
    {
        FileObject folder = ResourceTestUtils.createFolder(root, "testdir");
        FileObject file = ResourceTestUtils.createFile(folder, "myfile");

        assertEquals("/testdir/myfile", relativeNameCreator.createRelativePath(file));

    }
}
