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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The coordinate Transformer, and represents a deserializing result of par file.
 *
 * <p>If the parameters is zero, such as the unsupported components, the transformations are
 * identity transformation on such components. For example, the transformation by the TKY2JGD and
 * the PatchJGD par is identity transformation on altitude, and by the PatchJGD(H) par is so on
 * latitude and longitude.
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * // From SemiDynaEXE2023.par
 * HashMap<Integer, Parameter> m = new HashMap<>();
 * m.put(54401005, new Parameter(-0.00622, 0.01516, 0.0946));
 * m.put(54401055, new Parameter(-0.0062, 0.01529, 0.08972));
 * m.put(54401100, new Parameter(-0.00663, 0.01492, 0.10374));
 * m.put(54401150, new Parameter(-0.00664, 0.01506, 0.10087));
 *
 * Transformer tf = new Transformer(Format.SemiDynaEXE, m);
 *
 * // Forward transformation
 * tf.forward(new Point(36.10377479, 140.087855041, 2.34));
 * // returns Point[latitude=36.103773017086695, longitude=140.08785924333452, altitude=2.4363138578103]
 *
 * // Backward transformation
 * tf.backward(new Point(36.103773017086695, 140.08785924333452, 2.4363138578103));
 * // returns Point[latitude=36.10377479000002, longitude=140.087855041, altitude=2.339999999578243]
 *
 * // Verified backward transformation
 * tf.backwardSafe(new Point(36.103773017086695, 140.08785924333452, 2.4363138578103));
 * // returns Point[latitude=36.10377479, longitude=140.087855041, altitude=2.3399999999970085]
 * }</pre>
 */
public class Transformer {
  /** not null */
  private final Format format;

  /** not null */
  private final Map<Integer, Parameter> parameter;

  private final String description;

  /**
   * Makes a {@link Transformer}.
   *
   * <p>This is equivalent to {@code Transformer(format, parameter, null)}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Transformer tf = new Transformer(Format.SemiDynaEXE, new HashMap<>());
   * assert tf.format().equals(Format.SemiDynaEXE);
   * assert tf.parameter().equals(new HashMap<>());
   * assert tf.description().equals(Optional.empty());
   * }</pre>
   *
   * @param format The format of par file, <strong>may not be null</strong>.
   * @param parameter The transformation parameter, <strong>may not be null</strong>.
   * @see Transformer#Transformer(Format, Map, String)
   * @see Transformer#format()
   * @see Transformer#parameter()
   * @see Transformer#description()
   */
  public Transformer(final Format format, final Map<Integer, Parameter> parameter) {
    this.format = Objects.requireNonNull(format, "format");
    this.parameter = Objects.requireNonNull(parameter, "parameter");
    this.description = null;
  }

  /**
   * Makes a {@link Transformer} with description.
   *
   * <p>The entry represents single line of par file's parameter section, <strong>may not be
   * null</strong>, and the key and the value is not null.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Transformer tf = new Transformer(Format.SemiDynaEXE, new HashMap<>(), "my parameter");
   * assert tf.format().equals(Format.SemiDynaEXE);
   * assert tf.parameter().equals(new HashMap<>());
   * assert tf.description().equals(Optional.of("my parameter"));
   * }</pre>
   *
   * @param format The format of par file, <strong>may not be null</strong>.
   * @param parameter The transformation parameter, <strong>may not be null</strong>.
   * @param description The description.
   * @see Transformer#format()
   * @see Transformer#parameter()
   * @see Transformer#description()
   */
  public Transformer(
      final Format format, final Map<Integer, Parameter> parameter, final String description) {
    this.format = Objects.requireNonNull(format, "format");
    this.parameter = Objects.requireNonNull(parameter, "parameter");
    this.description = description;
  }

  /**
   * Deserialize par-formatted {@code String} into a {@link Transformer}.
   *
   * <p>This is equivalent to {@code Transformer.fromString(content, format, null)}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * String contents = `<15 lines>
   * MeshCode dB(sec)  dL(sec) dH(m)
   * 12345678   0.00001   0.00002   0.00003`;
   *
   * Transformer tf = Transformer.fromString(contents, Format.SemiDynaEXE);
   * assert tf.parameter().get(12345678).equals(new Parameter(0.00001   0.00002   0.00003));
   * }</pre>
   *
   * @param content The par formatted text, <strong>may not be null</strong>.
   * @param format The format of the {@code content}, <strong>may not be null</strong>.
   * @return A {@link Transformer} instance, <strong>not null</strong>.
   * @throws ParseParException When invalid data found.
   * @see Transformer#fromString(String, Format, String)
   */
  public static Transformer fromString(final String content, final Format format)
      throws ParseParException {
    Objects.requireNonNull(content, "content");
    Objects.requireNonNull(format, "format");

    return Transformer.fromString(content, format, null);
  }

  /**
   * Deserialize par-formatted {@code String} into a {@link Transformer} with description.
   *
   * <p>This fills by {@code 0.0} for altitude parameter when {@link Format#TKY2JGD} or {@link
   * Format#PatchJGD} given, and for latitude and longitude when {@link Format#PatchJGD_H} or {@link
   * Format#HyokoRev} given.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * String contents = `<15 lines>
   * MeshCode dB(sec)  dL(sec) dH(m)
   * 12345678   0.00001   0.00002   0.00003`;
   * String description = "My parameter";
   *
   * Transformer tf = Transformer.fromString(contents, description, Format.SemiDynaEXE);
   * assert tf.parameter().get(12345678).equals(new Parameter(0.00001, 0.00002, 0.00003));
   * assert tf.description().equals(Optional.of("My parameter"));
   * }</pre>
   *
   * @param content The par formatted text, <strong>may not be null</strong>.
   * @param format The format of the {@code content}, <strong>may not be null</strong>.
   * @param description The description.
   * @return A {@link Transformer} instance, <strong>not null</strong>.
   * @throws ParseParException When invalid data found.
   */
  public static Transformer fromString(
      final String content, final Format format, final String description)
      throws ParseParException {
    Objects.requireNonNull(content, "content");
    Objects.requireNonNull(format, "format");

    try (final BufferedReader reader = new BufferedReader(new StringReader(content))) {
      return Parser.readValue(reader, format, description);
    } catch (final IOException neverHappen) {
      throw new RuntimeException("UNREACHABLE");
    }
  }

  private static double bilinearInterpolation(
      final double sw,
      final double se,
      final double nw,
      final double ne,
      final double latitude,
      final double longitude) {
    return sw * (1.0 - longitude) * (1.0 - latitude)
        + se * longitude * (1.0 - latitude)
        + nw * (1.0 - longitude) * latitude
        + ne * longitude * latitude;
  }

  /**
   * Returns the format.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Transformer tf = new Transformer(Format.SemiDynaEXE, new HashMap<>(), "my parameter");
   * assert tf.format().equals(Format.SemiDynaEXE);
   * }</pre>
   *
   * @return The format, <strong>not null</strong>.
   */
  public Format format() {
    return this.format;
  }

  /**
   * Returns the parameters.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Transformer tf = new Transformer(Format.SemiDynaEXE, new HashMap<>(), "my parameter");
   * assert tf.parameter().equals(new HashMap<>());
   * }</pre>
   *
   * @return The parameter, <strong>not null</strong>.
   */
  public Map<Integer, Parameter> parameter() {
    return this.parameter;
  }

  /**
   * Returns the description (the header of par file).
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * Transformer tf = new Transformer(Format.SemiDynaEXE, new HashMap<>(), "my parameter");
   * assert tf.description().equals(Optional.of("my parameter"));
   * }</pre>
   *
   * @return The description (the header of par file), <strong>not null</strong>.
   */
  public Optional<String> description() {
    return Optional.ofNullable(this.description);
  }

  /**
   * Returns the forward-transformed position.
   *
   * <p>The {@link Point#latitude()} should satisfy {@code 0.0 <=} and {@code <= 66.666...} and the
   * {@link Point#longitude()} does {@code 100.0 <=} and {@code <= 180.0}.
   *
   * <p>This is formally equivalent to {@code point + this.forwardCorrection(point)}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * // From SemiDynaEXE2023.par
   * HashMap<Integer, Parameter> m = new HashMap<>();
   * m.put(54401005, new Parameter(-0.00622, 0.01516, 0.0946));
   * m.put(54401055, new Parameter(-0.0062, 0.01529, 0.08972));
   * m.put(54401100, new Parameter(-0.00663, 0.01492, 0.10374));
   * m.put(54401150, new Parameter(-0.00664, 0.01506, 0.10087));
   *
   * Transformer tf = new Transformer(Format.SemiDynaEXE, m);
   *
   * Point point = tf.forward(new Point(36.10377479, 140.087855041, 2.34));
   * assert point.equals(new Point(36.103773017086695, 140.08785924333452, 2.4363138578103));
   * }</pre>
   *
   * @param point The origin of transformation, <strong>may not be null</strong>.
   * @return The forwardly transformed point, <strong>not null</strong>.
   * @throws ParameterNotFoundException When the parameter is not found.
   * @throws PointOutOfRangeException When the {@code point} is ouf-of-bounds.
   * @see Transformer#forwardCorrection(Point)
   */
  public Point forward(final Point point)
      throws ParameterNotFoundException, PointOutOfRangeException {
    final Correction correction = this.forwardCorrection(point);
    return new Point(
        point.latitude + correction.latitude,
        point.longitude + correction.longitude,
        point.altitude + correction.altitude);
  }

  /**
   * Returns the backward-transformed position.
   *
   * <p>The {@link Point#latitude()} should satisfy {@code 0.0 <=} and {@code <= 66.666...} and the
   * {@link Point#longitude()} does {@code 100.0 <=} and {@code <= 180.0}.
   *
   * <p>This is <em>not</em> exact as the original <em>TKY2JGD for Windows Ver.1.3.79</em> and the
   * web APIs are (as far as we researched).
   *
   * <p>There are points where unable to perform backward transformation even if they are the
   * results of the forward transformation, because the forward transformation moves them to the
   * area where the parameter does not support.
   *
   * <p>We note that {@link Transformer#backwardSafe(Point)} performs verified backward
   * transformation.
   *
   * <p>This is formally equivalent to {@code point + this.backwardCorrection(point)}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * // From SemiDynaEXE2023.par
   * HashMap<Integer, Parameter> m = new HashMap<>();
   * m.put(54401005, new Parameter(-0.00622, 0.01516, 0.0946));
   * m.put(54401055, new Parameter(-0.0062, 0.01529, 0.08972));
   * m.put(54401100, new Parameter(-0.00663, 0.01492, 0.10374));
   * m.put(54401150, new Parameter(-0.00664, 0.01506, 0.10087));
   *
   * Transformer tf = new Transformer(Format.SemiDynaEXE, m);
   *
   * Point point = tf.backward(new Point(36.103773017086695, 140.08785924333452, 2.4363138578103));
   * assert point.equals(new Point(36.10377479000002, 140.087855041, 2.339999999578243));
   * }</pre>
   *
   * @param point The origin of transformation, <strong>may not be null</strong>.
   * @return The backwardly transformed point, <strong>not null</strong>.
   * @throws ParameterNotFoundException When the parameter not found.
   * @throws ParameterNotFoundException When the parameter is not found.
   * @throws PointOutOfRangeException When the {@code point} is ouf-of-bounds.
   * @see Transformer#backwardCorrection(Point)
   * @see Transformer#backwardSafe(Point)
   */
  public Point backward(final Point point)
      throws ParameterNotFoundException, PointOutOfRangeException {
    final Correction correction = this.backwardCorrection(point);
    return new Point(
        point.latitude + correction.latitude,
        point.longitude + correction.longitude,
        point.altitude + correction.altitude);
  }

  /**
   * Returns the validated backward-transformed position.
   *
   * <p>The result's drifting from the exact solution is less than error of the GIAJ latitude and
   * longitude parameter, {@code 2.7e-9} [deg], for each latitude and longitude. The altitude's
   * drifting is less than {@code 1e-5} [m] which is error of the GIAJ altitude parameter.
   *
   * <p>The {@link Point#latitude()} should satisfy {@code 0.0 <=} and {@code <= 66.666...} and the
   * {@link Point#longitude()} does {@code 100.0 <=} and {@code <= 180.0}.
   *
   * <p>This is formally equivalent to {@code point + this.backwardSafeCorrection(point)}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * // From SemiDynaEXE2023.par
   * HashMap<Integer, Parameter> m = new HashMap<>();
   * m.put(54401005, new Parameter(-0.00622, 0.01516, 0.0946));
   * m.put(54401055, new Parameter(-0.0062, 0.01529, 0.08972));
   * m.put(54401100, new Parameter(-0.00663, 0.01492, 0.10374));
   * m.put(54401150, new Parameter(-0.00664, 0.01506, 0.10087));
   *
   * Transformer tf = new Transformer(Format.SemiDynaEXE, m);
   *
   * Point point = tf.backwardSafe(new Point(36.103773017086695, 140.08785924333452, 2.4363138578103));
   * assert point.equals(new Point(36.10377479, 140.087855041, 2.3399999999970085));
   * }</pre>
   *
   * @param point The origin of transformation, <strong>may not be null</strong>.
   * @return The backwardly transformed point, <strong>not null</strong>.
   * @throws ParameterNotFoundException When parameter is not found.
   * @throws CorrectionNotFoundException When verification failed.
   * @throws PointOutOfRangeException When the {@code point} is ouf-of-bounds.
   * @see Transformer#backwardSafeCorrection(Point)
   * @see Transformer#backward(Point)
   */
  public Point backwardSafe(final Point point)
      throws CorrectionNotFoundException, ParameterNotFoundException, PointOutOfRangeException {
    final Correction correction = this.backwardSafeCorrection(point);
    return new Point(
        point.latitude + correction.latitude,
        point.longitude + correction.longitude,
        point.altitude + correction.altitude);
  }

  /**
   * Return the correction on forward-transformation.
   *
   * <p>The {@link Point#latitude()} should satisfy {@code 0.0 <=} and {@code <= 66.666...} and the
   * {@link Point#longitude()} does {@code 100.0 <=} and {@code <= 180.0}.
   *
   * <p>This is used by {@link Transformer#forward(Point)}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * // From SemiDynaEXE2023.par
   * HashMap<Integer, Parameter> m = new HashMap<>();
   * m.put(54401005, new Parameter(-0.00622, 0.01516, 0.0946));
   * m.put(54401055, new Parameter(-0.0062, 0.01529, 0.08972));
   * m.put(54401100, new Parameter(-0.00663, 0.01492, 0.10374));
   * m.put(54401150, new Parameter(-0.00664, 0.01506, 0.10087));
   *
   * Transformer tf = new Transformer(Format.SemiDynaEXE, m);
   *
   * Point point = tf.forwardCorrection(new Point(336.10377479, 140.087855041, 2.34));
   * assert point.equals(new Correction(-1.7729133100878255e-06, 4.202334510058886e-06, 0.09631385781030007));
   * }</pre>
   *
   * @param point The origin of transformation, <strong>may not be null</strong>.
   * @return The correction on forward transformation, <strong>not null</strong>.
   * @throws ParameterNotFoundException When the parameter is not found.
   * @throws PointOutOfRangeException When the {@code point} is ouf-of-bounds.
   * @see Transformer#forward(Point)
   */
  public Correction forwardCorrection(final Point point)
      throws ParameterNotFoundException, PointOutOfRangeException {
    Objects.requireNonNull(point, "point");

    final MeshCell cell;
    try {
      cell = MeshCell.ofPoint(point, this.format.meshUnit());
    } catch (final Exception e) {
      throw new PointOutOfRangeException(e);
    }

    final Quadruple quadruple = this.parameterQuadruple(cell);
    final MeshCell.Position position = cell.position(point);

    final double SCALE = 3600.0;

    final double latitude =
        bilinearInterpolation(
                quadruple.sw.latitude,
                quadruple.se.latitude,
                quadruple.nw.latitude,
                quadruple.ne.latitude,
                position.y,
                position.x)
            / SCALE;

    final double longitude =
        bilinearInterpolation(
                quadruple.sw.longitude,
                quadruple.se.longitude,
                quadruple.nw.longitude,
                quadruple.ne.longitude,
                position.y,
                position.x)
            / SCALE;

    final double altitude =
        bilinearInterpolation(
            quadruple.sw.altitude,
            quadruple.se.altitude,
            quadruple.nw.altitude,
            quadruple.ne.altitude,
            position.y,
            position.x);

    return new Correction(latitude, longitude, altitude);
  }

  /**
   * Return the correction on backward-transformation.
   *
   * <p>The {@link Point#latitude()} should satisfy {@code 0.00333... <=} and {@code <= 66.666...}
   * and the {@link Point#longitude()} does {@code 100.0 <=} and {@code <= 180.0}.
   *
   * <p>This is used by {@link Transformer#backward(Point)}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * // From SemiDynaEXE2023.par
   * HashMap<Integer, Parameter> m = new HashMap<>();
   * m.put(54401005, new Parameter(-0.00622, 0.01516, 0.0946));
   * m.put(54401055, new Parameter(-0.0062, 0.01529, 0.08972));
   * m.put(54401100, new Parameter(-0.00663, 0.01492, 0.10374));
   * m.put(54401150, new Parameter(-0.00664, 0.01506, 0.10087));
   *
   * Transformer tf = new Transformer(Format.SemiDynaEXE, m);
   *
   * Point point = tf.backwardCorrection(new Point(36.103773017086695, 140.08785924333452, 2.4363138578103));
   * assert point.equals(new Correction(1.7729133219831587e-06, -4.202334509042613e-06, -0.0963138582320569));
   * }</pre>
   *
   * @param point The origin of transformation, <strong>may not be null</strong>.
   * @return The correction on backward transformation, <strong>not null</strong>.
   * @throws ParameterNotFoundException When the parameter is not found.
   * @throws PointOutOfRangeException When the {@code point} is ouf-of-bounds.
   * @see Transformer#backward(Point)
   * @see Transformer#backwardSafeCorrection(Point)
   */
  public Correction backwardCorrection(final Point point)
      throws ParameterNotFoundException, PointOutOfRangeException {
    Objects.requireNonNull(point, "point");

    final double DELTA = 1.0 / 300.0;

    final Point temporal =
        new Point(point.latitude - DELTA, point.longitude + DELTA, point.altitude);
    Correction correction = this.forwardCorrection(temporal);

    final Point reference =
        new Point(
            point.latitude - correction.latitude,
            point.longitude - correction.longitude,
            point.altitude - correction.altitude);

    // actual correction
    correction = this.forwardCorrection(reference);
    return new Correction(-correction.latitude, -correction.longitude, -correction.altitude);
  }

  /**
   * Return the verified correction on backward-transformation.
   *
   * <p>The {@link Point#latitude()} should satisfy {@code 0.0 <=} and {@code <= 66.666...} and the
   * {@link Point#longitude()} does {@code 100.0 <=} and {@code <= 180.0}.
   *
   * <p>This is used by {@link Transformer#backwardSafe(Point)}.
   *
   * <h4>Example</h4>
   *
   * <pre>{@code
   * // From SemiDynaEXE2023.par
   * HashMap<Integer, Parameter> m = new HashMap<>();
   * m.put(54401005, new Parameter(-0.00622, 0.01516, 0.0946));
   * m.put(54401055, new Parameter(-0.0062, 0.01529, 0.08972));
   * m.put(54401100, new Parameter(-0.00663, 0.01492, 0.10374));
   * m.put(54401150, new Parameter(-0.00664, 0.01506, 0.10087));
   *
   * Transformer tf = new Transformer(Format.SemiDynaEXE, m);
   *
   * Point point = tf.backwardSafeCorrection(new Point(36.103773017086695, 140.08785924333452, 2.4363138578103));
   * assert point.equals(new Correction(1.772913310099049e-06, -4.202334510033827e-06, -0.0963138578132916));
   * }</pre>
   *
   * @param point The origin of transformation, <strong>may not be null</strong>.
   * @return The correction on backward transformation, <strong>not null</strong>.
   * @throws ParameterNotFoundException When the parameter is not found.
   * @throws CorrectionNotFoundException When verification failed.
   * @throws PointOutOfRangeException When the {@code point} is ouf-of-bounds.
   * @see Transformer#backwardSafe(Point)
   * @see Transformer#backwardCorrection(Point)
   */
  public Correction backwardSafeCorrection(final Point point)
      throws CorrectionNotFoundException, ParameterNotFoundException, PointOutOfRangeException {
    Objects.requireNonNull(point, "point");

    final double SCALE = 3600.;
    final double CRITERIA = 2.5e-9;
    final int ITERATION = 3;

    double yn = point.latitude;
    double xn = point.longitude;

    for (int i = 0; i < ITERATION; i++) {
      final Point current = new Point(yn, xn, 0.0);

      final MeshCell cell;
      try {
        cell = MeshCell.ofPoint(current, this.format.meshUnit());
      } catch (final Exception e) {
        throw new PointOutOfRangeException(e);
      }
      final Quadruple quadruple = parameterQuadruple(cell);
      final MeshCell.Position position = cell.position(current);

      final double corr_y =
          bilinearInterpolation(
                  quadruple.sw.latitude,
                  quadruple.se.latitude,
                  quadruple.nw.latitude,
                  quadruple.ne.latitude,
                  position.y,
                  position.x)
              / SCALE;

      final double corr_x =
          bilinearInterpolation(
                  quadruple.sw.longitude,
                  quadruple.se.longitude,
                  quadruple.nw.longitude,
                  quadruple.ne.longitude,
                  position.y,
                  position.x)
              / SCALE;

      final double fx = point.longitude - (xn + corr_x);
      final double fy = point.latitude - (yn + corr_y);

      // fx_x, fx_y, fy_x, fy_y

      double a1;
      double a2;

      a1 = quadruple.se.longitude - quadruple.sw.longitude;
      a2 = quadruple.ne.longitude - quadruple.nw.longitude;
      final double fx_x = -1.0 - (a1 * (1.0 - yn) + a2 * yn) / SCALE;

      a1 = quadruple.nw.longitude - quadruple.sw.longitude;
      a2 = quadruple.ne.longitude - quadruple.se.longitude;
      final double fx_y = -(a1 * (1.0 - xn) + a2 * xn) / SCALE;

      a1 = quadruple.se.latitude - quadruple.sw.latitude;
      a2 = quadruple.ne.latitude - quadruple.nw.latitude;
      final double fy_x = -(a1 * (1.0 - yn) + a2 * yn) / SCALE;

      a1 = quadruple.ne.latitude - quadruple.sw.latitude;
      a2 = quadruple.ne.latitude - quadruple.se.latitude;
      final double fy_y = -1.0 - (a1 * (1.0 - xn) + a2 * xn) / SCALE;

      // det
      final double det = fx_x * fy_y - fy_x * fy_x;

      xn -= (fy_y * fx - fx_y * fy) / det;
      yn -= (fx_x * fy - fy_x * fx) / det;

      // verify
      final Correction correction = this.forwardCorrection(new Point(yn, xn, 0.0));

      final double delta_x = point.longitude - (xn + correction.longitude);
      final double delta_y = point.latitude - (yn + correction.latitude);

      if (Math.abs(delta_x) <= CRITERIA && Math.abs(delta_y) <= CRITERIA) {
        return new Correction(-correction.latitude, -correction.longitude, -correction.altitude);
      }
    }

    throw new CorrectionNotFoundException();
  }

  private Quadruple parameterQuadruple(final MeshCell cell) throws ParameterNotFoundException {
    int meshcode;

    meshcode = cell.southWest.toMeshcode();
    final Parameter sw = this.parameter.get(meshcode);
    if (Objects.isNull(sw)) {
      throw new ParameterNotFoundException("south west");
    }

    meshcode = cell.southEast.toMeshcode();
    final Parameter se = this.parameter.get(meshcode);
    if (Objects.isNull(se)) {
      throw new ParameterNotFoundException("south east");
    }

    meshcode = cell.northWest.toMeshcode();
    final Parameter nw = this.parameter.get(meshcode);
    if (Objects.isNull(nw)) {
      throw new ParameterNotFoundException("north west");
    }

    meshcode = cell.northEast.toMeshcode();
    final Parameter ne = this.parameter.get(meshcode);
    if (Objects.isNull(ne)) {
      throw new ParameterNotFoundException("north east");
    }

    return new Quadruple(sw, se, nw, ne);
  }

  @Override
  public String toString() {
    final Optional<String> description =
        Optional.ofNullable(this.description)
            .map(s -> 13 < s.length() ? String.format("'%10s...'", s) : s)
            .map(s -> s.replace("\n", "\\n"));
    return String.format(
        "Transformer[format=%s, parameter=%s(%d entries), description=%s]",
        this.format,
        this.parameter.getClass().getCanonicalName(),
        this.parameter.size(),
        description);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof Transformer;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (o instanceof Transformer) {
      final Transformer other = (Transformer) o;
      if (other.canEqual(this)) {
        return Objects.equals(this.format, other.format)
            && Objects.equals(this.description, other.description)
            && Objects.equals(this.parameter, other.parameter);
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;

    result = result * PRIME + this.format.hashCode();
    result = result * PRIME + (this.description == null ? 43 : this.description.hashCode());
    result = result * PRIME + this.parameter.hashCode();

    return result;
  }

  private static class Quadruple {
    protected final Parameter sw;
    protected final Parameter se;
    protected final Parameter nw;
    protected final Parameter ne;

    private Quadruple(
        final Parameter sw, final Parameter se, final Parameter nw, final Parameter ne) {
      this.sw = sw;
      this.se = se;
      this.nw = nw;
      this.ne = ne;
    }
  }
}
