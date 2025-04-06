"use client";

import React, { useState } from "react";
import {
  Container,
  Typography,
  Paper,
  TextField,
  Button,
  Box,
  Alert,
} from "@mui/material";
import Navbar from "../../components/Navbar";
import apiClient from "../../services/apiClient";

const UpdateBalancePage = () => {
  const [email, setEmail] = useState("");
  const [annualLeaveBalance, setAnnualLeaveBalance] = useState(0);
  const [sickLeaveBalance, setSickLeaveBalance] = useState(0);
  const [casualLeaveBalance, setCasualLeaveBalance] = useState(0);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    try {
      const response = await apiClient.put("/leave/balance/update", {
        email,
        annualLeaveBalance,
        sickLeaveBalance,
        casualLeaveBalance,
      });
      if (response.data.success) {
        setSuccess("Leave balance updated successfully.");
      } else {
        setError("Failed to update leave balance.");
      }
    } catch (err: any) {
      setError(
        err.response?.data?.message || "Error updating leave balance."
      );
    }
  };

  return (
    <>
      <Navbar />
      <Container sx={{ mt: 4 }}>
        <Paper sx={{ p: 3, maxWidth: 600, mx: "auto" }}>
          <Typography variant="h4" gutterBottom>
            Update Employee Leave Balance
          </Typography>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}
          {success && (
            <Alert severity="success" sx={{ mb: 2 }}>
              {success}
            </Alert>
          )}
          <Box component="form" onSubmit={handleSubmit} noValidate>
            <TextField
              label="Employee Email"
              fullWidth
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              sx={{ mb: 2 }}
              required
            />
            <TextField
              label="Annual Leave Balance"
              type="number"
              fullWidth
              value={annualLeaveBalance}
              onChange={(e) =>
                setAnnualLeaveBalance(Number(e.target.value))
              }
              sx={{ mb: 2 }}
              required
            />
            <TextField
              label="Sick Leave Balance"
              type="number"
              fullWidth
              value={sickLeaveBalance}
              onChange={(e) => setSickLeaveBalance(Number(e.target.value))}
              sx={{ mb: 2 }}
              required
            />
            <TextField
              label="Casual Leave Balance"
              type="number"
              fullWidth
              value={casualLeaveBalance}
              onChange={(e) =>
                setCasualLeaveBalance(Number(e.target.value))
              }
              sx={{ mb: 2 }}
              required
            />
            <Button variant="contained" type="submit" fullWidth>
              Update Balance
            </Button>
          </Box>
        </Paper>
      </Container>
    </>
  );
};

export default UpdateBalancePage;
