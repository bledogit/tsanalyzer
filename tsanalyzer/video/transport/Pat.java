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

package tsnanalyzer.video.transport;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Program Association Table.  This class contains the data associated
 * to the program association table parsed from the transport stream.  It 
 * also holds references to other data structures with diverse PSI information
 * like the program map tables for each program associated with the stream.
 * 
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public class Pat extends PsiTable {

    private HashMap programs = new HashMap();
    
    protected  int transport_stream_id;
    protected  int network_PID = 0;
    protected  Nit nit = new Nit();
            
    public Pat(byte data[]) {
        super(data);
        
        transport_stream_id = reserved2 >> 2;
        int end = section_length - 9;
        while (end > 0) {
            int program_number = data[ptr++] & 0xff;
            program_number <<= 8;
            program_number += data[ptr++] & 0xff;

            int program_pid = data[ptr++] & 0xff;
            program_pid <<= 8;
            program_pid += data[ptr++] & 0xff;
            program_pid &= 0x1fff;

            if (program_number == 0) {
                // network pid
                this.network_PID = program_pid;
            } else {
                Pmt program;
                if (!programs.containsKey(program_pid)) {
                    program = new Pmt();
                    programs.put(program_pid, program);
                } else {
                    program = (Pmt)programs.get(program_pid);
                }
                program.program_pid = program_pid;
                program.program_no = program_number;
                
            }
            end -= 4;

        }

    }

    public HashMap getPrograms() {
        return programs;
    }

    @Override
    public String toString() {
        String ret = super.toString();
        Collection c = programs.values();
        Iterator i = c.iterator();
        ret += " +- transport_stream_id " + transport_stream_id + "\n";
        ret += " +- programs: \n";
        
        while (i.hasNext()) {
            Pmt p = (Pmt) i.next();
            ret += "    +- Program " + p.program_no + " pid = " + p.program_pid + "\n";

        }

        return ret;
    }
    
    /**
     * Finds if PID is a program table. 
     * @param pid   Stream PID
     * @return True if stream contains a Program Table
     */
    public boolean isProgramTable(int pid) {
        boolean ret = programs.containsKey(pid);
        return ret;
    }
    
    /**
     * Finds if PID belongs to the Network Information Table
     * @param pid Stream PID
     * @return true if stream contains a Network Information Table
     */
    public boolean isNetworkPID(int pid) {
        return (network_PID == pid);
    }
    
    /**
     * Gets program map contained on an specific stream.
     * @param pid   Stream's PID
     * @return      Program Map Table
     */
    public Pmt getProgram(int pid) {
        return (Pmt)programs.get(pid);
    }
    
    /**
     * Gets Network Information Table for the transport stream
     * @return Network Information Table
     */
    public Nit getNetwork() {
        return (nit);
    }
}
