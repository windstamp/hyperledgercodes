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

        // Submit the specified transaction.
        {
            const json = {
                marble: Buffer.from('{"name":"marble1","color":"blue","size":35,"owner":"tom","price":99}')
            };
            await contract.createTransaction('initMarble').setTransient(json).submit();
            console.log('Transaction has been submitted');
        }
        {
            const json = {
                marble: Buffer.from('{"name":"marble2","color":"red","size":50,"owner":"tom","price":102}')
            };
            await contract.createTransaction('initMarble').setTransient(json).submit();
            console.log('Transaction has been submitted');
        }
        {
            const json = {
                marble: Buffer.from('{"name":"marble3","color":"blue","size":70,"owner":"tom","price":103}')
            };
            await contract.createTransaction('initMarble').setTransient(json).submit();
            console.log('Transaction has been submitted');
        }

        // Disconnect from the gateway.
        await gateway.disconnect();

    } catch (error) {
        console.error(`Failed to submit transaction: ${error}`);
        process.exit(1);
    }
}

main();
