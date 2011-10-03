package com.enonic.labs.dav;

import com.enonic.labs.dav.resourcetree.ResourceTree;
import com.enonic.labs.dav.resourcetree.ResourceTreeBuilder;
import com.enonic.labs.dav.selector.ResourceSelector;
import org.apache.commons.vfs2.FileObject;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Nov 4, 2010
 * Time: 7:31:21 AM
 */
public class ResourceTreeBuilderTest
{

    FileObject source;
    FileObject target;


    @Before
    public void setUp() throws Exception
    {
        source = ResourceTestUtils.createFileSystem("ram://ram/test/root");
        target = ResourceTestUtils.createFileSystem("ram://ram2/test/root");
    }

    @Test
    public void testCreateTree() throws Exception
    {

        int createdNodes = ResourceTestUtils.createTree(source, 3, 3, 3);

        ResourceTreeBuilder treeBuilder = new ResourceTreeBuilder(source, new ResourceSelector());
        treeBuilder.setDiffDetailLevel(DiffDetailLevel.CONTENT);

        ResourceTree resourceTree = treeBuilder.getResourceTree();

        assertEquals(createdNodes, resourceTree.size());
    }

}
