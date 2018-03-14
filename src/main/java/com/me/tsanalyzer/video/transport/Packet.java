/*
 * Transport Stream Analyzer 
 * Copyright 2011 Jose Mortensen
 * 
 * This file is part of tsAnalyzer.
 * 
 * tsAnalyzer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * tsAnalyzer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.me.tsanalyzer.video.transport;

import com.me.tsanalyzer.video.util.TableBuilder;
import com.me.tsanalyzer.video.util.VideoException;

/**
 * Transport Stream Packet.  This class decodes and represents a 
 * transport stream packet.
 * 
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public class Packet {

    protected long number;
    
    protected boolean transportError;         // 1
    protected boolean payloadStart;           // 1
    protected boolean transportPriority;      // 1
    protected int pid;                        // 13
    
    protected int scramblingControl;          // 2
    protected int adaptationFieldControl;     // 2
    protected int continuityCounter;          // 4
    
    // if adaptationFieldControl == "10" or adaptationFieldControl == "11"
    protected AdaptationField  af;             // variable
    
    // if adaptationFieldControl == "01" or adaptationFieldControl == "11"
    protected int dataPtr = 0;
    
    byte[] data;
    
    Packet(byte[] buffer) throws VideoException {
        int ptr = 0;
        data = buffer;
        if (buffer[ptr++] != 0x47) {
            throw new VideoException("Wrong Sync Byte");
        }

        // extract pid
        pid = buffer[ptr++] & 0xff;
        pid <<= 8;
        pid += buffer[ptr++] & 0xff;
        transportError =    (pid & 0x8000) != 0;
        payloadStart =      (pid & 0x4000) != 0;
        transportPriority = (pid & 0x2000) != 0;
        pid &= 0x1fff;

        // AF flags
        adaptationFieldControl = (buffer[ptr] >> 4) & 3;
        scramblingControl = (buffer[ptr] >> 6) & 3;
        continuityCounter = buffer[ptr++] & 0xf;

        dataPtr = ptr;
        
        if (adaptationFieldControl == 2 || adaptationFieldControl == 3) {
            af = new AdaptationField(buffer, ptr);
            dataPtr = af.ptr;
        } 
        
        if ((adaptationFieldControl == 1) || ( adaptationFieldControl == 3)) {
        } else {
            dataPtr = 3;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TS Packet:\n");
        sb.append(" +- pid: ").append(pid).append("\n");
        sb.append(" +- packet number : ").append(number).append("\n");
        sb.append(" +- transport_error: ").append(transportError).append("\n");
        sb.append(" +- payloadStart : ").append(payloadStart).append("\n");
        sb.append(" +- transportPriority : ").append(transportPriority).append("\n");
        sb.append(" +- scramblingControl: ").append(scramblingControl).append("\n");
        sb.append(" +- adaptationFieldControl: ").append(adaptationFieldControl).append("\n");
        sb.append(" +- continuityCounter: ").append(continuityCounter).append("\n");
                
        if (af != null) {
            sb.append(af.toString());
        }
        sb.append("\nData:\n");

        sb.append(TableBuilder.BuildTable(data, 16, "    "));
        
        return sb.toString();
    }
}
