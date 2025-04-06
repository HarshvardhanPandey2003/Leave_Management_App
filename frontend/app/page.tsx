// app/page.tsx
"use client";

import React from "react";
import Navbar from "./components/Navbar";
import { useAuth } from "./context/AuthContext";
import EmployeeHome from "./components/EmployeeHome";
import AdminHome from "./components/AdminHome";

export default function HomePage() {
  const { user } = useAuth();

  // Only allow authenticated users; if no user is logged in, return null or redirect.
  if (!user) {
    return null;
  }

  return (
    <>
      <Navbar />
      {user.role === "ADMIN" ? <AdminHome /> : <EmployeeHome />}
    </>
  );
}
