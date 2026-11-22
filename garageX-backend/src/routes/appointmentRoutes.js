const express = require("express");
const protect = require("../middleware/authMiddleware");
const allowRoles = require("../middleware/roleMiddleware");
const { createAppointment, getAppointments, getAppointment, updateAppointment, assignMechanic, cancelAppointment } = require("../controllers/appointmentController");

const router = express.Router();
router.use(protect);
router.route("/").get(getAppointments).post(allowRoles("USER"), createAppointment);
router.put("/:id", updateAppointment);
router.get("/:id", getAppointment);
router.patch("/:id/cancel", cancelAppointment);
router.patch("/:id/assign", allowRoles("ADMIN"), assignMechanic);

module.exports = router;
