const mongoose = require("mongoose");

const appointmentSchema = new mongoose.Schema(
  {
    customer: { type: mongoose.Schema.Types.ObjectId, ref: "User", required: true },
    vehicle: { type: mongoose.Schema.Types.ObjectId, ref: "Vehicle", required: true },
    service: { type: mongoose.Schema.Types.ObjectId, ref: "Service", required: true },
    mechanic: { type: mongoose.Schema.Types.ObjectId, ref: "User", default: null },
    appointmentDate: { type: Date, required: true },
    problemDescription: { type: String, default: "" },
    status: {
      type: String,
      enum: ["PENDING", "CONFIRMED", "ASSIGNED", "IN_PROGRESS", "COMPLETED", "CANCELLED", "REJECTED"],
      default: "PENDING"
    },
    mechanicNotes: { type: String, default: "" },
    estimatedCost: { type: Number, default: 0, min: 0 },
    finalCost: { type: Number, default: 0, min: 0 }
  },
  { timestamps: true }
);

module.exports = mongoose.model("Appointment", appointmentSchema);
