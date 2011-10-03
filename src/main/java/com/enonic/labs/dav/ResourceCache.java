package com.enonic.labs.dav;


import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;

import com.enonic.labs.dav.client.ResourceLocation;
import com.enonic.labs.dav.resourcetree.ResourceTree;
import com.enonic.labs.dav.resourcetree.ResourceTreeBuilder;
import com.enonic.labs.dav.resourcetree.ResourceTreeStorage;
import com.enonic.labs.dav.selector.DefaultResourceSelector;
import com.enonic.labs.dav.selector.ResourceSelector;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Dec 4, 2010
 * Time: 10:31:27 PM
 */
public class ResourceCache
{
    FileObject cacheRoot;

    FileSystemManager fileSystemManager;

    public ResourceCache( FileObject cacheRoot, FileSystemManager fileSystemManager )
    {
        this.cacheRoot = cacheRoot;
        this.fileSystemManager = fileSystemManager;
    }

    public ResourceTree getResourceTree( ResourceLocation location )
        throws Exception
    {
        FileObject locationRoot = location.getRoot();

        final String resourceTreeFileName = getResourceTreeFileName( locationRoot );

        FileObject cachedTree = cacheRoot.resolveFile( resourceTreeFileName );

        if ( cachedTree.exists() )
        {
            final long cacheTimestamp = cachedTree.getContent().getLastModifiedTime();

            if ( location.isForceCacheRefresh() )
            {
                doNukeCache( "Cache nuked, forced ", resourceTreeFileName );
            }
            else if ( location.cacheExpired( cacheTimestamp ) )
            {
                doNukeCache( "Cache nuked, expired", resourceTreeFileName );
            }
            else
            {
                System.out.println( "Returning cached tree for " + resourceTreeFileName );
                return ResourceTreeStorage.getResourceTree( cachedTree.getContent() );
            }
        }

        System.out.println( "getResourceTree cachedTreeName: " + cachedTree.getName().getPath() );

        ResourceSelector resourceSelector = new DefaultResourceSelector();

        System.out.println( "Fetching resourceTree" );
        ResourceTree resourceTree = doCreateResourceTree( locationRoot, resourceSelector );

        System.out.println( "Storing resourcetree at cache-file: " + resourceTreeFileName );
        ResourceTreeStorage.storeResourceTree( cacheRoot, resourceTreeFileName, resourceTree );

        return resourceTree;
    }

    public void nukeCache( ResourceLocation location )
        throws Exception
    {
        FileObject locationRoot = location.getRoot();

        final String resourceTreeFileName = getResourceTreeFileName( locationRoot );

        doNukeCache( "Cache nuked manually", resourceTreeFileName );
    }

    private String getResourceTreeFileName( FileObject locationRoot )
    {
        return cacheRoot.getName().getPath() + locationRoot.getName().getPath() + "-cache.cache";
    }


    private void doNukeCache( String message, String resourceTreeFileName )
        throws Exception
    {
        System.out.println( message );
        ResourceTreeStorage.deleteResourceTree( cacheRoot, resourceTreeFileName );
    }

    private ResourceTree doCreateResourceTree( FileObject root, ResourceSelector resourceSelector )
        throws Exception
    {
        ResourceTreeBuilder resourceTreeBuilder = new ResourceTreeBuilder( root, resourceSelector );
        resourceTreeBuilder.setDiffDetailLevel( DiffDetailLevel.SIZE );
        ResourceTree resourceTree = resourceTreeBuilder.getResourceTree();

        return resourceTree;
    }

    public void updateCache( ResourceLocation location, ResourceTree resourceTree )
        throws Exception
    {
        final String resourceTreeFileName = getResourceTreeFileName( location.getRoot() );

        System.out.println( "Updating cache for: " + resourceTreeFileName );

        ResourceTreeStorage.storeResourceTree( cacheRoot, resourceTreeFileName, resourceTree );
    }
}