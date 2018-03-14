/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.me.tsanalyzer.video.transport;

/**
 *
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public class PsiTable {

    protected  int table_id;                       // 8 uimsbf
    protected  boolean section_syntax_indicator;   // 1 bslbf
    protected  int zero;                           // '0' 1 bslbf
    protected  int reserved1;                      //reserved 2 bslbf
    protected  int section_length;                 // 12 uimsbf
    protected  int reserved2;                      // reserved 18 bslbf
    protected  int version_number;                 // 5 uimsbf
    protected  int current_next_indicator;         // 1 bslbf
    protected  int section_number;                 // 8 uimsbf
    protected  int last_section_number;            // 8 uimsbf
    protected  int ptr = 0;

    public int getLastSectionNumber() {
        return last_section_number;
    }

    public int getSectionLength() {
        return section_length;
    }

    public int getSectionNumber() {
        return section_number;
    }

    public int getTableId() {
        return table_id;
    }

    public int getVersionNumber() {
        return version_number;
    }
    
    
    
    
    public PsiTable(byte[] data) {
        this.process(data);
    }
    
    public PsiTable() {
    };
    
    public void process (byte[] data) {
        ptr = 0;
        ptr += 1+(data[ptr++]& 0xff);   // pointer field + padding
        table_id = data[ptr++] & 0xff;
        section_syntax_indicator = (data[ptr] & 0x80) != 0;
        zero = (data[ptr] & 0x40);
        reserved1 = (data[ptr] & 0x30) >> 4;
        section_length = (data[ptr++] & 0xf) << 8;
        section_length += (data[ptr++] & 0xff);
        reserved2 = (data[ptr++] & 0xff) << 8;
        reserved2 += data[ptr++] & 0xff;
        reserved2 <<= 2;
        reserved2 += (data[ptr] & 0xc0) >> 6;
        version_number = (data[ptr] & 0x3e) >> 1;
        current_next_indicator = data[ptr++] & 1;
        section_number = data[ptr++] & 0xff;
        last_section_number = data[ptr++] & 0xff;
    }

    int getPtr() {
        return ptr;
    }
    
    @Override
    public String toString() {
        String s = " +- table_id: " + Integer.toHexString(table_id) + "\n";
        s += " +- section_syntax_indicator: " + section_syntax_indicator + "\n";
        s += " +- section_length: " + section_length + "\n";
        s += " +- version_number: " + version_number + "\n";
        s += " +- current_next_indicator : " + current_next_indicator + "\n";
        s += " +- section_number: " + section_number + "\n";
        s += " +- last_section_number : " + last_section_number + "\n";
        return s;
    }
}
