package com.enonic.labs.dav.resourcetree;

import java.util.Map;

import com.google.common.collect.MapDifference;

import com.enonic.labs.dav.client.ResourceLocation;
import com.enonic.labs.dav.selector.ResourceFileTools;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Dec 10, 2010
 * Time: 12:20:17 PM
 */
public class ResourceTreeDiffResolver
{
    private boolean deleteMissingFromTarget = false;

    private boolean printOnly = false;

    public ResourceTreeDiffResolver( boolean deleteMissingFromTarget )
    {
        this.deleteMissingFromTarget = deleteMissingFromTarget;
    }

    public ResourceTreeDiffResolver( boolean deleteMissingFromTarget, boolean printOnly )
    {
        this.deleteMissingFromTarget = deleteMissingFromTarget;
        this.printOnly = printOnly;
    }

    public void resolveTreeDifferences( ResourceTreeCompareResult compareResult, ResourceLocation source, ResourceLocation target )
        throws Exception
    {
        addMissingFileObjects( source, target, compareResult );

        if ( deleteMissingFromTarget )
        {
            deleteMissingFiles( compareResult, target );
        }
        else if ( compareResult.getEntriesOnlyInTarget().size() > 0 )
        {
            System.out.println( "deleteMissingOnSync=false, no deletion attempted" );
        }

        overWriteChangedFiles( compareResult, source, target );
    }

    private void overWriteChangedFiles( ResourceTreeCompareResult compareResult, ResourceLocation source, ResourceLocation target )
        throws Exception
    {
        Map<String, MapDifference.ValueDifference<String>> differences = compareResult.getDiffMap().entriesDiffering();

        for ( String fileName : differences.keySet() )
        {
            System.out.println( "Overwrite changed file: " + fileName );

            if ( !printOnly )
            {
                ResourceFileTools.copyFile( source.getRoot(), fileName, target.getRoot() );
            }
        }
    }

    private void deleteMissingFiles( ResourceTreeCompareResult compareResult, ResourceLocation target )
        throws Exception
    {
        Map<String, String> targetOnlyEntries = compareResult.getEntriesOnlyInTarget();

        for ( String targetRelativeName : targetOnlyEntries.keySet() )
        {
            System.out.println( "Delete from " + target.getName() + ": " + targetRelativeName );

            if ( !printOnly )
            {
                ResourceFileTools.deleteFile( target.getRoot(), targetRelativeName, true );
            }
        }
    }

    private void addMissingFileObjects( ResourceLocation source, ResourceLocation target, ResourceTreeCompareResult compareResult )
        throws Exception
    {
        Map<String, String> sourceOnlyEntries = compareResult.getDiffMap().entriesOnlyOnLeft();

        for ( String sourceRelativeName : sourceOnlyEntries.keySet() )
        {
            System.out.println( "Adding files from source missing on target: " + sourceRelativeName );

            if ( !printOnly )
            {
                ResourceFileTools.copyFile( source.getRoot(), sourceRelativeName, target.getRoot() );
            }

        }
    }
}
