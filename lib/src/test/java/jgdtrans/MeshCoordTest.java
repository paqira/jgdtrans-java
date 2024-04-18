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

import org.junit.jupiter.api.Test;

public class MeshCoordTest {
  @Test
  void constructor() {
    assertDoesNotThrow(() -> new MeshCoord(99, 7, 9));

    assertThrows(ValueOutOfRangeException.class, () -> new MeshCoord(0, 0, 10));
    assertThrows(ValueOutOfRangeException.class, () -> new MeshCoord(0, 8, 0));
    assertThrows(ValueOutOfRangeException.class, () -> new MeshCoord(100, 0, 0));

    assertThrows(ValueOutOfRangeException.class, () -> new MeshCoord(0, 0, -1));
    assertThrows(ValueOutOfRangeException.class, () -> new MeshCoord(0, -1, 0));
    assertThrows(ValueOutOfRangeException.class, () -> new MeshCoord(-1, 0, 0));
  }

  @Test
  void identity() throws ValueOutOfRangeException {
    MeshCoord coord;

    // latitude
    for (int f = 0; f < 100; f++) {
      for (int s = 0; s < 8; s++) {
        for (int t = 0; t < 10; t++) {
          coord = new MeshCoord(f, s, t);
          assertEquals(coord, MeshCoord.ofLatitude(coord.toLatitude(), MeshUnit.ONE));
        }
      }
    }

    // longitude
    for (int f = 0; f < 80; f++) {
      for (int s = 0; s < 8; s++) {
        for (int t = 0; t < 10; t++) {
          coord = new MeshCoord(f, s, t);
          assertEquals(coord, MeshCoord.ofLongitude(coord.toLongitude(), MeshUnit.ONE));
        }
      }
    }

    coord = new MeshCoord(80, 0, 0);
    assertEquals(coord, MeshCoord.ofLatitude(coord.toLatitude(), MeshUnit.ONE));
  }
}
