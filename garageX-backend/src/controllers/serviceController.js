const mongoose = require("mongoose");
const Service = require("../models/Service");

async function getServices(req, res) {
  const filter = req.user?.role === "ADMIN" ? {} : { isActive: true };
  const services = await Service.find(filter).sort({ name: 1 });
  res.json({ success: true, count: services.length, services });
}

async function getService(req, res) {
  if (!mongoose.isValidObjectId(req.params.id)) return res.status(400).json({ success: false, message: "Invalid service ID" });
  const service = await Service.findById(req.params.id);
  if (!service) return res.status(404).json({ success: false, message: "Service not found" });
  res.json({ success: true, service });
}

async function createService(req, res) {
  try {
    const { name, description, price, estimatedTime } = req.body;
    if (!name || price === undefined) return res.status(400).json({ success: false, message: "Name and price are required" });
    const service = await Service.create({ name, description, price, estimatedTime });
    res.status(201).json({ success: true, message: "Service created", service });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
}

async function updateService(req, res) {
  if (!mongoose.isValidObjectId(req.params.id)) return res.status(400).json({ success: false, message: "Invalid service ID" });
  const service = await Service.findByIdAndUpdate(req.params.id, req.body, { new: true, runValidators: true });
  if (!service) return res.status(404).json({ success: false, message: "Service not found" });
  res.json({ success: true, message: "Service updated", service });
}

async function deleteService(req, res) {
  if (!mongoose.isValidObjectId(req.params.id)) return res.status(400).json({ success: false, message: "Invalid service ID" });
  const service = await Service.findByIdAndDelete(req.params.id);
  if (!service) return res.status(404).json({ success: false, message: "Service not found" });
  res.json({ success: true, message: "Service deleted" });
}

module.exports = { getServices, getService, createService, updateService, deleteService };
