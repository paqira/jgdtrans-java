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

/**
 * The mesh unit, or approximate length of cell's edge.
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * MeshUnit unit = MeshUnit.ONE;
 * assert unit.integer() == 1;
 *
 * // prints MeshUnit.ONE[integer=1]
 * System.out.println(unit);
 * }</pre>
 */
public enum MeshUnit {
  /** For {@code 1} [km]. */
  ONE,
  /** For {@code 5} [km]. */
  FIVE;

  /**
   * Returns {@code 1} when {@code this} is {@link MeshUnit#ONE}, and {@code 5} when {@code this} is
   * {@link MeshUnit#FIVE}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * assert MeshUnit.ONE.integer() == 1;
   * assert MeshUnit.FIVE.integer() == 5;
   * }</pre>
   *
   * @return {@code 1} or {@code 5}.
   */
  protected int toInteger() {
    switch (this) {
      case ONE:
        return 1;
      case FIVE:
        return 5;
      default:
        throw new RuntimeException("UNREACHABLE");
    }
  }

  @Override
  public String toString() {
    return String.format("MeshUnit.%s[integer=%d]", super.toString(), this.toInteger());
  }
}
