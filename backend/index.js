require("dotenv").config();

const express = require("express");

const cors = require("cors");

const app = express();

const paymentRoutes =
require("./routes/paymentRoutes");

const orderRoutes =
require("./routes/orderRoutes");

const snapRoutes =
    require("./routes/snapRoutes");

app.use(cors());

app.use(express.json());

app.use(

    "/api/payment",

    paymentRoutes

);

app.use(

    "/api/orders",

    orderRoutes

);

app.use(

    "/api/snap",

    snapRoutes

);

app.get("/", (req,res)=>{

    res.send("Bakso Backend Running");

});

const PORT =
process.env.PORT || 3000;

app.listen(PORT, ()=>{

    console.log(

        `Server running on ${PORT}`

    );

});