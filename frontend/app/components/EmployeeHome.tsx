// app/components/EmployeeHome.tsx
"use client";

import React, { useState, useEffect } from "react";
import {
  Container,
  Typography,
  Box,
  Paper,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  CircularProgress
} from "@mui/material";
import Link from "next/link";
import { Add as AddIcon } from "@mui/icons-material";
import apiClient from "../services/apiClient";

interface LeaveRequest {
  id: number;
  startDate: string;
  endDate: string;
  reason: string;
  leaveType: string;
  status: string;
  appliedAt: string;
}

const EmployeeHome = () => {
  const [leaveRequests, setLeaveRequests] = useState<LeaveRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Fetch the logged-in employee's leave requests
  useEffect(() => {
    apiClient
      .get("/leave/my-requests")
      .then((response) => {
        setLeaveRequests(response.data);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Error fetching leave requests:", err);
        setError("Failed to load leave requests. Please try again later.");
        setLoading(false);
      });
  }, []);

  const getStatusColor = (status: string) => {
    switch (status) {
      case "APPROVED":
        return "success";
      case "REJECTED":
        return "error";
      case "CANCELLED":
        return "warning";
      default:
        return "info";
    }
  };

  const formatDate = (dateString: string) =>
    new Date(dateString).toLocaleDateString();

  return (
    <Box sx={{ minHeight: "100vh", bgcolor: "#222222", color: "white", py: 4 }}>
      <Container>
        <Typography variant="h4" gutterBottom>
          Employee Dashboard
        </Typography>
        <Box sx={{ textAlign: "right", mb: 4 }}>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            component={Link}
            href="/leave/apply"
          >
            Apply for Leave
          </Button>
        </Box>
        <Paper sx={{ p: 3 }}>
          <Typography variant="h5" gutterBottom>
            My Leave Requests
          </Typography>
          {loading ? (
            <Box sx={{ display: "flex", justifyContent: "center", p: 3 }}>
              <CircularProgress />
            </Box>
          ) : error ? (
            <Typography color="error">{error}</Typography>
          ) : leaveRequests.length === 0 ? (
            <Box sx={{ textAlign: "center", py: 4 }}>
              <Typography sx={{ mb: 2 }}>
                You haven't made any leave requests yet.
              </Typography>
              <Button
                variant="contained"
                component={Link}
                href="/leave/apply"
                startIcon={<AddIcon />}
              >
                Apply for your first leave
              </Button>
            </Box>
          ) : (
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Type</TableCell>
                    <TableCell>From</TableCell>
                    <TableCell>To</TableCell>
                    <TableCell>Reason</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Applied On</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {leaveRequests.map((request) => (
                    <TableRow key={request.id}>
                      <TableCell>{request.leaveType}</TableCell>
                      <TableCell>{formatDate(request.startDate)}</TableCell>
                      <TableCell>{formatDate(request.endDate)}</TableCell>
                      <TableCell>{request.reason}</TableCell>
                      <TableCell>
                        <Chip
                          label={request.status}
                          color={getStatusColor(request.status) as any}
                          size="small"
                        />
                      </TableCell>
                      <TableCell>{formatDate(request.appliedAt)}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </Paper>
      </Container>
    </Box>
  );
};

export default EmployeeHome;
