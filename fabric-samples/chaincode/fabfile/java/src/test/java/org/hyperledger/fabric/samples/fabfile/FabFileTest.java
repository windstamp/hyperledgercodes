/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.fabfile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

public final class FabFileTest {

    private final class MockKeyValue implements KeyValue {

        private final String key;
        private final String value;

        MockKeyValue(final String key, final String value) {
            super();
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getStringValue() {
            return this.value;
        }

        @Override
        public byte[] getValue() {
            return this.value.getBytes();
        }

    }

    private final class MockFileResultsIterator implements QueryResultsIterator<KeyValue> {

        private final List<KeyValue> fileList;

        MockFileResultsIterator() {
            super();

            fileList = new ArrayList<KeyValue>();

            fileList.add(new MockKeyValue("0x080bf510fcbf18b91105470639e9561022937712",
                    "{\"hash\":\"0x080bf510fcbf18b91105470639e9561022937712\",\"name\":\"Exchange\",\"content\":\"abcde\"}"));
            fileList.add(new MockKeyValue("0x95e6f48254609a6ee006f7d493c8e5fb97094cef",
                    "{\"hash\":\"0x95e6f48254609a6ee006f7d493c8e5fb97094cef\",\"name\":\"ERC20Proxy\",\"content\":\"12345\"}"));
            // fileList.add(new MockKeyValue("CAR002",
            //         "{\"color\":\"green\",\"make\":\"Hyundai\",\"model\":\"Tucson\",\"owner\":\"Jin Soo\"}"));
            // fileList.add(new MockKeyValue("CAR007",
            //         "{\"color\":\"violet\",\"make\":\"Fiat\",\"model\":\"Punto\",\"owner\":\"Pari\"}"));
            // fileList.add(new MockKeyValue("CAR009",
            //         "{\"color\":\"brown\",\"make\":\"Holden\",\"model\":\"Barina\",\"owner\":\"Shotaro\"}"));
        }

        @Override
        public Iterator<KeyValue> iterator() {
            return fileList.iterator();
        }

        @Override
        public void close() throws Exception {
            // do nothing
        }

    }

    @Test
    public void invokeUnknownTransaction() {
        FabFile contract = new FabFile();
        Context ctx = mock(Context.class);

        Throwable thrown = catchThrowable(() -> {
            contract.unknownTransaction(ctx);
        });

        assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                .hasMessage("Undefined contract method called");
        assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo(null);

        verifyZeroInteractions(ctx);
    }

    @Nested
    class InvokeQueryFileTransaction {

        @Test
        public void whenFileExists() {
            FabFile contract = new FabFile();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("0x080bf510fcbf18b91105470639e9561022937712"))
                    .thenReturn("{\"hash\":\"0x080bf510fcbf18b91105470639e9561022937712\",\"name\":\"Exchange\",\"content\":\"abcde\"}");

            File file = contract.queryFile(ctx, "0x080bf510fcbf18b91105470639e9561022937712");

            assertThat(file).isEqualTo(new File("0x080bf510fcbf18b91105470639e9561022937712", "Exchange", "abcde"));
        }

        @Test
        public void whenFileDoesNotExist() {
            FabFile contract = new FabFile();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("0x1dc4c1cefef38a777b15aa20260a54e584b16c48")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.queryFile(ctx, "0x1dc4c1cefef38a777b15aa20260a54e584b16c48");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("File 0x1dc4c1cefef38a777b15aa20260a54e584b16c48 does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("FILE_NOT_FOUND".getBytes());
        }
    }

    @Test
    void invokeInitLedgerTransaction() {
        FabFile contract = new FabFile();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);

        contract.initLedger(ctx);

        InOrder inOrder = inOrder(stub);
        inOrder.verify(stub).putStringState("0x080bf510fcbf18b91105470639e9561022937712",
                "{\"hash\":\"0x080bf510fcbf18b91105470639e9561022937712\",\"name\":\"Exchange\",\"content\":\"abcde\"}");
        inOrder.verify(stub).putStringState("0x95e6f48254609a6ee006f7d493c8e5fb97094cef",
                "{\"hash\":\"0x95e6f48254609a6ee006f7d493c8e5fb97094cef\",\"name\":\"ERC20Proxy\",\"content\":\"12345\"}");
        // inOrder.verify(stub).putStringState("CAR002",
        //         "{\"color\":\"green\",\"make\":\"Hyundai\",\"model\":\"Tucson\",\"owner\":\"Jin Soo\"}");
        // inOrder.verify(stub).putStringState("CAR003",
        //         "{\"color\":\"yellow\",\"make\":\"Volkswagen\",\"model\":\"Passat\",\"owner\":\"Max\"}");
        // inOrder.verify(stub).putStringState("CAR004",
        //         "{\"color\":\"black\",\"make\":\"Tesla\",\"model\":\"S\",\"owner\":\"Adrian\"}");
        // inOrder.verify(stub).putStringState("CAR005",
        //         "{\"color\":\"purple\",\"make\":\"Peugeot\",\"model\":\"205\",\"owner\":\"Michel\"}");
        // inOrder.verify(stub).putStringState("CAR006",
        //         "{\"color\":\"white\",\"make\":\"Chery\",\"model\":\"S22L\",\"owner\":\"Aarav\"}");
        // inOrder.verify(stub).putStringState("CAR007",
        //         "{\"color\":\"violet\",\"make\":\"Fiat\",\"model\":\"Punto\",\"owner\":\"Pari\"}");
        // inOrder.verify(stub).putStringState("CAR008",
        //         "{\"color\":\"indigo\",\"make\":\"Tata\",\"model\":\"nano\",\"owner\":\"Valeria\"}");
        // inOrder.verify(stub).putStringState("CAR009",
        //         "{\"color\":\"brown\",\"make\":\"Holden\",\"model\":\"Barina\",\"owner\":\"Shotaro\"}");
    }

    @Nested
    class InvokeCreateFileTransaction {

        @Test
        public void whenFileExists() {
            FabFile contract = new FabFile();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("0x080bf510fcbf18b91105470639e9561022937712"))
                    .thenReturn("{\"hash\":\"0x080bf510fcbf18b91105470639e9561022937712\",\"name\":\"Exchange\",\"content\":\"abcde\"}");

            Throwable thrown = catchThrowable(() -> {
                contract.createFile(ctx, "0x080bf510fcbf18b91105470639e9561022937712", "Exchange", "abcde");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("File 0x080bf510fcbf18b91105470639e9561022937712 already exists");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("FILE_ALREADY_EXISTS".getBytes());
        }

        @Test
        public void whenFileDoesNotExist() {
            FabFile contract = new FabFile();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("0x080bf510fcbf18b91105470639e9561022937712")).thenReturn("");

            File file = contract.createFile(ctx, "0x080bf510fcbf18b91105470639e9561022937712", "Exchange", "abcde");

            assertThat(file).isEqualTo(new File("0x080bf510fcbf18b91105470639e9561022937712", "Exchange", "abcde"));
        }
    }

}
