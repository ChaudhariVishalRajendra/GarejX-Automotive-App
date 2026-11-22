const Garage = require("../models/Garage");

async function getGarage(req, res) {
  let garage = await Garage.findOne();
  if (!garage) garage = await Garage.create({});
  res.json({ success: true, garage });
}

async function updateGarage(req, res) {
  let garage = await Garage.findOne();
  if (!garage) garage = new Garage();

  ["name", "phone", "email", "address", "openingTime", "closingTime"].forEach((field) => {
    if (req.body[field] !== undefined) garage[field] = req.body[field];
  });

  await garage.save();
  res.json({ success: true, message: "Garage settings updated", garage });
}

module.exports = { getGarage, updateGarage };
