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
 * Represents the unit mesh cell (mesh cell or cell shortly).
 *
 * <p>This is a quadruplet of the mesh nodes (and unit), and has no other {@link MeshNode} inside
 * {@code this} in the {@code unit}.
 *
 * <p>The cell is, roughly, a square with {@code unit} [km] length edges.
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * Point point = new Point(36.10377479, 140.087855041);
 *
 * MeshCell cell = MeshCell.ofPoint(point, MeshUnit.ONE)
 * assert cell.equals(new MeshCell(
 *     MeshNode.ofMeshcode(54401027),
 *     MeshNode.ofMeshcode(54401028),
 *     MeshNode.ofMeshcode(54401037),
 *     MeshNode.ofMeshcode(54401038),
 *     MeshUnit.ONE
 * ));
 * }</pre>
 */
public class MeshCell {
  protected final MeshNode southWest;
  protected final MeshNode southEast;
  protected final MeshNode northWest;
  protected final MeshNode northEast;
  protected final MeshUnit unit;

  /**
   * Makes a {@link MeshCell} instance.
   *
   * <p>The cell must be a <em>unit cell</em> in the {@code unit}.
   *
   * <p>The {@link MeshCoord#third} of the nodes must be {@code 1} or {@code 5} if {@code unit} is
   * {@link MeshUnit#FIVE}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = new Point(36.10377479, 140.087855041);
   *
   * MeshCell cell = MeshCell.ofPoint(point, MeshUnit.ONE)
   * assert cell.equals(new MeshCell(
   *     MeshNode.ofMeshcode(54401027),
   *     MeshNode.ofMeshcode(54401028),
   *     MeshNode.ofMeshcode(54401037),
   *     MeshNode.ofMeshcode(54401038),
   *     MeshUnit.ONE
   * ));
   * }</pre>
   *
   * @param southWest the south-west node of the cell, may not be null.
   * @param southEast the south-east node of the cell, may not be null.
   * @param northWest the north-west node of the cell, may not be null.
   * @param northEast the north-east node of the cell, may not be null.
   * @param unit the mesh unit, may not be null.
   */
  public MeshCell(
      final MeshNode southWest,
      final MeshNode southEast,
      final MeshNode northWest,
      final MeshNode northEast,
      final MeshUnit unit) {
    Objects.requireNonNull(southWest, "southWest");
    Objects.requireNonNull(southEast, "southEast");
    Objects.requireNonNull(northWest, "northWest");
    Objects.requireNonNull(northEast, "northEast");
    Objects.requireNonNull(unit, "unit");

    if (MeshUnit.FIVE.equals(unit)) {
      if (!southWest.latitude.testMeshUnitFive()) {
        throw new InvalidUnitException("longitude of southWest");
      } else if (!southWest.longitude.testMeshUnitFive()) {
        throw new InvalidUnitException("latitude of southWest");
      } else if (!southEast.latitude.testMeshUnitFive()) {
        throw new InvalidUnitException("latitude of southEast");
      } else if (!southEast.longitude.testMeshUnitFive()) {
        throw new InvalidUnitException("longitude of southEast");
      } else if (!northWest.latitude.testMeshUnitFive()) {
        throw new InvalidUnitException("latitude of northWest");
      } else if (!northWest.longitude.testMeshUnitFive()) {
        throw new InvalidUnitException("longitude of northWest");
      } else if (!northEast.latitude.testMeshUnitFive()) {
        throw new InvalidUnitException("latitude of northEast");
      } else if (!northEast.longitude.testMeshUnitFive()) {
        throw new InvalidUnitException("longitude of northEast");
      }
    }

    final MeshCoord nextLatitude = southWest.latitude.nextUp(unit);
    final MeshCoord nextLongitude = southWest.longitude.nextUp(unit);
    if (!northWest.equals(new MeshNode(nextLatitude, southWest.longitude))) {
      throw new InvalidCellException();
    } else if (!southEast.equals(new MeshNode(southWest.latitude, nextLongitude))) {
      throw new InvalidCellException();
    } else if (!northEast.equals(new MeshNode(nextLatitude, nextLongitude))) {
      throw new InvalidCellException();
    }

    this.southWest = southWest;
    this.southEast = southEast;
    this.northWest = northWest;
    this.northEast = northEast;
    this.unit = unit;
  }

  /**
   * Makes a {@link MeshCell} with the south-east {@link MeshNode} which represented by meshcode
   * {@code meshcode}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshCell cell = MeshCell.ofMeshcode(54401027, MeshUnit.ONE);
   * assert cell.equals(new MeshCell(
   *     MeshNode.ofMeshcode(54401027),
   *     MeshNode.ofMeshcode(54401028),
   *     MeshNode.ofMeshcode(54401037),
   *     MeshNode.ofMeshcode(54401038),
   *     MeshUnit.ONE
   * ));
   * }</pre>
   *
   * <pre>{@code
   * MeshCell cell = MeshCell.ofMeshcode(54401005, MeshUnit.FIVE);
   * assert cell.equals(new MeshCell(
   *     MeshNode.ofMeshcode(54401005),
   *     MeshNode.ofMeshcode(54401100),
   *     MeshNode.ofMeshcode(54401055),
   *     MeshNode.ofMeshcode(54401150),
   *     MeshUnit.FIVE
   * ));
   * }</pre>
   *
   * @param meshcode The meshcode
   * @param unit The unit of the mesh
   * @return The meth cell
   */
  public static MeshCell ofMeshcode(final int meshcode, final MeshUnit unit) {
    final MeshNode meshNode = MeshNode.ofMeshcode(meshcode);
    return ofMeshNode(meshNode, unit);
  }

  /**
   * Return the unit cell which has {@code node} as a south-east node.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshCell cell = MeshCell.ofMeshNode(
   *     new ofMeshNode.ofMeshcode(54401027),
   *     MeshUnit.ONE
   * );
   * assert cell.equals(new MeshCell(
   *     MeshNode.ofMeshcode(54401027),
   *     MeshNode.ofMeshcode(54401028),
   *     MeshNode.ofMeshcode(54401037),
   *     MeshNode.ofMeshcode(54401038),
   *     MeshUnit.ONE
   * ));
   * }</pre>
   *
   * @param node The south-west mesh node of the resulting cell.
   * @param unit The unit of the mesh
   * @return The meth cell
   */
  public static MeshCell ofMeshNode(final MeshNode node, final MeshUnit unit)
      throws InvalidCellException, InvalidUnitException, ArithmeticException {
    if (MeshUnit.FIVE.equals(unit)) {
      if (!node.latitude.testMeshUnitFive()) {
        throw new InvalidUnitException();
      } else if (!node.longitude.testMeshUnitFive()) {
        throw new InvalidUnitException();
      }
    }

    final MeshCoord nextLatitude = node.latitude.nextUp(unit);
    final MeshCoord nextLongitude = node.longitude.nextUp(unit);

    final MeshNode se = new MeshNode(node.latitude, nextLongitude);
    final MeshNode nw = new MeshNode(nextLatitude, node.longitude);
    final MeshNode ne = new MeshNode(nextLatitude, nextLongitude);

    return new MeshCell(node, se, nw, ne, unit);
  }

  /**
   * Makes a {@link MeshCell} which contains the {@link Point}.
   *
   * <p>We note that the result does not depend on {@link Point#altitude()}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = Point(36.10377479, 140.087855041, 10.0)
   *
   * MeshCell cell = MeshCell.ofPoint(point, MeshUnit.ONE);
   * assert cell.equals(new MeshCell(
   *     MeshNode.ofMeshcode(54401027),
   *     MeshNode.ofMeshcode(54401028),
   *     MeshNode.ofMeshcode(54401037),
   *     MeshNode.ofMeshcode(54401038),
   *     MeshUnit.ONE
   * ));
   *
   * MeshCell cell = MeshCell.ofPoint(point, MeshUnit.FIVE);
   * assert cell.equals(new MeshCell(
   *     MeshNode.ofMeshcode(54401005),
   *     MeshNode.ofMeshcode(54401100),
   *     MeshNode.ofMeshcode(54401055),
   *     MeshNode.ofMeshcode(54401150),
   *     MeshUnit.ONE
   * ));
   * }</pre>
   *
   * @param point The point
   * @param unit The unit of the mesh
   * @return The mesh cell
   * @see Point#meshCell(MeshUnit)
   */
  public static MeshCell ofPoint(final Point point, final MeshUnit unit) {
    final MeshNode meshNode = MeshNode.ofPoint(point, unit);
    return ofMeshNode(meshNode, unit);
  }

  /**
   * Returns the south-west node of the cell.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = Point(36.10377479, 140.087855041, 10.0)
   *
   * MeshCell cell = MeshCell.ofPoint(point, MeshUnit.ONE);
   * assert cell.southWest().equals(MeshNode.ofMeshcode(54401027));
   * }</pre>
   *
   * @return The south-west node of the cell, not null.
   */
  public MeshNode southWest() {
    return this.southWest;
  }

  /**
   * Returns the south-east node of the cell.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = Point(36.10377479, 140.087855041, 10.0)
   *
   * MeshCell cell = MeshCell.ofPoint(point, MeshUnit.ONE);
   * assert cell.southEast().equals(MeshNode.ofMeshcode(54401028));
   * }</pre>
   *
   * @return the south-east node of the cell, not null.
   */
  public MeshNode southEast() {
    return this.southEast;
  }

  /**
   * Returns the north-west node of the cell.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = Point(36.10377479, 140.087855041, 10.0)
   *
   * MeshCell cell = MeshCell.ofPoint(point, MeshUnit.ONE);
   * assert cell.northWest().equals(MeshNode.ofMeshcode(54401037));
   * }</pre>
   *
   * @return The north-west node of the cell, not null.
   */
  public MeshNode northWest() {
    return this.northWest;
  }

  /**
   * Returns the north-east node of the cell.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = Point(36.10377479, 140.087855041, 10.0)
   *
   * MeshCell cell = MeshCell.ofPoint(point, MeshUnit.ONE);
   * assert cell.northEast().equals(MeshNode.ofMeshcode(54401038));
   * }</pre>
   *
   * @return The north-east node of the cell, not null.
   */
  public MeshNode northEast() {
    return this.northEast;
  }

  /**
   * Returns the mesh unit.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = Point(36.10377479, 140.087855041, 10.0)
   *
   * MeshCell cell = MeshCell.ofPoint(point, MeshUnit.ONE);
   * assert cell.unit().equals(MeshUnit.ONE);
   *
   * MeshCell cell = MeshCell.ofPoint(point, MeshUnit.FIVE);
   * assert cell.unit().equals(MeshUnit.FIVE);
   * }</pre>
   *
   * @return The mesh unit, not null.
   */
  public MeshUnit unit() {
    return this.unit;
  }

  /**
   * Return the position in the cell.
   *
   * <p>The result's components takes values from {@code 0.0} to {@code 1.0} (inclusive), if {@link
   * Point#latitude()} and/or {@link Point#longitude()} is inside `this`.
   *
   * <p>We note that the result is a (latitude, longitude) pair, not a (right-handed) (x, y) pair.
   *
   * @param point The point
   * @return The position, a pair of the latitude and the longitude, in the cell
   */
  protected Position position(final Point point) {
    final double latitude = point.latitude - this.southWest.latitude.toLatitude();
    final double longitude = point.longitude - this.southWest.longitude.toLongitude();

    switch (this.unit) {
      case ONE:
        return new Position(80.0 * longitude, 120.0 * latitude);
      case FIVE:
        return new Position(16.0 * longitude, 24.0 * latitude);
      default:
        throw new RuntimeException("UNREACHABLE");
    }
  }

  @Override
  public String toString() {
    return String.format(
        "MeshCell[southWest=%s, southEast=%s, northWest=%s, northEast=%s, unit=%s]",
        this.southWest, this.southEast, this.northWest, this.northEast, this.unit);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof MeshCell;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (o instanceof MeshCell) {
      final MeshCell other = (MeshCell) o;
      if (other.canEqual(this)) {
        return Objects.equals(this.unit, other.unit)
            && Objects.equals(this.southWest, other.southWest)
            && Objects.equals(this.southEast, other.southEast)
            && Objects.equals(this.northWest, other.northWest)
            && Objects.equals(this.northEast, other.northEast);
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;

    result = result * PRIME + this.southWest.hashCode();
    result = result * PRIME + this.southEast.hashCode();
    result = result * PRIME + this.northWest.hashCode();
    result = result * PRIME + this.northEast.hashCode();
    result = result * PRIME + this.unit.hashCode();

    return result;
  }

  protected static class Position {
    protected final double x;
    protected final double y;

    Position(final double x, final double y) {
      this.x = x;
      this.y = y;
    }
  }
}
