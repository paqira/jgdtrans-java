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

/** Provides utility methods for operating {@code Mesh} etc. */
public class Meshs {
  /**
   * Returns {@code true} when {@code meshcode} is valid.
   *
   * <h2>Example</h2>
   *
   * <pre>{@code
   * assert isMeshcode(54401027) == true;
   * assert isMeshcode(-1) == false;
   * assert isMeshcode(100000000) == false;
   * }</pre>
   *
   * @param meshcode a test value
   * @return {@code true} when {@code meshcode} is valid.
   */
  public static boolean isMeshcode(final int meshcode) {
    try {
      MeshNode.ofMeshcode(meshcode);
    } catch (final ValueOutOfRangeException ignore) {
      return false;
    }
    return true;
  }
}
