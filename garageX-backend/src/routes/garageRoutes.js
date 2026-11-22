const express = require("express");
const protect = require("../middleware/authMiddleware");
const allowRoles = require("../middleware/roleMiddleware");
const { getGarage, updateGarage } = require("../controllers/garageController");

const router = express.Router();
router.get("/", protect, getGarage);
router.put("/", protect, allowRoles("ADMIN"), updateGarage);

module.exports = router;
