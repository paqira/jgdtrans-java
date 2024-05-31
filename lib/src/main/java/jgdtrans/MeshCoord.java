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
   * Smallest {@link MeshCoord} value.
   *
   * <p>Equals to {@code MeshCoord(0, 0, 0)}.
   */
  public static final MeshCoord MIN = new MeshCoord(0, 0, 0, null);

  /**
   * Smallest {@link MeshCoord} value.
   *
   * <p>Equals to {@code MeshCoord(99, 7, 9)}.
   */
  public static final MeshCoord MAX = new MeshCoord(99, 7, 9, null);

  /** non-checked constructor */
  protected MeshCoord(final int first, final int second, final int third, final Void sentinel) {
    this.first = first;
    this.second = second;
    this.third = third;
  }

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
   * @throws ValueOutOfRangeException When any of {@code first}, {@code second} and {@code third} is
   *     out-of-range.
   */
  public MeshCoord(final int first, final int second, final int third)
      throws ValueOutOfRangeException {
    if (first < MIN.first || MAX.first < first) {
      throw new ValueOutOfRangeException("first");
    } else if (second < MIN.second || MAX.second < second) {
      throw new ValueOutOfRangeException("second");
    } else if (third < MIN.third || MAX.third < third) {
      throw new ValueOutOfRangeException("third");
    }

    this.first = first;
    this.second = second;
    this.third = third;
  }

  private static MeshCoord ofDegree(final double degree, final MeshUnit meshUnit)
      throws ValueOutOfRangeException {
    final int integer = (int) Math.floor(degree);

    final int first = integer % 100;
    final int second = (int) Math.floor(8.0 * degree) - 8 * integer;
    final int third = (int) Math.floor(80.0 * degree) - 80 * integer - 10 * second;

    switch (meshUnit) { // Callee checks unit is not null
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
   * meshUnit}.
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
   * @param meshUnit The mesh unit, <strong>may not be null</strong>.
   * @return A {@link MeshCoord} instance, <strong>not null</strong>.
   * @throws ValueOutOfRangeException When {@code degree} is out-of-range.
   * @see MeshCoord#toLatitude()
   */
  public static MeshCoord ofLatitude(final double degree, final MeshUnit meshUnit)
      throws ValueOutOfRangeException {
    double value = 3.0 * degree / 2.0;
    if ((Double.doubleToRawLongBits(degree) % 2L) != 0L) {
      value = Math.nextUp(value);
    }

    if (value < 0.0 || 100.0 <= value) {
      throw new ValueOutOfRangeException("degree (latitude)");
    }

    return MeshCoord.ofDegree(value, Objects.requireNonNull(meshUnit, "meshUnit"));
  }

  /**
   * Makes the greatest {@link MeshCoord} instance less than the longitude {@code t} with {@code
   * meshUnit}.
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
   * @param meshUnit The mesh unit, <strong>may not be null</strong>.
   * @return A {@link MeshCoord} instance, <strong>not null</strong>.
   * @throws ValueOutOfRangeException When {@code degree} is out-of-range.
   * @see MeshCoord#toLongitude()
   */
  public static MeshCoord ofLongitude(final double degree, final MeshUnit meshUnit)
      throws ValueOutOfRangeException {
    if (degree < 100.0 || 180.0 < degree) {
      throw new ValueOutOfRangeException("degree (longitude)");
    }

    return MeshCoord.ofDegree(degree, Objects.requireNonNull(meshUnit, "meshUnit"));
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
   * @return The first digit, {@code 0} to {@code 99}, <strong>not null</strong>.
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
   * @return The second digit, {@code 0} to {@code 7}, <strong>not null</strong>.
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
   * @return The third digit, {@code 0} to {@code 9}, <strong>not null</strong>.
   */
  public int third() {
    return this.third;
  }

  /**
   * Returns {@code true} when {@code this} is compatible to the {@code meshUnit}.
   *
   * <p>This always returns {@code true} when {@code unit} is {@link MeshUnit#ONE}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * MeshCoord coord = new MeshCoord(1, 2, 3);
   * assert coord.isMeshUnit(MeshUnit.ONE) == true;
   * assert coord.isMeshUnit(MeshUnit.FIVE) == false;
   * }</pre>
   *
   * @param meshUnit The mesh unit, <strong>may not be null</strong>.
   * @return {@code true} when {@code this} is compatible to the {@code meshUnit}.
   */
  public boolean isMeshUnit(final MeshUnit meshUnit) {
    switch (Objects.requireNonNull(meshUnit, "meshUnit")) {
      case ONE:
        return true;
      case FIVE:
        return this.third % meshUnit.toInteger() == 0;
      default:
        throw new RuntimeException("UNREACHABLE");
    }
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
   * @see MeshCoord#ofLatitude(double, MeshUnit)
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
   * @see MeshCoord#ofLongitude(double, MeshUnit)
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
   * @param meshUnit The mesh unit, <strong>may not be null</strong>.
   * @return The up-next {@link MeshCoord} instance, <strong>not null</strong>.
   * @throws InvalidUnitException When {@code meshUnit} is {@link MeshUnit#FIVE} although {@link
   *     MeshCoord#third()} is {@code 0} or {@code 5}.
   * @throws MeshCoordOverflowException When {@code this} is {@code MeshCoord(first=99, second=7,
   *     third=9)}.
   */
  public MeshCoord nextUp(final MeshUnit meshUnit)
      throws InvalidUnitException, MeshCoordOverflowException {
    if (!this.isMeshUnit(meshUnit)) {
      throw new InvalidUnitException();
    }

    final int bound = MeshUnit.ONE.equals(meshUnit) ? 9 : 5;

    if (this.third == bound) {
      if (this.second == MAX.second) {
        if (this.first == MAX.third) {
          throw new MeshCoordOverflowException();
        }
        return new MeshCoord(this.first + 1, MIN.second, MIN.third);
      }
      return new MeshCoord(this.first, this.second + 1, MIN.third);
    }
    return new MeshCoord(this.first, this.second, this.third + meshUnit.toInteger());
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
   * @param meshUnit The mesh unit, <strong>may not be null</strong>.
   * @return The up-next {@link MeshCoord} instance, <strong>not null</strong>.
   * @throws InvalidUnitException When {@code meshUnit} is {@link MeshUnit#FIVE} although {@link
   *     MeshCoord#third()} is {@code 0} or {@code 5}.
   * @throws MeshCoordOverflowException When {@code this} is {@code MeshCoord(first=0, second=0,
   *     third=0)}.
   */
  public MeshCoord nextDown(final MeshUnit meshUnit)
      throws InvalidUnitException, MeshCoordOverflowException {
    if (!this.isMeshUnit(meshUnit)) {
      throw new InvalidUnitException();
    }

    final int bound = MeshUnit.ONE.equals(meshUnit) ? 9 : 5;

    if (this.third == MIN.third) {
      if (this.second == MIN.second) {
        if (this.first == MIN.third) {
          throw new MeshCoordOverflowException();
        }
        return new MeshCoord(this.first - 1, MAX.second, bound);
      }
      return new MeshCoord(this.first, this.second - 1, bound);
    }
    return new MeshCoord(this.first, this.second, this.third - meshUnit.toInteger());
  }

  @Override
  public int compareTo(final MeshCoord o) {
    Objects.requireNonNull(o, "o");
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

  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof MeshCoord) {
      final MeshCoord other = (MeshCoord) o;
      return this.first == other.first && this.second == other.second && this.third == other.third;
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
