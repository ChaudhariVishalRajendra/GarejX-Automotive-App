const mongoose = require("mongoose");

const garageSchema = new mongoose.Schema(
  {
    name: { type: String, default: "GarejX Garage" },
    phone: { type: String, default: "" },
    email: { type: String, default: "" },
    address: { type: String, default: "" },
    openingTime: { type: String, default: "09:00" },
    closingTime: { type: String, default: "19:00" }
  },
  { timestamps: true }
);

module.exports = mongoose.model("Garage", garageSchema);
