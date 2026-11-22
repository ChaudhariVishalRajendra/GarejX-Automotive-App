const SOSRequest = require("../models/SOSRequest");

exports.createSOS = async (req, res) => {
  try {
    const { phone, latitude, longitude, locationName, issueType, notes } = req.body;
    if (!phone || latitude === undefined || longitude === undefined) {
      return res.status(400).json({ success: false, message: "Phone and coordinates are required" });
    }

    const sos = await SOSRequest.create({
      user: req.user.id,
      phone,
      latitude,
      longitude,
      locationName: locationName || "Current Roadside Location",
      issueType: issueType || "TOWING",
      notes: notes || ""
    });

    res.status(201).json({
      success: true,
      message: "Emergency Roadside Assistance dispatched. A technician is on the way.",
      sos
    });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};

exports.getSOSRequests = async (req, res) => {
  try {
    let query = {};
    if (req.user.role !== "ADMIN") {
      query = { user: req.user.id };
    }
    const list = await SOSRequest.find(query).sort({ createdAt: -1 });
    res.json({ success: true, count: list.length, sosRequests: list });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};
