/**
 * Unofficial coordinate transformer by <em>Gridded Correction Parameter</em> which Geospatial
 * Information Authority of Japan (GIAJ, formerly GSIJ) distributing.
 *
 * <p>国土地理院が公開しているパラメータファイル（par ファイル）による座標変換（順逆変換）の非公式な実装です。
 *
 * <h2>Features</h2>
 *
 * <ul>
 *   <li>Supports offline transformation (no web API)
 *       <ul>
 *         <li>オフライン変換（web API 不使用）
 *       </ul>
 *   <li>Supports both original forward and backward transformation
 *       <ul>
 *         <li>順変換と逆変換の両方をサポート
 *       </ul>
 *   <li>Supports verified backward transformation
 *       <ul>
 *         <li>精度を保証した逆変換のサポート
 *       </ul>
 *   <li>Supports all <a href="https://www.gsi.go.jp/sokuchikijun/tky2jgd.html" >TKY2JGD</a>, <a
 *       href="https://vldb.gsi.go.jp/sokuchi/surveycalc/patchjgd/index.html" >PatchJGD</a> <a
 *       href="https://vldb.gsi.go.jp/sokuchi/surveycalc/patchjgd_h/index.html" >PatchJGD(H)</a>, <a
 *       href="https://vldb.gsi.go.jp/sokuchi/surveycalc/hyokorev/hyokorev.html" >HyokoRev</a>, <a
 *       href="https://vldb.gsi.go.jp/sokuchi/surveycalc/semidyna/web/index.html" >SemiDynaEXE</a>
 *       and <a href="https://positions.gsi.go.jp/cdcs" >POS2JGD</a> (geonetF3 and ITRF2014)
 *       <ul>
 *         <li>For example, Tokyo Datum to JGD2000 (<a href="https://epsg.io/4301" >EPSG:4301</a> to
 *             <a href="https://epsg.io/4612" >EPSG:4612</a>) and JGD2000 to JGD2011 (<a
 *             href="https://epsg.io/4612" >EPSG:4612</a> to <a href="https://epsg.io/6668"
 *             >EPSG:6668</a>)
 *         <li>上記の全てをサポート
 *       </ul>
 *   <li>Clean implementation
 *       <ul>
 *         <li>保守が容易な実装
 *       </ul>
 * </ul>
 *
 * <h2>Usage</h2>
 *
 * This package does not contain parameter files, download it from GIAJ.
 *
 * <p>このパッケージはパラメータファイルを提供しません。公式サイトよりダウンロードしてください。
 *
 * <pre>{@code
 * import java.io.File;
 * import java.io.Files;
 * import java.nio.charset.StandardCharsets;
 *
 * import paqira.jgdtrans.*;
 *
 * public class Main {
 *     public static void main(String[] args)
 *             throws IOException, CorrectionNotFoundException, ParameterNotFoundException, ParseParException {
 *         File file = new File("SemiDyna2023.par");
 *         String contents = Files.readString(file.toPath(), StandardCharsets.UTF_8);
 *
 *         Transformer tf = Transformer.fromString(contents, ParFileFormat.SemiDynaEXE);
 *
 *         // Geospatial Information Authority of Japan
 *         Point origin = new Point(36.10377479, 140.087855041, 2.34);
 *
 *         // forward transformation
 *         Point result = tf.forward(origin);
 *         // prints Point[latitude=36.103773017086695, longitude=140.08785924333452, altitude=2.4363138578103]
 *         System.out.println(result);
 *
 *         // backward transformation
 *         Point p = tf.backward(result);
 *         // prints Point[latitude=36.10377479000002, longitude=140.087855041, altitude=2.339999999578243
 *         System.out.println(p);
 *
 *         // verified backward transformation
 *         Point q = tf.backwardSafe(result);
 *         // prints Point[latitude=36.10377479, longitude=140.087855041, altitude=2.3399999999970085
 *         System.out.println(q);
 *     }
 * }
 * }</pre>
 *
 * <h2>Licence</h2>
 *
 * Apache-2.0
 *
 * <h2>Reference</h2>
 *
 * <ol>
 *   <li>Geospatial Information Authority of Japan (GIAJ, 国土地理院): <a href="https://www.gsi.go.jp/"
 *       >https://www.gsi.go.jp/</a>, (English) <a href="https://www.gsi.go.jp/ENGLISH/"
 *       >https://www.gsi.go.jp/ENGLISH/</a>.
 *   <li><em>TKY2JGD for Windows Ver.1.3.79</em> (reference implementation): <a
 *       href="https://www.gsi.go.jp/sokuchikijun/tky2jgd_download.html" >
 *       https://www.gsi.go.jp/sokuchikijun/tky2jgd_download.html</a> released under <a
 *       href="https://www.gsi.go.jp/kikakuchousei/kikakuchousei40182.html" >国土地理院コンテンツ利用規約</a>
 *       which compatible to CC BY 4.0.
 *   <li>Other implementation: Rust <a href="https://github.com/paqira/jgdtrans-rs"
 *       >https://github.com/paqira/jgdtrans-rs</a>, Python <a
 *       href="https://github.com/paqira/jgdtrans-py" >https://github.com/paqira/jgdtrans-py</a> ,
 *       JavaScript/TypeScript <a href="https://github.com/paqira/jgdtrans-js"
 *       >https://github.com/paqira/jgdtrans-js</a>.
 * </ol>
 */
package jgdtrans;
