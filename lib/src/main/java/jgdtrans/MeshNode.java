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
 * Represents mesh node, a pair of the {@link MeshCoord} instances.
 *
 * <p>We note that this supports non-negative latitude and longitude only, and {@code longitude}
 * satisfies {@code MeshCoord(0, 0, 0) <=} and {@code <= MeshCoord(80, 0, 0)}.
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * Point point = new Point(36.10377479, 140.087855041);
 * MeshNode node;
 *
 * node = MeshNode.ofPoint(point, MeshUnit.ONE);
 * assert node.equals(new MeshNode(new MeshCoord(54, 1, 2), new MeshCoord(40, 0, 7)));
 *
 * node = MeshNode.ofPoint(point, MeshUnit.FIVE);
 * assert node.equals(new MeshNode(new MeshCoord(54, 1, 0), new MeshCoord(40, 0, 5)));
 *
 * // prints MeshNode[latitude=MeshCoord[first=54, second=1, third=2], longitude=MeshCoord[first=40, second=0, third=7]]
 * System.out.println(MeshNode.ofMeshcode(54401027));
 * }</pre>
 */
public class MeshNode {
  protected final MeshCoord latitude;
  protected final MeshCoord longitude;

  /**
   * Makes a {@link MeshNode} instance.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshNode node = new MeshNode(new MeshCoord(54, 1, 2), new MeshCoord(40, 0, 7));
   * assert node.latitude().equals(new MeshCoord(54, 1, 2));
   * assert node.longitude().equals(new MeshCoord(40, 0, 7));
   * }</pre>
   *
   * @param latitude The mesh coord of latitude, <strong>may not be null</strong>.
   * @param longitude The mesh coord of longitude, <strong>may not be null</strong>.
   * @throws ValueOutOfRangeException When {@code longitude} is out-of-range.
   */
  public MeshNode(final MeshCoord latitude, final MeshCoord longitude)
      throws ValueOutOfRangeException {
    Objects.requireNonNull(longitude, "longitude");

    if (longitude.first == 80) {
      if (longitude.second == 0) {
        if (0 < longitude.third) {
          throw new ValueOutOfRangeException("third of longitude");
        }
      } else if (0 < longitude.second) {
        throw new ValueOutOfRangeException("second of longitude");
      }
    } else if (80 < longitude.first) {
      throw new ValueOutOfRangeException("first of longitude");
    }

    this.latitude = Objects.requireNonNull(latitude, "latitude");
    this.longitude = longitude;
  }

  /**
   * Makes a {@link MeshNode} instance represented by {@code meshcode}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshNode node = MeshNode.ofMeshcode(54401027);
   * assert node.equals(new MeshNode(new MeshCoord(54, 1, 2), new MeshCoord(40, 0, 7)));
   * }</pre>
   *
   * @param meshcode The meshcode.
   * @return A {@link MeshNode} instance, <strong>not null</strong>.
   * @see MeshNode#toMeshcode()
   * @throws ValueOutOfRangeException When {@code meshcode} is out-of-range.
   */
  public static MeshNode ofMeshcode(final int meshcode) throws ValueOutOfRangeException {
    if (meshcode < 0 || 10000_00_00 <= meshcode) {
      throw new ValueOutOfRangeException("meshcode");
    }

    int rest;

    final int lat_first = meshcode / (100_00_00);
    rest = meshcode % (100_00_00);
    final int lng_first = rest / 100_00;
    rest %= 100_00;

    final int lat_second = rest / (10_00);
    rest %= 10 * 100;
    final int lng_second = rest / 100;
    rest %= 100;

    final int lat_third = rest / 10;
    final int lng_third = rest % 10;

    return new MeshNode(
        new MeshCoord(lat_first, lat_second, lat_third),
        new MeshCoord(lng_first, lng_second, lng_third));
  }

  /**
   * Makes the nearest north-west {@link MeshNode} of {@code point}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = new Point(36.103774791666666, 140.08785504166664, 10.0);
   * MeshNode node;
   *
   * node = MeshNode.ofPoint(point, MeshUnit.ONE);
   * assert node.equals(new MeshNode(new MeshCoord(54, 1, 2), new MeshCoord(40, 0, 7)));
   *
   * node = MeshNode.ofPoint(point, MeshUnit.FIVE);
   * assert node.equals(new MeshNode(new MeshCoord(54, 1, 0), new MeshCoord(40, 0, 5)));
   * }</pre>
   *
   * @param point The point, <strong>may not be null</strong>.
   * @param meshUnit the mesh unit, <strong>may not be null</strong>.
   * @return A {@link MeshNode} instance, <strong>not null</strong>.
   * @see Point#meshNode(MeshUnit)
   * @throws ValueOutOfRangeException When {@code point} is out-of-range.
   */
  public static MeshNode ofPoint(final Point point, final MeshUnit meshUnit)
      throws ValueOutOfRangeException {
    Objects.requireNonNull(point, "point");

    final MeshCoord latitude = MeshCoord.ofLatitude(point.latitude, meshUnit);
    final MeshCoord longitude = MeshCoord.ofLongitude(point.longitude, meshUnit);

    return new MeshNode(latitude, longitude);
  }

  /**
   * Returns the mesh coord of latitude.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshNode node = new MeshNode(new MeshCoord(54, 1, 2), new MeshCoord(40, 0, 7));
   * assert node.latitude().equals(new MeshCoord(54, 1, 2));
   * }</pre>
   *
   * @return The mesh coord of latitude, <strong>not null</strong>.
   */
  public MeshCoord latitude() {
    return this.latitude;
  }

  /**
   * The mesh coord of longitude
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshNode node = new MeshNode(new MeshCoord(54, 1, 2), new MeshCoord(40, 0, 7));
   * assert node.longitude().equals(new MeshCoord(40, 0, 7));
   * }</pre>
   *
   * <p>This satisfies {@code MeshCoord(0, 0, 0) <=} and {@code <= MeshCoord(80, 0, 0)}.
   *
   * @return The mesh coord of longitude, <strong>not null</strong>.
   */
  public MeshCoord longitude() {
    return this.longitude;
  }

  /**
   * Returns {@code true} when {@code this} is compatible to the {@code meshUnit}.
   *
   * <p>This always returns {@code true} when {@code meshUnit} is {@link MeshUnit#ONE}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshNode node = MeshNode.ofMeshcode(54401027);
   * assert node.isUnit(MeshUnit.ONE) == true;
   * assert node.isUnit(MeshUnit.FIVE) == false;
   * }</pre>
   *
   * @param meshUnit The mesh unit, <strong>may not be null</strong>.
   * @return {@code true} when {@code this} is compatible to the {@code unit}.
   */
  public boolean isMeshUnit(final MeshUnit meshUnit) {
    return this.latitude.isMeshUnit(meshUnit) && this.longitude.isMeshUnit(meshUnit);
  }

  /**
   * Returns a meshcode represents {@code this}.
   *
   * <p>The result is up to 8 digits.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshNode node = new MeshNode(new MeshCoord(54, 1, 2), new MeshCoord(40, 0, 7));
   * assert node.toMeshcode() == 54401027;
   * }</pre>
   *
   * @return The meshcode.
   * @see MeshNode#ofMeshcode(int)
   */
  public int toMeshcode() {
    return (this.latitude.first * 100 + this.longitude.first) * 10_000
        + (this.latitude.second * 10 + this.longitude.second) * 100
        + (this.latitude.third * 10 + this.longitude.third);
  }

  /**
   * Returns a {@link Point} (latitude and longitude) where {@code this} locates.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshNode node = new MeshNode(new MeshCoord(54, 1, 2), new MeshCoord(40, 0, 7));
   * assert node.toPoint().equals(new Point(36.1, 140.0875, 0.0));
   * }</pre>
   *
   * @return A {@link Point} of the mesh node, <strong>not null</strong>.
   * @see Point#meshNode(MeshUnit)
   */
  public Point toPoint() {
    return new Point(this.latitude.toLatitude(), this.longitude.toLongitude(), 0.0);
  }

  @Override
  public String toString() {
    return String.format("MeshNode[latitude=%s, longitude=%s]", this.latitude, this.longitude);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof MeshNode;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (o instanceof MeshNode) {
      final MeshNode other = (MeshNode) o;
      if (other.canEqual(this)) {
        return Objects.equals(this.latitude, other.latitude)
            && Objects.equals(this.longitude, other.longitude);
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;

    result = result * PRIME + this.latitude.hashCode();
    result = result * PRIME + this.longitude.hashCode();

    return result;
  }
}
