# jgdtrans for Java

[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.paqira/jgdtrans?logo=java)](https://mvnrepository.com/artifact/io.github.paqira/jgdtrans)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/paqira/jgdtrans-java/ci.yaml?logo=GitHub)
[![Static Badge](https://img.shields.io/badge/doc-passing-default?logo=GitHub)](https://paqira.github.io/jgdtrans-java/)
![GitHub License](https://img.shields.io/github/license/paqira/jgdtrans-java)

Unofficial coordinate transformer by _Gridded Correction Parameter_
which Geospatial Information Authority of Japan (GIAJ, formerly GSIJ) distributing
for Java.

国土地理院が公開しているパラメータファイル（par ファイル）による座標変換（順逆変換）の非公式な実装です。

Features:

- Supports offline transformation (no web API)
    - オフライン変換（web API 不使用）
- Supports both original forward and backward transformation
    - 順変換と逆変換の両方をサポート
- Supports verified backward transformation
    - 精度を保証した逆変換のサポート
- Supports all [TKY2JGD], [PatchJGD], [PatchJGD(H)], [HyokoRev], [SemiDynaEXE]
  and [POS2JGD] (geonetF3 and ITRF2014)
    - For example, Tokyo Datum to JGD2000 ([EPSG:4301] to [EPSG:4612])
      and JGD2000 to JGD2011 ([EPSG:4612] to [EPSG:6668])
    - 上記の全てをサポート
- Clean implementation
    - 保守が容易な実装

[TKY2JGD]: https://www.gsi.go.jp/sokuchikijun/tky2jgd.html
[PatchJGD]: https://vldb.gsi.go.jp/sokuchi/surveycalc/patchjgd/index.html
[PatchJGD(H)]: https://vldb.gsi.go.jp/sokuchi/surveycalc/patchjgd_h/index.html
[HyokoRev]: https://vldb.gsi.go.jp/sokuchi/surveycalc/hyokorev/hyokorev.html
[SemiDynaEXE]: https://vldb.gsi.go.jp/sokuchi/surveycalc/semidyna/web/index.html
[POS2JGD]: https://positions.gsi.go.jp/cdcs

[EPSG:4301]: https://epsg.io/4301
[EPSG:4612]: https://epsg.io/4612
[EPSG:6668]: https://epsg.io/6668

## Usage

`jgdtrans` supports `Java8` and higher.

This package does not contain parameter files, download it from GIAJ.

このパッケージはパラメータファイルを提供しません。公式サイトよりダウンロードしてください。

Sample code:

```java
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import jgdtrans.*;

public class Main {
    public static void main(String[] args)
            throws IOException, CorrectionNotFoundException, ParameterNotFoundException, ParseParException {
        File file = new File("SemiDyna2023.par");
        String contents = Files.readString(file.toPath(), StandardCharsets.UTF_8);

        Transformer tf = Transformer.fromString(contents, Format.SemiDynaEXE);

        // Geospatial Information Authority of Japan
        Point origin = new Point(36.10377479, 140.087855041, 2.34);

        // forward transformation
        Point result = tf.forward(origin);
        // prints Point[latitude=36.103773017086695, longitude=140.08785924333452, altitude=2.4363138578103]
        System.out.println(result);

        // backward transformation 
        Point p = tf.backward(result);
        // prints Point[latitude=36.10377479, longitude=140.087855041, altitude=2.34]
        System.out.println(p);

        // backward transformation compatible to GIAJ web app/APIs
        Point p = tf.backwardCompat(result);
        // prints Point[latitude=36.10377479000002, longitude=140.087855041, altitude=2.339999999578243]
        System.out.println(p);
    }
}
```

## Licence

Apache-2.0

## Reference

1. Geospatial Information Authority of Japan (GIAJ, 国土地理院):
   <https://www.gsi.go.jp/>, (English) <https://www.gsi.go.jp/ENGLISH/>.
2. _TKY2JGD for Windows Ver.1.3.79_ (reference implementation):
   <https://www.gsi.go.jp/sokuchikijun/tky2jgd_download.html>
   released under [国土地理院コンテンツ利用規約] which compatible to CC BY 4.0.
3. Other implementation:
   Rust <https://github.com/paqira/jgdtrans-rs>,
   Python <https://github.com/paqira/jgdtrans-py>,
   JavaScript/TypeScript <https://github.com/paqira/jgdtrans-js>.

[国土地理院コンテンツ利用規約]: https://www.gsi.go.jp/kikakuchousei/kikakuchousei40182.html
