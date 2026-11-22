require("dotenv").config();
const mongoose = require("mongoose");
const Brand = require("./src/models/Brand");
const VehicleModel = require("./src/models/VehicleModel");
const ServiceCategory = require("./src/models/ServiceCategory");
const Banner = require("./src/models/Banner");
const User = require("./src/models/User");
const WalletTransaction = require("./src/models/WalletTransaction");

async function seedSuperApp() {
  try {
    console.log("Connecting to MongoDB...");
    await mongoose.connect(process.env.MONGO_URI || "mongodb://localhost:27017/garejx");
    console.log("MongoDB connected.");

    console.log("Seeding Brands (Screenshot 1: 32+ Manufacturers)...");
    await Brand.deleteMany({});
    
    // Cloudinary / Wikimedia SVG and transparent WebP brand logos
    const brandsData = [
      { name: "Maruti Suzuki", code: "MARUTI", logoUrl: "https://www.carlogos.org/car-logos/suzuki-logo-2001.png", order: 1 },
      { name: "Hyundai", code: "HYUNDAI", logoUrl: "https://www.carlogos.org/car-logos/hyundai-logo-2011.png", order: 2 },
      { name: "Honda", code: "HONDA", logoUrl: "https://www.carlogos.org/car-logos/honda-logo-2000.png", order: 3 },
      { name: "Tata", code: "TATA", logoUrl: "https://www.carlogos.org/car-logos/tata-logo-2000.png", order: 4 },
      { name: "Ford", code: "FORD", logoUrl: "https://www.carlogos.org/car-logos/ford-logo-2017.png", order: 5 },
      { name: "Volkswagen", code: "VOLKSWAGEN", logoUrl: "https://www.carlogos.org/car-logos/volkswagen-logo-2019.png", order: 6 },
      { name: "Mahindra", code: "MAHINDRA", logoUrl: "https://www.carlogos.org/car-logos/mahindra-logo-2021.png", order: 7 },
      { name: "Renault", code: "RENAULT", logoUrl: "https://www.carlogos.org/car-logos/renault-logo-2021.png", order: 8 },
      { name: "Chevrolet", code: "CHEVROLET", logoUrl: "https://www.carlogos.org/car-logos/chevrolet-logo-2013.png", order: 9 },
      { name: "Toyota", code: "TOYOTA", logoUrl: "https://www.carlogos.org/car-logos/toyota-logo-2020.png", order: 10 },
      { name: "Skoda", code: "SKODA", logoUrl: "https://www.carlogos.org/car-logos/skoda-logo-2023.png", order: 11 },
      { name: "Nissan", code: "NISSAN", logoUrl: "https://www.carlogos.org/car-logos/nissan-logo-2020.png", order: 12 },
      { name: "Fiat", code: "FIAT", logoUrl: "https://www.carlogos.org/car-logos/fiat-logo-2020.png", order: 13 },
      { name: "Datsun", code: "DATSUN", logoUrl: "https://www.carlogos.org/car-logos/datsun-logo-2013.png", order: 14 },
      { name: "BMW", code: "BMW", logoUrl: "https://www.carlogos.org/car-logos/bmw-logo-2020.png", order: 15 },
      { name: "Kia", code: "KIA", logoUrl: "https://www.carlogos.org/car-logos/kia-logo-2021.png", order: 16 },
      { name: "Audi", code: "AUDI", logoUrl: "https://www.carlogos.org/car-logos/audi-logo-2016.png", order: 17 },
      { name: "Mercedes-Benz", code: "MERCEDES", logoUrl: "https://www.carlogos.org/car-logos/mercedes-benz-logo-2011.png", order: 18 },
      { name: "Jeep", code: "JEEP", logoUrl: "https://www.carlogos.org/car-logos/jeep-logo-1993.png", order: 19 },
      { name: "Mitsubishi", code: "MITSUBISHI", logoUrl: "https://www.carlogos.org/car-logos/mitsubishi-logo-2000.png", order: 20 },
      { name: "MG", code: "MG", logoUrl: "https://www.carlogos.org/car-logos/mg-logo-2021.png", order: 21 },
      { name: "Land Rover", code: "LANDROVER", logoUrl: "https://www.carlogos.org/car-logos/land-rover-logo-2020.png", order: 22 },
      { name: "Volvo", code: "VOLVO", logoUrl: "https://www.carlogos.org/car-logos/volvo-logo-2021.png", order: 23 },
      { name: "Jaguar", code: "JAGUAR", logoUrl: "https://www.carlogos.org/car-logos/jaguar-logo-2021.png", order: 24 },
      { name: "SsangYong", code: "SSANGYONG", logoUrl: "https://www.carlogos.org/car-logos/ssangyong-logo-2000.png", order: 25 },
      { name: "Isuzu", code: "ISUZU", logoUrl: "https://www.carlogos.org/car-logos/isuzu-logo-1991.png", order: 26 },
      { name: "Mini", code: "MINI", logoUrl: "https://www.carlogos.org/car-logos/mini-logo-2018.png", order: 27 },
      { name: "Force", code: "FORCE", logoUrl: "https://www.carlogos.org/car-logos/force-motors-logo.png", order: 28 },
      { name: "Opel", code: "OPEL", logoUrl: "https://www.carlogos.org/car-logos/opel-logo-2020.png", order: 29 },
      { name: "Porsche", code: "PORSCHE", logoUrl: "https://www.carlogos.org/car-logos/porsche-logo-2014.png", order: 30 },
      { name: "Daewoo", code: "DAEWOO", logoUrl: "https://www.carlogos.org/car-logos/daewoo-logo-1994.png", order: 31 },
      { name: "Premier", code: "PREMIER", logoUrl: "https://www.carlogos.org/car-logos/premier-logo.png", order: 32 }
    ];

    const insertedBrands = await Brand.insertMany(brandsData);
    console.log(`Seeded ${insertedBrands.length} Brands.`);

    const marutiBrand = insertedBrands.find(b => b.code === "MARUTI");
    const hyundaiBrand = insertedBrands.find(b => b.code === "HYUNDAI");
    const tataBrand = insertedBrands.find(b => b.code === "TATA");

    console.log("Seeding Vehicle Models (Screenshot 2: Studio transparent cutouts)...");
    await VehicleModel.deleteMany({});

    const modelsData = [
      // Maruti Models
      { brand: marutiBrand._id, brandName: "Maruti Suzuki", name: "Swift", bodyType: "Hatchback", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/159099/swift-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["PETROL", "CNG"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 1 },
      { brand: marutiBrand._id, brandName: "Maruti Suzuki", name: "Wagon R", bodyType: "Hatchback", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/112947/wagon-r-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["PETROL", "CNG"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 2 },
      { brand: marutiBrand._id, brandName: "Maruti Suzuki", name: "Swift Dzire", bodyType: "Sedan", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/170173/dzire-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["PETROL", "CNG"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 3 },
      { brand: marutiBrand._id, brandName: "Maruti Suzuki", name: "Baleno", bodyType: "Hatchback", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/102663/baleno-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["PETROL", "CNG"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 4 },
      { brand: marutiBrand._id, brandName: "Maruti Suzuki", name: "Alto", bodyType: "Hatchback", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/39013/alto-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["PETROL", "CNG"], supportedTransmissions: ["MANUAL"], order: 5 },
      { brand: marutiBrand._id, brandName: "Maruti Suzuki", name: "Ritz", bodyType: "Hatchback", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/13965/ritz-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["PETROL", "DIESEL"], supportedTransmissions: ["MANUAL"], order: 6 },
      { brand: marutiBrand._id, brandName: "Maruti Suzuki", name: "Celerio", bodyType: "Hatchback", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/101569/celerio-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["PETROL", "CNG"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 7 },
      { brand: marutiBrand._id, brandName: "Maruti Suzuki", name: "Ciaz", bodyType: "Sedan", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/41197/ciaz-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["PETROL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 8 },
      { brand: marutiBrand._id, brandName: "Maruti Suzuki", name: "Alto K10", bodyType: "Hatchback", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/127563/alto-k10-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["PETROL", "CNG"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 9 },
      { brand: marutiBrand._id, brandName: "Maruti Suzuki", name: "Ertiga", bodyType: "MUV", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/115777/ertiga-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["PETROL", "CNG"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 10 },
      { brand: marutiBrand._id, brandName: "Maruti Suzuki", name: "Brezza", bodyType: "SUV", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/107543/vitara-brezza-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["PETROL", "CNG"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 11 },

      // Hyundai Models
      { brand: hyundaiBrand._id, brandName: "Hyundai", name: "i20", bodyType: "Hatchback", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/150591/i20-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["PETROL", "DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 1 },
      { brand: hyundaiBrand._id, brandName: "Hyundai", name: "Creta", bodyType: "SUV", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/141115/creta-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["PETROL", "DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 2 },
      { brand: hyundaiBrand._id, brandName: "Hyundai", name: "Venue", bodyType: "SUV", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/121943/venue-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["PETROL", "DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 3 },

      // Tata Models
      { brand: tataBrand._id, brandName: "Tata", name: "Nexon", bodyType: "SUV", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/141867/nexon-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["PETROL", "DIESEL", "ELECTRIC"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 1 },
      { brand: tataBrand._id, brandName: "Tata", name: "Punch", bodyType: "SUV", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/39015/punch-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["PETROL", "CNG", "ELECTRIC"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 2 },
      { brand: tataBrand._id, brandName: "Tata", name: "Harrier", bodyType: "SUV", imageUrl: "https://imgd.aeplcdn.com/370x208/n/cw/ec/142515/harrier-facelift-exterior-right-front-three-quarter.jpeg", supportedFuelTypes: ["DIESEL"], supportedTransmissions: ["MANUAL", "AUTOMATIC"], order: 3 }
    ];

    await VehicleModel.insertMany(modelsData);
    console.log(`Seeded ${modelsData.length} Vehicle Models with studio cutouts.`);

    console.log("Seeding Service Categories (Screenshot 4: 12-Category Grid)...");
    await ServiceCategory.deleteMany({});

    const categoriesData = [
      { code: "CAR_SERVICES", title: "Car Services", badgeText: "Special Discount", iconName: "car_repair", iconColor: "#6B1D2F", order: 1 },
      { code: "DENTING_PAINTING", title: "Denting & Painting", badgeText: "Grade-A Booth", iconName: "format_paint", iconColor: "#6B1D2F", order: 2 },
      { code: "AC_SERVICE", title: "AC Service & Repair", badgeText: "Gas Refill ₹999", iconName: "ac_unit", iconColor: "#6B1D2F", order: 3 },
      { code: "CAR_SPA", title: "Car Spa & Cleaning", badgeText: "Interior Clean", iconName: "local_car_wash", iconColor: "#6B1D2F", order: 4 },
      { code: "DETAILING", title: "Detailing Services", badgeText: "Ceramic Coat", iconName: "auto_awesome", iconColor: "#6B1D2F", order: 5 },
      { code: "BATTERIES", title: "Batteries", badgeText: "Free In-Bay Check", iconName: "battery_charging_full", iconColor: "#6B1D2F", order: 6 },
      { code: "TYRES_WHEELS", title: "Tyres & Wheel Care", badgeText: "Wheel Alignment", iconName: "tire_repair", iconColor: "#6B1D2F", order: 7 },
      { code: "INSPECTIONS", title: "Car Inspections", badgeText: "Used Car Audit", iconName: "fact_check", iconColor: "#6B1D2F", order: 8 },
      { code: "WINDSHIELDS", title: "Windshields & Lights", badgeText: "OEM Glass", iconName: "lightbulb", iconColor: "#6B1D2F", order: 9 },
      { code: "SUSPENSION", title: "Suspension & Fitments", badgeText: "Shock Absorbers", iconName: "build", iconColor: "#6B1D2F", order: 10 },
      { code: "INSURANCE", title: "Insurance Claims", badgeText: "Cashless Support", iconName: "shield", iconColor: "#6B1D2F", order: 11 },
      { code: "SOS_ROADSIDE", title: "Emergency SOS", badgeText: "24/7 Roadside", iconName: "emergency", iconColor: "#6B1D2F", order: 12 }
    ];

    await ServiceCategory.insertMany(categoriesData);
    console.log(`Seeded 12 Service Categories.`);

    console.log("Seeding Hero Promo Banners (Screenshot 4)...");
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
    console.log(`Seeded Hero Banners.`);

    console.log("Setting Customer Wallet Balance & Transactions (Screenshot 5)...");
    const customer = await User.findOne({ role: "USER" });
    if (customer) {
      customer.walletBalance = 1500;
      customer.isClubMember = true;
      await customer.save();

      await WalletTransaction.deleteMany({ user: customer._id });
      await WalletTransaction.create({
        user: customer._id,
        amount: 1500,
        type: "CREDIT",
        description: "Welcome Bonus Credits",
        referenceId: "WELCOME-1500"
      });
      console.log(`Updated customer wallet to ₹1,500.`);
    }

    console.log("SuperApp seeding finished successfully!");
    process.exit(0);
  } catch (error) {
    console.error("Error seeding SuperApp:", error);
    process.exit(1);
  }
}

seedSuperApp();
