/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.fabfile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class FileTest {

    @Nested
    class Equality {

        @Test
        public void isReflexive() {
            File file = new File("0x080bf510fcbf18b91105470639e9561022937712", "Exchange", "abcde");

            assertThat(file).isEqualTo(file);
        }

        @Test
        public void isSymmetric() {
            File carA = new File("0x080bf510fcbf18b91105470639e9561022937712", "Exchange", "abcde");
            File carB = new File("0x080bf510fcbf18b91105470639e9561022937712", "Exchange", "abcde");

            assertThat(carA).isEqualTo(carB);
            assertThat(carB).isEqualTo(carA);
        }

        @Test
        public void isTransitive() {
            File carA = new File("0x080bf510fcbf18b91105470639e9561022937712", "Exchange", "abcde");
            File carB = new File("0x080bf510fcbf18b91105470639e9561022937712", "Exchange", "abcde");
            File carC = new File("0x080bf510fcbf18b91105470639e9561022937712", "Exchange", "abcde");

            assertThat(carA).isEqualTo(carB);
            assertThat(carB).isEqualTo(carC);
            assertThat(carA).isEqualTo(carC);
        }

        @Test
        public void handlesInequality() {
            File carA = new File("0x080bf510fcbf18b91105470639e9561022937712", "Exchange", "abcde");
            File carB = new File("0x95e6f48254609a6ee006f7d493c8e5fb97094cef", "ERC20Proxy", "12345");

            assertThat(carA).isNotEqualTo(carB);
        }

        @Test
        public void handlesOtherObjects() {
            File carA = new File("0x080bf510fcbf18b91105470639e9561022937712", "Exchange", "abcde");
            String carB = "not a file";

            assertThat(carA).isNotEqualTo(carB);
        }

        @Test
        public void handlesNull() {
            File file = new File("0x080bf510fcbf18b91105470639e9561022937712", "Exchange", "abcde");

            assertThat(file).isNotEqualTo(null);
        }
    }

    @Test
    public void toStringIdentifiesCar() {
        File file = new File("0x080bf510fcbf18b91105470639e9561022937712", "Exchange", "abcde");

        assertThat(file.toString()).isEqualTo("File@61a77e4f [make=Exchange, hash=0x080bf510fcbf18b91105470639e9561022937712, content=abcde]");
    }
}
