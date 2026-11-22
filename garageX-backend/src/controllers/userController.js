const bcrypt = require("bcryptjs");
const mongoose = require("mongoose");
const User = require("../models/User");

async function getUsers(req, res) {
  const users = await User.find().select("-password").sort({ createdAt: -1 });
  res.json({ success: true, count: users.length, users });
}

async function getUser(req, res) {
  if (!mongoose.isValidObjectId(req.params.id)) return res.status(400).json({ success: false, message: "Invalid user ID" });
  const user = await User.findById(req.params.id).select("-password");
  if (!user) return res.status(404).json({ success: false, message: "User not found" });
  res.json({ success: true, user });
}

async function createStaff(req, res) {
  try {
    const { name, email, phone, password, role } = req.body;
    if (!name || !email || !phone || !password || !["ADMIN", "MECHANIC"].includes(role)) {
      return res.status(400).json({ success: false, message: "name, email, phone, password and valid role are required" });
    }

    const exists = await User.findOne({ email: email.toLowerCase() });
    if (exists) return res.status(409).json({ success: false, message: "Email already registered" });

    const user = await User.create({ name, email, phone, password: await bcrypt.hash(password, 10), role });
    res.status(201).json({ success: true, message: `${role} created`, user: { ...user.toObject(), password: undefined } });
  } catch (error) {
    res.status(500).json({ success: false, message: error.message });
  }
}

async function updateUser(req, res) {
  if (!mongoose.isValidObjectId(req.params.id)) return res.status(400).json({ success: false, message: "Invalid user ID" });
  const user = await User.findById(req.params.id);
  if (!user) return res.status(404).json({ success: false, message: "User not found" });

  ["name", "phone", "address", "profileImage", "role"].forEach((field) => {
    if (req.body[field] !== undefined) user[field] = req.body[field];
  });

  await user.save();
  res.json({ success: true, message: "User updated", user: { ...user.toObject(), password: undefined } });
}

async function deleteUser(req, res) {
  if (!mongoose.isValidObjectId(req.params.id)) return res.status(400).json({ success: false, message: "Invalid user ID" });
  if (req.params.id === req.user._id.toString()) return res.status(400).json({ success: false, message: "You cannot delete your own account" });
  const user = await User.findByIdAndDelete(req.params.id);
  if (!user) return res.status(404).json({ success: false, message: "User not found" });
  res.json({ success: true, message: "User deleted" });
}

module.exports = { getUsers, getUser, createStaff, updateUser, deleteUser };
