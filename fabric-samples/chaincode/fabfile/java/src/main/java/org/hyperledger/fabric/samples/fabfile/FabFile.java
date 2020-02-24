/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.fabfile;

import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;

/**
 * Java implementation of the Fabric File Contract described in the Writing Your
 * First Application tutorial
 */
@Contract(
        name = "FabFile",
        info = @Info(
                title = "FabFile contract",
                description = "The hyperlegendary file contract",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "f.carr@example.com",
                        name = "F Carr",
                        url = "https://hyperledger.example.com")))
@Default
public final class FabFile implements ContractInterface {

    private final Genson genson = new Genson();

    private enum FabFileErrors {
        FILE_NOT_FOUND,
        FILE_ALREADY_EXISTS
    }

    /**
     * Retrieves a file with the specified hash from the ledger.
     *
     * @param ctx the transaction context
     * @param hash the hash
     * @return the File found on the ledger if there was one
     */
    @Transaction()
    public File queryFile(final Context ctx, final String hash) {
        final ChaincodeStub stub = ctx.getStub();
        final String fileState = stub.getStringState(hash);

        if (fileState.isEmpty()) {
            final String errorMessage = String.format("File %s does not exist", hash);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabFileErrors.FILE_NOT_FOUND.toString());
        }

        final File file = genson.deserialize(fileState, File.class);

        return file;
    }

    /**
     * Creates some initial Files on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction()
    public void initLedger(final Context ctx) {
        final ChaincodeStub stub = ctx.getStub();

        final String[] fileData = {
                "{ \"hash\": \"0x080bf510fcbf18b91105470639e9561022937712\", \"name\": \"Exchange\", \"content\": \"abcde\" }",
                "{ \"hash\": \"0x95e6f48254609a6ee006f7d493c8e5fb97094cef\", \"name\": \"ERC20Proxy\", \"content\": \"12345\" }" };

        for (int i = 0; i < fileData.length; i++) {
            // final String key = fileData[i].hash;
            String key = "";

            if (i == 0)
                key = "0x080bf510fcbf18b91105470639e9561022937712";
            else if (i == 1)
                key = "0x95e6f48254609a6ee006f7d493c8e5fb97094cef";

            final File file = genson.deserialize(fileData[i], File.class);
            final String fileState = genson.serialize(file);
            stub.putStringState(key, fileState);
        }
    }

    /**
     * Creates a new file on the ledger.
     *
     * @param ctx   the transaction context
     * @param hash   the hash for the new file
     * @param name  the name of the new file
     * @param content the content of the new file
     * @return the created File
     */
    @Transaction()
    public File createFile(final Context ctx, final String hash, final String name, final String content) {
        final ChaincodeStub stub = ctx.getStub();

        String fileState = stub.getStringState(hash);
        if (!fileState.isEmpty()) {
            final String errorMessage = String.format("File %s already exists", hash);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabFileErrors.FILE_ALREADY_EXISTS.toString());
        }

        final File file = new File(hash, name, content);
        fileState = genson.serialize(file);
        stub.putStringState(hash, fileState);

        return file;
    }
}
