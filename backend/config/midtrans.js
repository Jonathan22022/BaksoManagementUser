require("dotenv").config();

console.log("===============");
console.log("Production :", process.env.MIDTRANS_IS_PRODUCTION);
console.log("Server Key :", process.env.MIDTRANS_SERVER_KEY);
console.log("Client Key :", process.env.MIDTRANS_CLIENT_KEY);
console.log("===============");

const midtransClient = require("midtrans-client");

const coreApi = new midtransClient.CoreApi({

    isProduction:
        process.env.MIDTRANS_IS_PRODUCTION === "true",

    serverKey:
        process.env.MIDTRANS_SERVER_KEY,

    clientKey:
        process.env.MIDTRANS_CLIENT_KEY
});

module.exports = coreApi;