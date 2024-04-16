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
 * Format of par file.
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * Format format = Format.TKY2JGD;
 * assert format.unit().equals(MeshUnit.ONE);
 *
 * // prints Format.TKY2JGD
 * System.out.println(format);
 * }</pre>
 */
public enum Format {
  /** Format of TKY2JGD. */
  TKY2JGD,
  /** Format of PatchJGD. */
  PatchJGD,
  /** Format of PatchJGD(H). */
  PatchJGD_H,
  /**
   * Format of composition of PatchJGD and PatchJGD(H) par files.
   *
   * <p>The {@link Format#PatchJGD_HV} is for composition of PatchJGD and PatchJGD(H) par files for
   * the same event, e.g. {@code touhokutaiheiyouoki2011.par} and {@code
   * touhokutaiheiyouoki2011_h.par}. We note that transformation works fine with such data, and GIAJ
   * does not distribute such file.
   *
   * <p>It should fill by zero for the parameters of remaining transformation in areas where it
   * supports only part of the transformation as a result of composition in order to support whole
   * area of each parameter, e.g. altitude of Chubu (中部地方) on the composition of {@code
   * touhokutaiheiyouoki2011.par} and {@code touhokutaiheiyouoki2011_h.par}.
   *
   * <p>The composite data should be in the same format as SemiDynaEXE.
   */
  PatchJGD_HV,
  /** Format of HyokoRev. */
  HyokoRev,
  /** Format of SemiDynaEXE. */
  SemiDynaEXE,
  /** Format of geonetF3 (POS2JGD). */
  geonetF3,
  /** Format of ITRF2014 (POS2JGD). */
  ITRF2014;

  /**
   * Returns unit of the format.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * assert Format.TKY2JGD.meshUnit().equals(MeshUnit.ONE);
   * assert Format.SemiDynaEXE.meshUnit().equals(MeshUnit.FIVE);
   * }</pre>
   *
   * @return the unit of the format, not null.
   */
  public MeshUnit meshUnit() {
    switch (this) {
      case TKY2JGD:
      case PatchJGD:
      case PatchJGD_H:
      case PatchJGD_HV:
      case HyokoRev:
        return MeshUnit.ONE;
      case SemiDynaEXE:
      case geonetF3:
      case ITRF2014:
        return MeshUnit.FIVE;
      default:
        throw new RuntimeException("UNREACHABLE");
    }
  }

  @Override
  public String toString() {
    return String.format("Format.%s", super.toString());
  }
}
