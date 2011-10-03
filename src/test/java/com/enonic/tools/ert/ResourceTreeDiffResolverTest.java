package com.enonic.tools.ert;

import org.apache.commons.vfs2.FileObject;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

import com.enonic.tools.ert.client.ResourceLocation;
import com.enonic.tools.ert.resourcetree.ResourceTree;
import com.enonic.tools.ert.resourcetree.ResourceTreeBuilder;
import com.enonic.tools.ert.resourcetree.ResourceTreeCompareResult;
import com.enonic.tools.ert.resourcetree.ResourceTreeComparer;
import com.enonic.tools.ert.selector.DefaultResourceSelector;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Dec 10, 2010
 * Time: 12:27:12 PM
 */
public class ResourceTreeDiffResolverTest
    extends TestCase
{
    ResourceLocation sourceLocation = new ResourceLocation( "test1", "ram://ram/test/root" );

    ResourceLocation targetLocation = new ResourceLocation( "test2", "ram://ram2/test/root" );


    @Before
    public void setUp()
        throws Exception
    {
        sourceLocation.setRoot( ResourceTestUtils.createFileSystem( "ram://ram/test/root" ) );
        targetLocation.setRoot( ResourceTestUtils.createFileSystem( "ram://ram2/test/root" ) );

        FileObject source = sourceLocation.getRoot();
        ResourceTestUtils.createFile( source, "source1.xsl" );
        FileObject common = ResourceTestUtils.createFile( source, "common1.xsl" );

        FileObject target = targetLocation.getRoot();
        target.copyFrom( source, new DefaultResourceSelector() );

        //ResourceTestUtils.createFile(source, "source2.xsl");

    }


    @Test
    public void testDiff()
        throws Exception
    {
        ResourceTreeBuilder sourceTreeBuilder = new ResourceTreeBuilder( sourceLocation.getRoot(), new DefaultResourceSelector() );
        sourceTreeBuilder.setDiffDetailLevel( DiffDetailLevel.CONTENT );
        ResourceTree sourceTree = sourceTreeBuilder.getResourceTree();

        ResourceTreeBuilder targetTreeBuider = new ResourceTreeBuilder( targetLocation.getRoot(), new DefaultResourceSelector() );
        targetTreeBuider.setDiffDetailLevel( DiffDetailLevel.CONTENT );
        ResourceTree targetTree = targetTreeBuider.getResourceTree();

        ResourceTreeCompareResult compareResult = ResourceTreeComparer.compareTrees( sourceTree, targetTree );

        assertTrue( compareResult.noDifferences() );

    }
}
