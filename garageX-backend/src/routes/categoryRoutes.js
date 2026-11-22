const express = require("express");
const { getCategories, getBanners } = require("../controllers/categoryController");
const protect = require("../middleware/authMiddleware");

const router = express.Router();
router.use(protect);

router.get("/categories", getCategories);
router.get("/banners", getBanners);

module.exports = router;
