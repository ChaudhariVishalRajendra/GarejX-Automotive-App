const express = require("express");
const { getWallet, addMoney } = require("../controllers/walletController");
const protect = require("../middleware/authMiddleware");

const router = express.Router();
router.use(protect);

router.get("/", getWallet);
router.post("/topup", addMoney);

module.exports = router;
