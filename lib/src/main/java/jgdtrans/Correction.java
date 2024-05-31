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
 * The transformation correction.
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * Correction corr = new Correction(1.0, 2.0, 3.0);
 *
 * assert corr.latitude() == 1.0;
 * assert corr.longitude() == 2.0;
 * assert corr.altitude() == 3.0;
 *
 * // prints "Correction[latitude=1.0, longitude=2.0, altitude=3.0]"
 * System.out.println(corr);
 * }</pre>
 */
public class Correction {
  protected final double latitude;
  protected final double longitude;
  protected final double altitude;

  /**
   * Makes a {@link Correction}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Correction corr = new Correction(1.0, 2.0, 3.0);
   *
   * assert corr.latitude() == 1.0;
   * assert corr.longitude() == 2.0;
   * assert corr.altitude() == 3.0;
   * }</pre>
   *
   * @param latitude The latitude correction [deg].
   * @param longitude The longitude correction [deg].
   * @param altitude The altitude correction [m].
   */
  public Correction(final double latitude, final double longitude, final double altitude) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.altitude = altitude;
  }

  /**
   * Returns the latitude correction [deg].
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Correction corr = new Correction(1.0, 2.0, 3.0);
   * assert corr.latitude() == 1.0;
   * }</pre>
   *
   * @return The latitude correction [deg].
   */
  public double latitude() {
    return this.latitude;
  }

  /**
   * Returns the longitude correction [deg].
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Correction corr = new Correction(1.0, 2.0, 3.0);
   * assert corr.longitude() == 2.0;
   * }</pre>
   *
   * @return The latitude correction [deg].
   */
  public double longitude() {
    return this.longitude;
  }

  /**
   * Returns the altitude correction [m].
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Correction corr = new Correction(1.0, 2.0, 3.0);
   * assert corr.altitude() == 3.0;
   * }</pre>
   *
   * @return The altitude correction [m].
   */
  public double altitude() {
    return this.altitude;
  }

  /**
   * Returns {@code Math.hypot(this.latitude(), this.longitude())}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Correction corr = new Correction(1.0, 1.0, 0.0);
   * assert corr.horizontal() == 1.4142135623730951;
   * }</pre>
   *
   * @return {@code Math.hypot(this.latitude(), this.longitude())}.
   */
  public double horizontal() {
    return Math.hypot(this.latitude, this.longitude);
  }

  @Override
  public String toString() {
    return String.format(
        "Correction[latitude=%f, longitude=%f, altitude=%f]",
        this.latitude, this.longitude, this.altitude);
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (o instanceof Correction) {
      final Correction other = (Correction) o;
      return Double.compare(this.latitude, other.latitude) == 0
          && Double.compare(this.longitude, other.longitude) == 0
          && Double.compare(this.altitude, other.altitude) == 0;
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;

    result = result * PRIME + Double.hashCode(this.latitude);
    result = result * PRIME + Double.hashCode(this.longitude);
    result = result * PRIME + Double.hashCode(this.altitude);

    return result;
  }
}
