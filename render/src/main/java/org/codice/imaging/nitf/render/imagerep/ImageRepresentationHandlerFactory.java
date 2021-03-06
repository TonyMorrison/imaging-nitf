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
package org.codice.imaging.nitf.render.imagerep;

import java.util.HashMap;
import java.util.Map;

import org.codice.imaging.nitf.core.image.ImageBand;
import org.codice.imaging.nitf.core.image.ImageSegment;
import org.codice.imaging.nitf.render.datareader.DataReaderFactory;
import org.codice.imaging.nitf.render.datareader.IOReaderFunction;

/**
 * Factory class for creating image representation handlers.
 */
public final class ImageRepresentationHandlerFactory {

    private static final int NOT_VISIBLE_MAPPED = -1;
    private static final int BAND_NOT_FOUND = -2;

    private ImageRepresentationHandlerFactory() {
    }

    /**
     * Get an appropriate ImageRepresentationHandler for the specified image
     * segment.
     *
     * @param segment the image segment specifying the image characteristics to
     * be read.
     * @return a handler for the segment, or a "no render" handler if an
     * appropriate handler could not be found.
     */
    public static ImageRepresentationHandler forImageSegment(final ImageSegment segment) {

        switch (segment.getImageRepresentation()) {
            case MONOCHROME:
                return getMonoImageRepresentationHandler(segment, 0);
            case RGBTRUECOLOUR:
                return getRgbImageRepresentationHandler(segment);
            case MULTIBAND:
                return getHandlerForMultiband(segment);
            case RGBLUT:
                return getRgbLUTImageRepresentationHandler(segment, 0);
            //add other (more complex) cases here
            case NOTFORDISPLAY:
            default:
                return new NoDisplayImageRepresentationHandler();
        }
    }

    private static ImageRepresentationHandler getRgbImageRepresentationHandler(final ImageSegment segment) {
        Map<Integer, Integer> bandMapping;
        switch (segment.getNumberOfBitsPerPixelPerBand()) {
        case Byte.SIZE:
            bandMapping = getRgbImageRepresentationMapping(segment);
            return new Rgb24ImageRepresentationHandler(bandMapping, segment.getActualBitsPerPixelPerBand());
        case Short.SIZE:
            bandMapping = getRgbImageRepresentationMapping(segment);
            return new Rgb48ImageRepresentationHandler(bandMapping,
                    segment.getActualBitsPerPixelPerBand(),
                    segment.getPixelJustification(),
                    segment.getNumberOfBitsPerPixelPerBand());
        default:
            // It can be 8, 16 or 32 once we are at CLEVEL 6, but so far we can only do 8 or 16.
            // TODO: implement 32 bit support [IMG-113]
            return null;
        }
    }

    private static Map<Integer, Integer> getRgbImageRepresentationMapping(final ImageSegment imageSegment) {
        Map<Integer, Integer> mapping = new HashMap<>();
        for (int bandIndex = 0; bandIndex < imageSegment.getNumBands(); bandIndex++) {
            int leftShift;
            switch (imageSegment.getImageBandZeroBase(bandIndex).getImageRepresentation()) {
                case "R":
                    leftShift = 2 * Byte.SIZE;
                    break;
                case "G":
                    leftShift = Byte.SIZE;
                    break;
                case "B":
                    leftShift = 0;
                    break;
                default:
                    leftShift = NOT_VISIBLE_MAPPED;
            }
            mapping.put(bandIndex, leftShift);
        }
        return mapping;
    }

    private static ImageRepresentationHandler getHandlerForMultiband(final ImageSegment segment) {
        if (irepbandsHasRgb(segment)) {
            return getRgbImageRepresentationHandler(segment);
        }
        int firstLookupTableBandZeroBase = getFirstLookupBandZeroBase(segment);
        if (firstLookupTableBandZeroBase != BAND_NOT_FOUND) {
            return getRgbLUTImageRepresentationHandler(segment, firstLookupTableBandZeroBase);
        }
        int firstMonoBandZeroBase = getFirstMonoBandZeroBase(segment);
        if (firstMonoBandZeroBase != BAND_NOT_FOUND) {
            return getMonoImageRepresentationHandler(segment, firstMonoBandZeroBase);
        }
        // No representation, try showing first band
        return getMonoImageRepresentationHandler(segment, 0);
    }

    private static boolean irepbandsHasRgb(final ImageSegment segment) {
        boolean hasR = false;
        boolean hasG = false;
        boolean hasB = false;
        for (int i = 0; i < segment.getNumBands(); i++) {
            ImageBand band = segment.getImageBandZeroBase(i);
            if (null != band.getImageRepresentation()) {
                switch (band.getImageRepresentation()) {
                    case "R":
                        hasR = true;
                        break;
                    case "G":
                        hasG = true;
                        break;
                    case "B":
                        hasB = true;
                        break;
                    default:
                        break;
                }
            }
        }
        return (hasR && hasG && hasB);
    }

    private static int getFirstMonoBandZeroBase(final ImageSegment segment) {
        for (int bandIndex = 0; bandIndex < segment.getNumBands(); bandIndex++) {
            ImageBand band = segment.getImageBandZeroBase(bandIndex);
            if (null != band.getImageRepresentation() && ("M".equals(band.getImageRepresentation()))) {
                return bandIndex;
            }
        }
        return BAND_NOT_FOUND;
    }

    private static int getFirstLookupBandZeroBase(final ImageSegment segment) {
        for (int bandIndex = 0; bandIndex < segment.getNumBands(); bandIndex++) {
            ImageBand band = segment.getImageBandZeroBase(bandIndex);
            if (null != band.getImageRepresentation() && ("LU".equals(band.getImageRepresentation()))) {
                return bandIndex;
            }
        }
        return BAND_NOT_FOUND;
    }

    private static ImageRepresentationHandler getMonoImageRepresentationHandler(final ImageSegment segment, final int selectedBandZeroBase) {
        switch (segment.getPixelValueType()) {
            case BILEVEL:
                return getMonoBilevelImageRepresentationHandler(segment, selectedBandZeroBase);
            case INTEGER:
                return getMonoIntegerImageRepresentationHandler(segment, selectedBandZeroBase);
            case SIGNEDINTEGER:
                // TODO: [IMG-108] implement this
                return null;
            case REAL:
                // TODO: [IMG-107] implement this
                return null;
            case COMPLEX:
                // TODO: [IMG-109] implement this
                return null;
            default:
                throw new UnsupportedOperationException("Unsupported pixel value type:" + segment.getPixelValueType().getTextEquivalent());
        }
    }

    private static ImageRepresentationHandler getMonoBilevelImageRepresentationHandler(final ImageSegment segment, final int selectedBandZeroBase) {
        if (segment.getNumberOfBitsPerPixelPerBand() != 1) {
            throw new UnsupportedOperationException("Pixel Value of bilevel (B) must be 1 bit per pixel (NBPP = 1)");
        }
        return new Mono1ImageRepresentationHandler(selectedBandZeroBase, DataReaderFactory.forImageSegment(segment));
    }

    private static ImageRepresentationHandler getMonoIntegerImageRepresentationHandler(final ImageSegment segment, final int selectedBandZeroBase) {
        if (segment.getNumberOfBitsPerPixelPerBand() == Byte.SIZE) {
            return new Mono8IntegerImageRepresentationHandler(selectedBandZeroBase, DataReaderFactory.forImageSegment(segment));
        } else if (segment.getNumberOfBitsPerPixelPerBand() <= Short.SIZE) {
            return new Mono16IntegerImageRepresentationHandler(selectedBandZeroBase, DataReaderFactory.forImageSegment(segment));
        } else {
            // TODO: add 32 [IMG-110] and 64 [IMG-111] NBPP cases
            return null;
        }
    }

    private static ImageRepresentationHandler getRgbLUTImageRepresentationHandler(final ImageSegment segment, final int selectedBandZeroBase) {
        IOReaderFunction readerFunc = DataReaderFactory.forImageSegment(segment);
        if (readerFunc != null) {
            return new RGBLUTImageRepresentationHandler(selectedBandZeroBase, segment, readerFunc);
        } else {
            return null;
        }
    }
}
