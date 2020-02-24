/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;

public class ClientApp {

	public static void main(String[] args) throws Exception {
		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallet.createFileSystemWallet(walletPath);

		// load a CCP
		Path networkConfigPath = Paths.get("..", "..", "first-network", "connection-org1.yaml");

		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "user1").networkConfig(networkConfigPath).discovery(true);

		// create a gateway connection
		try (Gateway gateway = builder.connect()) {

			// get the network and contract
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract("fabfile");

			network.getChannel().queryBlockByNumber(0).getBlock().getHeader();
			
			byte[] result;

			result = contract.evaluateTransaction("queryFile", "0x080bf510fcbf18b91105470639e9561022937712");
			System.out.println(new String(result));

			contract.submitTransaction("createFile", "0xefc70a1b18c432bdc64b596838b4d138f6bc6cad", "ERC721Proxy", "xyz");

			result = contract.evaluateTransaction("queryFile", "0xefc70a1b18c432bdc64b596838b4d138f6bc6cad");
			System.out.println(new String(result));
		}
	}

}
