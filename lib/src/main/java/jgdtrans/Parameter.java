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
 * The parameter triplet.
 *
 * <p>We emphasize that the unit of latitude and longitude is [sec], not [deg].
 *
 * <p>It should fill by {@code 0.0} instead of {@link Double#NaN} if the parameter does not exist,
 * as the parser does.
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * Parameter param = new Parameter(1.0, 2.0, 3.0);
 *
 * assert param.latitude() == 1.0;
 * assert param.longitude() == 2.0;
 * assert param.altitude() == 3.0;
 *
 * // prints "Parameter[latitude=1.0, longitude=2.0, altitude=3.0]"
 * System.out.println(param);
 * }</pre>
 */
public class Parameter {
  protected final double latitude;
  protected final double longitude;
  protected final double altitude;

  /**
   * Makes a {@link Parameter}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Parameter param = new Parameter(1.0, 2.0, 3.0);
   *
   * assert param.latitude() == 1.0;
   * assert param.longitude() == 2.0;
   * assert param.altitude() == 3.0;
   * }</pre>
   *
   * @param latitude The latitude parameter [sec].
   * @param longitude The latitude parameter [sec].
   * @param altitude The altitude parameter [m].
   */
  public Parameter(final double latitude, final double longitude, final double altitude) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.altitude = altitude;
  }

  /**
   * Returns the latitude parameter [sec].
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Parameter param = new Parameter(1.0, 2.0, 3.0);
   * assert param.latitude() == 1.0;
   * }</pre>
   *
   * @return The latitude parameter [sec].
   */
  public double latitude() {
    return this.latitude;
  }

  /**
   * Returns the latitude parameter [sec].
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Parameter param = new Parameter(1.0, 2.0, 3.0);
   * assert param.longitude() == 2.0;
   * }</pre>
   *
   * @return The latitude parameter [sec].
   */
  public double longitude() {
    return this.longitude;
  }

  /**
   * Returns the altitude parameter [m].
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Parameter param = new Parameter(1.0, 2.0, 3.0);
   * assert param.altitude() == 3.0;
   * }</pre>
   *
   * @return The altitude parameter [m].
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
   * Parameter param = new Parameter(1.0, 1.0, 0.0);
   * assert param.horizontal() == 1.4142135623730951;
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
        "Parameter[latitude=%f, longitude=%f, altitude=%f]",
        this.latitude, this.longitude, this.altitude);
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Parameter) {
      final Parameter other = (Parameter) o;
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
