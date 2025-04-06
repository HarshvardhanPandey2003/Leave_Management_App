// app/auth/signup/page.tsx
"use client";

import React from "react";
import SignupForm from "../../components/auth/SignupForm";
import Navbar from "../../components/Navbar";

const SignupPage = () => {
  return (
    <>
      <Navbar />
      <SignupForm />
    </>
  );
};

export default SignupPage;
