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
 * Represents mesh coordinate, namely, discrete latitude and/or longitude.
 *
 * <p>This supports total ordering, and non-negative latitude and/or longitude only.
 *
 * <p>The coordinate has three digits, {@code first}, {@code second} and {@code third}, the {@code
 * first} values {@code 0} to {@code 9}, the {@code second} does {@code 0} to {@code 7} and the
 * {@code third} does {@code 0} to {@code 99} inclusive.
 *
 * <p>We note that the third digit takes either {@code 0} or {@code 5} only on the mesh with {@link
 * MeshUnit#FIVE}.
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * MeshCoord coord = new MeshCoord(1, 2, 3);
 *
 * coord.first() == 1;
 * coord.second() == 2;
 * coord.third() == 3;
 *
 * // prints MeshCoord[first=1, second=2, third=3]
 * System.out.println(coord);
 * }</pre>
 */
public class MeshCoord implements Comparable<MeshCoord> {
  protected final int first;
  protected final int second;
  protected final int third;

  /**
   * Makes a {@link MeshCoord}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshCoord coord = new MeshCoord(1, 2, 3);
   *
   * coord.first() == 1;
   * coord.second() == 2;
   * coord.third() == 3;
   * }</pre>
   *
   * @param first The first digit, {@code 0} to {@code 99}.
   * @param second The second digit, {@code 0} to {@code 7}.
   * @param third The third digit, {@code 0} to {@code 9}.
   */
  public MeshCoord(final int first, final int second, final int third) {
    if (!testFirst(first)) {
      throw new ValueOutOfRangeException("first");
    } else if (!testSecond(second)) {
      throw new ValueOutOfRangeException("second");
    } else if (!testThird(third)) {
      throw new ValueOutOfRangeException("third");
    }

    this.first = first;
    this.second = second;
    this.third = third;
  }

  private static boolean testFirst(final int first) {
    return 0 <= first && first < 100;
  }

  private static boolean testSecond(final int second) {
    return 0 <= second && second < 8;
  }

  private static boolean testThird(final int third) {
    return 0 <= third && third < 10;
  }

  private static MeshCoord ofDegree(final double v, final MeshUnit unit) {
    final int integer = (int) Math.floor(v);

    final int first = integer % 100;
    final int second = (int) Math.floor(8.0 * v) - 8 * integer;
    final int third = (int) Math.floor(80.0 * v) - 80 * integer - 10 * second;

    switch (unit) { // Callee checks unit is not null
      case ONE:
        return new MeshCoord(first, second, third);
      case FIVE:
        return new MeshCoord(first, second, third < 5 ? 0 : 5);
      default:
        throw new RuntimeException("UNREACHABLE");
    }
  }

  /**
   * Makes the greatest {@link MeshCoord} instance less than the latitude {@code t} with {@code
   * unit}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshCoord coord = MeshCoord.ofLatitude(36.103774791666666, MeshUnit.ONE);
   * assert coord.equals(new MeshCoord(54, 1, 2));
   * }</pre>
   *
   * <pre>{@code
   * MeshCoord coord = MeshCoord.ofLatitude(36.103774791666666, MeshUnit.FIVE);
   * assert coord.equals(new MeshCoord(54, 1, 0));
   * }</pre>
   *
   * @param degree The latitude [deg] which satisfies {@code 0.0 <=} and {@code <= 66.666...}.
   * @param unit The mesh unit, may not be null.
   * @return A {@code MeshCoord} instance, not null.
   * @throws ValueOutOfRangeException If {@code degree} is out-of-range.
   */
  public static MeshCoord ofLatitude(final double degree, final MeshUnit unit)
      throws ValueOutOfRangeException {
    double value = 3.0 * degree / 2.0;
    if ((Double.doubleToRawLongBits(degree) & 0b1L) == 0b1L) {
      value = Math.nextUp(value);
    }

    if (value < 0.0 || 100.0 <= value) {
      throw new ValueOutOfRangeException("degree (latitude)");
    }

    return MeshCoord.ofDegree(value, Objects.requireNonNull(unit, "unit"));
  }

  /**
   * Makes the greatest {@link MeshCoord} instance less than the longitude {@code t} with {@code
   * unit}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshCoord coord = MeshCoord.ofLongitude(140.08785504166664, MeshUnit.ONE);
   * assert coord.equals(new MeshCoord(40, 0, 7));
   * }</pre>
   *
   * <pre>{@code
   * MeshCoord coord = MeshCoord.ofLongitude(140.08785504166664, MeshUnit.FIVE);
   * assert coord.equals(new MeshCoord(40, 0, 5));
   * }</pre>
   *
   * @param degree The longitude [deg] which satisfies {@code 100.0 <=} and {@code <= 180.0}.
   * @param unit The mesh unit, may not be null.
   * @return A {@code MeshCoord} instance, not null.
   * @throws ValueOutOfRangeException If {@code degree} is out-of-range.
   */
  public static MeshCoord ofLongitude(final double degree, final MeshUnit unit)
      throws ValueOutOfRangeException {
    if (degree < 100.0 || 180.0 < degree) {
      throw new ValueOutOfRangeException("degree (longitude)");
    }

    return MeshCoord.ofDegree(degree, Objects.requireNonNull(unit, "unit"));
  }

  /**
   * Returns the first digit.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshCoord coord = new MeshCoord(1, 2, 3);
   * assert coord.first() == 1;
   * }</pre>
   *
   * @return The first digit, {@code 0} to {@code 99}.
   */
  public int first() {
    return this.first;
  }

  /**
   * Returns the second digit.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshCoord coord = new MeshCoord(1, 2, 3);
   * assert coord.second() == 2;
   * }</pre>
   *
   * @return The second digit, {@code 0} to {@code 7}.
   */
  public int second() {
    return this.second;
  }

  /**
   * Returns the third digit.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshCoord coord = new MeshCoord(1, 2, 3);
   * assert coord.third() == 3;
   * }</pre>
   *
   * @return The third digit, {@code 0} to {@code 9}.
   */
  public int third() {
    return this.third;
  }

  /**
   * Returns {@code true} if {@code this} is compatible to the {@code unit}.
   *
   * <p>This always returns {@code true} if {@code unit} is {@link MeshUnit#ONE}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshCoord coord = new MeshCoord(1, 2, 3);
   * assert coord.isUnit(MeshUnit.ONE) == true;
   * assert coord.isUnit(MeshUnit.FIVE) == false;
   * }</pre>
   *
   * @param unit The mesh unit.
   * @return {@code true} if {@code this} is compatible to the {@code unit}.
   */
  public boolean isUnit(final MeshUnit unit) {
    switch (Objects.requireNonNull(unit, "unit")) {
      case ONE:
        return true;
      case FIVE:
        return this.third % unit.integer() == 0;
      default:
        throw new RuntimeException("UNREACHABLE");
    }
  }

  protected boolean testMeshUnitFive() {
    return this.third == 0 || this.third == 5;
  }

  private double toDegree() {
    return (double) this.first + (double) this.second / 8.0 + (double) this.third / 80.0;
  }

  /**
   * Returns the latitude that {@code this} converts into.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * double latitude = 36.103774791666666;
   * MeshCoord coord;
   *
   * coord = MeshCoord.ofLatitude(latitude, MeshUnit.ONE);
   * assert coord.toLatitude() == 36.1;
   *
   * coord = MeshCoord.ofLatitude(latitude, MeshUnit.FIVE);
   * assert coord.toLatitude() == 36.083333333333336;
   * }</pre>
   *
   * @return The latitude [deg].
   */
  public double toLatitude() {
    return 2.0 * this.toDegree() / 3.0;
  }

  /**
   * Returns the longitude that {@code this} converts into.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * double longitude = 140.08785504166664;
   * MeshCoord coord;
   *
   * coord = MeshCoord.ofLongitude(longitude, MeshUnit.ONE);
   * assert coord.toLongitude() == 140.0875;
   *
   * coord = MeshCoord.ofLongitude(longitude, MeshUnit.FIVE);
   * assert coord.toLongitude() == 140.0625;
   * }</pre>
   *
   * @return The longitude [deg].
   */
  public double toLongitude() {
    return 100.0 + this.toDegree();
  }

  /**
   * Returns the smallest {@link MeshCoord} instance greater than {@code this}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * assert new MeshCoord(0, 0, 0).nextUp(MeshUnit.ONE).equals(new MeshCoord(0, 0, 1));
   * assert new MeshCoord(0, 0, 9).nextUp(MeshUnit.ONE).equals(new MeshCoord(0, 1, 0));
   * assert new MeshCoord(0, 7, 9).nextUp(MeshUnit.ONE).equals(new MeshCoord(1, 0, 0));
   * }</pre>
   *
   * <pre>{@code
   * assert new MeshCoord(0, 0, 0).nextUp(MeshUnit.FIVE).equals(new MeshCoord(0, 0, 5));
   * assert new MeshCoord(0, 0, 5).nextUp(MeshUnit.FIVE).equals(new MeshCoord(0, 1, 0));
   * assert new MeshCoord(0, 7, 5).nextUp(MeshUnit.FIVE).equals(new MeshCoord(1, 0, 0));
   * }</pre>
   *
   * @param unit The mesh unit, may not be null.
   * @return The up-next {@link MeshCoord} instance, not null.
   * @throws InvalidUnitException If {@code unit} is {@link MeshUnit#FIVE} although {@link
   *     MeshCoord#third()} is either {@code 0} or {@code 5}.
   * @throws OverflowException If {@code this} is {@code MeshCoord(first=99, second=7, third=9)}.
   */
  public MeshCoord nextUp(final MeshUnit unit) throws InvalidUnitException {
    if (!this.isUnit(unit)) {
      throw new InvalidUnitException();
    }

    final int bound = MeshUnit.ONE.equals(unit) ? 9 : 5;

    if (this.third == bound) {
      if (this.second == 7) {
        if (this.first == 99) {
          throw new OverflowException("overflow");
        }
        return new MeshCoord(this.first + 1, 0, 0);
      }
      return new MeshCoord(this.first, this.second + 1, 0);
    }
    return new MeshCoord(this.first, this.second, this.third + unit.integer());
  }

  /**
   * Returns the greatest {@link MeshCoord} instance less than {@code this}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * assert new MeshCoord(0, 0, 1).nextDown(MeshUnit.ONE).equals(new MeshCoord(0, 0, 0));
   * assert new MeshCoord(0, 1, 0).nextDown(MeshUnit.ONE).equals(new MeshCoord(0, 0, 9));
   * assert new MeshCoord(1, 0, 0).nextDown(MeshUnit.ONE).equals(new MeshCoord(0, 7, 9));
   * }</pre>
   *
   * <pre>{@code
   * assert new MeshCoord(0, 0, 5).nextDown(MeshUnit.FIVE).equals(new MeshCoord(0, 0, 0));
   * assert new MeshCoord(0, 1, 0).nextDown(MeshUnit.FIVE).equals(new MeshCoord(0, 0, 5));
   * assert new MeshCoord(1, 0, 0).nextDown(MeshUnit.FIVE).equals(new MeshCoord(0, 7, 5));
   * }</pre>
   *
   * @param unit The mesh unit, may not be null.
   * @return The up-next {@link MeshCoord} instance, not null.
   * @throws InvalidUnitException If {@code unit} is {@link MeshUnit#FIVE} although {@link
   *     MeshCoord#third()} is either {@code 0} or {@code 5}.
   * @throws OverflowException If {@code this} is {@code MeshCoord(first=0, second=0, third=0)}.
   */
  public MeshCoord nextDown(final MeshUnit unit) throws InvalidUnitException {
    if (!this.isUnit(unit)) {
      throw new InvalidUnitException();
    }

    final int bound = MeshUnit.ONE.equals(unit) ? 9 : 5;

    if (this.third == 0) {
      if (this.second == 0) {
        if (this.first == 0) {
          throw new OverflowException("overflow");
        }
        return new MeshCoord(this.first - 1, 7, bound);
      }
      return new MeshCoord(this.first, this.second - 1, bound);
    }
    return new MeshCoord(this.first, this.second, this.third - unit.integer());
  }

  @Override
  public int compareTo(final MeshCoord o) {
    final int first = Integer.compare(this.first, o.first);
    if (first == 0) {
      final int second = Integer.compare(this.second, o.second);
      if (second == 0) {
        return Integer.compare(this.third, o.third);
      } else {
        return second;
      }
    } else {
      return first;
    }
  }

  @Override
  public String toString() {
    return String.format(
        "MeshCoord[first=%d, second=%d, third=%d]", this.first, this.second, this.third);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof MeshCoord;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (o instanceof MeshCoord) {
      final MeshCoord other = (MeshCoord) o;
      if (other.canEqual(this)) {
        return this.first == other.first
            && this.second == other.second
            && this.third == other.third;
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;

    result = result * PRIME + this.first;
    result = result * PRIME + this.second;
    result = result * PRIME + this.third;

    return result;
  }
}
