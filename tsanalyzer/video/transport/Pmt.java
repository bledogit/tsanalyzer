/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tsnanalyzer.video.transport;

import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public class Pmt extends PsiTable {

    protected  int program_no = 0;
    protected  int program_pid = 0;
    protected  int pcr_pid = 0;
    protected  int program_info_len = 0;
    private HashMap descriptors = new HashMap();
    private HashMap streams = new HashMap();
    private HashMap allStreams = null;

    public int getPcr_pid() {
        return pcr_pid;
    }

    public int getProgramInfoLen() {
        return program_info_len;
    }

    public int getProgramNumber() {
        return program_no;
    }

    public int getProgramPid() {
        return program_pid;
    }

    
    
    
    public HashMap getAllStreams() {
        return allStreams;
    }

    public void setAllStreams(HashMap allStreams) {
        this.allStreams = allStreams;
    }
    
    public HashMap getDescriptors() {
        return descriptors;
    }

    public HashMap getStreams() {
        return streams;
    }

    public Pmt() {
    }

    void setStreams(HashMap streams) {
        this.streams = streams;
    }

    @Override
    public void process(byte[] data) {
        super.process(data);
        int program_number = reserved2 >> 2;

        int length = section_length;

        if (table_id == 2) {

            pcr_pid = data[ptr++] & 0x1f;
            pcr_pid <<= 8;
            pcr_pid = data[ptr++] & 0xff;

            program_info_len = data[ptr++] & 0xf;
            program_info_len <<= 8;
            program_info_len = data[ptr++] & 0xff;

            length -= 9 + 4;

            int descriptor_bytes = program_info_len;

            while (descriptor_bytes > 0) {
                Descriptor descriptor;
                int descriptor_tag = data[ptr++] & 0xff;
                if (descriptors.containsKey(descriptor_tag)) {
                    descriptor = (Descriptor) descriptors.get(descriptor_tag);
                } else {
                    descriptor = Descriptor.CreateDescriptor(descriptor_tag);
                    descriptors.put(descriptor_tag, descriptor);
                }

                descriptor.descriptor_tag = descriptor_tag;
                descriptor.descriptor_length = data[ptr++] & 0xff;
                descriptor.setData(data, ptr, descriptor.descriptor_length);
                ptr += descriptor.descriptor_length;

                descriptor_bytes -= descriptor.descriptor_length + 2;
            }


            length -= program_info_len;

            while (length > 0) {
                int stream_type = data[ptr++] & 0xff;
                int elementary_pid = data[ptr++] & 0x1f;
                elementary_pid <<= 8;
                elementary_pid += data[ptr++] & 0xff;

                Stream stream;
                // pid already found
                if (allStreams.containsKey(elementary_pid)) {
                    stream = (Stream) allStreams.get(elementary_pid);
                } else {
                    stream = new Stream(elementary_pid);
                    allStreams.put(elementary_pid, stream);
                }

                streams.put(stream.pid, stream);
                stream.stream_type = stream_type;

                int es_info_length = data[ptr++] & 0x0f;
                es_info_length <<= 8;
                es_info_length += data[ptr++] & 0xff;

                descriptor_bytes = es_info_length;

                while (descriptor_bytes > 0) {
                    int descriptor_tag = data[ptr++] & 0xff;
                    int descriptor_lenght = data[ptr++] & 0xff;

                    stream.getDescriptors().containsKey(descriptor_tag);

                    Descriptor descriptor;

                    if (stream.getDescriptors().containsKey(descriptor_tag)) {
                        descriptor = (Descriptor) stream.getDescriptors().get(descriptor_tag);
                    } else {
                        descriptor = Descriptor.CreateDescriptor(descriptor_tag);
                        stream.getDescriptors().put(descriptor_tag, descriptor);
                    }

                    descriptor.descriptor_tag = descriptor_tag;
                    descriptor.descriptor_length = descriptor_lenght;
                    descriptor.setData(data, ptr, descriptor.descriptor_length);
                    ptr += descriptor.descriptor_length;

                    descriptor_bytes -= descriptor_lenght + 2;
                }


                length -= 5 + es_info_length;
            }
        } // table id = 2
    }

    @Override
    public String toString() {
        String s = " +- PMT Information " + Integer.toHexString(table_id) + "\n";
        s += " +- program_number: " + program_no + "\n";
        s += " +- program_pid: " + program_pid + "\n";
        s += " +- pcr_pid: " + pcr_pid + "\n";

        for (Descriptor d : (Collection<Descriptor>)descriptors.values()) {
            s += "    +- " + d.getDescriptorInfo() + "\n";
        }
        
        for (Stream st: (Collection<Stream>)streams.values()) {
            s += " +- Stream: " + st.getStreamType() + "\n";
            for (Descriptor d : (Collection<Descriptor>)st.getDescriptors().values()) {
                s += "    +- " + d.getDescriptorInfo() + "\n";
            }            
        }
 
        return s;
    }


}
