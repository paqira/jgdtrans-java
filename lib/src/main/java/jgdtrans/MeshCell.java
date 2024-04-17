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
 * <p>This is a quadruplet of the mesh nodes (and mesh unit), and has no other {@link MeshNode}
 * inside {@code this} in the {@code meshUnit}.
 *
 * <p>The cell is, roughly, a square with {@code meshUnit} [km] length edges.
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * Point point = new Point(36.10377479, 140.087855041);
 *
 * MeshCell cell = MeshCell.ofPoint(point, MeshUnit.ONE);
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
  protected final MeshUnit meshUnit;

  /**
   * Makes a {@link MeshCell} instance.
   *
   * <p>The cell must be a <em>unit cell</em> in the {@code meshUnit}.
   *
   * <p>The {@link MeshCoord#third} of the nodes must be {@code 1} or {@code 5} if {@code meshUnit}
   * is {@link MeshUnit#FIVE}.
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
   * @param meshUnit the mesh unit, may not be null.
   */
  public MeshCell(
      final MeshNode southWest,
      final MeshNode southEast,
      final MeshNode northWest,
      final MeshNode northEast,
      final MeshUnit meshUnit) {
    Objects.requireNonNull(southWest, "southWest");
    Objects.requireNonNull(southEast, "southEast");
    Objects.requireNonNull(northWest, "northWest");
    Objects.requireNonNull(northEast, "northEast");
    Objects.requireNonNull(meshUnit, "meshUnit");

    if (!southWest.isMeshUnit(meshUnit)) {
      throw new InvalidUnitException("southWest");
    } else if (!southEast.isMeshUnit(meshUnit)) {
      throw new InvalidUnitException("southEast");
    } else if (!northWest.isMeshUnit(meshUnit)) {
      throw new InvalidUnitException("northWest");
    } else if (!northEast.isMeshUnit(meshUnit)) {
      throw new InvalidUnitException("northEast");
    }

    final MeshCoord nextLatitude = southWest.latitude.nextUp(meshUnit);
    final MeshCoord nextLongitude = southWest.longitude.nextUp(meshUnit);
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
    this.meshUnit = meshUnit;
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
   * @param meshUnit The unit of the mesh
   * @return The meth cell
   */
  public static MeshCell ofMeshcode(final int meshcode, final MeshUnit meshUnit) {
    final MeshNode meshNode = MeshNode.ofMeshcode(meshcode);
    return ofMeshNode(meshNode, meshUnit);
  }

  /**
   * Return the unit cell which has {@code node} as a south-east node.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshCell cell = MeshCell.ofMeshNode(
   *     MeshNode.ofMeshcode(54401027),
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
   * @param meshUnit The unit of the mesh.
   * @return The meth cell
   */
  public static MeshCell ofMeshNode(final MeshNode node, final MeshUnit meshUnit)
      throws InvalidCellException, InvalidUnitException, ArithmeticException {
    final MeshCoord nextLatitude = node.latitude.nextUp(meshUnit);
    final MeshCoord nextLongitude = node.longitude.nextUp(meshUnit);

    final MeshNode se = new MeshNode(node.latitude, nextLongitude);
    final MeshNode nw = new MeshNode(nextLatitude, node.longitude);
    final MeshNode ne = new MeshNode(nextLatitude, nextLongitude);

    return new MeshCell(node, se, nw, ne, meshUnit);
  }

  /**
   * Makes a {@link MeshCell} which contains the {@link Point}.
   *
   * <p>We note that the result does not depend on {@link Point#altitude()}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = new Point(36.10377479, 140.087855041, 10.0);
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
   *     MeshUnit.FIVE
   * ));
   * }</pre>
   *
   * @param point The point
   * @param meshUnit The unit of the mesh
   * @return The mesh cell
   * @see Point#meshCell(MeshUnit)
   */
  public static MeshCell ofPoint(final Point point, final MeshUnit meshUnit) {
    final MeshNode meshNode = MeshNode.ofPoint(point, meshUnit);
    return ofMeshNode(meshNode, meshUnit);
  }

  /**
   * Returns the south-west node of the cell.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Point point = new Point(36.10377479, 140.087855041, 10.0)
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
   * Point point = new Point(36.10377479, 140.087855041, 10.0);
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
   * Point point = new Point(36.10377479, 140.087855041, 10.0);
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
   * Point point = new Point(36.10377479, 140.087855041, 10.0);
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
   * Point point = new Point(36.10377479, 140.087855041, 10.0);
   *
   * MeshCell cell = MeshCell.ofPoint(point, MeshUnit.ONE);
   * assert cell.meshUnit().equals(MeshUnit.ONE);
   *
   * MeshCell cell = MeshCell.ofPoint(point, MeshUnit.FIVE);
   * assert cell.meshUnit().equals(MeshUnit.FIVE);
   * }</pre>
   *
   * @return The mesh unit, not null.
   */
  public MeshUnit meshUnit() {
    return this.meshUnit;
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

    switch (this.meshUnit) {
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
        "MeshCell[southWest=%s, southEast=%s, northWest=%s, northEast=%s, meshUnit=%s]",
        this.southWest, this.southEast, this.northWest, this.northEast, this.meshUnit);
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
        return Objects.equals(this.meshUnit, other.meshUnit)
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
    result = result * PRIME + this.meshUnit.hashCode();

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
