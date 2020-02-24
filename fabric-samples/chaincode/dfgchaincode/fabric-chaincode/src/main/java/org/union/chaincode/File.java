package org.union.chaincode;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType()
public final class File {

    @Property()
    private final String hash;  // file content hash

    @Property()
    private final String extraHash;     // file content with extra info hash

    @Property()
    private final Long timestamp;

    @Property()
    private final String txHash;
    @Property()
    private final String extra;

    public String getHash() {
        return hash;
    }

    public String getExtraHash() {
        return extraHash;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getTxHash() {
        return txHash;
    }

    public String getExtra() {
        return extra;
    }

    public File(@JsonProperty("hash") final String hash, @JsonProperty("extraHash") final String extraHash,
                @JsonProperty("timestamp") final Long timestamp, @JsonProperty("txHash") final String txHash,
                @JsonProperty("extra") final String extra) {
        this.hash = hash;
        this.extraHash = extraHash;
        this.timestamp = timestamp;
        this.txHash = txHash;
        this.extra = extra;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        File other = (File) obj;

        return Objects.deepEquals(new String[] {getHash(), getExtraHash(), getTimestamp().toString(), getTxHash(), getExtra()},
                new String[] {other.getHash(), other.getExtraHash(), other.getTimestamp().toString(), getTxHash(), getExtra()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHash(), getExtraHash(), getTimestamp(), getTxHash(), getExtra());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [hash=" + hash + ", extraHash="
                + extraHash + ", timestamp=" + timestamp + ", txHash=" + txHash + ", extra=" + extra + "]";
    }
}
