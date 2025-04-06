// app/components/AdminHome.tsx
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
  CircularProgress,
} from "@mui/material";
import Link from "next/link";
import apiClient from "../services/apiClient";

interface LeaveRequest {
  id: number;
  startDate: string;
  endDate: string;
  reason: string;
  leaveType: string;
  status: string;
  appliedAt: string;
  name?: string;
}

const AdminHome = () => {
  const [leaveRequests, setLeaveRequests] = useState<LeaveRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // Fetch all leave requests for admin
  useEffect(() => {
    apiClient
      .get("/leave/all")
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
          Admin Dashboard
        </Typography>
        <Box sx={{ display: "flex", flexWrap: "wrap", gap: 2, mb: 4 }}>
          <Box
            sx={{
              flex: "1",
              minWidth: "280px",
              border: "1px solid #444",
              p: 2,
              borderRadius: "4px",
            }}
          >
            <Typography variant="h6" color="primary">
              Pending Approvals
            </Typography>
            <Typography variant="h3" color="text.secondary">
              {
                leaveRequests.filter((req) => req.status === "PENDING")
                  .length
              }
            </Typography>
            <Button
              variant="contained"
              size="small"
              component={Link}
              href="/admin/approve"
              sx={{ mt: 2 }}
            >
              Review Requests
            </Button>
          </Box>
          <Box
            sx={{
              flex: "1",
              minWidth: "280px",
              border: "1px solid #444",
              p: 2,
              borderRadius: "4px",
            }}
          >
            <Typography variant="h6" color="primary">
              Admin Actions
            </Typography>
            <Button
              variant="contained"
              fullWidth
              component={Link}
              href="/admin/update-balance"
              sx={{ mt: 2 }}
            >
              Update Employee Leave Balance
            </Button>
          </Box>
        </Box>
        <Paper sx={{ p: 3 }}>
          <Typography variant="h5" gutterBottom>
            Recent Leave Requests
          </Typography>
          {loading ? (
            <Box sx={{ display: "flex", justifyContent: "center", p: 3 }}>
              <CircularProgress />
            </Box>
          ) : error ? (
            <Typography color="error">{error}</Typography>
          ) : leaveRequests.length === 0 ? (
            <Typography>No leave requests found.</Typography>
          ) : (
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Employee</TableCell>
                    <TableCell>Type</TableCell>
                    <TableCell>From</TableCell>
                    <TableCell>To</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Applied On</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {leaveRequests.slice(0, 5).map((request) => (
                    <TableRow key={request.id}>
                      <TableCell>{request.name}</TableCell>
                      <TableCell>{request.leaveType}</TableCell>
                      <TableCell>{formatDate(request.startDate)}</TableCell>
                      <TableCell>{formatDate(request.endDate)}</TableCell>
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
          {leaveRequests.length > 5 && (
            <Box sx={{ textAlign: "center", mt: 2 }}>
              <Button component={Link} href="/admin/approve" variant="text">
                View All Requests
              </Button>
            </Box>
          )}
        </Paper>
      </Container>
    </Box>
  );
};

export default AdminHome;
