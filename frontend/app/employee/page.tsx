"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";
import {
  Container,
  Typography,
  Button,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Box,
} from "@mui/material";
import Navbar from "../components/Navbar";
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

const EmployeeHomepage: React.FC = () => {
  const [leaveRequests, setLeaveRequests] = useState<LeaveRequest[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>("");

  useEffect(() => {
    const fetchLeaveRequests = async () => {
      try {
        const response = await apiClient.get("/leave/my-requests");
        setLeaveRequests(response.data);
      } catch (err: any) {
        console.error("Error fetching leave requests", err);
        setError("Failed to load leave requests.");
      } finally {
        setLoading(false);
      }
    };

    fetchLeaveRequests();
  }, []);

  return (
    <Box
      sx={{
        backgroundColor: "#333",
        minHeight: "100vh",
        color: "#fff",
        py: 2,
      }}
    >
      <Navbar />
      <Container maxWidth="lg" sx={{ pt: 4 }}>
        <Typography variant="h3" component="h1" gutterBottom>
          Employee Dashboard
        </Typography>
        <Button
          variant="contained"
          color="primary"
          component={Link}
          href="/leave/apply"
          sx={{ mb: 3 }}
        >
          Create Leave Request
        </Button>
        <Typography variant="h5" gutterBottom>
          My Leave Requests
        </Typography>
        {loading ? (
          <Typography>Loading...</Typography>
        ) : error ? (
          <Typography color="error">{error}</Typography>
        ) : leaveRequests.length === 0 ? (
          <Typography>No leave requests found.</Typography>
        ) : (
          <TableContainer component={Paper} sx={{ mt: 2 }}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Start Date</TableCell>
                  <TableCell>End Date</TableCell>
                  <TableCell>Type</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Applied At</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {leaveRequests.map((request) => (
                  <TableRow key={request.id}>
                    <TableCell>{request.id}</TableCell>
                    <TableCell>{request.startDate}</TableCell>
                    <TableCell>{request.endDate}</TableCell>
                    <TableCell>{request.leaveType}</TableCell>
                    <TableCell>{request.status}</TableCell>
                    <TableCell>
                      {new Date(request.appliedAt).toLocaleString()}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Container>
    </Box>
  );
};

export default EmployeeHomepage;
