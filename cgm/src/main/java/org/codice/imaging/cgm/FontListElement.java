/*
 * Copyright (c) 2014, 2016, Codice
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.codice.imaging.cgm;

import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class FontListElement extends ElementHelpers implements AbstractElement {

    private final List<String> fonts = new ArrayList<>();

    FontListElement() {
        super(CgmIdentifier.FONT_LIST);
    }

    @Override
    public void readParameters(final CgmInputReader dataReader, final int parameterListLength) throws IOException {
        int bytesRead = 0;
        while (bytesRead < parameterListLength) {
            String fontName = dataReader.getStringFixed();
            fonts.add(fontName);
            bytesRead += fontName.length();
            bytesRead++;
        }
    }

    @Override
    public void addStringDescription(final StringBuilder builder) {
        for (String fontName : fonts) {
            builder.append("\tFont: ");
            builder.append(fontName);
            builder.append(System.lineSeparator());
        }
    }

    @Override
    public void render(final Graphics2D g2, final CgmGraphicState graphicState) {
        graphicState.setFontList(fonts);
    }
}
