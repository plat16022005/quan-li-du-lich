const mongoose = require("mongoose");
const User = require("./src/models/user");
const bcrypt = require("bcryptjs");

mongoose.connect("mongodb://127.0.0.1:27017/node_fullstack").then(async () => {
  const admin = await User.findOne({ role: "admin" });
  if (admin) {
    console.log("Admin found:", admin.email);
    console.log("If you don't know the password, we can reset it.");
  } else {
    console.log("No admin found. Creating one...");
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash("admin123", salt);
    await User.create({
      email: "admin@example.com",
      password: hashedPassword,
      name: "Administrator",
      role: "admin",
      is_active: true
    });
    console.log("Admin created:");
    console.log("Email: admin@example.com");
    console.log("Password: admin123");
  }
  process.exit(0);
});
