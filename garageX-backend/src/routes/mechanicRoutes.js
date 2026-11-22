const express = require("express");
const protect = require("../middleware/authMiddleware");
const allowRoles = require("../middleware/roleMiddleware");
const { getMechanics, getMyJobs, getDashboard } = require("../controllers/mechanicController");

const router = express.Router();
router.use(protect);
router.get("/", allowRoles("ADMIN", "MECHANIC"), getMechanics);
router.get("/my-jobs", allowRoles("MECHANIC"), getMyJobs);
router.get("/dashboard", allowRoles("MECHANIC"), getDashboard);

module.exports = router;
