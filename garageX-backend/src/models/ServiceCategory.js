const mongoose = require("mongoose");

const serviceCategorySchema = new mongoose.Schema(
  {
    code: { type: String, required: true, unique: true, uppercase: true, trim: true },
    title: { type: String, required: true },
    badgeText: { type: String, default: "" },
    iconName: { type: String, required: true },
    iconColor: { type: String, default: "#6B1D2F" },
    order: { type: Number, default: 0 },
    isActive: { type: Boolean, default: true }
  },
  { timestamps: true }
);

module.exports = mongoose.model("ServiceCategory", serviceCategorySchema);
