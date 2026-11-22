const mongoose = require("mongoose");
const Vehicle = require("../models/Vehicle");

async function createVehicle(req, res) {
  try {
    const { vehicleNumber, brand, model, fuelType, manufacturingYear, kilometers } = req.body;

    if (!vehicleNumber || !brand || !model || !fuelType) {
      return res.status(400).json({ success: false, message: "vehicleNumber, brand, model and fuelType are required" });
    }

    const vehicle = await Vehicle.create({
      user: req.user._id,
      vehicleNumber,
      brand,
      model,
      fuelType,
      manufacturingYear,
      kilometers
    });

    res.status(201).json({ success: true, message: "Vehicle created", vehicle });
  } catch (error) {
    if (error.code === 11000) return res.status(409).json({ success: false, message: "Vehicle number already exists" });
    res.status(500).json({ success: false, message: error.message });
  }
}

async function getVehicles(req, res) {
  const filter = req.user.role === "ADMIN" ? {} : { user: req.user._id };
  const vehicles = await Vehicle.find(filter).populate("user", "name email phone").sort({ createdAt: -1 });
  res.json({ success: true, count: vehicles.length, vehicles });
}

async function getVehicle(req, res) {
  if (!mongoose.isValidObjectId(req.params.id)) return res.status(400).json({ success: false, message: "Invalid vehicle ID" });

  const vehicle = await Vehicle.findById(req.params.id).populate("user", "name email phone");
  if (!vehicle) return res.status(404).json({ success: false, message: "Vehicle not found" });

  if (req.user.role !== "ADMIN" && vehicle.user._id.toString() !== req.user._id.toString()) {
    return res.status(403).json({ success: false, message: "Access denied" });
  }

  res.json({ success: true, vehicle });
}

async function updateVehicle(req, res) {
  if (!mongoose.isValidObjectId(req.params.id)) return res.status(400).json({ success: false, message: "Invalid vehicle ID" });

  const vehicle = await Vehicle.findById(req.params.id);
  if (!vehicle) return res.status(404).json({ success: false, message: "Vehicle not found" });

  if (req.user.role !== "ADMIN" && vehicle.user.toString() !== req.user._id.toString()) {
    return res.status(403).json({ success: false, message: "Access denied" });
  }

  const fields = ["vehicleNumber", "brand", "model", "fuelType", "manufacturingYear", "kilometers"];
  fields.forEach((field) => {
    if (req.body[field] !== undefined) vehicle[field] = req.body[field];
  });

  await vehicle.save();
  res.json({ success: true, message: "Vehicle updated", vehicle });
}

async function deleteVehicle(req, res) {
  if (!mongoose.isValidObjectId(req.params.id)) return res.status(400).json({ success: false, message: "Invalid vehicle ID" });

  const vehicle = await Vehicle.findById(req.params.id);
  if (!vehicle) return res.status(404).json({ success: false, message: "Vehicle not found" });

  if (req.user.role !== "ADMIN" && vehicle.user.toString() !== req.user._id.toString()) {
    return res.status(403).json({ success: false, message: "Access denied" });
  }

  await vehicle.deleteOne();
  res.json({ success: true, message: "Vehicle deleted" });
}

module.exports = { createVehicle, getVehicles, getVehicle, updateVehicle, deleteVehicle };
