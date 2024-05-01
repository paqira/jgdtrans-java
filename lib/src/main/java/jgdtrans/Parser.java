/*
 * Copyright 2024 Kentaro Tatsumi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jgdtrans;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import java.util.TreeMap;

// FIXME: need better logic
class Parser {
  protected final Format format;
  private final int header;
  private final Range meshcode;
  private final Range latitude;
  private final Range longitude;
  private final Range altitude;

  Parser(
      final Format format,
      final int header,
      final Range meshcode,
      final Range latitude,
      final Range longitude,
      final Range altitude) {
    this.format = format;
    this.header = header;
    this.meshcode = meshcode;
    this.latitude = latitude;
    this.longitude = longitude;
    this.altitude = altitude;
  }

  protected static Parser ofFormat(final Format format) {
    switch (format) {
      case TKY2JGD:
        return new Parser(
            Format.TKY2JGD, 2, new Range(0, 8), new Range(9, 18), new Range(19, 28), null);
      case PatchJGD:
        return new Parser(
            Format.PatchJGD, 16, new Range(0, 8), new Range(9, 18), new Range(19, 28), null);
      case PatchJGD_H:
        return new Parser(Format.PatchJGD_H, 16, new Range(0, 8), null, null, new Range(9, 18));
      case PatchJGD_HV:
        return new Parser(
            Format.PatchJGD_HV,
            16,
            new Range(0, 8),
            new Range(9, 18),
            new Range(19, 28),
            new Range(29, 38));
      case SemiDynaEXE:
        return new Parser(
            Format.SemiDynaEXE,
            16,
            new Range(0, 8),
            new Range(9, 18),
            new Range(19, 28),
            new Range(29, 38));
      case HyokoRev:
        return new Parser(Format.HyokoRev, 16, new Range(0, 8), null, null, new Range(12, 21));
      case geonetF3:
        return new Parser(
            Format.geonetF3,
            18,
            new Range(0, 8),
            new Range(12, 21),
            new Range(22, 31),
            new Range(32, 41));
      case ITRF2014:
        return new Parser(
            Format.ITRF2014,
            18,
            new Range(0, 8),
            new Range(12, 21),
            new Range(22, 31),
            new Range(32, 41));
      default:
        throw new RuntimeException("UNREACHABLE");
    }
  }

  protected static int parseMeshcode(final String line, final Range range, final int lineNo)
      throws ParseParException {
    try {
      final String substring = line.substring(range.start, range.stop);
      return Integer.parseUnsignedInt(substring.trim());
    } catch (final IndexOutOfBoundsException e) {
      throw new ParseParException("meshcode not found, line " + lineNo, e);
    } catch (final NumberFormatException e) {
      throw new ParseParException("invalid meshcode, line " + lineNo, e);
    }
  }

  protected static double parseValue(
      final String line, final Range range, final String name, final int lineNo)
      throws ParseParException {
    if (Objects.isNull(range)) {
      return 0.0;
    } else {
      try {
        final String substring = line.substring(range.start, range.stop);
        return Double.parseDouble(substring.trim());
      } catch (final IndexOutOfBoundsException e) {
        throw new ParseParException(name + " not found, line " + lineNo, e);
      } catch (final NumberFormatException e) {
        throw new ParseParException("invalid " + name + ", line " + lineNo, e);
      }
    }
  }

  protected static Transformer readValue(
      final BufferedReader reader, final Format format, final String description)
      throws IOException, ParseParException {
    final Parser parser = Parser.ofFormat(format);
    final String header = parser.header(reader);
    final TreeMap<Integer, Parameter> parameter = parser.parameter(reader);
    return new Transformer(
        parser.format, parameter, Objects.isNull(description) ? header : description);
  }

  /** Takes this.header lines. */
  protected String header(final BufferedReader reader) throws IOException, ParseParException {
    final String[] temp = new String[this.header];

    String line;
    for (int i = 0; i < this.header; i++) {
      line = reader.readLine();
      if (Objects.isNull(line)) throw new ParseParException("header");
      temp[i] = line;
    }

    return String.join("\n", temp) + "\n";
  }

  protected TreeMap<Integer, Parameter> parameter(final BufferedReader reader)
      throws IOException, ParseParException {
    // make parameter
    final TreeMap<Integer, Parameter> parameter = new TreeMap<>();

    int lineNo = this.header + 1;

    // temp vars
    String line;
    int meshcode;
    double latitude;
    double longitude;
    double altitude;

    while (Objects.nonNull(line = reader.readLine())) {
      meshcode = parseMeshcode(line, this.meshcode, lineNo);
      latitude = parseValue(line, this.latitude, "latitude", lineNo);
      longitude = parseValue(line, this.longitude, "longitude", lineNo);
      altitude = parseValue(line, this.altitude, "altitude", lineNo);

      parameter.put(meshcode, new Parameter(latitude, longitude, altitude));
      lineNo += 1;
    }

    return parameter;
  }

  protected static class Range {
    private final int start;
    private final int stop;

    Range(final int start, final int stop) {
      this.start = start;
      this.stop = stop;
    }
  }
}
