const express = require("express");
const protect = require("../middleware/authMiddleware");
const allowRoles = require("../middleware/roleMiddleware");
const { getUsers, getUser, createStaff, updateUser, deleteUser } = require("../controllers/userController");

const router = express.Router();
router.use(protect, allowRoles("ADMIN"));
router.get("/", getUsers);
router.get("/:id", getUser);
router.post("/staff", createStaff);
router.put("/:id", updateUser);
router.delete("/:id", deleteUser);

module.exports = router;
