package org.union.chaincode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.union.chaincode.common.Const;
import org.union.chaincode.common.ResponseData;
import org.union.chaincode.enums.FabFileErrorsEnum;

import java.io.IOException;

/**
 * Java implementation of the Fabric File Contract
 */
@Contract(
        name = "FabFile",
        info = @Info(
                title = "FabFile contract",
                description = "The hyperlegendary file contract",
                version = "1.0",
                license = @License(
                        name = "",
                        url = ""),
                contact = @Contact(
                        email = "",
                        name = "",
                        url = "")))
@Default
public final class FabFile implements ContractInterface {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Retrieves a file with the specified hash from the ledger.
     *
     * @param ctx the transaction context
     * @param hash the hash
     * @return the File found on the ledger if there was one
     */
    @Transaction()
    public ResponseData<File> queryFile(final Context ctx, final String hash) {
        final ChaincodeStub stub = ctx.getStub();
        final String fileState = stub.getStringState(hash);

        if (fileState.isEmpty()) {
            return ResponseData.fail(FabFileErrorsEnum.FILE_NOT_FOUND.toString());
        }
        try {
            return ResponseData.ok(objectMapper.readValue(fileState, File.class));
        } catch (IOException e) {
            return ResponseData.fail(FabFileErrorsEnum.JSON_DESERIALIZATIONFEATURE_ERROR.toString());
        }
    }

    /**
     * Creates some initial Files on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction()
    public void initLedger(final Context ctx) {
    }

    /**
     * Creates a new file on the ledger.
     *
     * @param ctx   the transaction context
     * @param hash   the hash for the new file
     * @param extraHash  the hash of the new file with extra info
     * @return the created File
     */
    @Transaction()
    public ResponseData<File> createFile(final Context ctx, final String hash, final String extraHash, final String extra) throws JsonProcessingException {
        final ChaincodeStub stub = ctx.getStub();
        if (hash.length() > Const.HASH_MAX_LENGTH || extraHash.length() > Const.HASH_MAX_LENGTH
            || extra.length() > Const.EXTRA_MAX_LENGTH) {
            return ResponseData.fail(FabFileErrorsEnum.LENGTH_TOO_LARGE.toString());
        }
        String fileState = stub.getStringState(hash);
        if (!fileState.isEmpty()) {
            return ResponseData.fail(FabFileErrorsEnum.FILE_ALREADY_EXISTS.toString());
        }

        final File file = new File(hash, extraHash, stub.getTxTimestamp().toEpochMilli(), stub.getTxId(), extra);
        fileState = objectMapper.writeValueAsString(file);
        stub.putStringState(hash, fileState);
        return ResponseData.ok(file);
    }
}
