require("dotenv").config();
const mongoose = require("mongoose");
const bcrypt = require("bcryptjs");
const User = require("./src/models/User");
const Service = require("./src/models/Service");
const Garage = require("./src/models/Garage");
const Vehicle = require("./src/models/Vehicle");

async function seedDatabase() {
  try {
    console.log("Connecting to MongoDB...");
    await mongoose.connect(process.env.MONGO_URI || "mongodb://localhost:27017/garejx");
    console.log("MongoDB connected.");

    console.log("Clearing existing data...");
    await User.deleteMany({});
    await Service.deleteMany({});
    await Garage.deleteMany({});
    await Vehicle.deleteMany({});

    console.log("Creating Garage...");
    await Garage.create({
      name: "GarejX College Garage",
      phone: "+919876543210",
      email: "contact@garejx.com",
      address: "123 University Road, City",
      openingTime: "09:00",
      closingTime: "18:00"
    });

    console.log("Creating Users...");
    const hashedPassword = await bcrypt.hash("password123", 10);
    
    const admin = await User.create({
      name: "Admin User",
      email: "admin@garejx.com",
      phone: "1234567890",
      password: hashedPassword,
      role: "ADMIN"
    });

    const mechanic = await User.create({
      name: "Mike Mechanic",
      email: "mechanic@garejx.com",
      phone: "9876543211",
      password: hashedPassword,
      role: "MECHANIC"
    });

    const customer = await User.create({
      name: "John Customer",
      email: "customer@garejx.com",
      phone: "1122334455",
      password: hashedPassword,
      role: "USER"
    });

    console.log("Creating Services...");
    const services = await Service.insertMany([
      { name: "Basic Wash", description: "Exterior wash and basic interior vacuum.", price: 500, estimatedTime: 30 },
      { name: "Full Service", description: "Complete checkup, oil change, filters, and wash.", price: 2500, estimatedTime: 120 },
      { name: "Oil Change", description: "Engine oil and filter change.", price: 1000, estimatedTime: 45 },
      { name: "Brake Inspection", description: "Brake pad and disc inspection/cleaning.", price: 800, estimatedTime: 60 },
      { name: "Wheel Alignment", description: "Computerized wheel alignment and balancing.", price: 1200, estimatedTime: 60 }
    ]);

    console.log("Creating Vehicle for Customer...");
    await Vehicle.create({
      user: customer._id,
      vehicleNumber: "MH12AB1234",
      brand: "Hyundai",
      model: "i20",
      fuelType: "PETROL",
      manufacturingYear: 2020,
      kilometers: 15000
    });

    console.log("Seed complete! You can log in with: customer@garejx.com / password123");
    process.exit(0);
  } catch (error) {
    console.error("Error seeding database:", error);
    process.exit(1);
  }
}

seedDatabase();
