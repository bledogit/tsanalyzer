/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tsnanalyzer.video.transport;

/**
 *
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public class Pcr {
    protected  long pcr = -1;

    public long getPcr() {
        return pcr;
    }

    public void setPcr(long pcr) {
        this.pcr = pcr;
    }
}
