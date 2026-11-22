const express = require("express");
const protect = require("../middleware/authMiddleware");
const allowRoles = require("../middleware/roleMiddleware");
const { createBill, getBills, getBill, updatePayment } = require("../controllers/billController");

const router = express.Router();
router.use(protect);
router.get("/", getBills);
router.get("/:id", getBill);
router.post("/", allowRoles("ADMIN", "MECHANIC"), createBill);
router.patch("/:id/payment", allowRoles("ADMIN", "MECHANIC"), updatePayment);

module.exports = router;
