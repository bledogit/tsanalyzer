/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.me.tsanalyzer.video.util;

/**
 *
 * @author Jose Mortensen <jose.mortensen@nielsen.com>
 */
public class TableBuilder {

    public static StringBuilder BuildTable(byte[] data, int cols, String lead) {
        StringBuilder sb = new StringBuilder();
        sb.append(lead);
        for (int i = 0; i < data.length; i++) {
            sb.append(String.format("%1$02x ", data[i]&0xff));
            if ((i % cols) == (cols-1)) {
                sb.append("\n");
                sb.append(lead);
            }
        }
        return sb;
    }
}
