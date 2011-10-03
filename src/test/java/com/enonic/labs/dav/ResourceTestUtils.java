package com.enonic.labs.dav;

import com.enonic.labs.dav.selector.ResourceFileSelectorInfo;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Nov 4, 2010
 * Time: 7:31:45 AM
 */
public class ResourceTestUtils
{

    public final static String FOLDER_BASENAME = "folder";
    public final static String FILE_BASENAME = "file";

    public static FileObject createFileSystem(String path) throws Exception
    {
        StandardFileSystemManager fileSystemManager;
        fileSystemManager = new StandardFileSystemManager();
        fileSystemManager.init();

        String source = path;

        FileObject sourceRoot = fileSystemManager.resolveFile(source);

        if (!sourceRoot.exists())
        {
            sourceRoot.createFolder();
        }

        return sourceRoot;
    }


    public static int createTree(FileObject root, int maxDepth, int numberOfFiles, int numberOfFolders) throws Exception
    {
        int numberOfCreatedFolders = 0;

        numberOfCreatedFolders = numberOfCreatedFolders + createFoldersWithFiles(root, maxDepth, 0, 1, numberOfFolders, numberOfFiles);
        numberOfCreatedFolders = numberOfCreatedFolders + createFoldersWithFiles(root, maxDepth, 0, 2, numberOfFolders, numberOfFiles);
        numberOfCreatedFolders = numberOfCreatedFolders + createFoldersWithFiles(root, maxDepth, 0, 3, numberOfFolders, numberOfFiles);
        numberOfCreatedFolders = numberOfCreatedFolders + createFoldersWithFiles(root, maxDepth, 0, 4, numberOfFolders, numberOfFiles);

        return numberOfCreatedFolders;
    }

    public static int createFoldersWithFiles(FileObject root, int maxDepth, int parentDepth, int folderNum, int numberOfFolders, int numberOfFiles) throws Exception
    {
        int numberOfCreatedObjects = 0;

        int thisDepth = parentDepth + 1;

        String folderName = FOLDER_BASENAME + thisDepth + "_" + folderNum;

        FileObject folder = root.resolveFile(folderName);
        if (!folder.exists())
        {
            folder.createFolder();
            numberOfCreatedObjects++;
        }


        if (thisDepth <= (maxDepth - 1))
        {
            for (int i = 1; i <= numberOfFolders; i++)
            {
                numberOfCreatedObjects = numberOfCreatedObjects + createFoldersWithFiles(folder, maxDepth, thisDepth, i, numberOfFolders, numberOfFiles);
            }
        }

        for (int i = 1; i <= numberOfFiles; i++)
        {
            createFile(folder, thisDepth + "_" + folderNum, i);
            numberOfCreatedObjects++;
        }

        return numberOfCreatedObjects;

    }

    public static FileObject createFile(FileObject root, String fileName) throws Exception
    {
        FileObject file = root.resolveFile(fileName);

        if (!file.exists())
        {
            file.createFile();

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            bout.write(fileName.getBytes());


            final OutputStream fileOutputStream = file.getContent().getOutputStream();

            try
            {
                fileOutputStream.write(createDataSize(20000).getBytes());
            }
            catch (IOException e)
            {
                System.out.println("Could not write to file");
            }
            finally
            {
                fileOutputStream.close();
            }


        }

        return file;
    }


    public static void createFile(FileObject root, String parentPostFix, int fileNum) throws Exception
    {
        String fileName = FILE_BASENAME + parentPostFix + "_" + fileNum;

        FileObject file = root.resolveFile(fileName);

        if (!file.exists())
        {
            file.createFile();

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            bout.write(fileName.getBytes());


            final OutputStream fileOutputStream = file.getContent().getOutputStream();

            try
            {
                fileOutputStream.write(createDataSize(20000).getBytes());
            }
            catch (IOException e)
            {
                System.out.println("Could not write to file");
            }
            finally
            {
                fileOutputStream.close();
            }


        }

    }


    public static FileObject createFolder(FileObject parent, String folderName) throws Exception
    {

        FileObject folder = parent.resolveFile(folderName);
        if (!folder.exists())
        {
            folder.createFolder();

        }

        return folder;
    }


    public static ResourceFileSelectorInfo createSelectorInfo(FileObject root, FileObject child, int depth)
    {
        final ResourceFileSelectorInfo info = new ResourceFileSelectorInfo();
        info.setBaseFolder(root);
        info.setDepth(depth);
        info.setFile(child);
        return info;
    }

    public static String createDataSize(int msgSize)
    {
        char[] chars = new char[msgSize];
        Arrays.fill(chars, RandomStringUtils.random(1).charAt(0));

        return new String(chars);

        /*
        // Java chars are 2 bytes
        msgSize = (msgSize / 2) / fileName.length();
        msgSize = msgSize * 1024;
        StringBuilder sb = new StringBuilder(msgSize);
        for (int i = 0; i < msgSize; i++)
        {
            sb.append(fileName);
        }

        System.out.println(sb.toString());
        System.out.println("\n\n");

        return sb.toString();
        */
    }


}
