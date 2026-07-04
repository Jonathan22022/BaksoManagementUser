const express = require("express");

const router = express.Router();

const snapController =
    require("../controllers/snapController");

router.post(

    "/create",

    snapController.createSnap

);

module.exports = router;