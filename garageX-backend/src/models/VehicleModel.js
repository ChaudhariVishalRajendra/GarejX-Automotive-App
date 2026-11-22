const mongoose = require("mongoose");

const vehicleModelSchema = new mongoose.Schema(
  {
    brand: { type: mongoose.Schema.Types.ObjectId, ref: "Brand", required: true },
    brandName: { type: String, required: true },
    name: { type: String, required: true, trim: true },
    bodyType: { type: String, default: "Hatchback" },
    imageUrl: { type: String, required: true },
    supportedFuelTypes: {
      type: [String],
      default: ["PETROL", "DIESEL", "CNG"]
    },
    supportedTransmissions: {
      type: [String],
      default: ["MANUAL", "AUTOMATIC"]
    },
    order: { type: Number, default: 0 }
  },
  { timestamps: true }
);

module.exports = mongoose.model("VehicleModel", vehicleModelSchema);
