/*
 * SPDX-License-Identifier: Apache-2.0
 */

'use strict';

const { Contract } = require('fabric-contract-api');

class FabFile extends Contract {

    async initLedger(ctx) {
        console.info('============= START : Initialize Ledger ===========');
        const files = [
            {
                hash: '0x080bf510fcbf18b91105470639e9561022937712',
                name: 'Exchange',
            },
            {
                hash: '0x95e6f48254609a6ee006f7d493c8e5fb97094cef',
                name: 'ERC20Proxy',
            },
        ];

        for (let i = 0; i < files.length; i++) {
            files[i].docType = 'file';
            await ctx.stub.putState(files[i].hash, Buffer.from(JSON.stringify(files[i])));
            console.info('Added <--> ', files[i]);
        }
        console.info('============= END : Initialize Ledger ===========');
    }

    async queryFile(ctx, fileHash) {
        const fileAsBytes = await ctx.stub.getState(fileHash); // get the file from chaincode state
        if (!fileAsBytes || fileAsBytes.length === 0) {
            throw new Error(`${fileHash} does not exist`);
        }
        console.log(fileAsBytes.toString());
        return fileAsBytes.toString();
    }

    async createFile(ctx, hash, name) {
        console.info('============= START : Create File ===========');

        const file = {
            hash,
            docType: 'file',
            name,
        };

        await ctx.stub.putState(hash, Buffer.from(JSON.stringify(file)));
        console.info('============= END : Create File ===========');
    }
    
}

module.exports = FabFile;
