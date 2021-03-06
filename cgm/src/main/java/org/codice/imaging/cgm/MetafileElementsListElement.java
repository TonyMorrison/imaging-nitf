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


class MetafileElementsListElement extends ElementHelpers implements AbstractElement {

    private final List<CgmIdentifier> elementsList = new ArrayList<>();

    MetafileElementsListElement() {
        super(CgmIdentifier.METAFILE_ELEMENT_LIST);
    }

    @Override
    public void readParameters(final CgmInputReader dataReader, final int parameterListLength) throws IOException {
        int numElementsSpecified = dataReader.readSignedIntegerAtIntegerPrecision();
        for (int i = 0; i < numElementsSpecified; ++i) {
            int elementClass = dataReader.readSignedIntegerAtIndexPrecision();
            int elementId = dataReader.readSignedIntegerAtIndexPrecision();
            CgmIdentifier identifier = CgmIdentifier.findIdentifier(elementClass, elementId);
            elementsList.add(identifier);
        }
    }

    @Override
    public void addStringDescription(final StringBuilder builder) {
        for (CgmIdentifier identifier : elementsList) {
            builder.append("\tElement: ");
            builder.append(identifier.getFriendlyName());
            builder.append(System.lineSeparator());
        }
    }

    @Override
    public void render(final Graphics2D g2, final CgmGraphicState graphicState) {
        // Nothing
    }

}
