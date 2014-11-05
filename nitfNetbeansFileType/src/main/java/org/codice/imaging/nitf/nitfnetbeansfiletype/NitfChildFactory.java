/*
 * Copyright (c) Codice Foundation
 *
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 *
 */
package org.codice.imaging.nitf.nitfnetbeansfiletype;

import java.util.List;
import org.codice.imaging.nitf.core.AbstractCommonNitfSegment;
import org.codice.imaging.nitf.core.NitfDataExtensionSegmentHeader;
import org.codice.imaging.nitf.core.NitfGraphicSegmentHeader;
import org.codice.imaging.nitf.core.NitfImageSegmentHeader;
import org.codice.imaging.nitf.core.NitfParseStrategy;
import org.codice.imaging.nitf.core.NitfTextSegmentHeader;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

class NitfChildFactory extends ChildFactory<AbstractCommonNitfSegment> {

    private final NitfParseStrategy nitf;

    public NitfChildFactory(final NitfParseStrategy nitfData) {
        nitf = nitfData;
    }

    @Override
    protected boolean createKeys(final List list) {
        list.addAll(nitf.getImageSegmentHeaders());
        list.addAll(nitf.getGraphicSegments());
        list.addAll(nitf.getTextSegments());
        list.addAll(nitf.getDataExtensionSegments());
        return true;
    }

    @Override
    protected Node createNodeForKey(final AbstractCommonNitfSegment key) {
        if (key instanceof NitfImageSegmentHeader) {
            return new NitfImageSegmentNode((NitfImageSegmentHeader) key);
        } else if (key instanceof NitfGraphicSegmentHeader) {
            return new NitfGraphicSegmentNode((NitfGraphicSegmentHeader) key);
        } else if (key instanceof NitfTextSegmentHeader) {
            return new NitfTextSegmentNode((NitfTextSegmentHeader) key);
        } else if (key instanceof NitfDataExtensionSegmentHeader) {
            return new NitfDataExtensionSegmentNode((NitfDataExtensionSegmentHeader) key);
        } else {
            Node childNode = new AbstractNode(Children.LEAF);
            childNode.setDisplayName(key.getClass().getSimpleName() + " : " + key.getIdentifier());
            return childNode;
        }
    }
}
