const mongoose = require("mongoose");

const sosRequestSchema = new mongoose.Schema(
  {
    user: { type: mongoose.Schema.Types.ObjectId, ref: "User", required: true },
    phone: { type: String, required: true },
    latitude: { type: Number, required: true },
    longitude: { type: Number, required: true },
    locationName: { type: String, default: "" },
    issueType: {
      type: String,
      enum: ["TOWING", "FLAT_TYRE", "BATTERY_JUMPSTART", "EMERGENCY_FUEL", "OTHER"],
      default: "TOWING"
    },
    notes: { type: String, default: "" },
    status: {
      type: String,
      enum: ["OPEN", "DISPATCHED", "RESOLVED", "CANCELLED"],
      default: "OPEN"
    }
  },
  { timestamps: true }
);

module.exports = mongoose.model("SOSRequest", sosRequestSchema);
