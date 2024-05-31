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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Optional;
import jgdtrans.Format;
import jgdtrans.Parameter;
import jgdtrans.Transformer;
import org.junit.jupiter.api.Test;

public class BuilderTest {
  @Test
  void format() {
    assertThrows(NullPointerException.class, () -> Transformer.builder().build());

    assertEquals(
        Format.SemiDynaEXE, Transformer.builder().format(Format.SemiDynaEXE).build().format());
    assertEquals(Format.TKY2JGD, Transformer.builder().format(Format.TKY2JGD).build().format());
  }

  @Test
  void description() {
    assertEquals(
        Optional.empty(), Transformer.builder().format(Format.SemiDynaEXE).build().description());
    assertEquals(
        Optional.of("hi!"),
        Transformer.builder().format(Format.SemiDynaEXE).description("hi!").build().description());
  }

  @Test
  void parameter() {
    final Transformer tf =
        Transformer.builder()
            .format(Format.SemiDynaEXE)
            .parameter(0, new Parameter(0.00001, 0.00002, 0.00003))
            .build();

    final HashMap<Integer, Parameter> m = new HashMap<>();
    m.put(0, new Parameter(0.00001, 0.00002, 0.00003));
    assertEquals(m, tf.parameter());
  }
}
