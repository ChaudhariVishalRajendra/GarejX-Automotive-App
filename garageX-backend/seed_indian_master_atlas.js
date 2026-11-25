require("dotenv").config();
const mongoose = require("mongoose");
const bcrypt = require("bcryptjs");

const User = require("./src/models/User");
const Brand = require("./src/models/Brand");
const VehicleModel = require("./src/models/VehicleModel");
const ServiceCategory = require("./src/models/ServiceCategory");
const Service = require("./src/models/Service");
const Banner = require("./src/models/Banner");
const Vehicle = require("./src/models/Vehicle");
const Appointment = require("./src/models/Appointment");
const WalletTransaction = require("./src/models/WalletTransaction");
const Garage = require("./src/models/Garage");

async function seedIndianMasterData() {
  try {
    const mongoUri = process.env.MONGODB_URI || "mongodb+srv://root:Vishal100@garejx.y5piiqn.mongodb.net/?retryWrites=true&w=majority";
    console.log("Connecting to MongoDB Atlas...");
    await mongoose.connect(mongoUri);
    console.log(`Connected to MongoDB Atlas: ${mongoose.connection.name}`);

    // ==========================================
    // 1. SEED USERS (Customer, Mechanic, Admin)
    // ==========================================
    console.log("Seeding Users...");
    const hashedPassword = await bcrypt.hash("password@100", 10);

    // Customer
    let customer = await User.findOne({ email: "customer@garejx.com" });
    if (!customer) {
      customer = await User.create({
        name: "John Customer",
        email: "customer@garejx.com",
        phone: "+91 9876543210",
        password: hashedPassword,
        role: "USER",
        walletBalance: 1500,
        isClubMember: true
      });
    } else {
      customer.password = hashedPassword;
      customer.walletBalance = 1500;
      customer.isClubMember = true;
      await customer.save();
    }

    // Mechanic
    let mechanic = await User.findOne({ email: "mechanic@garejx.com" });
    if (!mechanic) {
      mechanic = await User.create({
        name: "Ramesh Sharma (Senior Technician)",
        email: "mechanic@garejx.com",
        phone: "+91 9876543211",
        password: hashedPassword,
        role: "MECHANIC"
      });
    } else {
      mechanic.password = hashedPassword;
      await mechanic.save();
    }

    // Admin
    let admin = await User.findOne({ email: "admin@garejx.com" });
    if (!admin) {
      admin = await User.create({
        name: "Vikram Malhotra (Workshop Owner)",
        email: "admin@garejx.com",
        phone: "+91 9876543212",
        password: hashedPassword,
        role: "ADMIN"
      });
    } else {
      admin.password = hashedPassword;
      await admin.save();
    }
    console.log("Seeded Users: customer@garejx.com, mechanic@garejx.com, admin@garejx.com (password: password@100 / password123)");

    // ==========================================
    // 2. SEED 12 POPULAR INDIAN CAR BRANDS
    // ==========================================
    console.log("Seeding 12 Indian Car Brands...");
    await Brand.deleteMany({});
    await VehicleModel.deleteMany({});

    const brandsData = [
      { name: "Maruti Suzuki", code: "MARUTI", logoUrl: "https://www.carlogos.org/car-logos/suzuki-logo-2001.png", country: "India", order: 1 },
      { name: "Hyundai", code: "HYUNDAI", logoUrl: "https://www.carlogos.org/car-logos/hyundai-logo-2011.png", country: "South Korea", order: 2 },
      { name: "Tata Motors", code: "TATA", logoUrl: "https://www.carlogos.org/car-logos/tata-logo-2000.png", country: "India", order: 3 },
      { name: "Mahindra", code: "MAHINDRA", logoUrl: "https://www.carlogos.org/car-logos/mahindra-logo-2021.png", country: "India", order: 4 },
      { name: "Toyota", code: "TOYOTA", logoUrl: "https://www.carlogos.org/car-logos/toyota-logo-2020.png", country: "Japan", order: 5 },
      { name: "Honda", code: "HONDA", logoUrl: "https://www.carlogos.org/car-logos/honda-logo-2000.png", country: "Japan", order: 6 },
      { name: "Kia", code: "KIA", logoUrl: "https://www.carlogos.org/car-logos/kia-logo-2021.png", country: "South Korea", order: 7 },
      { name: "Volkswagen", code: "VOLKSWAGEN", logoUrl: "https://www.carlogos.org/car-logos/volkswagen-logo-2019.png", country: "Germany", order: 8 },
      { name: "Skoda", code: "SKODA", logoUrl: "https://www.carlogos.org/car-logos/skoda-logo-2023.png", country: "Czech Republic", order: 9 },
      { name: "MG (Morris Garages)", code: "MG", logoUrl: "https://www.carlogos.org/car-logos/mg-logo-2021.png", country: "UK", order: 10 },
      { name: "Renault", code: "RENAULT", logoUrl: "https://www.carlogos.org/car-logos/renault-logo-2021.png", country: "France", order: 11 },
      { name: "Ford", code: "FORD", logoUrl: "https://www.carlogos.org/car-logos/ford-logo-2017.png", country: "USA", order: 12 }
    ];

    const insertedBrands = await Brand.insertMany(brandsData);
    const brandMap = {};
    insertedBrands.forEach(b => { brandMap[b.code] = b; });
    console.log(`Seeded ${insertedBrands.length} Indian Brands.`);

    // ==========================================
    // 3. SEED MODELS FOR EACH BRAND
    // ==========================================
    console.log("Seeding Popular Models for each Brand...");
    const modelsData = [
      // Maruti Suzuki
      { brand: brandMap["MARUTI"]._id, brandName: "Maruti Suzuki", name: "Swift", bodyType: "Hatchback", imageUrl: "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=600", supportedFuelTypes: ["PETROL", "CNG"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 1 },
      { brand: brandMap["MARUTI"]._id, brandName: "Maruti Suzuki", name: "Baleno", bodyType: "Hatchback", imageUrl: "https://images.unsplash.com/photo-1583121274602-3e2820c69888?w=600", supportedFuelTypes: ["PETROL", "CNG"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 2 },
      { brand: brandMap["MARUTI"]._id, brandName: "Maruti Suzuki", name: "Brezza", bodyType: "Compact SUV", imageUrl: "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=600", supportedFuelTypes: ["PETROL", "CNG"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 3 },
      { brand: brandMap["MARUTI"]._id, brandName: "Maruti Suzuki", name: "Dzire", bodyType: "Sedan", imageUrl: "https://images.unsplash.com/photo-1542282088-72c9c27ed0cd?w=600", supportedFuelTypes: ["PETROL", "CNG"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 4 },
      { brand: brandMap["MARUTI"]._id, brandName: "Maruti Suzuki", name: "Grand Vitara", bodyType: "Mid SUV", imageUrl: "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600", supportedFuelTypes: ["PETROL", "HYBRID"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 5 },

      // Hyundai
      { brand: brandMap["HYUNDAI"]._id, brandName: "Hyundai", name: "Creta", bodyType: "Mid SUV", imageUrl: "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=600", supportedFuelTypes: ["PETROL", "DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 1 },
      { brand: brandMap["HYUNDAI"]._id, brandName: "Hyundai", name: "i20", bodyType: "Hatchback", imageUrl: "https://images.unsplash.com/photo-1502877338535-766e1452684a?w=600", supportedFuelTypes: ["PETROL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 2 },
      { brand: brandMap["HYUNDAI"]._id, brandName: "Hyundai", name: "Venue", bodyType: "Compact SUV", imageUrl: "https://images.unsplash.com/photo-1541899481282-d53bffe3c35d?w=600", supportedFuelTypes: ["PETROL", "DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 3 },
      { brand: brandMap["HYUNDAI"]._id, brandName: "Hyundai", name: "Verna", bodyType: "Sedan", imageUrl: "https://images.unsplash.com/photo-1555353540-64580b51c258?w=600", supportedFuelTypes: ["PETROL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 4 },

      // Tata Motors
      { brand: brandMap["TATA"]._id, brandName: "Tata Motors", name: "Nexon", bodyType: "Compact SUV", imageUrl: "https://images.unsplash.com/photo-1511919884226-fd3cad34687c?w=600", supportedFuelTypes: ["PETROL", "DIESEL", "ELECTRIC"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 1 },
      { brand: brandMap["TATA"]._id, brandName: "Tata Motors", name: "Punch", bodyType: "Micro SUV", imageUrl: "https://images.unsplash.com/photo-1508974239320-0a029497e820?w=600", supportedFuelTypes: ["PETROL", "CNG", "ELECTRIC"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 2 },
      { brand: brandMap["TATA"]._id, brandName: "Tata Motors", name: "Harrier", bodyType: "Full SUV", imageUrl: "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600", supportedFuelTypes: ["DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 3 },
      { brand: brandMap["TATA"]._id, brandName: "Tata Motors", name: "Safari", bodyType: "7-Seater SUV", imageUrl: "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=600", supportedFuelTypes: ["DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 4 },

      // Mahindra
      { brand: brandMap["MAHINDRA"]._id, brandName: "Mahindra", name: "Thar", bodyType: "4x4 Off-roader", imageUrl: "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600", supportedFuelTypes: ["PETROL", "DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 1 },
      { brand: brandMap["MAHINDRA"]._id, brandName: "Mahindra", name: "Scorpio-N", bodyType: "Full SUV", imageUrl: "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=600", supportedFuelTypes: ["PETROL", "DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 2 },
      { brand: brandMap["MAHINDRA"]._id, brandName: "Mahindra", name: "XUV700", bodyType: "Luxury SUV", imageUrl: "https://images.unsplash.com/photo-1555353540-64580b51c258?w=600", supportedFuelTypes: ["PETROL", "DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 3 },

      // Toyota
      { brand: brandMap["TOYOTA"]._id, brandName: "Toyota", name: "Fortuner", bodyType: "Full SUV", imageUrl: "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600", supportedFuelTypes: ["PETROL", "DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 1 },
      { brand: brandMap["TOYOTA"]._id, brandName: "Toyota", name: "Innova Crysta", bodyType: "MPV", imageUrl: "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=600", supportedFuelTypes: ["DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 2 },
      { brand: brandMap["TOYOTA"]._id, brandName: "Toyota", name: "Urban Cruiser Hyryder", bodyType: "Hybrid SUV", imageUrl: "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=600", supportedFuelTypes: ["PETROL", "HYBRID", "CNG"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 3 },

      // Honda
      { brand: brandMap["HONDA"]._id, brandName: "Honda", name: "City", bodyType: "Sedan", imageUrl: "https://images.unsplash.com/photo-1555353540-64580b51c258?w=600", supportedFuelTypes: ["PETROL", "HYBRID"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 1 },
      { brand: brandMap["HONDA"]._id, brandName: "Honda", name: "Amaze", bodyType: "Compact Sedan", imageUrl: "https://images.unsplash.com/photo-1542282088-72c9c27ed0cd?w=600", supportedFuelTypes: ["PETROL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 2 },
      { brand: brandMap["HONDA"]._id, brandName: "Honda", name: "Elevate", bodyType: "Mid SUV", imageUrl: "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=600", supportedFuelTypes: ["PETROL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 3 },

      // Kia
      { brand: brandMap["KIA"]._id, brandName: "Kia", name: "Seltos", bodyType: "Mid SUV", imageUrl: "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=600", supportedFuelTypes: ["PETROL", "DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 1 },
      { brand: brandMap["KIA"]._id, brandName: "Kia", name: "Sonet", bodyType: "Compact SUV", imageUrl: "https://images.unsplash.com/photo-1541899481282-d53bffe3c35d?w=600", supportedFuelTypes: ["PETROL", "DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 2 },

      // Volkswagen
      { brand: brandMap["VOLKSWAGEN"]._id, brandName: "Volkswagen", name: "Virtus", bodyType: "Sedan", imageUrl: "https://images.unsplash.com/photo-1555353540-64580b51c258?w=600", supportedFuelTypes: ["PETROL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 1 },
      { brand: brandMap["VOLKSWAGEN"]._id, brandName: "Volkswagen", name: "Taigun", bodyType: "Mid SUV", imageUrl: "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=600", supportedFuelTypes: ["PETROL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 2 },

      // Skoda
      { brand: brandMap["SKODA"]._id, brandName: "Skoda", name: "Slavia", bodyType: "Sedan", imageUrl: "https://images.unsplash.com/photo-1555353540-64580b51c258?w=600", supportedFuelTypes: ["PETROL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 1 },
      { brand: brandMap["SKODA"]._id, brandName: "Skoda", name: "Kushaq", bodyType: "Mid SUV", imageUrl: "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=600", supportedFuelTypes: ["PETROL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 2 },

      // MG
      { brand: brandMap["MG"]._id, brandName: "MG (Morris Garages)", name: "Hector", bodyType: "Mid SUV", imageUrl: "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=600", supportedFuelTypes: ["PETROL", "DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 1 },
      { brand: brandMap["MG"]._id, brandName: "MG (Morris Garages)", name: "ZS EV", bodyType: "Electric SUV", imageUrl: "https://images.unsplash.com/photo-1511919884226-fd3cad34687c?w=600", supportedFuelTypes: ["ELECTRIC"], supportedTransmissions: ["AUTOMATIC"], order: 2 },

      // Renault
      { brand: brandMap["RENAULT"]._id, brandName: "Renault", name: "Kwid", bodyType: "Hatchback", imageUrl: "https://images.unsplash.com/photo-1502877338535-766e1452684a?w=600", supportedFuelTypes: ["PETROL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 1 },
      { brand: brandMap["RENAULT"]._id, brandName: "Renault", name: "Kiger", bodyType: "Compact SUV", imageUrl: "https://images.unsplash.com/photo-1541899481282-d53bffe3c35d?w=600", supportedFuelTypes: ["PETROL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 2 },

      // Ford
      { brand: brandMap["FORD"]._id, brandName: "Ford", name: "EcoSport", bodyType: "Compact SUV", imageUrl: "https://images.unsplash.com/photo-1541899481282-d53bffe3c35d?w=600", supportedFuelTypes: ["PETROL", "DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 1 },
      { brand: brandMap["FORD"]._id, brandName: "Ford", name: "Endeavour", bodyType: "Full 4x4 SUV", imageUrl: "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=600", supportedFuelTypes: ["DIESEL"], supportedTransmissions: ["AUTOMATIC"], order: 2 }
    ];

    const insertedModels = await VehicleModel.insertMany(modelsData);
    console.log(`Seeded ${insertedModels.length} Car Models across 12 Brands.`);

    // ==========================================
    // 4. SEED 12 SERVICE CATEGORIES
    // ==========================================
    console.log("Seeding 12 Service Categories...");
    await ServiceCategory.deleteMany({});
    const categoriesData = [
      { code: "CAR_SERVICES", title: "Car Services", badgeText: "Special Discount", iconName: "car_repair", order: 1 },
      { code: "DENTING_PAINTING", title: "Denting & Painting", badgeText: "Grade-A Booth", iconName: "format_paint", order: 2 },
      { code: "AC_SERVICE", title: "AC Service & Repair", badgeText: "Gas Refill ₹999", iconName: "ac_unit", order: 3 },
      { code: "CAR_SPA", title: "Car Spa & Cleaning", badgeText: "Interior Foam", iconName: "local_car_wash", order: 4 },
      { code: "DETAILING", title: "Detailing Services", badgeText: "9H Ceramic", iconName: "auto_awesome", order: 5 },
      { code: "BATTERIES", title: "Batteries", badgeText: "Free In-Bay Check", iconName: "battery_charging_full", order: 6 },
      { code: "TYRES_WHEELS", title: "Tyres & Wheel Care", badgeText: "Laser Alignment", iconName: "tire_repair", order: 7 },
      { code: "INSPECTIONS", title: "Car Inspections", badgeText: "50-Point Audit", iconName: "fact_check", order: 8 },
      { code: "WINDSHIELDS", title: "Windshields & Lights", badgeText: "OEM Glass", iconName: "lightbulb", order: 9 },
      { code: "SUSPENSION", title: "Suspension & Fitments", badgeText: "Gas Absorbers", iconName: "build", order: 10 },
      { code: "INSURANCE", title: "Insurance Claims", badgeText: "Cashless Support", iconName: "shield", order: 11 },
      { code: "SOS_ROADSIDE", title: "Emergency SOS", badgeText: "24/7 Roadside", iconName: "emergency", order: 12 }
    ];
    await ServiceCategory.insertMany(categoriesData);
    console.log("Seeded 12 Service Categories.");

    // ==========================================
    // 5. SEED WORKSHOP SERVICE PACKAGES
    // ==========================================
    console.log("Seeding Workshop Service Packages...");
    await Service.deleteMany({});
    const servicesData = [
      { name: "Basic Wash & Vacuum", description: "High-pressure foam exterior wash, tire dressing, and interior dry vacuuming.", price: 499, estimatedTime: 45 },
      { name: "Periodic Standard Service", description: "Engine oil replacement, oil filter replacement, 40-point safety checkup, coolant top-up.", price: 2499, estimatedTime: 120 },
      { name: "Comprehensive Major Service", description: "Full synthetic oil, spark plug check, throttle body clean, brake bleeding, AC gas top-up, wheel alignment.", price: 4999, estimatedTime: 240 },
      { name: "AC Gas Refill & Filter Cleaning", description: "Complete refrigerant gas recharge, condenser coil wash, and antibacterial cabin treatment.", price: 999, estimatedTime: 60 },
      { name: "Computerized Wheel Alignment & Balancing", description: "4-wheel laser alignment, computerized dynamic balancing with weights.", price: 799, estimatedTime: 45 },
      { name: "Brake Overhaul & Pad Replacement", description: "Brake rotor inspection, caliper pin greasing, disc pad replacement with OEM pads.", price: 1299, estimatedTime: 60 },
      { name: "3M Ceramic Coating (Paint Protection)", description: "Multi-stage paint correction, swirl mark removal, and 9H dual layer hydrophobic coat.", price: 4999, estimatedTime: 360 }
    ];
    const insertedServices = await Service.insertMany(servicesData);
    console.log(`Seeded ${insertedServices.length} Workshop Services.`);

    // ==========================================
    // 6. SEED HERO PROMO BANNERS
    // ==========================================
    console.log("Seeding Hero Promo Banners...");
    await Banner.deleteMany({});
    const bannersData = [
      {
        title: "CHECK AND PAY CHALLAN ONLINE",
        subtitle: "100% Paperless & Instant Verification",
        actionUrl: "/challan",
        imageUrl: "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=800&auto=format&fit=crop&q=80",
        tag: "PAY NOW >",
        order: 1
      },
      {
        title: "Monsoon Shield Package",
        subtitle: "40-Point Free Monsoon Checkup + Free Wiper Blades",
        actionUrl: "/monsoon",
        imageUrl: "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&auto=format&fit=crop&q=80",
        tag: "BOOK NOW >",
        order: 2
      }
    ];
    await Banner.insertMany(bannersData);
    console.log("Seeded Hero Banners.");

    // ==========================================
    // 7. SEED CUSTOMER FLEET VEHICLES
    // ==========================================
    console.log("Seeding Customer Fleet Vehicles...");
    await Vehicle.deleteMany({ user: customer._id });

    const creta = await Vehicle.create({
      user: customer._id,
      brand: "Hyundai",
      model: "Creta",
      vehicleNumber: "GJ-01-AB-1234",
      fuelType: "DIESEL",
      transmission: "AUTOMATIC",
      manufacturingYear: 2022,
      kilometers: 28500
    });

    const swift = await Vehicle.create({
      user: customer._id,
      brand: "Maruti Suzuki",
      model: "Swift",
      vehicleNumber: "MH-02-CD-5678",
      fuelType: "PETROL",
      transmission: "MANUAL",
      manufacturingYear: 2021,
      kilometers: 34000
    });

    const nexon = await Vehicle.create({
      user: customer._id,
      brand: "Tata Motors",
      model: "Nexon EV",
      vehicleNumber: "DL-03-EF-9012",
      fuelType: "ELECTRIC",
      transmission: "AUTOMATIC",
      manufacturingYear: 2023,
      kilometers: 14200
    });

    customer.activeVehicle = creta._id;
    await customer.save();
    console.log(`Seeded 3 Customer Vehicles (Active: Creta GJ-01-AB-1234).`);

    // ==========================================
    // 8. SEED APPOINTMENTS / ORDERS HISTORY
    // ==========================================
    console.log("Seeding Appointment History for Customer...");
    await Appointment.deleteMany({ customer: customer._id });

    const appt1 = await Appointment.create({
      customer: customer._id,
      vehicle: creta._id,
      service: insertedServices[1]._id, // Periodic Standard Service
      mechanic: mechanic._id,
      appointmentDate: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000), // 7 days ago
      problemDescription: "Engine oil change & routine inspection",
      status: "COMPLETED",
      estimatedCost: 2499,
      finalCost: 2499,
      mechanicNotes: "Standard periodic service completed. Synthetic oil changed."
    });

    const appt2 = await Appointment.create({
      customer: customer._id,
      vehicle: swift._id,
      service: insertedServices[3]._id, // AC Gas Refill
      mechanic: mechanic._id,
      appointmentDate: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000), // 2 days ago
      problemDescription: "AC cooling low in afternoon",
      status: "COMPLETED",
      estimatedCost: 999,
      finalCost: 999,
      mechanicNotes: "Refrigerant recharged. Condenser cleaned."
    });

    const appt3 = await Appointment.create({
      customer: customer._id,
      vehicle: nexon._id,
      service: insertedServices[0]._id, // Basic Wash
      mechanic: mechanic._id,
      appointmentDate: new Date(Date.now() + 2 * 24 * 60 * 60 * 1000), // 2 days later
      problemDescription: "Weekend car wash & interior vacuuming",
      status: "CONFIRMED",
      estimatedCost: 499,
      finalCost: 499,
      mechanicNotes: "Bay 2 reserved."
    });

    console.log(`Seeded 3 Appointments for customer (Order History count: 3).`);

    // ==========================================
    // 9. SEED WALLET TRANSACTIONS
    // ==========================================
    await WalletTransaction.deleteMany({ user: customer._id });
    await WalletTransaction.create({
      user: customer._id,
      amount: 1500,
      type: "CREDIT",
      description: "GarejX Miles Club Welcome Credits",
      referenceId: "WELCOME-1500"
    });
    console.log("Seeded Wallet balance ₹1,500 with Welcome Credit ledger.");

    // ==========================================
    // 10. SEED GARAGE DETAILS
    // ==========================================
    await Garage.deleteMany({});
    await Garage.create({
      name: "GarejX Prime Workshop & Diagnostic Center",
      phone: "+91 9876543210",
      email: "contact@garejx.com",
      address: "Plot 42, Automotive Hub, S.G. Highway, Ahmedabad, Gujarat",
      openingTime: "08:30",
      closingTime: "20:00"
    });
    console.log("Seeded Garage workshop profile.");

    console.log("\n=============================================");
    console.log("SUCCESS: Master Indian Data Seeded to Atlas!");
    console.log("=============================================");
    console.log("Customer: customer@garejx.com / password@100");
    console.log("Mechanic: mechanic@garejx.com / password@100");
    console.log("Admin:    admin@garejx.com    / password@100");
    console.log("Brands: 12 | Models: 28 | Categories: 12 | Services: 7 | Fleet Cars: 3 | Orders: 3");
    process.exit(0);
  } catch (err) {
    console.error("FATAL SEED ERROR:", err);
    process.exit(1);
  }
}

seedIndianMasterData();
