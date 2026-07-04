require("dotenv").config();

const midtransClient = require("midtrans-client");

const coreApi = new midtransClient.CoreApi({
    isProduction: false,
    serverKey: process.env.MIDTRANS_SERVER_KEY,
    clientKey: process.env.MIDTRANS_CLIENT_KEY
});

async function test() {
    try {
        const result = await coreApi.charge({
            payment_type: "qris",
            transaction_details: {
                order_id: "TEST-" + Date.now(),
                gross_amount: 10000
            }
        });

        console.log(result);
    } catch (err) {
        console.log(err);
    }
}

test();