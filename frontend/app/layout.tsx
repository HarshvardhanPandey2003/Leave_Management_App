// app/layout.tsx
"use client";

import React from "react";
import { AuthProvider } from "./context/AuthContext";
import { CssBaseline, Container } from "@mui/material";

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body style={{ backgroundColor: "#2e2e2e", margin: 0 }}>
        <CssBaseline />
        <AuthProvider>
          <Container maxWidth="lg">
            {children}
          </Container>
        </AuthProvider>
      </body>
    </html>
  );
}
