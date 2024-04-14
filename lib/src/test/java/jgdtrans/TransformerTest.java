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

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class TransformerTest {
  @Test
  void Tky2Jgd() throws ParameterNotFoundException {
    // web
    final HashMap<Integer, Parameter> m = new HashMap<>();
    // forward
    m.put(54401027, new Parameter(11.49105, -11.80078, 0.0));
    m.put(54401037, new Parameter(11.48732, -11.80198, 0.0));
    m.put(54401028, new Parameter(11.49096, -11.80476, 0.0));
    m.put(54401038, new Parameter(11.48769, -11.80555, 0.0));
    // backward
    m.put(54401047, new Parameter(11.48373, -11.80318, 0.0));
    m.put(54401048, new Parameter(11.48438, -11.80689, 0.0));
    final Transformer tf = new Transformer(Format.TKY2JGD, m);

    Point actual;
    Point origin;

    origin = new Point(36.103774791666666, 140.08785504166664, 0);
    actual = tf.forward(origin);
    assertEquals(36.106966281, actual.latitude, 0.00000001);
    assertEquals(140.084576867, actual.longitude, 0.00000001);
    assertEquals(0.0, actual.altitude);

    origin = new Point(36.10696628160147, 140.08457686629436, 0.0);
    actual = tf.backward(origin);
    assertEquals(36.103774792, actual.latitude, 0.00000001);
    assertEquals(140.087855042, actual.longitude, 0.00000001);
    assertEquals(0.0, actual.altitude);
  }

  @Test
  void PatchJGD_HV() throws ParameterNotFoundException {
    // web
    final HashMap<Integer, Parameter> m = new HashMap<>();
    // forward
    m.put(57413454, new Parameter(-0.05984, 0.22393, -1.25445));
    m.put(57413464, new Parameter(-0.06011, 0.22417, -1.24845));
    m.put(57413455, new Parameter(-0.0604, 0.2252, -1.29));
    m.put(57413465, new Parameter(-0.06064, 0.22523, -1.27667));
    // backward
    m.put(57413474, new Parameter(-0.06037, 0.22424, -0.35308));
    m.put(57413475, new Parameter(-0.06089, 0.22524, 0.0));
    final Transformer tf = new Transformer(Format.PatchJGD_HV, m);

    Point actual;
    Point origin;

    origin = new Point(38.2985120586605, 141.5559006163195, 0.0);
    actual = tf.forward(origin);
    assertEquals(38.298495306, actual.latitude, 0.00000001);
    assertEquals(141.555963019, actual.longitude, 0.00000001);
    assertEquals(-1.263, actual.altitude, 0.001);

    origin = new Point(38.29849530463122, 141.55596301776936, 0.0);
    actual = tf.backward(origin);
    assertEquals(38.298512058, actual.latitude, 0.00000001);
    assertEquals(141.555900614, actual.longitude, 0.00000001);
    assertEquals(1.264, actual.altitude, 0.001);
  }

  @Test
  void SemiDynaEXE() throws ParameterNotFoundException {
    final HashMap<Integer, Parameter> m = new HashMap<>();
    m.put(54401005, new Parameter(-0.00622, 0.01516, 0.0946));
    m.put(54401055, new Parameter(-0.0062, 0.01529, 0.08972));
    m.put(54401100, new Parameter(-0.00663, 0.01492, 0.10374));
    m.put(54401150, new Parameter(-0.00664, 0.01506, 0.10087));
    final Transformer tf = new Transformer(Format.SemiDynaEXE, m);

    Point actual;
    Point origin;

    // web
    origin = new Point(36.103774791666666, 140.08785504166664, 0.0);
    actual = tf.forward(origin);
    assertEquals(36.103773019, actual.latitude, 0.00000001);
    assertEquals(140.087859244, actual.longitude, 0.00000001);
    assertEquals(0.096, actual.altitude, 0.001);

    origin = new Point(36.10377301875336, 140.08785924400115, 0.0);
    actual = tf.backward(origin);
    assertEquals(36.103774792, actual.latitude, 0.00000001);
    assertEquals(140.087855042, actual.longitude, 0.00000001);
    assertEquals(-0.096, actual.altitude, 0.001);

    // exact by Decimal
    origin = new Point(36.103774791666666, 140.08785504166664, 0.0);
    actual = tf.forward(origin);
    assertEquals(36.10377301875335, actual.latitude, 0.000000000001);
    assertEquals(140.08785924400115, actual.longitude, 0.000000000001);
    assertEquals(0.09631385775572238, actual.altitude, 0.000000000001);

    actual = tf.backward(actual);
    assertEquals(36.10377479166668, actual.latitude, 0.000000000001);
    assertEquals(140.08785504166664, actual.longitude, 0.000000000001);
    assertEquals(-4.2175864502150125955e-10, actual.altitude, 0.000000000001);
  }

  // parser

  @Test
  void parseTKY2JGD() throws ParseParFileException {
    String s;
    Transformer tf;
    HashMap<Integer, Parameter> m;

    // simple
    s = "\n\n12345678   0.00001   0.00002";
    tf = Transformer.fromString(s, Format.TKY2JGD);

    assertEquals(Format.TKY2JGD, tf.format());
    assertEquals(Optional.of("\n"), tf.description());

    m = new HashMap<>();
    m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
    assertEquals(tf.parameter(), m);

    // ends with \n
    s = "\n\n12345678   0.00001   0.00002\n";
    tf = Transformer.fromString(s, Format.TKY2JGD);

    assertEquals(Format.TKY2JGD, tf.format());
    assertEquals(Optional.of("\n"), tf.description());

    m = new HashMap<>();
    m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
    assertEquals(m, tf.parameter());

    // multi lines
    s = "\n\n12345678   0.00001   0.00002\n90123345 -10.00001 -10.00002";
    tf = Transformer.fromString(s, Format.TKY2JGD);

    assertEquals(Format.TKY2JGD, tf.format());
    assertEquals(Optional.of("\n"), tf.description());

    m = new HashMap<>();
    m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
    m.put(90123345, new Parameter(-10.00001, -10.00002, 0.0));
    assertEquals(tf.parameter(), m);

    // with description
    s = "\n\n12345678   0.00001   0.00002\n";
    tf = Transformer.fromString(s, Format.TKY2JGD, "hi!");

    assertEquals(Format.TKY2JGD, tf.format());
    assertEquals(Optional.of("hi!"), tf.description());

    m = new HashMap<>();
    m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
    assertEquals(m, tf.parameter());

    // with null description
    s = "\n\n12345678   0.00001   0.00002\n";
    tf = Transformer.fromString(s, Format.TKY2JGD, null);

    assertEquals(Format.TKY2JGD, tf.format());
    assertEquals(Optional.of("\n"), tf.description());

    m = new HashMap<>();
    m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
    assertEquals(m, tf.parameter());
  }

  @Test
  void parsePatchJGD() throws ParseParFileException {
    String s;
    Transformer tf;
    HashMap<Integer, Parameter> m;

    // simple
    s =
        Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
            + "12345678   0.00001   0.00002";
    tf = Transformer.fromString(s, Format.PatchJGD);

    assertEquals(Format.PatchJGD, tf.format());
    assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), tf.description());

    m = new HashMap<>();
    m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
    assertEquals(tf.parameter(), m);

    // ends with \n
    s =
        Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
            + "12345678   0.00001   0.00002\n";
    tf = Transformer.fromString(s, Format.PatchJGD);

    assertEquals(Format.PatchJGD, tf.format());
    assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), tf.description());

    m = new HashMap<>();
    m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
    assertEquals(m, tf.parameter());

    // multi lines
    s =
        Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
            + "12345678   0.00001   0.00002\n90123345 -10.00001 -10.00002";
    tf = Transformer.fromString(s, Format.PatchJGD);

    assertEquals(Format.PatchJGD, tf.format());
    assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), tf.description());

    m = new HashMap<>();
    m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
    m.put(90123345, new Parameter(-10.00001, -10.00002, 0.0));
    assertEquals(tf.parameter(), m);

    // with description
    s =
        Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
            + "12345678   0.00001   0.00002\n";
    tf = Transformer.fromString(s, Format.PatchJGD, "hi!");

    assertEquals(Format.PatchJGD, tf.format());
    assertEquals(Optional.of("hi!"), tf.description());

    m = new HashMap<>();
    m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
    assertEquals(m, tf.parameter());

    // with null description
    s =
        Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
            + "12345678   0.00001   0.00002\n";
    tf = Transformer.fromString(s, Format.PatchJGD, null);

    assertEquals(Format.PatchJGD, tf.format());
    assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), tf.description());

    m = new HashMap<>();
    m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
    assertEquals(m, tf.parameter());
  }

  @Test
  void parsePatchJGD_H() throws ParseParFileException {
    String s;
    Transformer tf;
    HashMap<Integer, Parameter> m;

    // simple
    s =
        Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
            + "12345678   0.00001   0.00000";
    tf = Transformer.fromString(s, Format.PatchJGD_H);

    assertEquals(Format.PatchJGD_H, tf.format());
    assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), tf.description());

    m = new HashMap<>();
    m.put(12345678, new Parameter(0.0, 0.0, 0.00001));
    assertEquals(tf.parameter(), m);

    // ends with \n
    s =
        Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
            + "12345678   0.00001   0.00002\n";
    tf = Transformer.fromString(s, Format.PatchJGD_H);

    assertEquals(Format.PatchJGD_H, tf.format());
    assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), tf.description());

    m = new HashMap<>();
    m.put(12345678, new Parameter(0.0, 0.0, 0.00001));
    assertEquals(m, tf.parameter());

    // multi lines
    s =
        Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
            + "12345678   0.00001   0.00002\n90123345 -10.00001 -10.00002";
    tf = Transformer.fromString(s, Format.PatchJGD_H);

    assertEquals(Format.PatchJGD_H, tf.format());
    assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), tf.description());

    m = new HashMap<>();
    m.put(12345678, new Parameter(0.0, 0.0, 0.00001));
    m.put(90123345, new Parameter(0.0, 0.0, -10.00001));
    assertEquals(tf.parameter(), m);

    // with description
    s =
        Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
            + "12345678   0.00001   0.00002\n";
    tf = Transformer.fromString(s, Format.PatchJGD_H, "hi!");

    assertEquals(Format.PatchJGD_H, tf.format());
    assertEquals(Optional.of("hi!"), tf.description());

    m = new HashMap<>();
    m.put(12345678, new Parameter(0.0, 0.0, 0.00001));
    assertEquals(m, tf.parameter());

    // with null description
    s =
        Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
            + "12345678   0.00001   0.00002\n";
    tf = Transformer.fromString(s, Format.PatchJGD_H, null);

    assertEquals(Format.PatchJGD_H, tf.format());
    assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), tf.description());

    m = new HashMap<>();
    m.put(12345678, new Parameter(0.0, 0.0, 0.00001));
    assertEquals(m, tf.parameter());
  }
}
