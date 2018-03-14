/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.me.tsanalyzer.ui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author mortjo01
 */
public class TsSource {
    File file = null;
    InputStream is = null;
    boolean m3u8 = false;
    
    public TsSource (InputStream is) {
        this.is = is;
    }
    
    public TsSource (File file) throws FileNotFoundException {
        this.file = file;
        is = new FileInputStream(file);
    }    
    
    public TsSource (URL url) throws IOException {
        if (url.getFile().contains("m3u8")) {
            m3u8 = true;
        }
        
        
        this.is = url.openConnection().getInputStream();
    }
    
    public int read(byte[] buffer) throws IOException {
        return is.read(buffer);
    }
    
    public long length() {
        long len = 0;
        
        if (file != null)
            len = file.length();
        else if (is != null)
            len = Long.MAX_VALUE;
        return len;
    }
}
