const PartnerRequest = require("../models/PartnerRequest");

exports.applyPartner = async (req, res) => {
  try {
    const { fullName, phone, email, workshopName, city, experienceYears, servicesOffered } = req.body;
    if (!fullName || !phone || !workshopName || !city) {
      return res.status(400).json({ success: false, message: "Full Name, Phone, Workshop Name, and City are required" });
    }

    const application = await PartnerRequest.create({
      fullName,
      phone,
      email: email || "",
      workshopName,
      city,
      experienceYears: Number(experienceYears) || 1,
      servicesOffered: Array.isArray(servicesOffered) ? servicesOffered : []
    });

    res.status(201).json({
      success: true,
      message: "Partner application submitted successfully. Our team will contact you within 24 hours.",
      application
    });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};

exports.getPartnerApplications = async (req, res) => {
  try {
    const applications = await PartnerRequest.find().sort({ createdAt: -1 });
    res.json({ success: true, count: applications.length, applications });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};
