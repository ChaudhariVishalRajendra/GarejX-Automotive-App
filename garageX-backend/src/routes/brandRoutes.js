const express = require("express");
const { getBrands, getModelsByBrand } = require("../controllers/brandController");
const protect = require("../middleware/authMiddleware");

const router = express.Router();
router.use(protect);

router.get("/", getBrands);
router.get("/:brandId/models", getModelsByBrand);

module.exports = router;
