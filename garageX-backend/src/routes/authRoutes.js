const express = require("express");
const { register, login, me, changePassword, updateActiveVehicle } = require("../controllers/authController");
const protect = require("../middleware/authMiddleware");

const router = express.Router();
router.post("/register", register);
router.post("/login", login);
router.get("/me", protect, me);
router.put("/change-password", protect, changePassword);
router.patch("/active-vehicle", protect, updateActiveVehicle);

module.exports = router;
