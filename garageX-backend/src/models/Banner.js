const mongoose = require("mongoose");

const bannerSchema = new mongoose.Schema(
  {
    title: { type: String, required: true },
    subtitle: { type: String, default: "" },
    actionUrl: { type: String, default: "" },
    imageUrl: { type: String, required: true },
    tag: { type: String, default: "PROMO" },
    order: { type: Number, default: 0 },
    isActive: { type: Boolean, default: true }
  },
  { timestamps: true }
);

module.exports = mongoose.model("Banner", bannerSchema);
