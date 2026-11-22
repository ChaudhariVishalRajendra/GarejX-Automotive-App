const Brand = require("../models/Brand");
const VehicleModel = require("../models/VehicleModel");

exports.getBrands = async (req, res) => {
  try {
    const brands = await Brand.find().sort({ order: 1, name: 1 });
    res.json({ success: true, count: brands.length, brands });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};

exports.getModelsByBrand = async (req, res) => {
  try {
    const { brandId } = req.params;
    let query = {};
    if (brandId.match(/^[0-9a-fA-F]{24}$/)) {
      query = { brand: brandId };
    } else {
      query = { brandName: new RegExp(`^${brandId}$`, "i") };
    }
    const models = await VehicleModel.find(query).sort({ order: 1, name: 1 });
    res.json({ success: true, count: models.length, models });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};
