/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.me.tsanalyzer.ui;

import com.me.tsnanalyzer.video.transport.Descriptor;
import com.me.tsnanalyzer.video.transport.Pmt;
import com.me.tsnanalyzer.video.transport.Stream;
import java.net.URL;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public class TsStreamNode extends DefaultMutableTreeNode {

    static final String INFO = "resources/info.png";
    static final String AUDIO = "resources/audio.png";
    static final String VIDEO = "resources/video.png";
    static final String OTHER = "resources/other.png";
    static final String STREAM = "resources/stream.png";
    static final String LOCK = "resources/lock.png";
    static final String TRANSPORT = "resources/transport.png";
    String type;
    Object obj;

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    TsStreamNode(String type, Object obj) {
        if ((obj instanceof Pmt) && (obj != null)) 
            setUserObject("Program " + ((Pmt)obj).getProgramNumber());
        else if ((obj instanceof Stream) && (obj != null)) 
            setUserObject(((Stream)obj).getStreamType());
        else if ((obj instanceof Descriptor) && (obj != null)) 
            setUserObject(((Descriptor)obj).getDescriptorInfo());
        else  
            setUserObject(obj);
            
        this.type = type;
        this.obj = obj;
    }

    public javax.swing.Icon getIcon() {
        URL url = getClass().getResource(type);
        javax.swing.Icon icon = new javax.swing.ImageIcon(url);
        return icon;
    }
}
