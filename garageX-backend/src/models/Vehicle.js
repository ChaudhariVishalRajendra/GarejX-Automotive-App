const mongoose = require("mongoose");

const vehicleSchema = new mongoose.Schema(
  {
    user: { type: mongoose.Schema.Types.ObjectId, ref: "User", required: true },
    vehicleNumber: { type: String, required: true, trim: true, uppercase: true },
    brand: { type: String, required: true, trim: true },
    model: { type: String, required: true, trim: true },
    fuelType: { type: String, enum: ["PETROL", "DIESEL", "CNG", "ELECTRIC", "HYBRID"], required: true },
    transmission: { type: String, enum: ["MANUAL", "AUTOMATIC"], default: "MANUAL" },
    carImageUrl: { type: String, default: "" },
    manufacturingYear: { type: Number },
    kilometers: { type: Number, default: 0, min: 0 }
  },
  { timestamps: true }
);

vehicleSchema.index({ vehicleNumber: 1 }, { unique: true });

module.exports = mongoose.model("Vehicle", vehicleSchema);
