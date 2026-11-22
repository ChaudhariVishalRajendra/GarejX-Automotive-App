const User = require("../models/User");
const WalletTransaction = require("../models/WalletTransaction");

exports.getWallet = async (req, res) => {
  try {
    const user = await User.findById(req.user.id).select("walletBalance isClubMember");
    if (!user) {
      return res.status(404).json({ success: false, message: "User not found" });
    }

    const transactions = await WalletTransaction.find({ user: req.user.id }).sort({ createdAt: -1 });

    res.json({
      success: true,
      balance: user.walletBalance || 1500,
      isClubMember: user.isClubMember || false,
      annualSavings: 19900,
      transactions
    });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};

exports.addMoney = async (req, res) => {
  try {
    const { amount, description } = req.body;
    const addAmt = Number(amount) || 0;
    if (addAmt <= 0) {
      return res.status(400).json({ success: false, message: "Invalid amount" });
    }

    const user = await User.findById(req.user.id);
    user.walletBalance = (user.walletBalance || 0) + addAmt;
    await user.save();

    const txn = await WalletTransaction.create({
      user: req.user.id,
      amount: addAmt,
      type: "CREDIT",
      description: description || "Wallet Top-up",
      referenceId: "TXN-" + Date.now()
    });

    res.json({
      success: true,
      balance: user.walletBalance,
      transaction: txn
    });
  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};
