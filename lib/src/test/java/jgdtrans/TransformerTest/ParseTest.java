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
package jgdtrans.TransformerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jgdtrans.Format;
import jgdtrans.Parameter;
import jgdtrans.ParseParException;
import jgdtrans.Transformer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ParseTest {
  @Nested
  class TKY2JGD {
    String s;
    Transformer tf;
    HashMap<Integer, Parameter> m;

    @Test
    void simple() throws ParseParException {
      this.s = "\n\n12345678   0.00001   0.00002";
      this.tf = Transformer.fromString(this.s, Format.TKY2JGD);

      assertEquals(Format.TKY2JGD, this.tf.format());
      assertEquals(Optional.of("\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
      assertEquals(this.tf.parameter(), this.m);
    }

    @Test
    void endsWithNewline() throws ParseParException {
      this.s = "\n\n12345678   0.00001   0.00002\n";
      this.tf = Transformer.fromString(this.s, Format.TKY2JGD);

      assertEquals(Format.TKY2JGD, this.tf.format());
      assertEquals(Optional.of("\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
      assertEquals(this.m, this.tf.parameter());
    }

    @Test
    void multiline() throws ParseParException {
      this.s = "\n\n12345678   0.00001   0.00002\n90123345 -10.00001 -10.00002";
      this.tf = Transformer.fromString(this.s, Format.TKY2JGD);

      assertEquals(Format.TKY2JGD, this.tf.format());
      assertEquals(Optional.of("\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
      this.m.put(90123345, new Parameter(-10.00001, -10.00002, 0.0));
      assertEquals(this.tf.parameter(), this.m);
    }

    @Test
    void withDescription() throws ParseParException {
      this.s = "\n\n12345678   0.00001   0.00002\n";
      this.tf = Transformer.fromString(this.s, Format.TKY2JGD, "hi!");

      assertEquals(Format.TKY2JGD, this.tf.format());
      assertEquals(Optional.of("hi!"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
      assertEquals(this.m, this.tf.parameter());
    }

    @Test
    void withNullDescription() throws ParseParException {
      this.s = "\n\n12345678   0.00001   0.00002\n";
      this.tf = Transformer.fromString(this.s, Format.TKY2JGD, null);

      assertEquals(Format.TKY2JGD, this.tf.format());
      assertEquals(Optional.of("\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
      assertEquals(this.m, this.tf.parameter());
    }
  }

  @Nested
  class PatchJGD {
    String s;
    Transformer tf;
    HashMap<Integer, Parameter> m;

    @Test
    void simple() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "12345678   0.00001   0.00002";
      this.tf = Transformer.fromString(this.s, Format.PatchJGD);

      assertEquals(Format.PatchJGD, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
      assertEquals(this.tf.parameter(), this.m);
    }

    @Test
    void endsWithNewline() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "12345678   0.00001   0.00002\n";
      this.tf = Transformer.fromString(this.s, Format.PatchJGD);

      assertEquals(Format.PatchJGD, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
      assertEquals(this.m, this.tf.parameter());
    }

    @Test
    void multiline() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "12345678   0.00001   0.00002\n90123345 -10.00001 -10.00002";
      this.tf = Transformer.fromString(this.s, Format.PatchJGD);

      assertEquals(Format.PatchJGD, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
      this.m.put(90123345, new Parameter(-10.00001, -10.00002, 0.0));
      assertEquals(this.tf.parameter(), this.m);
    }

    @Test
    void withDescription() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "12345678   0.00001   0.00002\n";
      this.tf = Transformer.fromString(this.s, Format.PatchJGD, "hi!");

      assertEquals(Format.PatchJGD, this.tf.format());
      assertEquals(Optional.of("hi!"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
      assertEquals(this.m, this.tf.parameter());
    }

    @Test
    void withNullDescription() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "12345678   0.00001   0.00002\n";
      this.tf = Transformer.fromString(this.s, Format.PatchJGD, null);

      assertEquals(Format.PatchJGD, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(12345678, new Parameter(0.00001, 0.00002, 0.0));
      assertEquals(this.m, this.tf.parameter());
    }
  }

  @Nested
  class PatchJGD_H {
    String s;
    Transformer tf;
    HashMap<Integer, Parameter> m;

    @Test
    void simple() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "12345678   0.00001   0.00000";
      this.tf = Transformer.fromString(this.s, Format.PatchJGD_H);

      assertEquals(Format.PatchJGD_H, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(12345678, new Parameter(0.0, 0.0, 0.00001));
      assertEquals(this.tf.parameter(), this.m);
    }

    @Test
    void endsWithNewline() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "12345678   0.00001   0.00002\n";
      this.tf = Transformer.fromString(this.s, Format.PatchJGD_H);

      assertEquals(Format.PatchJGD_H, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(12345678, new Parameter(0.0, 0.0, 0.00001));
      assertEquals(this.m, this.tf.parameter());
    }

    @Test
    void multiline() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "12345678   0.00001   0.00002\n90123345 -10.00001 -10.00002";
      this.tf = Transformer.fromString(this.s, Format.PatchJGD_H);

      assertEquals(Format.PatchJGD_H, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(12345678, new Parameter(0.0, 0.0, 0.00001));
      this.m.put(90123345, new Parameter(0.0, 0.0, -10.00001));
      assertEquals(this.tf.parameter(), this.m);
    }

    @Test
    void withDescription() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "12345678   0.00001   0.00002\n";
      this.tf = Transformer.fromString(this.s, Format.PatchJGD_H, "hi!");

      assertEquals(Format.PatchJGD_H, this.tf.format());
      assertEquals(Optional.of("hi!"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(12345678, new Parameter(0.0, 0.0, 0.00001));
      assertEquals(this.m, this.tf.parameter());
    }

    @Test
    void withNullDescription() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "12345678   0.00001   0.00002\n";
      this.tf = Transformer.fromString(this.s, Format.PatchJGD_H, null);

      assertEquals(Format.PatchJGD_H, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(12345678, new Parameter(0.0, 0.0, 0.00001));
      assertEquals(this.m, this.tf.parameter());
    }
  }

  @Nested
  class HyokoRev {
    String s;
    Transformer tf;
    HashMap<Integer, Parameter> m;

    @Test
    void parseHyokoRev() throws ParseParException {
      String s;
      Transformer tf;
      HashMap<Integer, Parameter> m;

      // simple
      s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "00000000      0.00003   0.00000";
      tf = Transformer.fromString(s, Format.HyokoRev);

      assertEquals(Format.HyokoRev, tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), tf.description());

      m = new HashMap<>();
      m.put(0, new Parameter(0.0, 0.0, 0.00003));
      assertEquals(tf.parameter(), m);

      // ends with \n
      s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "00000000      0.00003   0.00000\n";
      tf = Transformer.fromString(s, Format.HyokoRev);

      assertEquals(Format.HyokoRev, tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), tf.description());

      m = new HashMap<>();
      m.put(0, new Parameter(0.0, 0.0, 0.00003));
      assertEquals(m, tf.parameter());

      // multi lines
      s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "00000000      0.00003   0.00000\n10000000    -10.00003   0.00000";
      tf = Transformer.fromString(s, Format.HyokoRev);

      assertEquals(Format.HyokoRev, tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), tf.description());

      m = new HashMap<>();
      m.put(0, new Parameter(0.0, 0.0, 0.00003));
      m.put(10000000, new Parameter(0.0, 0.0, -10.00003));
      assertEquals(tf.parameter(), m);

      // with description
      s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "00000000      0.00003   0.00000\n";
      tf = Transformer.fromString(s, Format.HyokoRev, "hi!");

      assertEquals(Format.HyokoRev, tf.format());
      assertEquals(Optional.of("hi!"), tf.description());

      m = new HashMap<>();
      m.put(0, new Parameter(0.0, 0.0, 0.00003));
      assertEquals(m, tf.parameter());

      // with null description
      s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "00000000      0.00003   0.00000\n";
      tf = Transformer.fromString(s, Format.HyokoRev, null);

      assertEquals(Format.HyokoRev, tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), tf.description());

      m = new HashMap<>();
      m.put(0, new Parameter(0.0, 0.0, 0.00003));
      assertEquals(m, tf.parameter());
    }
  }

  @Nested
  class SemiDynaEXE {
    String s;
    Transformer tf;
    HashMap<Integer, Parameter> m;

    @Test
    void simple() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "00000000   0.00001   0.00002   0.00003";
      this.tf = Transformer.fromString(this.s, Format.SemiDynaEXE);

      assertEquals(Format.SemiDynaEXE, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(0, new Parameter(0.00001, 0.00002, 0.00003));
      assertEquals(this.tf.parameter(), this.m);
    }

    @Test
    void endsWithNewline() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "00000000   0.00001   0.00002   0.00003\n";
      this.tf = Transformer.fromString(this.s, Format.SemiDynaEXE);

      assertEquals(Format.SemiDynaEXE, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(0, new Parameter(0.00001, 0.00002, 0.00003));
      assertEquals(this.m, this.tf.parameter());
    }

    @Test
    void multiline() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "00000000   0.00001   0.00002   0.00003\n10000000 -10.00001 -10.00002 -10.00003";
      this.tf = Transformer.fromString(this.s, Format.SemiDynaEXE);

      assertEquals(Format.SemiDynaEXE, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(0, new Parameter(0.00001, 0.00002, 0.00003));
      this.m.put(10000000, new Parameter(-10.00001, -10.00002, -10.00003));
      assertEquals(this.tf.parameter(), this.m);
    }

    @Test
    void withDescription() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "00000000   0.00001   0.00002   0.00003\n";
      this.tf = Transformer.fromString(this.s, Format.SemiDynaEXE, "hi!");

      assertEquals(Format.SemiDynaEXE, this.tf.format());
      assertEquals(Optional.of("hi!"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(0, new Parameter(0.00001, 0.00002, 0.00003));
      assertEquals(this.m, this.tf.parameter());
    }

    @Test
    void withNullDescription() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(16L).collect(Collectors.joining())
              + "00000000   0.00001   0.00002   0.00003\n";
      this.tf = Transformer.fromString(this.s, Format.SemiDynaEXE, null);

      assertEquals(Format.SemiDynaEXE, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(0, new Parameter(0.00001, 0.00002, 0.00003));
      assertEquals(this.m, this.tf.parameter());
    }
  }

  @Nested
  class geonetF3 {
    String s;
    Transformer tf;
    HashMap<Integer, Parameter> m;

    @Test
    void simple() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(18L).collect(Collectors.joining())
              + "00000000      0.00001   0.00002   0.00003";
      this.tf = Transformer.fromString(this.s, Format.geonetF3);

      assertEquals(Format.geonetF3, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(0, new Parameter(0.00001, 0.00002, 0.00003));
      assertEquals(this.tf.parameter(), this.m);
    }

    @Test
    void endsWithNewline() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(18L).collect(Collectors.joining())
              + "00000000      0.00001   0.00002   0.00003\n";
      this.tf = Transformer.fromString(this.s, Format.geonetF3);

      assertEquals(Format.geonetF3, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(0, new Parameter(0.00001, 0.00002, 0.00003));
      assertEquals(this.m, this.tf.parameter());
    }

    @Test
    void multiline() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(18L).collect(Collectors.joining())
              + "00000000      0.00001   0.00002   0.00003\n10000000    -10.00001 -10.00002 -10.00003";
      this.tf = Transformer.fromString(this.s, Format.geonetF3);

      assertEquals(Format.geonetF3, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(0, new Parameter(0.00001, 0.00002, 0.00003));
      this.m.put(10000000, new Parameter(-10.00001, -10.00002, -10.00003));
      assertEquals(this.tf.parameter(), this.m);
    }

    @Test
    void withDescription() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(18L).collect(Collectors.joining())
              + "00000000      0.00001   0.00002   0.00003\n";
      this.tf = Transformer.fromString(this.s, Format.geonetF3, "hi!");

      assertEquals(Format.geonetF3, this.tf.format());
      assertEquals(Optional.of("hi!"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(0, new Parameter(0.00001, 0.00002, 0.00003));
      assertEquals(this.m, this.tf.parameter());
    }

    @Test
    void withNullDescription() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(18L).collect(Collectors.joining())
              + "00000000      0.00001   0.00002   0.00003\n";
      this.tf = Transformer.fromString(this.s, Format.geonetF3, null);

      assertEquals(Format.geonetF3, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(0, new Parameter(0.00001, 0.00002, 0.00003));
      assertEquals(this.m, this.tf.parameter());
    }
  }

  @Nested
  class ITRF2014 {
    String s;
    Transformer tf;
    HashMap<Integer, Parameter> m;

    @Test
    void simple() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(18L).collect(Collectors.joining())
              + "00000000      0.00001   0.00002   0.00003";
      this.tf = Transformer.fromString(this.s, Format.ITRF2014);

      assertEquals(Format.ITRF2014, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(0, new Parameter(0.00001, 0.00002, 0.00003));
      assertEquals(this.tf.parameter(), this.m);
    }

    @Test
    void endsWithNewline() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(18L).collect(Collectors.joining())
              + "00000000      0.00001   0.00002   0.00003\n";
      this.tf = Transformer.fromString(this.s, Format.ITRF2014);

      assertEquals(Format.ITRF2014, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(0, new Parameter(0.00001, 0.00002, 0.00003));
      assertEquals(this.m, this.tf.parameter());
    }

    @Test
    void multiline() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(18L).collect(Collectors.joining())
              + "00000000      0.00001   0.00002   0.00003\n10000000    -10.00001 -10.00002 -10.00003";
      this.tf = Transformer.fromString(this.s, Format.ITRF2014);

      assertEquals(Format.ITRF2014, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(0, new Parameter(0.00001, 0.00002, 0.00003));
      this.m.put(10000000, new Parameter(-10.00001, -10.00002, -10.00003));
      assertEquals(this.tf.parameter(), this.m);
    }

    @Test
    void withDescription() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(18L).collect(Collectors.joining())
              + "00000000      0.00001   0.00002   0.00003\n";
      this.tf = Transformer.fromString(this.s, Format.ITRF2014, "hi!");

      assertEquals(Format.ITRF2014, this.tf.format());
      assertEquals(Optional.of("hi!"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(0, new Parameter(0.00001, 0.00002, 0.00003));
      assertEquals(this.m, this.tf.parameter());
    }

    @Test
    void withNullDescription() throws ParseParException {
      this.s =
          Stream.generate(() -> "\n").limit(18L).collect(Collectors.joining())
              + "00000000      0.00001   0.00002   0.00003\n";
      this.tf = Transformer.fromString(this.s, Format.ITRF2014, null);

      assertEquals(Format.ITRF2014, this.tf.format());
      assertEquals(Optional.of("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"), this.tf.description());

      this.m = new HashMap<>();
      this.m.put(0, new Parameter(0.00001, 0.00002, 0.00003));
      assertEquals(this.m, this.tf.parameter());
    }
  }
}
