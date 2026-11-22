const mongoose = require("mongoose");

async function connectDB() {
  const mongoUrl = process.env.MONGODB_URI;

  if (!mongoUrl) {
    throw new Error("MONGODB_URI is missing in .env");
  }

  await mongoose.connect(mongoUrl);
  console.log("MongoDB connected");
}

module.exports = connectDB;
