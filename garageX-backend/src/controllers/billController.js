const mongoose = require("mongoose");
const Bill = require("../models/Bill");
const Appointment = require("../models/Appointment");

function calculateItems(items = []) {
  return items.map((item) => ({
    name: item.name,
    quantity: Number(item.quantity),
    price: Number(item.price),
    total: Number(item.quantity) * Number(item.price)
  }));
}

async function createBill(req, res) {
  try {
    const { appointment, items = [], tax = 0, discount = 0, paymentMethod = "CASH" } = req.body;
    if (!appointment) return res.status(400).json({ success: false, message: "appointment is required" });

    const appointmentData = await Appointment.findById(appointment);
    if (!appointmentData) return res.status(404).json({ success: false, message: "Appointment not found" });

    const billItems = calculateItems(items);
    const subtotal = billItems.reduce((sum, item) => sum + item.total, 0);
    const grandTotal = Math.max(0, subtotal + Number(tax) - Number(discount));

    const bill = await Bill.create({
      appointment,
      customer: appointmentData.customer,
      items: billItems,
      subtotal,
      tax: Number(tax),
      discount: Number(discount),
      grandTotal,
      paymentMethod
    });

    await bill.populate("customer", "name email phone");
    res.status(201).json({ success: true, message: "Bill created", bill });
  } catch (error) {
    if (error.code === 11000) return res.status(409).json({ success: false, message: "Bill already exists for this appointment" });
    res.status(500).json({ success: false, message: error.message });
  }
}

async function getBills(req, res) {
  const filter = req.user.role === "USER" ? { customer: req.user._id } : {};
  const bills = await Bill.find(filter).populate("customer", "name email phone").populate("appointment").sort({ createdAt: -1 });
  res.json({ success: true, count: bills.length, bills });
}

async function getBill(req, res) {
  if (!mongoose.isValidObjectId(req.params.id)) return res.status(400).json({ success: false, message: "Invalid bill ID" });
  const bill = await Bill.findById(req.params.id).populate("customer", "name email phone").populate("appointment");
  if (!bill) return res.status(404).json({ success: false, message: "Bill not found" });
  if (req.user.role === "USER" && bill.customer._id.toString() !== req.user._id.toString()) return res.status(403).json({ success: false, message: "Access denied" });
  res.json({ success: true, bill });
}

async function updatePayment(req, res) {
  const { paymentStatus, paymentMethod } = req.body;
  if (!paymentStatus && !paymentMethod) return res.status(400).json({ success: false, message: "Payment status or method is required" });

  const bill = await Bill.findById(req.params.id);
  if (!bill) return res.status(404).json({ success: false, message: "Bill not found" });

  if (paymentStatus) bill.paymentStatus = paymentStatus;
  if (paymentMethod) bill.paymentMethod = paymentMethod;
  await bill.save();

  res.json({ success: true, message: "Payment updated", bill });
}

module.exports = { createBill, getBills, getBill, updatePayment };
