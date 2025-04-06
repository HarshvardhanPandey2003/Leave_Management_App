"use client";

import React, { useState, useEffect } from "react";
import {
  Container,
  Typography,
  Box,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  Chip,
  CircularProgress,
  Alert,
} from "@mui/material";
import Navbar from "../../components/Navbar";
import apiClient from "../../services/apiClient";

interface LeaveRequest {
  id: number;
  name: string;
  email: string;
  startDate: string;
  endDate: string;
  reason: string;
  leaveType: string;
  status: string;
  appliedAt: string;
}

const ApprovalsPage = () => {
  const [requests, setRequests] = useState<LeaveRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionError, setActionError] = useState("");
  const [actionSuccess, setActionSuccess] = useState("");

  useEffect(() => {
    fetchRequests();
  }, []);

  const fetchRequests = () => {
    apiClient
      .get("/leave/all")
      .then((response) => {
        // Filter for pending requests only
        const pendingRequests = response.data.filter(
          (req: LeaveRequest) => req.status === "PENDING"
        );
        setRequests(pendingRequests);
        setLoading(false);
      })
      .catch((error) => {
        setError("Failed to fetch leave requests");
        setLoading(false);
      });
  };

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

  const handleApprove = (id: number) => {
    setActionError("");
    setActionSuccess("");
    apiClient
      .put(`/leave/approve/${id}`)
      .then((response) => {
        setActionSuccess("Leave request approved successfully.");
        fetchRequests();
      })
      .catch(() => {
        setActionError("Failed to approve leave request.");
      });
  };

  const handleReject = (id: number) => {
    setActionError("");
    setActionSuccess("");
    // For simplicity, we are sending an empty rejection reason.
    // You could prompt for a reason if needed.
    apiClient
      .put(`/leave/reject/${id}`, {})
      .then((response) => {
        setActionSuccess("Leave request rejected successfully.");
        fetchRequests();
      })
      .catch(() => {
        setActionError("Failed to reject leave request.");
      });
  };

  const formatDate = (dateString: string) =>
    new Date(dateString).toLocaleDateString();

  return (
    <>
      <Navbar />
      <Container sx={{ mt: 4 }}>
        <Paper sx={{ p: 3 }}>
          <Typography variant="h4" gutterBottom>
            Pending Leave Approvals
          </Typography>
          {actionError && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {actionError}
            </Alert>
          )}
          {actionSuccess && (
            <Alert severity="success" sx={{ mb: 2 }}>
              {actionSuccess}
            </Alert>
          )}
          {loading ? (
            <Box sx={{ display: "flex", justifyContent: "center", p: 3 }}>
              <CircularProgress />
            </Box>
          ) : error ? (
            <Typography color="error">{error}</Typography>
          ) : requests.length === 0 ? (
            <Typography>No pending leave requests.</Typography>
          ) : (
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Employee</TableCell>
                    <TableCell>Email</TableCell>
                    <TableCell>Type</TableCell>
                    <TableCell>From</TableCell>
                    <TableCell>To</TableCell>
                    <TableCell>Reason</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Applied On</TableCell>
                    <TableCell>Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {requests.map((req) => (
                    <TableRow key={req.id}>
                      <TableCell>{req.name}</TableCell>
                      <TableCell>{req.email}</TableCell>
                      <TableCell>{req.leaveType}</TableCell>
                      <TableCell>{formatDate(req.startDate)}</TableCell>
                      <TableCell>{formatDate(req.endDate)}</TableCell>
                      <TableCell>{req.reason}</TableCell>
                      <TableCell>
                        <Chip
                          label={req.status}
                          color={getStatusColor(req.status) as any}
                          size="small"
                        />
                      </TableCell>
                      <TableCell>{formatDate(req.appliedAt)}</TableCell>
                      <TableCell>
                        <Button
                          variant="outlined"
                          size="small"
                          onClick={() => handleApprove(req.id)}
                          sx={{ mr: 1 }}
                        >
                          Approve
                        </Button>
                        <Button
                          variant="outlined"
                          size="small"
                          color="error"
                          onClick={() => handleReject(req.id)}
                        >
                          Reject
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </Paper>
      </Container>
    </>
  );
};

export default ApprovalsPage;
