const mongoose = require("mongoose");
const Appointment = require("../models/Appointment");
const Vehicle = require("../models/Vehicle");
const Service = require("../models/Service");
const User = require("../models/User");

const populate = [
  { path: "customer", select: "name email phone" },
  { path: "vehicle" },
  { path: "service" },
  { path: "mechanic", select: "name email phone" }
];

async function createAppointment(req, res) {
  try {
    const { vehicle, service, appointmentDate, problemDescription } = req.body;

    if (!vehicle || !service || !appointmentDate) {
      return res.status(400).json({ success: false, message: "vehicle, service and appointmentDate are required" });
    }

    const vehicleData = await Vehicle.findOne({ _id: vehicle, user: req.user._id });
    if (!vehicleData) return res.status(400).json({ success: false, message: "Vehicle not found for this user" });

    const serviceData = await Service.findOne({ _id: service, isActive: true });
    if (!serviceData) return res.status(400).json({ success: false, message: "Active service not found" });

    const appointment = await Appointment.create({
      customer: req.user._id,
      vehicle,
      service,
      appointmentDate,
      problemDescription,
      estimatedCost: serviceData.price
    });

    await appointment.populate(populate);
    res.status(201).json({ success: true, message: "Appointment created", appointment });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
}

async function getAppointments(req, res) {
  let filter = {};
  if (req.user.role === "USER") filter.customer = req.user._id;
  if (req.user.role === "MECHANIC") filter.mechanic = req.user._id;

  const appointments = await Appointment.find(filter).populate(populate).sort({ appointmentDate: -1 });
  res.json({ success: true, count: appointments.length, appointments });
}

async function getAppointment(req, res) {
  if (!mongoose.isValidObjectId(req.params.id)) return res.status(400).json({ success: false, message: "Invalid appointment ID" });

  const appointment = await Appointment.findById(req.params.id).populate(populate);
  if (!appointment) return res.status(404).json({ success: false, message: "Appointment not found" });

  const isOwner = appointment.customer._id.toString() === req.user._id.toString();
  const isMechanic = appointment.mechanic?._id.toString() === req.user._id.toString();
  if (req.user.role === "USER" && !isOwner) return res.status(403).json({ success: false, message: "Access denied" });
  if (req.user.role === "MECHANIC" && !isMechanic) return res.status(403).json({ success: false, message: "Access denied" });

  res.json({ success: true, appointment });
}

async function updateAppointment(req, res) {
  if (!mongoose.isValidObjectId(req.params.id)) return res.status(400).json({ success: false, message: "Invalid appointment ID" });

  const appointment = await Appointment.findById(req.params.id);
  if (!appointment) return res.status(404).json({ success: false, message: "Appointment not found" });

  if (req.user.role === "USER") {
    if (appointment.customer.toString() !== req.user._id.toString()) return res.status(403).json({ success: false, message: "Access denied" });
    if (req.body.status && req.body.status !== "CANCELLED") return res.status(403).json({ success: false, message: "User can only cancel an appointment" });
  }

  if (req.user.role === "MECHANIC" && appointment.mechanic?.toString() !== req.user._id.toString()) {
    return res.status(403).json({ success: false, message: "Access denied" });
  }

  const fields = ["appointmentDate", "problemDescription", "status", "mechanicNotes", "finalCost"];
  fields.forEach((field) => {
    if (req.body[field] !== undefined) appointment[field] = req.body[field];
  });

  await appointment.save();
  await appointment.populate(populate);
  res.json({ success: true, message: "Appointment updated", appointment });
}

async function assignMechanic(req, res) {
  const { mechanicId } = req.body;
  if (!mongoose.isValidObjectId(mechanicId)) return res.status(400).json({ success: false, message: "Valid mechanicId is required" });

  const mechanic = await User.findOne({ _id: mechanicId, role: "MECHANIC" });
  if (!mechanic) return res.status(400).json({ success: false, message: "Mechanic not found" });

  const appointment = await Appointment.findByIdAndUpdate(
    req.params.id,
    { mechanic: mechanicId, status: "ASSIGNED" },
    { new: true }
  ).populate(populate);

  if (!appointment) return res.status(404).json({ success: false, message: "Appointment not found" });
  res.json({ success: true, message: "Mechanic assigned", appointment });
}

async function cancelAppointment(req, res) {
  const appointment = await Appointment.findById(req.params.id);
  if (!appointment) return res.status(404).json({ success: false, message: "Appointment not found" });

  if (req.user.role === "USER" && appointment.customer.toString() !== req.user._id.toString()) {
    return res.status(403).json({ success: false, message: "Access denied" });
  }

  appointment.status = "CANCELLED";
  await appointment.save();
  res.json({ success: true, message: "Appointment cancelled", appointment });
}

module.exports = { createAppointment, getAppointments, getAppointment, updateAppointment, assignMechanic, cancelAppointment };
