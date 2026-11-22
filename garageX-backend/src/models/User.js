const mongoose = require("mongoose");

const userSchema = new mongoose.Schema(
  {
    name: { type: String, required: true, trim: true },
    email: { type: String, required: true, unique: true, lowercase: true, trim: true },
    phone: { type: String, required: true, trim: true },
    password: { type: String, required: true },
    role: { type: String, enum: ["ADMIN", "MECHANIC", "USER"], default: "USER" },
    address: { type: String, default: "" },
    profileImage: { type: String, default: "" },
    activeVehicle: { type: mongoose.Schema.Types.ObjectId, ref: "Vehicle", default: null },
    walletBalance: { type: Number, default: 1500 },
    isClubMember: { type: Boolean, default: false }
  },
  { timestamps: true }
);

module.exports = mongoose.model("User", userSchema);
