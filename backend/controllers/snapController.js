const snap = require("../config/midtransSnap");
const db = require("../config/firebase");
const response = require("../utils/response");

exports.createSnap = async (req, res) => {

    try {

        const { orderId, total } = req.body;

        console.log("======================================");
        console.log("SNAP CREATE");
        console.log("Order ID :", orderId);
        console.log("Total    :", total);
        console.log("======================================");
        
        const orderRef = db
            .collection("orders")
            .doc(orderId);

        const orderDoc = await orderRef.get();

        if (!orderDoc.exists) {

            return response.error(
                res,
                "Order tidak ditemukan",
                404
            );

        }

        const parameter = {

            transaction_details: {

                order_id: orderId,

                gross_amount: total

            }

        };

        const transaction =
            await snap.createTransaction(parameter);

        await orderRef.update({

            snapToken:
                transaction.token,

            redirectUrl:
                transaction.redirect_url,

            paymentStatus:
                "waiting"

        });

        console.log("======================================");
        console.log("SNAP TOKEN BERHASIL");
        console.log("Token :", transaction.token);
        console.log("URL   :", transaction.redirect_url);
        console.log("======================================");

        return response.success(

            res,

            "Snap berhasil dibuat",

            {

                token:
                    transaction.token,

                redirectUrl:
                    transaction.redirect_url

            }

        );

    } catch(err){

        console.log("======================================");
        console.log("SNAP ERROR");
        console.log(err);
        console.log(err.ApiResponse);
        console.log("======================================");

        return response.error(
            res,
            err.message
        );
    }

};