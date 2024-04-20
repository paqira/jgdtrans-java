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

public class MeshsTest {
  @Test
  void constructor() {
    assertTrue(Meshes.isMeshcode(54401027));
    assertFalse(Meshes.isMeshcode(-1));
    assertFalse(Meshes.isMeshcode(100000000));
    assertFalse(Meshes.isMeshcode(10810000));
    assertFalse(Meshes.isMeshcode(10000800));
  }
}
