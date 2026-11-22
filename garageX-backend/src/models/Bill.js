const mongoose = require("mongoose");

const billSchema = new mongoose.Schema(
  {
    appointment: { type: mongoose.Schema.Types.ObjectId, ref: "Appointment", required: true, unique: true },
    customer: { type: mongoose.Schema.Types.ObjectId, ref: "User", required: true },
    items: [
      {
        name: { type: String, required: true },
        quantity: { type: Number, required: true, min: 1 },
        price: { type: Number, required: true, min: 0 },
        total: { type: Number, required: true, min: 0 }
      }
    ],
    subtotal: { type: Number, default: 0 },
    tax: { type: Number, default: 0 },
    discount: { type: Number, default: 0 },
    grandTotal: { type: Number, default: 0 },
    paymentStatus: { type: String, enum: ["PENDING", "PAID"], default: "PENDING" },
    paymentMethod: { type: String, enum: ["CASH", "UPI", "CARD", "OTHER"], default: "CASH" }
  },
  { timestamps: true }
);

module.exports = mongoose.model("Bill", billSchema);
