const express = require("express");
const cors = require("cors");
const mongoose = require("mongoose");

const authRoutes = require("./routes/authRoutes");
const vehicleRoutes = require("./routes/vehicleRoutes");
const serviceRoutes = require("./routes/serviceRoutes");
const appointmentRoutes = require("./routes/appointmentRoutes");
const mechanicRoutes = require("./routes/mechanicRoutes");
const billRoutes = require("./routes/billRoutes");
const userRoutes = require("./routes/userRoutes");
const garageRoutes = require("./routes/garageRoutes");
const brandRoutes = require("./routes/brandRoutes");
const categoryRoutes = require("./routes/categoryRoutes");
const walletRoutes = require("./routes/walletRoutes");
const sosRoutes = require("./routes/sosRoutes");
const partnerRoutes = require("./routes/partnerRoutes");

const app = express();

app.use(cors());
app.use(express.json());

app.get("/", (req, res) => {
  res.json({ success: true, message: "GarejX API is running" });
});

app.get("/api/health", (req, res) => {
  res.json({
    success: true,
    status: "UP",
    mongodb: mongoose.connection.readyState === 1 ? "CONNECTED" : "DISCONNECTED"
  });
});

app.use("/api/auth", authRoutes);
app.use("/api/vehicles", vehicleRoutes);
app.use("/api/services", serviceRoutes);
app.use("/api/appointments", appointmentRoutes);
app.use("/api/mechanics", mechanicRoutes);
app.use("/api/bills", billRoutes);
app.use("/api/users", userRoutes);
app.use("/api/garage", garageRoutes);
app.use("/api/brands", brandRoutes);
app.use("/api", categoryRoutes);
app.use("/api/wallet", walletRoutes);
app.use("/api/sos", sosRoutes);
app.use("/api/partners", partnerRoutes);

app.use((req, res) => {
  res.status(404).json({ success: false, message: "API route not found" });
});

app.use((error, req, res, next) => {
  console.error(error);
  res.status(500).json({ success: false, message: "Server error", error: error.message });
});

module.exports = app;
