package com.enonic.labs.dav;

import org.apache.commons.vfs2.FileObject;
import org.junit.Before;
import org.junit.Test;

import com.enonic.labs.dav.exception.DeleteResourceException;
import com.enonic.labs.dav.exception.ResourceNotFoundException;
import com.enonic.labs.dav.selector.ResourceFileTools;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Dec 10, 2010
 * Time: 10:23:40 PM
 */
public class ResourceFileToolsTest
{
    FileObject source;

    FileObject target;

    @Before
    public void setUp()
        throws Exception
    {

        source = ResourceTestUtils.createFileSystem( "ram://ram/test/root" );
        target = ResourceTestUtils.createFileSystem( "ram://ram2/test/root" );

        ResourceTestUtils.createFile( source, "file1.xsl" );
        FileObject folder1 = ResourceTestUtils.createFolder( source, "folder1" );
        ResourceTestUtils.createFile( folder1, "folder1_file1.xsl" );
        ResourceTestUtils.createFile( folder1, "folder1_file2.xsl" );
        FileObject folder1_1 = ResourceTestUtils.createFolder( folder1, "folder1_1" );
        ResourceTestUtils.createFile( folder1_1, "folder1_1_file1.xsl" );
    }


    @Test
    public void testCopyFile()
        throws Exception
    {
        final String sourceFileName = "folder1/folder1_file1.xsl";

        ResourceFileTools.copyFile( source, sourceFileName, target );

        FileObject copiedFile = target.resolveFile( "/ram2/test/root/folder1/folder1_file1.xsl" );

        assertTrue( copiedFile.exists() );
        assertTrue( "Content should be of equal size",
                    copiedFile.getContent().getSize() == source.resolveFile( sourceFileName ).getContent().getSize() );
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testCopyFromFile_nonExistingFile()
        throws Exception
    {
        ResourceFileTools.copyFile( source, "folder1/folder1_file1_dummy.xsl", target );
    }

    @Test
    public void testCopyFolder()
        throws Exception
    {
        final String sourceFileName = "folder1";

        ResourceFileTools.copyFile( source, sourceFileName, target );

        FileObject copiedFolder = target.resolveFile( "/ram2/test/root/folder1" );
        assertTrue( copiedFolder.exists() );
        assertEquals( copiedFolder.getChildren().length, 3 );
    }

    @Test
    public void testDeleteFolder()
        throws Exception
    {
        FileObject folder1 = source.resolveFile( "folder1" );
        assertTrue( folder1.exists() );

        boolean result = ResourceFileTools.deleteFile( folder1, "folder1_1", true );

        assertTrue( result );
        assertEquals( folder1.getChildren().length, 2 );
    }

    @Test(expected = DeleteResourceException.class)
    public void testDeleteFolderNotAllowed()
        throws Exception
    {
        FileObject folder1 = source.resolveFile( "folder1" );
        assertTrue( folder1.exists() );

        ResourceFileTools.deleteFile( folder1, "folder1_1", false );
    }

}
