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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PointTest {
  @Test
  void normalize() {
    // latitude
    assertEquals(new Point(0., 0.), new Point(0., 0.).normalize());
    assertEquals(new Point(0.0, 0.0), new Point(0.0, 0.0).normalize());
    assertEquals(new Point(-0.0, 0.0), new Point(-0.0, 0.0).normalize());
    assertEquals(new Point(20.0, 0.0), new Point(20.0, 0.0).normalize());
    assertEquals(new Point(-20.0, 0.0), new Point(-20.0, 0.0).normalize());
    assertEquals(new Point(0.0, 0.0), new Point(360.0, 0.0).normalize());
    assertEquals(new Point(-90.0, 0.0), new Point(270.0, 0.0).normalize());
    assertEquals(new Point(0.0, 0.0), new Point(180.0, 0.0).normalize());
    assertEquals(new Point(90.0, 0.0), new Point(90.0, 0.0).normalize());
    assertEquals(new Point(-0.0, 0.0), new Point(-360.0, 0.0).normalize());
    assertEquals(new Point(90.0, 0.0), new Point(-270.0, 0.0).normalize());
    assertEquals(new Point(0.0, 0.0), new Point(-180.0, 0.0).normalize());
    //      assertEquals(new Point(-0.0, 0.0), new Point(-180.0, 0.0).normalize());
    assertEquals(new Point(-90.0, 0.0), new Point(-90.0, 0.0).normalize());
    assertEquals(new Point(20., 0.0), new Point(380., 0.0).normalize());
    assertEquals(new Point(-70., 0.0), new Point(290., 0.0).normalize());
    assertEquals(new Point(-20., 0.0), new Point(200., 0.0).normalize());
    assertEquals(new Point(70., 0.0), new Point(110., 0.0).normalize());
    assertEquals(new Point(-20., 0.0), new Point(-380., 0.0).normalize());
    assertEquals(new Point(70., 0.0), new Point(-290., 0.0).normalize());
    assertEquals(new Point(20., 0.0), new Point(-200., 0.0).normalize());
    assertEquals(new Point(-70., 0.0), new Point(-110., 0.0).normalize());

    // longitude
    assertEquals(new Point(0.0, 0.0), new Point(0.0, 0.0).normalize());
    assertEquals(new Point(0.0, -0.0), new Point(0.0, -0.0).normalize());
    assertEquals(new Point(0.0, 20.0), new Point(0.0, 20.0).normalize());
    assertEquals(new Point(0.0, -20.0), new Point(0.0, -20.0).normalize());
    assertEquals(new Point(0.0, 0.0), new Point(0.0, 360.0).normalize());
    assertEquals(new Point(0.0, -90.0), new Point(0.0, 270.0).normalize());
    assertEquals(new Point(0.0, 180.0), new Point(0.0, 180.0).normalize());
    assertEquals(new Point(0.0, 90.0), new Point(0.0, 90.0).normalize());
    assertEquals(new Point(0.0, -0.0), new Point(0.0, -360.0).normalize());
    assertEquals(new Point(0.0, 90.0), new Point(0.0, -270.0).normalize());
    assertEquals(new Point(0.0, -180.0), new Point(0.0, -180.0).normalize());
    assertEquals(new Point(0.0, -90.0), new Point(0.0, -90.0).normalize());
    assertEquals(new Point(0.0, 20.0), new Point(0.0, 380.).normalize());
    assertEquals(new Point(0.0, -70.0), new Point(0.0, 290.).normalize());
    assertEquals(new Point(0.0, -160.0), new Point(0.0, 200.).normalize());
    assertEquals(new Point(0.0, 110.0), new Point(0.0, 110.).normalize());
    assertEquals(new Point(0.0, -20.0), new Point(0.0, -380.).normalize());
    assertEquals(new Point(0.0, 70.0), new Point(0.0, -290.).normalize());
    assertEquals(new Point(0.0, 160.0), new Point(0.0, -200.).normalize());
    assertEquals(new Point(0.0, -110.0), new Point(0.0, -110.).normalize());
  }
}
