const mongoose = require("mongoose");

const partnerRequestSchema = new mongoose.Schema(
  {
    fullName: { type: String, required: true, trim: true },
    phone: { type: String, required: true, trim: true },
    email: { type: String, default: "", trim: true },
    workshopName: { type: String, required: true, trim: true },
    city: { type: String, required: true, trim: true },
    experienceYears: { type: Number, default: 1 },
    servicesOffered: { type: [String], default: [] },
    status: {
      type: String,
      enum: ["PENDING", "APPROVED", "REJECTED"],
      default: "PENDING"
    }
  },
  { timestamps: true }
);

module.exports = mongoose.model("PartnerRequest", partnerRequestSchema);
