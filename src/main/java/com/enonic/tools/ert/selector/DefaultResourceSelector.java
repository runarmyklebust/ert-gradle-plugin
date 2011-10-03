package com.enonic.tools.ert.selector;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Dec 3, 2010
 * Time: 12:13:33 PM
 */
public class DefaultResourceSelector
    extends ResourceSelector
{

    public DefaultResourceSelector()
    {
        appendExcludePatterns( FileNamePattern.HIDDEN_FILES, FileNamePattern.SVN_FILES );
        setDefaultIncludeFiles( true );
    }
}
