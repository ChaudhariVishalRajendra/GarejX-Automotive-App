const Appointment = require("../models/Appointment");
const User = require("../models/User");

async function getMechanics(req, res) {
  const mechanics = await User.find({ role: "MECHANIC" }).select("-password").sort({ name: 1 });
  res.json({ success: true, count: mechanics.length, mechanics });
}

async function getMyJobs(req, res) {
  const jobs = await Appointment.find({ mechanic: req.user._id })
    .populate("customer", "name email phone")
    .populate("vehicle")
    .populate("service")
    .sort({ appointmentDate: -1 });

  res.json({ success: true, count: jobs.length, jobs });
}

async function getDashboard(req, res) {
  const [pending, assigned, inProgress, completed] = await Promise.all([
    Appointment.countDocuments({ mechanic: req.user._id, status: "PENDING" }),
    Appointment.countDocuments({ mechanic: req.user._id, status: "ASSIGNED" }),
    Appointment.countDocuments({ mechanic: req.user._id, status: "IN_PROGRESS" }),
    Appointment.countDocuments({ mechanic: req.user._id, status: "COMPLETED" })
  ]);

  res.json({ success: true, stats: { pending, assigned, inProgress, completed } });
}

module.exports = { getMechanics, getMyJobs, getDashboard };
