package com.klugesoftware.farmainvoice.utility;

import java.io.File;
import java.io.FileFilter;

public class XmlFileFilter implements FileFilter {
    @Override
    public boolean accept(File file) {
        String extension = "xml";
        if(file.isFile() && file.getName().toLowerCase().endsWith(extension))
            return true;
        else
            return false;
    }
}
