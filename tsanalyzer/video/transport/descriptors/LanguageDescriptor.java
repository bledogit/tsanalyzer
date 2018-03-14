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
package tsanalyzer.video.transport.descriptors;

import tsnanalyzer.video.transport.Descriptor;

/**
 * Language Descriptor. Parses a language descriptor
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public class LanguageDescriptor extends Descriptor {

    protected String[] languages;

    /** 
     * Decodes languages from language descriptor. 
     */
    private void decodeLanguage() {

        int lang_bytes = data.length;
        int nlang = lang_bytes / 4;

        languages = new String[nlang];

        for (int i = 0; i < nlang; i++) {
            languages[i] = "" + (char) data[i * nlang];
            languages[i] += (char) data[i * nlang + 1];
            languages[i] += (char) data[i * nlang + 2];
        }
    }

    /** Sets descriptor data
     * 
     * @param buffer  Buffer where the descriptor is located
     * @param pos     Position where the descriptor starts within buffer
     * @param len     Descriptor length
     */
    @Override
    public void setData(byte[] buffer, int pos, int len) {
        data = new byte[len];

        System.arraycopy(buffer, pos, data, 0, len);
        if (descriptor_tag == 0xa) {
            decodeLanguage();
        }
    }

    /**
     * Gets a text description of the descriptor
     * based on the descriptor type parsed from the
     * stream.
     * Covers descriptors documented on the following standards:
     * 13818-1, dvb, scte57 and scte65
     * 
     * @return Descriptor type description
     */
    @Override
    public String getDescriptorInfo() {
        String info = super.getDescriptorInfo() + " (";
        for (int i = 0; i < this.languages.length; i++) {
            for (String lan : languages) {
                info += lan + " ";
            }
        }

        return info + ")";
    }

    @Override
    public String toString() {
        StringBuilder ss = new StringBuilder();
        ss.append(super.getDescriptorInfo() + "\n");
        ss.append("  +-- Languages: (");
        for (String lan : languages) {
            ss.append(lan + " ");
        }
        ss.append(") \n");
        return ss.toString();
    }
}
