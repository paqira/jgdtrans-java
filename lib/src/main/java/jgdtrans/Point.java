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

import java.util.Objects;

/**
 * A triplet of latitude, longitude and altitude.
 *
 * <p>The constructor normalizes {@code latitude} and {@code longitude} to {@code -90.0 <=} and
 * {@code <= 90.0}, and {@code -180.0 <=} and {@code <= 180.0} respectively.
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * Point point = new Point(36.10377479, 140.087855041, 2.34);
 *
 * assert point.latitude() == 36.10377479;
 * assert point.longitude() == 140.087855041;
 * assert point.altitude() == 2.34;
 *
 * // prints "Point[latitude=36.10377479, longitude=140.087855041, altitude=2.34]"
 * System.out.println(point);
 * }</pre>
 */
public class Point {
  protected final double latitude;
  protected final double longitude;
  protected final double altitude;

  /**
   * The latitude of the point which satisfies {@code -90.0 <=} and {@code <= 90.0}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = new Point(36.10377479, 140.087855041, 2.34);
   * assert point.latitude() == 36.10377479;
   * }</pre>
   *
   * @return The latitude [deg].
   */
  public double latitude() {
    return this.latitude;
  }

  /**
   * The longitude of the point which satisfies {@code -180.0 <=} and {@code <= 180.0}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = new Point(36.10377479, 140.087855041, 2.34);
   * assert point.longitude() == 140.087855041;
   * }</pre>
   *
   * @return The longitude [deg].
   */
  public double longitude() {
    return this.longitude;
  }

  /**
   * The altitude of the point.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = new Point(36.10377479, 140.087855041, 2.34);
   * assert point.altitude() == 2.34;
   * }</pre>
   *
   * @return The altitude [m].
   */
  public double altitude() {
    return this.altitude;
  }

  /**
   * Makes a {@link Point} instance.
   *
   * <p>This is equivalent to {@code Point(latitude, longitude, 0.0)}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = new Point(36.10377479, 140.087855041);
   * assert point.latitude() == 36.10377479;
   * assert point.longitude() == 140.087855041;
   * assert point.altitude() == 0.0;
   * }</pre>
   *
   * @param latitude The latitude [deg].
   * @param longitude The longitude [deg].
   * @see Point#Point(double, double, double)
   */
  public Point(final double latitude, final double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.altitude = 0.0;
  }

  /**
   * Makes a {@link Point} instance with altitude.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = new Point(36.10377479, 140.087855041, 2.34);
   * assert point.latitude() == 36.10377479;
   * assert point.longitude() == 140.087855041;
   * assert point.altitude() == 2.34;
   * }</pre>
   *
   * @param latitude The latitude [deg].
   * @param longitude The longitude [deg].
   * @param altitude The altitude [m].
   */
  public Point(final double latitude, final double longitude, final double altitude) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.altitude = altitude;
  }

  /**
   * Makes a {@link Point} instance where the {@code meshcode} indicates.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = Point.ofMeshcode(54401027);
   * assert point.equals(new Point(36.1, 140.0875, 0.0));
   * }</pre>
   *
   * @param meshcode The meshcode.
   * @return A {@link Point} instance, <strong>not null</strong>.
   * @throws ValueOutOfRangeException If {@code meshcode} is out-of-range.
   */
  public static Point ofMeshcode(final int meshcode) throws ValueOutOfRangeException {
    final MeshNode node = MeshNode.ofMeshcode(meshcode);
    return ofMeshNode(node);
  }

  /**
   * Makes a {@link Point} instance where the {@code meshNode} locates.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshNode node = new MeshNode(new MeshCoord(54, 1, 2), new MeshCoord(40, 0, 7));
   * Point point = Point.ofMeshNode(node);
   * assert point.equals(new Point(36.1, 140.0875, 0.0));
   * }</pre>
   *
   * @param node The instance of {@link MeshNode}, <strong>may not be null</strong>.
   * @return A {@link Point} instance, <strong>not null</strong>.
   * @see MeshNode#ofPoint(Point, MeshUnit)
   */
  public static Point ofMeshNode(final MeshNode node) {
    return Objects.requireNonNull(node, "node").toPoint();
  }

  /**
   * Returns a meshcode represents the nearest south-east mesh node of {@code this}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = new Point(36.103774791666666, 140.08785504166664, 10.0);
   * assert point.toMeshcode(MeshUnit.ONE) == 54401027;
   * assert point.toMeshcode(MeshUnit.FIVE) == 54401005;
   * }</pre>
   *
   * @param meshUnit the unit of mesh, <strong>may not be null</strong>.
   * @return A meshcode.
   * @throws ValueOutOfRangeException If {@code this} is out-of-range.
   */
  public int toMeshcode(final MeshUnit meshUnit) throws ValueOutOfRangeException {
    return this.meshNode(meshUnit).toMeshcode();
  }

  /**
   * Returns the nearest south-east {@link MeshNode} of {@code this}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = new Point(36.103774791666666, 140.08785504166664, 10.0);
   * assert point.meshNode(MeshUnit.ONE).equals(
   *     new MeshNode(new MeshCoord(54, 1, 2), new MeshCoord(40, 0, 7))
   * );
   * assert point.meshNode(MeshUnit.FIVE).equals(
   *     new MeshNode(new MeshCoord(54, 1, 0), new MeshCoord(40, 0, 5))
   * );
   * }</pre>
   *
   * @param meshUnit the unit of mesh, <strong>may not be null</strong>.
   * @return A {@link MeshNode} instance, <strong>not null</strong>.
   * @see MeshNode#ofPoint(Point, MeshUnit)
   * @throws ValueOutOfRangeException If {@code this} is out-of-range.
   * @see MeshNode#ofPoint(Point, MeshUnit)
   */
  public MeshNode meshNode(final MeshUnit meshUnit) throws ValueOutOfRangeException {
    return MeshNode.ofPoint(this, meshUnit);
  }

  /**
   * Returns a {@link MeshCell} containing {@code this}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = new Point(36.103774791666666, 140.08785504166664, 10.0);
   *
   * MeshCell cell = point.meshCell(MeshUnit.ONE);
   * assert cell.equals(new MeshCell(
   *     MeshNode.ofMeshcode(54401027),
   *     MeshNode.ofMeshcode(54401028),
   *     MeshNode.ofMeshcode(54401037),
   *     MeshNode.ofMeshcode(54401038),
   *     MeshUnit.ONE
   * ));
   *
   * MeshCell cell = point.meshCell(MeshUnit.FIVE);
   * assert cell.equals(new MeshCell(
   *     MeshNode.ofMeshcode(54401005),
   *     MeshNode.ofMeshcode(54401100),
   *     MeshNode.ofMeshcode(54401055),
   *     MeshNode.ofMeshcode(54401150),
   *     MeshUnit.ONE
   * ));
   * }</pre>
   *
   * @param meshUnit the unit of mesh, <strong>may not be null</strong>.
   * @return A {@link MeshCell} instance, <strong>not null</strong>.
   * @see MeshCell#ofPoint(Point, MeshUnit)
   * @throws InvalidCellException When it does not construct a unit cell.
   * @throws MeshCoordOverflowException When it does not construct a unit cell.
   * @throws ValueOutOfRangeException When it does not construct a unit cell.
   * @see MeshCell#ofPoint(Point, MeshUnit)
   */
  public MeshCell meshCell(final MeshUnit meshUnit)
      throws InvalidCellException, MeshCoordOverflowException, ValueOutOfRangeException {
    return MeshCell.ofPoint(this, meshUnit);
  }

  protected static double normalizeLatitude(final double t) {
    if (Double.isNaN(t) || -90.0 <= t && t <= 90.0) {
      return t;
    }

    final double s = t % 360.0;
    if (s < -270.0 || 270.0 < s) {
      return s - Math.copySign(360.0, s);
    } else if (s < -90.0 || 90.0 < s) {
      return Math.copySign(180.0, s) - s;
    } else {
      return Math.copySign(s, t);
    }
  }

  protected static double normalizeLongitude(final double t) {
    if (Double.isNaN(t) || -180.0 <= t && t <= 180.0) {
      return t;
    }

    final double s = t % 360.0;
    if (s < -180.0 || 180.0 < s) {
      return s - Math.copySign(360.0, s);
    } else {
      return s;
    }
  }

  /**
   * Returns a normalized {@link Point} instance.
   *
   * <p>The resulting {@link Point} instance has normalized latitude and longitude which value
   * {@code -90.0 <=} and {@code <= 90.0}, and {@code -180.0 <=} and {@code <= 180.0} respectively.
   *
   * <p>This is not in-place.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = new Point(100.0, 200.0, 5.0);
   * assert point.normalize().equals(new Point(80.0, -160.0, 5.0));
   *
   * // is not in-place,
   * assert point.normalize().equals(new Point(100.0, 200.0, 5.0));
   * }</pre>
   *
   * @return The normalized point, <strong>not null</strong>.
   */
  public Point normalize() {
    return new Point(
        normalizeLatitude(this.latitude), normalizeLongitude(this.longitude), this.altitude);
  }

  /**
   * Make a {@link Point} instance by adding {@code correction}.
   *
   * <p>This is not in-place.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Correction corr = new Correction(1.0, 1.0, 1.0);
   * Point point = new Point(0.0, 0.0, 0.0);
   *
   * assert point.add(corr).equals(new Point(1.0, 1.0, 1.0));
   *
   * // is not in-place,
   * assert point.equals(new Point(0.0, 0.0, 0.0));
   * }</pre>
   *
   * @param correction The transformation correction, <strong>may not be null</strong>.
   * @return A new {@link Point} instance, <strong>not null</strong>.
   * @see Point#sub(Correction)
   */
  public Point add(final Correction correction) {
    Objects.requireNonNull(correction, "correction");
    return new Point(
        this.latitude + correction.latitude,
        this.longitude + correction.longitude,
        this.altitude + correction.altitude);
  }

  /**
   * Make a {@link Point} instance by subtracting {@code correction}.
   *
   * <p>This is not in-place.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Correction corr = new Correction(1.0, 1.0, 1.0);
   * Point point = new Point(0.0, 0.0, 0.0);
   *
   * assert point.sub(corr).equals(new Point(-1.0, -1.0, -1.0));
   *
   * // is not in-place,
   * assert point.equals(new Point(0.0, 0.0, 0.0));
   * }</pre>
   *
   * @param correction The transformation correction, <strong>may not be null</strong>.
   * @return A new {@link Point} instance, <strong>not null</strong>.
   * @see Point#add(Correction)
   */
  public Point sub(final Correction correction) {
    Objects.requireNonNull(correction, "correction");
    return new Point(
        this.latitude - correction.latitude,
        this.longitude - correction.longitude,
        this.altitude - correction.altitude);
  }

  @Override
  public String toString() {
    return String.format(
        "Point[latitude=%f, longitude=%f, altitude=%f]",
        this.latitude, this.longitude, this.altitude);
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Point) {
      final Point other = (Point) o;
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
