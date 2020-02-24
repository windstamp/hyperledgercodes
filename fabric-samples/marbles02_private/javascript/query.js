/*
 * SPDX-License-Identifier: Apache-2.0
 */

'use strict';

const { FileSystemWallet, Gateway } = require('fabric-network');
const path = require('path');

const ccpPath = path.resolve(__dirname, '..', '..', 'first-network', 'connection-org1.json');

async function main() {
    try {

        // Create a new file system based wallet for managing identities.
        const walletPath = path.join(process.cwd(), 'wallet');
        const wallet = new FileSystemWallet(walletPath);
        console.log(`Wallet path: ${walletPath}`);

        // Check to see if we've already enrolled the user.
        const userExists = await wallet.exists('user2');
        if (!userExists) {
            console.log('An identity for the user "user2" does not exist in the wallet');
            console.log('Run the registerUser.js application before retrying');
            return;
        }

        // Create a new gateway for connecting to our peer node.
        const gateway = new Gateway();
        await gateway.connect(ccpPath, { wallet, identity: 'user2', discovery: { enabled: true, asLocalhost: true } });

        // Get the network (channel) our contract is deployed to.
        const network = await gateway.getNetwork('mychannel');

        // Get the contract from the network.
        const contract = network.getContract('marblesp');

        // Evaluate the specified transaction.
        {
            const name = 'marble1';
            const result = await contract.evaluateTransaction('readMarble', name);
            console.log(`Transaction has been evaluated, result is: ${result.toString()}`);
        }
        {
            const name = 'marble1';
            const result = await contract.evaluateTransaction('readMarblePrivateDetails', name);
            console.log(`Transaction has been evaluated, result is: ${result.toString()}`);
        }
        {
            const start_key = 'marble1';
            const end_key = 'marble3';
            const result = await contract.evaluateTransaction('getMarblesByRange', start_key, end_key);
            console.log(`Transaction has been evaluated, result is: ${result.toString()}`);
        }

    } catch (error) {
        console.error(`Failed to evaluate transaction: ${error}`);
        process.exit(1);
    }
}

main();
