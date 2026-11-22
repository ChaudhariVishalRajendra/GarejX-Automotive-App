const express = require("express");
const protect = require("../middleware/authMiddleware");
const { createVehicle, getVehicles, getVehicle, updateVehicle, deleteVehicle } = require("../controllers/vehicleController");

const router = express.Router();
router.use(protect);
router.route("/").get(getVehicles).post(createVehicle);
router.route("/:id").get(getVehicle).put(updateVehicle).delete(deleteVehicle);

module.exports = router;
