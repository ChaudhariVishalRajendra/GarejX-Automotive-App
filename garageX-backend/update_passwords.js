require("dotenv").config();
const mongoose = require("mongoose");
const bcrypt = require("bcryptjs");
const User = require("./src/models/User");

async function run() {
  try {
    await mongoose.connect(process.env.MONGO_URI || "mongodb://localhost:27017/garejx");
    const hash = await bcrypt.hash("password@100", 10);
    
    // Update existing or create if missing
    const accounts = [
      { email: "customer@garejx.com", name: "Customer User", phone: "+91 9313611641", role: "USER" },
      { email: "mechanic@garejx.com", name: "Mike Mechanic", phone: "9876543211", role: "MECHANIC" },
      { email: "admin@garejx.com", name: "Admin User", phone: "1234567890", role: "ADMIN" }
    ];

    for (const acc of accounts) {
      let u = await User.findOne({ email: acc.email });
      if (u) {
        u.password = hash;
        await u.save();
        console.log(`Updated password to password@100 for: ${acc.email}`);
      } else {
        await User.create({ ...acc, password: hash });
        console.log(`Created account with password@100 for: ${acc.email}`);
      }
    }
    console.log("All accounts password set to password@100 successfully!");
    process.exit(0);
  } catch (e) {
    console.error(e);
    process.exit(1);
  }
}

run();
