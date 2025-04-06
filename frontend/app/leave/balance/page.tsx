"use client";

import React, { useEffect, useState } from "react";
import {
  Container,
  Typography,
  Box,
  Paper,
  CircularProgress,
  Alert,
} from "@mui/material";
import Navbar from "../../components/Navbar";
import apiClient from "../../services/apiClient";

interface LeaveBalance {
  annualLeaveBalance: number;
  sickLeaveBalance: number;
  casualLeaveBalance: number;
}

const LeaveBalancePage = () => {
  const [balance, setBalance] = useState<LeaveBalance | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    apiClient
      .get("/leave/balance")
      .then((response) => {
        setBalance(response.data);
        setLoading(false);
      })
      .catch((err: any) => {
        console.error("Error fetching leave balance:", err);
        setError("Failed to load leave balance. Please try again later.");
        setLoading(false);
      });
  }, []);

  return (
    <>
      <Navbar />
      <Container sx={{ mt: 4 }}>
        <Paper sx={{ p: 3 }}>
          <Typography variant="h4" gutterBottom>
            Leave Balance
          </Typography>
          {loading ? (
            <Box sx={{ display: "flex", justifyContent: "center", p: 3 }}>
              <CircularProgress />
            </Box>
          ) : error ? (
            <Alert severity="error">{error}</Alert>
          ) : balance ? (
            <Box>
              <Typography variant="h6">
                Annual Leave Balance: {balance.annualLeaveBalance} days
              </Typography>
              <Typography variant="h6">
                Sick Leave Balance: {balance.sickLeaveBalance} days
              </Typography>
              <Typography variant="h6">
                Casual Leave Balance: {balance.casualLeaveBalance} days
              </Typography>
            </Box>
          ) : null}
        </Paper>
      </Container>
    </>
  );
};

export default LeaveBalancePage;
