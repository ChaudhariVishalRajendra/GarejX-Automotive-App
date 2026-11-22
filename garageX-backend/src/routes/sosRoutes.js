const express = require("express");
const { createSOS, getSOSRequests } = require("../controllers/sosController");
const protect = require("../middleware/authMiddleware");

const router = express.Router();
router.use(protect);

router.post("/", createSOS);
router.get("/", getSOSRequests);

module.exports = router;
