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

public class MeshNodeTest {
  @Test
  void constructor() {
    assertDoesNotThrow(() -> new MeshNode(new MeshCoord(0, 0, 0), new MeshCoord(80, 0, 0)));

    assertThrows(
        ValueOutOfRangeException.class,
        () -> new MeshNode(new MeshCoord(0, 0, 0), new MeshCoord(80, 0, 1)));
    assertThrows(
        ValueOutOfRangeException.class,
        () -> new MeshNode(new MeshCoord(0, 0, 0), new MeshCoord(80, 1, 0)));
    assertThrows(
        ValueOutOfRangeException.class,
        () -> new MeshNode(new MeshCoord(0, 0, 0), new MeshCoord(81, 0, 0)));
  }

  @Test
  void fromMeshcode() {
    assertDoesNotThrow(() -> MeshNode.ofMeshcode(0));

    assertThrows(ValueOutOfRangeException.class, () -> MeshNode.ofMeshcode(-1));
    assertThrows(ValueOutOfRangeException.class, () -> MeshNode.ofMeshcode(10000_00_00));
  }
}
