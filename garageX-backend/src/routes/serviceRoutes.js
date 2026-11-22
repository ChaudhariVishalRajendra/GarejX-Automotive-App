const express = require("express");
const protect = require("../middleware/authMiddleware");
const allowRoles = require("../middleware/roleMiddleware");
const { getServices, getService, createService, updateService, deleteService } = require("../controllers/serviceController");

const router = express.Router();
router.get("/", protect, getServices);
router.get("/:id", protect, getService);
router.post("/", protect, allowRoles("ADMIN"), createService);
router.put("/:id", protect, allowRoles("ADMIN"), updateService);
router.delete("/:id", protect, allowRoles("ADMIN"), deleteService);

module.exports = router;
