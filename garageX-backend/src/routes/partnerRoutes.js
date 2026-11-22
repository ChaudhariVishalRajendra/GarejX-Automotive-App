const express = require("express");
const { applyPartner, getPartnerApplications } = require("../controllers/partnerController");
const protect = require("../middleware/authMiddleware");
const allowRoles = require("../middleware/roleMiddleware");

const router = express.Router();

// Application can be submitted by anyone or logged-in users
router.post("/apply", applyPartner);
router.get("/", protect, allowRoles("ADMIN"), getPartnerApplications);

module.exports = router;
