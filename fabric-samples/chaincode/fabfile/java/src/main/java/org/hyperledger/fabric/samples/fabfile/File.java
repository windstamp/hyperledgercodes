/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.fabfile;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class File {

    @Property()
    private final String name;

    @Property()
    private final String hash;

    @Property()
    private final String content;

    public String getName() {
        return name;
    }

    public String getHash() {
        return hash;
    }

    public String getContent() {
        return content;
    }

    public File(@JsonProperty("hash") final String hash, @JsonProperty("name") final String name,
            @JsonProperty("content") final String content) {
        this.hash = hash;
        this.name = name;
        this.content = content;
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

        return Objects.deepEquals(new String[] {getName(), getHash(), getContent()},
                new String[] {other.getName(), other.getHash(), other.getContent()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getHash(), getContent());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [name=" + name + ", hash="
                + hash + ", content=" + content + "]";
    }
}
