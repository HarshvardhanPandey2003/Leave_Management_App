"use client";

import React, { useState } from "react";
import {
  Container,
  Typography,
  TextField,
  Button,
  Paper,
  Box,
  Alert,
  MenuItem,
  FormControl,
  InputLabel,
  Select,
} from "@mui/material";
import Navbar from "../../components/Navbar";
import apiClient from "../../services/apiClient";
import { useRouter } from "next/navigation";

const ApplyLeavePage = () => {
  const router = useRouter();
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [leaveType, setLeaveType] = useState("ANNUAL");
  const [reason, setReason] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    try {
      await apiClient.post("/leave/apply", {
        startDate,
        endDate,
        leaveType,
        reason,
      });
      setSuccess("Leave request submitted successfully!");
      // Optionally, redirect to leave history after a short delay.
      setTimeout(() => {
        router.push("/leave/history");
      }, 1500);
    } catch (err: any) {
      console.error("Apply leave error:", err);
      setError(
        err.response?.data?.message ||
          "Error submitting leave request. Please try again."
      );
    }
  };

  return (
    <>
      <Navbar />
      <Container sx={{ mt: 4 }}>
        <Paper sx={{ p: 3, maxWidth: 600, mx: "auto" }}>
          <Typography variant="h4" gutterBottom>
            Apply for Leave
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
              label="Start Date"
              type="date"
              fullWidth
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              InputLabelProps={{ shrink: true }}
              sx={{ mb: 2 }}
              required
            />
            <TextField
              label="End Date"
              type="date"
              fullWidth
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              InputLabelProps={{ shrink: true }}
              sx={{ mb: 2 }}
              required
            />
            <FormControl fullWidth sx={{ mb: 2 }} required>
              <InputLabel id="leave-type-label">Leave Type</InputLabel>
              <Select
                labelId="leave-type-label"
                value={leaveType}
                label="Leave Type"
                onChange={(e) => setLeaveType(e.target.value)}
              >
                <MenuItem value="ANNUAL">Annual</MenuItem>
                <MenuItem value="SICK">Sick</MenuItem>
                <MenuItem value="CASUAL">Casual</MenuItem>
              </Select>
            </FormControl>
            <TextField
              label="Reason"
              fullWidth
              multiline
              minRows={3}
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              sx={{ mb: 2 }}
              required
            />
            <Button variant="contained" type="submit" fullWidth>
              Submit Request
            </Button>
          </Box>
        </Paper>
      </Container>
    </>
  );
};

export default ApplyLeavePage;
